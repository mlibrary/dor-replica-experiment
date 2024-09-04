package edu.umich.lib.dor.replicaexperiment.service;

import java.nio.file.Path;

import edu.umich.lib.dor.replicaexperiment.domain.Curator;

public class DepositFactory {
    InfoPackageService infoPackageService;
    RepositoryService repositoryService;
    ReplicaService replicaService;
    RepositoryClientRegistry repositoryClientRegistry;
    Path depositPath;

    public DepositFactory(
        InfoPackageService infoPackageService,
        RepositoryService repositoryService,
        ReplicaService replicaService,
        RepositoryClientRegistry repositoryClientRegistry,
        Path depositPath
    ) {
        this.infoPackageService = infoPackageService;
        this.repositoryService = repositoryService;
        this.replicaService = replicaService;
        this.repositoryClientRegistry = repositoryClientRegistry;
        this.depositPath = depositPath;
    }

    public Deposit create(
        Curator curator,
        String packageIdentifier,
        Path sourcePath,
        String repositoryName,
        String message
    ) {
        return new Deposit(
            infoPackageService,
            repositoryService,
            replicaService,
            repositoryClientRegistry,
            depositPath,
            curator,
            packageIdentifier,
            sourcePath,
            repositoryName,
            message
        );
    }
}
