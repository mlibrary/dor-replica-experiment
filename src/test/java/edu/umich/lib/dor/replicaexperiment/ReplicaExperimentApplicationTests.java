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
import edu.umich.lib.dor.replicaexperiment.service.OcflFilesystemRepositoryService;
import edu.umich.lib.dor.replicaexperiment.service.RepositoryManager;
import edu.umich.lib.dor.replicaexperiment.service.RepositoryService;

@Import(TestcontainersConfiguration.class)
@DataJpaTest
@ContextConfiguration(classes = {AppConfig.class, TestConfiguration.class})
@ComponentScan(basePackages={"edu.umich.lib.dor.replicaexperiment.service"}) 
@EntityScan(basePackages={"edu.umich.lib.dor.replicaexperiment.domain"})
@AutoConfigureTestDatabase(replace = Replace.NONE)
class ReplicaExperimentApplicationTests {
	private static final Log log = LogFactory.getLog(ReplicaExperimentApplication.class);

	private Path testReposPath = Paths.get("src", "test", "resources", "test_repositories");
	private String repoOneName = "repo_one";
	private Path repoOnePath = testReposPath.resolve("repo_one");
	private Path repoOneStoragePath = repoOnePath.resolve("storage");
	private Path repoOneWorkspacePath = repoOnePath.resolve("workspace");

	private String repoTwoName = "repo_two";
	private Path repoTwoPath = testReposPath.resolve("repo_two");
	private Path repoTwoStoragePath = repoTwoPath.resolve("storage");
	private Path repoTwoWorkspacePath = repoTwoPath.resolve("workspace");

	private Path depositPath = testReposPath.resolve("deposit");
	private String depositAIdentifier = "A";
	private Path depositAPath = depositPath.resolve("a");

	private Path stagingPath = testReposPath.resolve("staging");

	User testUser = new User("test", "test@example.edu");

	@Autowired
	private RepositoryManager repositoryManager;

	private RepositoryService repoOneService;
	private RepositoryService repoTwoService;

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
		this.repoOneService = new OcflFilesystemRepositoryService(
			repoOneStoragePath, repoOneWorkspacePath
		);
		this.repoTwoService = new OcflFilesystemRepositoryService(
			repoTwoStoragePath, repoTwoWorkspacePath
		);
	}

	@Test
	void contextLoads() {
	}

	@Test
	void repositoryManagerCanAddAPackageToARepository() {
		repositoryManager.registerRepositoryService(repoOneName, repoOneService);
		repositoryManager.setUser(testUser);
		repositoryManager.setStagingPath(stagingPath);
		log.debug(repositoryManager);

		repositoryManager.addPackageToRepository(
			depositAIdentifier, depositAPath, repoOneName, "first version!!!"
		);

		InfoPackage infoPackageA = repositoryManager.getInfoPackage(depositAIdentifier);
		Repository repository = repositoryManager.getRepository(repoOneName);

		assertEquals("A", infoPackageA.getIdentifier());

		assertEquals("FILE_SYSTEM", repository.getType());
		assertEquals(repoOneName, repository.getName());

		assertEquals(1, infoPackageA.getReplicas().size());
		assertTrue(infoPackageA.hasAReplicaIn(repoOneName));

		List<String> filePaths = repoOneService.getFilePaths(depositAIdentifier);
		for (String filePath: filePaths) {
			Path fullPath = repoOneStoragePath.resolve(filePath);
			assertTrue(Files.exists(fullPath));
		}
	}

	@Test
	void repositoryManagerCanReplicateAPackageToAnotherRepository() {
		repositoryManager.registerRepositoryService(repoOneName, repoOneService);
		repositoryManager.registerRepositoryService(repoTwoName, repoTwoService);
		repositoryManager.setUser(testUser);
		repositoryManager.setStagingPath(stagingPath);
		log.debug(repositoryManager);

		repositoryManager.addPackageToRepository(
			depositAIdentifier, depositAPath, repoOneName, "first version!!!"
		);
		repositoryManager.replicatePackageToAnotherRepository(
			depositAIdentifier, repoOneName, repoTwoName
		);

		InfoPackage infoPackage = repositoryManager.getInfoPackage(depositAIdentifier);
		assertEquals(2, infoPackage.getNumReplicas());
		Repository repositoryTwo = repositoryManager.getRepository(repoTwoName);
		var replicasInRepoTwo = repositoryTwo.getReplicas();
		assertEquals(1, replicasInRepoTwo.size());
		if (replicasInRepoTwo.size() == 1) {
			Replica repoTwoReplica = replicasInRepoTwo.iterator().next();
			assertEquals(depositAIdentifier, repoTwoReplica.getInfoPackage().getIdentifier());
		}

		List<String> filePaths = repoTwoService.getFilePaths(depositAIdentifier);
		for (String filePath: filePaths) {
			Path fullPath = repoTwoStoragePath.resolve(filePath);
			assertTrue(Files.exists(fullPath));
		}
	}
}
