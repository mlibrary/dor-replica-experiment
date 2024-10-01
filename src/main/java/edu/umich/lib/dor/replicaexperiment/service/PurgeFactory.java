package edu.umich.lib.dor.replicaexperiment.service;

public class PurgeFactory {
    private InfoPackageService infoPackageService;
    private RepositoryService repositoryService;
    private ReplicaService replicaService;
    private RepositoryClientRegistry repositoryClientRegistry;

    public PurgeFactory(
        InfoPackageService infoPackageService,
        RepositoryService repositoryService,
        ReplicaService replicaService,
        RepositoryClientRegistry repositoryClientRegistry
    ) {
        this.infoPackageService = infoPackageService;
        this.repositoryService = repositoryService;
        this.replicaService = replicaService;
        this.repositoryClientRegistry = repositoryClientRegistry;
    }

    public Purge create(
        String packageIdentifier,
        String repositoryName
    ) {
        return new Purge(
            infoPackageService,
            repositoryService,
            replicaService,
            repositoryClientRegistry,
            packageIdentifier,
            repositoryName
        );
    }
}
