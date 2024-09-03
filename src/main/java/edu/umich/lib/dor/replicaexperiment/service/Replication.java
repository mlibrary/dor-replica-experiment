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
    Repository sourceRepository;
    RepositoryClient sourceRepositoryClient;
    String targetRepositoryName;
    Repository targetRepository;
    RepositoryClient targetRepositoryClient;

    InfoPackage infoPackage;

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
        if (!infoPackage.hasAReplicaIn(sourceRepositoryName)) {
            throw new NoEntityException(
                String.format(
                    "No replica for package \"%s\" was found in repository \"%s\".",
                    packageIdentifier,
                    sourceRepositoryName
                )
            );
        }

        this.targetRepositoryName = targetRepositoryName;
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
        targetRepository = repositoryService.getRepository(targetRepositoryName);
        Replica replica = replicaService.createReplica(infoPackage, targetRepository);
        infoPackage.addReplica(replica);
        targetRepository.addReplica(replica);
    }
}
