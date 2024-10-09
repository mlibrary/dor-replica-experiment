package edu.umich.lib.dor.replicaexperiment.service;

import java.nio.file.Path;

import edu.umich.lib.dor.replicaexperiment.domain.Curator;

public class DepositFactory {
    InfoPackageService infoPackageService;
    RepositoryClient repositoryClient;
    Path depositPath;
    DepositDirectory depositDir;

    public DepositFactory(
        InfoPackageService infoPackageService,
        RepositoryClient repositoryClient,
        DepositDirectory depositDir
    ) {
        this.infoPackageService = infoPackageService;
        this.repositoryClient = repositoryClient;
        this.depositDir = depositDir;
    }

    public Deposit create(
        Curator curator,
        String packageIdentifier,
        Path sourcePath,
        String message
    ) {
        return new Deposit(
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
