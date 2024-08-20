package edu.umich.lib.dor.replicaexperiment;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import edu.umich.lib.dor.replicaexperiment.domain.InfoPackageRepository;
import edu.umich.lib.dor.replicaexperiment.domain.ReplicaRepository;
import edu.umich.lib.dor.replicaexperiment.domain.RepositoryRepository;
import edu.umich.lib.dor.replicaexperiment.service.RepositoryManager;

@Configuration
@ComponentScan("edu.umich.lib.dor.replicaexperiment.service")
@EnableTransactionManagement
@EnableJpaRepositories(basePackages="edu.umich.lib.dor.replicaexperiment.domain")
public class AppConfig {

    @Bean
	public RepositoryManager repositoryManager(
		RepositoryRepository repositoryRepository,
		InfoPackageRepository infoPackageRepository,
		ReplicaRepository replicaRepository
	) {
		return new RepositoryManager(
			repositoryRepository,
			infoPackageRepository,
			replicaRepository
		);
	}
}
