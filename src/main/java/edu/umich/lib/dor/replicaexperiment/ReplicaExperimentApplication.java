package edu.umich.lib.dor.replicaexperiment;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ReplicaExperimentApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ReplicaExperimentApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
	}
}
