package edu.umich.lib.dor.replicaexperiment;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan("edu.umich.lib.dor.replicaexperiment.service")
@EnableTransactionManagement
@EnableJpaRepositories(basePackages="edu.umich.lib.dor.replicaexperiment.domain")
public class TestsConfig {
}
