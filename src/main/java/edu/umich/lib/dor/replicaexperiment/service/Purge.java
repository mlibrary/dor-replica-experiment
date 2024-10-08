package edu.umich.lib.dor.replicaexperiment.service;

import edu.umich.lib.dor.replicaexperiment.domain.InfoPackage;
import edu.umich.lib.dor.replicaexperiment.exception.NoEntityException;

public class Purge implements Command {
    private InfoPackageService infoPackageService;
    private RepositoryClient repositoryClient;
    private String packageIdentifier;

    private InfoPackage infoPackage;

    public Purge(
        InfoPackageService infoPackageService,
        RepositoryClient repositoryClient,
        String packageIdentifier
    ) {
        this.infoPackageService = infoPackageService;
        this.repositoryClient = repositoryClient;
        this.packageIdentifier = packageIdentifier;

        this.infoPackage = infoPackageService.getInfoPackage(packageIdentifier);
        if (infoPackage == null) {
            throw new NoEntityException(
                String.format(
                    "No package with identifier \"%s\" was found.",
                    packageIdentifier
                )
            );
        }
    }

    public void execute() {
        repositoryClient.purgeObject(packageIdentifier);
        infoPackageService.deleteInfoPackage(infoPackage);
    }
}
