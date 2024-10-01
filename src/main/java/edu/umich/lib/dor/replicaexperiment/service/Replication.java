package edu.umich.lib.dor.replicaexperiment.service;

import java.nio.file.Path;

import edu.umich.lib.dor.replicaexperiment.domain.InfoPackage;
import edu.umich.lib.dor.replicaexperiment.domain.Replica;
import edu.umich.lib.dor.replicaexperiment.domain.Repository;
import edu.umich.lib.dor.replicaexperiment.exception.NoEntityException;

public class Replication implements Command {
    InfoPackageService infoPackageService;
    RepositoryService repositoryService;
    ReplicaService replicaService;
    RepositoryClientRegistry repositoryClientRegistry;
    Path stagingPath;
    String packageIdentifier;

    InfoPackage infoPackage;
    Repository sourceRepository;
    RepositoryClient sourceRepositoryClient;
    Repository targetRepository;
    RepositoryClient targetRepositoryClient;

    public Replication(
        InfoPackageService infoPackageService,
        RepositoryService repositoryService,
        ReplicaService replicaService,
        RepositoryClientRegistry repositoryClientRegistry,
        Path stagingPath,
        String packageIdentifier,
        String sourceRepositoryName,
        String targetRepositoryName
    ) {
        this.infoPackageService = infoPackageService;
        this.repositoryService = repositoryService;
        this.replicaService = replicaService;
        this.repositoryClientRegistry = repositoryClientRegistry;
        this.stagingPath = stagingPath;

        this.packageIdentifier = packageIdentifier;
        this.infoPackage = infoPackageService.getInfoPackage(packageIdentifier);
        if (infoPackage == null) {
            throw new NoEntityException(
                String.format(
                    "No packaged with identifier \"%s\" was found.",
                    packageIdentifier
                )
            );
        }

        this.sourceRepository = repositoryService.getRepository(sourceRepositoryName);
        if (sourceRepository == null) {
            throw new NoEntityException(
                String.format(
                    "No repository with name \"%s\" was found.",
                    targetRepositoryName
                )
            );
        }
        this.sourceRepositoryClient = repositoryClientRegistry.getClient(sourceRepositoryName);
        Replica sourceReplica = replicaService.getReplica(infoPackage, sourceRepository);
        if (sourceReplica == null) {
            throw new NoEntityException(
                String.format(
                    "No replica for package \"%s\" was found in repository \"%s\".",
                    packageIdentifier,
                    sourceRepositoryName
                )
            );
        }

        this.targetRepository = repositoryService.getRepository(targetRepositoryName);
        if (targetRepository == null) {
            throw new NoEntityException(
                String.format(
                    "No repository with name \"%s\" was found.",
                    targetRepositoryName
                )
            );
        }
        this.targetRepositoryClient = repositoryClientRegistry.getClient(targetRepositoryName);
    }

    public void execute() {
        Path objectPathInStaging = stagingPath.resolve(packageIdentifier);
        sourceRepositoryClient.exportObject(packageIdentifier, objectPathInStaging);
        targetRepositoryClient.importObject(objectPathInStaging);

        replicaService.createReplica(infoPackage, targetRepository);
    }
}
