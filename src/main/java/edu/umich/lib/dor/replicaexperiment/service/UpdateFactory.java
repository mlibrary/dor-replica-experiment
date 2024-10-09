package edu.umich.lib.dor.replicaexperiment.service;

import java.nio.file.Path;

import edu.umich.lib.dor.replicaexperiment.domain.Curator;

public class UpdateFactory {
    InfoPackageService infoPackageService;
    RepositoryClient repositoryClient;
    DepositDirectory depositDir;

    public UpdateFactory(
        InfoPackageService infoPackageService,
        RepositoryClient repositoryClient,
        DepositDirectory depositDir
    ) {
        this.infoPackageService = infoPackageService;
        this.repositoryClient = repositoryClient;
        this.depositDir = depositDir;
    }

    public Update create(
        Curator curator,
        String packageIdentifier,
        Path sourcePath,
        String message
    ) {
        return new Update(
            infoPackageService,
            repositoryClient,
            depositDir,
            curator,
            packageIdentifier,
            sourcePath,
            message
        );
    }
}
