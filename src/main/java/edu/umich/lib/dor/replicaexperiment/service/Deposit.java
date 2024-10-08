package edu.umich.lib.dor.replicaexperiment.service;

import java.nio.file.Path;

import edu.umich.lib.dor.replicaexperiment.domain.Curator;
import edu.umich.lib.dor.replicaexperiment.domain.InfoPackage;
import edu.umich.lib.dor.replicaexperiment.exception.EntityAlreadyExistsException;

public class Deposit implements Command {
    private InfoPackageService infoPackageService;
    private RepositoryClient repositoryClient;

    private Curator curator;
    private String packageIdentifier;
    String message;

    Package sourcePackage;

    public Deposit(
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

        InfoPackage existingPackage = infoPackageService.getInfoPackage(packageIdentifier);
        if (existingPackage != null) {
            throw new EntityAlreadyExistsException(
                String.format(
                    "A package with identifier \"%s\" already exists.",
                    packageIdentifier
                )
            );
        }

        this.sourcePackage = depositDir.getPackage(sourcePath);
    }

    public void execute() {
        repositoryClient.createObject(
            packageIdentifier, sourcePackage, curator, message
        );

        infoPackageService.createInfoPackage(packageIdentifier);
    }
}
