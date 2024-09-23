package edu.umich.lib.dor.replicaexperiment.service;

public class PurgeFactory {
    private InfoPackageService infoPackageService;
    private RepositoryService repositoryService;
    private ReplicaService replicaservice;
    private RepositoryClientRegistry repositoryClientRegistry;

    public PurgeFactory(
        InfoPackageService infoPackageService,
        RepositoryService repositoryService,
        ReplicaService replicaservice,
        RepositoryClientRegistry repositoryClientRegistry
    ) {
        this.infoPackageService = infoPackageService;
        this.repositoryService = repositoryService;
        this.replicaservice = replicaservice;
        this.repositoryClientRegistry = repositoryClientRegistry;
    }

    public Purge create(
        String packageIdentifier,
        String repositoryName
    ) {
        return new Purge(
            infoPackageService,
            repositoryService,
            replicaservice,
            repositoryClientRegistry,
            packageIdentifier,
            repositoryName
        );
    }
}
