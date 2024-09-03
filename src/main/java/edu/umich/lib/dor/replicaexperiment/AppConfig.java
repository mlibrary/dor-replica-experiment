package edu.umich.lib.dor.replicaexperiment;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import edu.umich.lib.dor.replicaexperiment.service.DepositFactory;
import edu.umich.lib.dor.replicaexperiment.service.InfoPackageService;
import edu.umich.lib.dor.replicaexperiment.service.OcflFilesystemRepositoryClient;
import edu.umich.lib.dor.replicaexperiment.service.ReplicaService;
import edu.umich.lib.dor.replicaexperiment.service.ReplicationFactory;
import edu.umich.lib.dor.replicaexperiment.service.RepositoryClient;
import edu.umich.lib.dor.replicaexperiment.service.RepositoryClientRegistry;
import edu.umich.lib.dor.replicaexperiment.service.RepositoryService;

@Configuration
@ComponentScan("edu.umich.lib.dor.replicaexperiment.service")
@EnableTransactionManagement
@EnableJpaRepositories(basePackages="edu.umich.lib.dor.replicaexperiment.domain")
public class AppConfig {
    @Autowired
    RepositoryService repositoryService;

    @Autowired
    InfoPackageService infoPackageService;

    @Autowired
    ReplicaService replicaService;

    @Bean
    RepositoryClientRegistry repositoryClientRegistry(
        RepositoryService repositoryService,
        Environment environment
    ) {
        Path repoOnePath = Paths.get(
            environment.getRequiredProperty("repository.repo_one.path")
        );
        Path repoTwoPath = Paths.get(
            environment.getRequiredProperty("repository.repo_two.path")
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

        RepositoryClientRegistry repositoryClientRegistry = new RepositoryClientRegistry();
        repositoryClientRegistry.register(repoOneName, repoOneClient);
        repositoryClientRegistry.register(repoTwoName, repoTwoClient);
        for (String repositoryName: repositoryClientRegistry.listClients()) {
            this.repositoryService.getOrCreateRepository(repositoryName);
        }
        return repositoryClientRegistry;
    }

    @Bean
    public DepositFactory depositFactory(
        RepositoryClientRegistry repositoryClientRegistry,
        RepositoryService repositoryService,
        InfoPackageService infoPackageService,
        ReplicaService replicaService,
        Environment environment
    ) {
        Path depositPath = Paths.get(
            environment.getRequiredProperty("repository.deposit.path")
        );
        return new DepositFactory(
            infoPackageService,
            repositoryService,
            replicaService,
            repositoryClientRegistry,
            depositPath
        );
    }

    @Bean
    public ReplicationFactory replicationFactory(
        RepositoryClientRegistry repositoryClientRegistry,
        RepositoryService repositoryService,
        InfoPackageService infoPackageService,
        ReplicaService replicaService,
        Environment environment
    ) {
        Path stagingPath = Paths.get(
            environment.getRequiredProperty("repository.staging.path")
        );
        return new ReplicationFactory(
            infoPackageService,
            repositoryService,
            replicaService,
            repositoryClientRegistry,
            stagingPath
        );
    }
}
