package edu.umich.lib.dor.replicaexperiment;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import edu.umich.lib.dor.replicaexperiment.service.RepositoryManager;

@SpringBootApplication
public class ReplicaExperimentApplication implements CommandLineRunner {
	private static final Log log = LogFactory.getLog(ReplicaExperimentApplication.class);

	@Autowired
	RepositoryManager repositoryManager;

	public static void main(String[] args) {
		SpringApplication.run(ReplicaExperimentApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		log.debug(repositoryManager.listRepositoryServices());
		log.debug(repositoryManager);
	}
}
