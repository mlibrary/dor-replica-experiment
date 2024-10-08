package edu.umich.lib.dor.replicaexperiment.service;

import java.nio.file.Path;

import edu.umich.lib.dor.replicaexperiment.domain.Curator;
import edu.umich.lib.dor.replicaexperiment.domain.InfoPackage;
import edu.umich.lib.dor.replicaexperiment.exception.NoEntityException;

public class Update implements Command {
    private InfoPackageService infoPackageService;
    private RepositoryClient repositoryClient;
    private Curator curator;
    private String packageIdentifier;
    private String message;

    private InfoPackage infoPackage;
    private Package sourcePackage;

    public Update(
        InfoPackageService infoPackageService,
        RepositoryClient repositoryClient,
        DepositDirectory depositDir,
        Curator curator,
        String packageIdentifier,
        Path sourcePath,
        String message
    ) {
        this.infoPackageService = infoPackageService;
        this.repositoryClient = repositoryClient;
        this.curator = curator;
        this.packageIdentifier = packageIdentifier;
        this.message = message;

        this.infoPackage = infoPackageService.getInfoPackage(packageIdentifier);
        if (infoPackage == null) {
            throw new NoEntityException(
                String.format(
                    "No package with identifier \"%s\" was found.",
                    packageIdentifier
                )
            );
        }

        this.sourcePackage = depositDir.getPackage(sourcePath);
    }

    public void execute() {
        repositoryClient.updateObjectFiles(
            packageIdentifier,
            sourcePackage,
            curator,
            message
        );
        infoPackageService.updateInfoPackage(infoPackage);
    }
}
