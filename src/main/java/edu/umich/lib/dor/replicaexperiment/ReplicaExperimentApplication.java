package edu.umich.lib.dor.replicaexperiment;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import edu.umich.lib.dor.replicaexperiment.service.RepositoryManager;

@Configuration
@ComponentScan("edu.umich.lib.dor.replicaexperiment.service")
@EnableAutoConfiguration
@EntityScan("edu.umich.lib.dor.replicaexperiment.domain")
public class ReplicaExperimentApplication implements CommandLineRunner {

	private static final Log log = LogFactory.getLog(ReplicaExperimentApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ReplicaExperimentApplication.class, args);
	}

	@Autowired
	RepositoryManager repositoryManager;

	@Override
	public void run(String... args) throws Exception {
		log.info("TO DO");
	}
}
