package edu.umich.lib.dor.replicaexperiment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import edu.umich.lib.dor.replicaexperiment.service.InfoPackageService;
import edu.umich.lib.dor.replicaexperiment.service.ReplicaService;
import edu.umich.lib.dor.replicaexperiment.service.RepositoryManager;
import edu.umich.lib.dor.replicaexperiment.service.RepositoryService;

@Configuration
@ComponentScan("edu.umich.lib.dor.replicaexperiment.service")
@EnableTransactionManagement
@EnableJpaRepositories(basePackages="edu.umich.lib.dor.replicaexperiment.domain")
public class TestsConfig {

	@Autowired
	RepositoryService repositoryService;

	@Autowired
	InfoPackageService infoPackageService;

	@Autowired
	ReplicaService replicaService;

    @Bean
	public RepositoryManager repositoryManager() {
		return new RepositoryManager(
			repositoryService,
			infoPackageService,
			replicaService
		);
	}
}
