package edu.umich.lib.dor.replicaexperiment.service;

import java.nio.file.Path;

import edu.umich.lib.dor.replicaexperiment.domain.Curator;

public class UpdateFactory {
    InfoPackageService infoPackageService;
    RepositoryService repositoryService;
    ReplicaService replicaService;
    RepositoryClientRegistry repositoryClientRegistry;
    DepositDirectory depositDir;

    public UpdateFactory(
        InfoPackageService infoPackageService,
        RepositoryService repositoryService,
        ReplicaService replicaService,
        RepositoryClientRegistry repositoryClientRegistry,
        DepositDirectory depositDir
    ) {
        this.infoPackageService = infoPackageService;
        this.repositoryService = repositoryService;
        this.replicaService = replicaService;
        this.repositoryClientRegistry = repositoryClientRegistry;
        this.depositDir = depositDir;
    }

    public Update create(
        Curator curator,
        String packageIdentifier,
        Path sourcePath,
        String repositoryName,
        String message
    ) {
        return new Update(
            infoPackageService,
            repositoryService,
            replicaService,
            repositoryClientRegistry,
            depositDir,
            curator,
            packageIdentifier,
            sourcePath,
            repositoryName,
            message
        );
    }
}
