package edu.umich.lib.dor.replicaexperiment;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import edu.umich.lib.dor.replicaexperiment.domain.InfoPackageRepository;
import edu.umich.lib.dor.replicaexperiment.domain.ReplicaRepository;
import edu.umich.lib.dor.replicaexperiment.domain.RepositoryRepository;
import edu.umich.lib.dor.replicaexperiment.service.OcflFilesystemRepositoryClient;
import edu.umich.lib.dor.replicaexperiment.service.RepositoryManager;
import edu.umich.lib.dor.replicaexperiment.service.RepositoryClient;

@Configuration
@ComponentScan("edu.umich.lib.dor.replicaexperiment.service")
@EnableTransactionManagement
@EnableJpaRepositories(basePackages="edu.umich.lib.dor.replicaexperiment.domain")
public class AppConfig {
    @Bean
	public RepositoryManager repositoryManager(
		RepositoryRepository repositoryRepository,
		InfoPackageRepository infoPackageRepository,
		ReplicaRepository replicaRepository,
        Environment environment
	) {
        Path repoOnePath = Paths.get(
			environment.getRequiredProperty("repository.repo_one.path")
		);
		Path repoTwoPath = Paths.get(
			environment.getRequiredProperty("repository.repo_two.path")
		);
		Path depositPath = Paths.get(
			environment.getRequiredProperty("repository.deposit.path")
		);
		Path stagingPath = Paths.get(
			environment.getRequiredProperty("repository.staging.path")
		);

		String repoOneName = "repo_one";
		Path repoOneStoragePath = repoOnePath.resolve("storage");
		Path repoOneWorkspacePath = repoOnePath.resolve("workspace");
		RepositoryClient repoOneClient = new OcflFilesystemRepositoryClient(
			repoOneStoragePath, repoOneWorkspacePath
		);

		String repoTwoName = "repo_two";
		Path repoTwoStoragePath = repoTwoPath.resolve("storage");
		Path repoTwoWorkspacePath = repoTwoPath.resolve("workspace");
		RepositoryClient repoTwoClient = new OcflFilesystemRepositoryClient(
			repoTwoStoragePath, repoTwoWorkspacePath
		);

		RepositoryManager manager = new RepositoryManager(
			repositoryRepository,
			infoPackageRepository,
			replicaRepository
		);
		manager.setDepositPath(depositPath);
        manager.setStagingPath(stagingPath);
		manager.registerRepository(repoOneName, repoOneClient);
		manager.registerRepository(repoTwoName, repoTwoClient);
        return manager;
	}
}
