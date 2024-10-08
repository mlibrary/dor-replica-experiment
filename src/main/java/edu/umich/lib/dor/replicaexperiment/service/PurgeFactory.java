package edu.umich.lib.dor.replicaexperiment.service;

public class PurgeFactory {
    private InfoPackageService infoPackageService;
    private RepositoryClient repositoryClient;

    public PurgeFactory(
        InfoPackageService infoPackageService,
        RepositoryClient repositoryClient
    ) {
        this.infoPackageService = infoPackageService;
        this.repositoryClient = repositoryClient;
    }

    public Purge create(String packageIdentifier) {
        return new Purge(
            infoPackageService, repositoryClient, packageIdentifier
        );
    }
}
