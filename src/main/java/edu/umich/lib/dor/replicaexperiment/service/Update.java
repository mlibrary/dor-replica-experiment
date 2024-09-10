package edu.umich.lib.dor.replicaexperiment.service;

import java.util.List;
import java.nio.file.Path;

import edu.umich.lib.dor.replicaexperiment.domain.Repository;
import edu.umich.lib.dor.replicaexperiment.domain.Curator;
import edu.umich.lib.dor.replicaexperiment.domain.InfoPackage;
import edu.umich.lib.dor.replicaexperiment.exception.NoEntityException;

public class Update implements Command {
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

    InfoPackage existingPackage;
    Repository repository;
    RepositoryClient repositoryClient;
    Path updatePackagePath;
    List<Path> updateFilePaths;

    public Update(
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

        this.existingPackage = infoPackageService.getInfoPackage(packageIdentifier);
        if (existingPackage == null) {
            throw new NoEntityException(
                String.format(
                    "No package with identifier \"%s\" was found.",
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
        if (!existingPackage.hasAReplicaIn(repositoryName)) {
            throw new NoEntityException(
                String.format(
                    "No replica for package \"%s\" was found in repository \"%s\".",
                    packageIdentifier,
                    repositoryName
                )
            );
        }

        this.updateFilePaths = depositDir.getPackageFilePaths(sourcePath);
        this.updatePackagePath = depositDir.getDepositPath().resolve(sourcePath);
    }

    public void execute() {
        repositoryClient.updateObjectFiles(
            packageIdentifier, updatePackagePath, updateFilePaths, curator, message
        );
        replicaService.updateReplica(existingPackage, repository);
    }
}
