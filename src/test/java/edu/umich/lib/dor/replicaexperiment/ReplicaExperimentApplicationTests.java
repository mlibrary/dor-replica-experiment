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
	private Path repoOnePath = testReposPath.resolve("repo_one");
	private Path repoOneStoragePath = repoOnePath.resolve("storage");
	private Path repoOneWorkspacePath = repoOnePath.resolve("workspace");
	private Path repoOneDepositPath = repoOnePath.resolve("deposit");

	private Path depositAPath = repoOneDepositPath.resolve("a");

	@Autowired
	private RepositoryManager repositoryManager;

	private RepositoryService repoOneService;

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
		this.repoOneService = new OcflFilesystemRepositoryService(
			repoOneStoragePath, repoOneWorkspacePath
		);
	}

	@Test
	void contextLoads() {
	}

	@Test
	void dorCanCreateAPackageInARepository() {
		repositoryManager.registerRepositoryService("repo_one", repoOneService);
		repositoryManager.setUser(new User("test", "test@example.edu"));

		String repositoryName = "repo_one";
		String identifier = "A";
		Path sourcePath = depositAPath;

		repositoryManager.addPackageToRepository(
			identifier, sourcePath, repositoryName, "first version!!!"
		);

		InfoPackage infoPackageA = repositoryManager.getInfoPackage(identifier);
		Repository repository = repositoryManager.getRepository(repositoryName);

		assertEquals("A", infoPackageA.getIdentifier());

		assertEquals("FILE_SYSTEM", repository.getType());
		assertEquals(repositoryName, repository.getName());

		assertTrue(infoPackageA.getReplicas().size() == 1);
		assertTrue(infoPackageA.hasAReplicaIn(repositoryName));

		List<String> filePaths = repoOneService.getFilePaths(identifier);
		for (String filePath: filePaths) {
			Path fullPath = repoOneStoragePath.resolve(filePath);
			assertTrue(Files.exists(fullPath));
		}
	}
}
