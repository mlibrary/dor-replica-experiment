package edu.umich.lib.dor.replicaexperiment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.FileSystemUtils;

import edu.umich.lib.dor.replicaexperiment.domain.InfoPackage;
import edu.umich.lib.dor.replicaexperiment.domain.Replica;
import edu.umich.lib.dor.replicaexperiment.domain.Repository;
import edu.umich.lib.dor.replicaexperiment.domain.User;
import edu.umich.lib.dor.replicaexperiment.service.Deposit;
import edu.umich.lib.dor.replicaexperiment.service.DepositFactory;
import edu.umich.lib.dor.replicaexperiment.service.InfoPackageService;
import edu.umich.lib.dor.replicaexperiment.service.OcflFilesystemRepositoryClient;
import edu.umich.lib.dor.replicaexperiment.service.ReplicaService;
import edu.umich.lib.dor.replicaexperiment.service.ReplicationFactory;
import edu.umich.lib.dor.replicaexperiment.service.RepositoryClient;
import edu.umich.lib.dor.replicaexperiment.service.RepositoryClientRegistry;
import edu.umich.lib.dor.replicaexperiment.service.RepositoryService;

@Import(TestcontainersConfiguration.class)
@DataJpaTest
@ContextConfiguration(classes = {TestsConfig.class, TestConfiguration.class})
@ComponentScan(basePackages={"edu.umich.lib.dor.replicaexperiment.service"}) 
@EntityScan(basePackages={"edu.umich.lib.dor.replicaexperiment.domain"})
@AutoConfigureTestDatabase(replace = Replace.NONE)
class ReplicaExperimentApplicationTests {
    static final Log log = LogFactory.getLog(ReplicaExperimentApplication.class);

    Path testReposPath = Paths.get("src", "test", "resources", "test_repositories");
    String repoOneName = "repo_one";
    Path repoOnePath = testReposPath.resolve("repo_one");
    Path repoOneStoragePath = repoOnePath.resolve("storage");
    Path repoOneWorkspacePath = repoOnePath.resolve("workspace");

    String repoTwoName = "repo_two";
    Path repoTwoPath = testReposPath.resolve("repo_two");
    Path repoTwoStoragePath = repoTwoPath.resolve("storage");
    Path repoTwoWorkspacePath = repoTwoPath.resolve("workspace");

    Path depositPath = testReposPath.resolve("deposit");
    String depositAIdentifier = "A";
    Path depositAPath = Paths.get("A");

    Path stagingPath = testReposPath.resolve("staging");

    User testUser = new User("test", "test@example.edu");

    @Autowired
    InfoPackageService infoPackageService;

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    ReplicaService replicaService;

    DepositFactory depositFactory;
    ReplicationFactory replicationFactory;

    RepositoryClient repoOneClient;
    RepositoryClient repoTwoClient;

    void resetDirPath(Path path) {
        if (Files.exists(path)) {
            try {
                FileSystemUtils.deleteRecursively(path);
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @BeforeEach
    void init() {
        resetDirPath(repoOneStoragePath);
        resetDirPath(repoTwoStoragePath);
        this.repoOneClient = new OcflFilesystemRepositoryClient(
            repoOneStoragePath, repoOneWorkspacePath
        );
        this.repoTwoClient = new OcflFilesystemRepositoryClient(
            repoTwoStoragePath, repoTwoWorkspacePath
        );

        RepositoryClientRegistry registry = new RepositoryClientRegistry();
        registry.register(repoOneName, repoOneClient);
        registry.register(repoTwoName, repoTwoClient);
        for (String repositoryName: registry.listClients()) {
            this.repositoryService.getOrCreateRepository(repositoryName);
        }

        this.depositFactory = new DepositFactory(
            infoPackageService, repositoryService, replicaService, registry, depositPath
        );
        this.replicationFactory = new ReplicationFactory(
            infoPackageService, repositoryService, replicaService, registry, stagingPath
        );
    }

    @Test
    void contextLoads() {
    }

    @Test
    void repositoryManagerDepositAPackageInARepository() {
        var deposit = depositFactory.create(
            testUser, depositAIdentifier, depositAPath, repoOneName, "first version!!!"
        );
        deposit.execute();

        InfoPackage infoPackageA = infoPackageService.getInfoPackage(depositAIdentifier);
        Repository repository = repositoryService.getRepository(repoOneName);

        assertEquals("A", infoPackageA.getIdentifier());

        assertEquals("FILE_SYSTEM", repository.getType());
        assertEquals(repoOneName, repository.getName());

        assertEquals(1, infoPackageA.getReplicas().size());
        assertTrue(infoPackageA.hasAReplicaIn(repoOneName));

        List<String> filePaths = repoOneClient.getFilePaths(depositAIdentifier);
        for (String filePath: filePaths) {
            Path fullPath = repoOneStoragePath.resolve(filePath);
            assertTrue(Files.exists(fullPath));
        }
    }

    @Test
    void repositoryManagerCanReplicateAPackageToAnotherRepository() {
        Deposit deposit = depositFactory.create(
            testUser, depositAIdentifier, depositAPath, repoOneName, "first version!!!"
        );
        deposit.execute();
        var replication = replicationFactory.create(depositAIdentifier, repoOneName, repoTwoName);
        replication.execute();

        InfoPackage infoPackage = infoPackageService.getInfoPackage(depositAIdentifier);
        assertEquals(2, infoPackage.getNumReplicas());
        Repository repositoryTwo = repositoryService.getRepository(repoTwoName);
        var replicasInRepoTwo = repositoryTwo.getReplicas();
        assertEquals(1, replicasInRepoTwo.size());
        if (replicasInRepoTwo.size() == 1) {
            Replica repoTwoReplica = replicasInRepoTwo.iterator().next();
            assertEquals(depositAIdentifier, repoTwoReplica.getInfoPackage().getIdentifier());
        }

        List<String> filePaths = repoTwoClient.getFilePaths(depositAIdentifier);
        for (String filePath: filePaths) {
            Path fullPath = repoTwoStoragePath.resolve(filePath);
            assertTrue(Files.exists(fullPath));
        }
    }
}
