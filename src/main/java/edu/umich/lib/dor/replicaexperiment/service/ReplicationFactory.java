package edu.umich.lib.dor.replicaexperiment.service;

import java.nio.file.Path;

public class ReplicationFactory {
    InfoPackageService infoPackageService;
    RepositoryService repositoryService;
    ReplicaService replicaservice;
    RepositoryClientRegistry repositoryClientRegistry;
    Path stagingPath;

    public ReplicationFactory(
        InfoPackageService infoPackageService,
        RepositoryService repositoryService,
        ReplicaService replicaservice,
        RepositoryClientRegistry repositoryClientRegistry,
        Path stagingPath
    ) {
        this.infoPackageService = infoPackageService;
        this.repositoryService = repositoryService;
        this.replicaservice = replicaservice;
        this.repositoryClientRegistry = repositoryClientRegistry;
        this.stagingPath = stagingPath;
    }

    public Replication create(
        String packageIdentifier,
        String sourceRepositoryName,
        String targetRepositoryName
    ) {
        return new Replication(
            infoPackageService,
            repositoryService,
            replicaservice,
            repositoryClientRegistry,
            stagingPath,
            packageIdentifier,
            sourceRepositoryName,
            targetRepositoryName
        );
    }
}
