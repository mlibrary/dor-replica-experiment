package edu.umich.lib.dor.replicaexperiment.service;

import java.nio.file.Path;

import edu.umich.lib.dor.replicaexperiment.domain.Curator;
import edu.umich.lib.dor.replicaexperiment.domain.Repository;
import edu.umich.lib.dor.replicaexperiment.exception.EntityAlreadyExistsException;
import edu.umich.lib.dor.replicaexperiment.exception.NoEntityException;

public class Deposit implements Command {
    InfoPackageService infoPackageService;
    RepositoryService repositoryService;
    ReplicaService replicaService;
    RepositoryClientRegistry repositoryClientRegistry;
    DepositDirectory depositDir;

    Curator curator;
    String packageIdentifier;
    Path sourcePath;
    String repositoryName;
    String message;

    Repository repository;
    RepositoryClient repositoryClient;
    Package sourcePackage;

    public Deposit(
        InfoPackageService infoPackageService,
        RepositoryService repositoryService,
        ReplicaService replicaService,
        RepositoryClientRegistry repositoryClientRegistry,
        DepositDirectory depositDir,
        Curator curator,
        String packageIdentifier,
        Path sourcePath,
        String repositoryName,
        String message
    ) {
        this.infoPackageService = infoPackageService;
        this.repositoryService = repositoryService;
        this.replicaService = replicaService;
        this.repositoryClientRegistry = repositoryClientRegistry;
        this.depositDir = depositDir;

        this.curator = curator;
        this.packageIdentifier = packageIdentifier;
        this.sourcePath = sourcePath;
        this.repositoryName = repositoryName;
        this.message = message;

        var existingPackage = infoPackageService.getInfoPackage(packageIdentifier);
        if (existingPackage != null) {
            throw new EntityAlreadyExistsException(
                String.format(
                    "A package with identifier \"%s\" already exists.",
                    packageIdentifier
                )
            );
        }

        this.repository = repositoryService.getRepository(repositoryName);
        if (repository == null) {
            throw new NoEntityException(
                String.format(
                    "No repository with name \"%s\" was found.",
                    repositoryName
                )
            );
        }

        this.repositoryClient = repositoryClientRegistry.getClient(repositoryName);
        this.sourcePackage = depositDir.getPackage(sourcePath);
    }

    public void execute() {
        repositoryClient.createObject(
            packageIdentifier, sourcePackage, curator, message
        );

        var infoPackage = infoPackageService.createInfoPackage(packageIdentifier);
        replicaService.createReplica(infoPackage, repository);
    }
}
