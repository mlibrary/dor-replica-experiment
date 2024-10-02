package edu.umich.lib.dor.replicaexperiment.service;

import java.nio.file.Path;

import edu.umich.lib.dor.replicaexperiment.domain.Curator;
import edu.umich.lib.dor.replicaexperiment.domain.InfoPackage;
import edu.umich.lib.dor.replicaexperiment.domain.Repository;
import edu.umich.lib.dor.replicaexperiment.exception.EntityAlreadyExistsException;
import edu.umich.lib.dor.replicaexperiment.exception.NoEntityException;

public class Deposit implements Command {
    private InfoPackageService infoPackageService;
    private ReplicaService replicaService;

    private Curator curator;
    private String packageIdentifier;
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
        this.replicaService = replicaService;

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

        InfoPackage infoPackage = infoPackageService.createInfoPackage(packageIdentifier);
        replicaService.createReplica(infoPackage, repository);
    }
}
