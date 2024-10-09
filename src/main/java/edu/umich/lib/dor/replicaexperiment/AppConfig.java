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

import edu.umich.lib.dor.replicaexperiment.service.DepositDirectory;
import edu.umich.lib.dor.replicaexperiment.service.DepositFactory;
import edu.umich.lib.dor.replicaexperiment.service.InfoPackageService;
import edu.umich.lib.dor.replicaexperiment.service.OcflFilesystemRepositoryClient;
import edu.umich.lib.dor.replicaexperiment.service.PurgeFactory;
import edu.umich.lib.dor.replicaexperiment.service.RepositoryClient;
import edu.umich.lib.dor.replicaexperiment.service.UpdateFactory;

@Configuration
@ComponentScan("edu.umich.lib.dor.replicaexperiment.service")
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "edu.umich.lib.dor.replicaexperiment.domain")
public class AppConfig {

    @Autowired
    InfoPackageService infoPackageService;

    @Bean
    RepositoryClient repositoryClient(
        Environment environment
    ) {
        Path repoOnePath = Paths.get(
            environment.getRequiredProperty("repository.repo_one.path")
        );
        Path repoOneStoragePath = repoOnePath.resolve("storage");
        Path repoOneWorkspacePath = repoOnePath.resolve("workspace");
        RepositoryClient repoOneClient = new OcflFilesystemRepositoryClient(
            repoOneStoragePath, repoOneWorkspacePath
        );
        return repoOneClient;
    }

    @Bean
    public DepositFactory depositFactory(
        RepositoryClient repositoryClient,
        InfoPackageService infoPackageService,
        Environment environment
    ) {
        Path depositPath = Paths.get(
            environment.getRequiredProperty("repository.deposit.path")
        );
        return new DepositFactory(
            infoPackageService,
            repositoryClient,
            new DepositDirectory(depositPath)
        );
    }

    @Bean
    public UpdateFactory updateFactory(
        RepositoryClient repositoryClient,
        InfoPackageService infoPackageService,
        Environment environment
    ) {
        Path depositPath = Paths.get(
            environment.getRequiredProperty("repository.deposit.path")
        );
        return new UpdateFactory(
            infoPackageService,
            repositoryClient,
            new DepositDirectory(depositPath)
        );
    }

    @Bean
    public PurgeFactory purgeFactory(
        RepositoryClient repositoryClient,
        InfoPackageService infoPackageService
    ) {
        return new PurgeFactory(
            infoPackageService,
            repositoryClient
        );
    }
}
