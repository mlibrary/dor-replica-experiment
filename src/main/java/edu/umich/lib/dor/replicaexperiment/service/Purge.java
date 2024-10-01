package edu.umich.lib.dor.replicaexperiment.service;

import edu.umich.lib.dor.replicaexperiment.domain.InfoPackage;
import edu.umich.lib.dor.replicaexperiment.domain.Replica;
import edu.umich.lib.dor.replicaexperiment.domain.Repository;
import edu.umich.lib.dor.replicaexperiment.exception.NoEntityException;

public class Purge implements Command {
    private ReplicaService replicaService;
    private String packageIdentifier;

    private Replica replica;
    private RepositoryClient repositoryClient;

    public Purge(
        InfoPackageService packageService,
        RepositoryService repositoryService,
        ReplicaService replicaService,
        RepositoryClientRegistry clientRegistry,
        String packageIdentifier,
        String repositoryName
    ) {
        this.replicaService = replicaService;
        this.packageIdentifier = packageIdentifier;

        InfoPackage infoPackage = packageService.getInfoPackage(packageIdentifier);
        if (infoPackage == null) {
            throw new NoEntityException(
                String.format(
                    "No packaged with identifier \"%s\" was found.",
                    packageIdentifier
                )
            );
        }

        Repository repository = repositoryService.getRepository(repositoryName);
        if (repository == null) {
            throw new NoEntityException(
                String.format(
                    "No repository with name \"%s\" was found.",
                    repositoryName
                )
            );
        }
        this.repositoryClient = clientRegistry.getClient(repositoryName);

        this.replica = this.replicaService.getReplica(infoPackage, repository);
        if (replica == null) {
            throw new NoEntityException(
                String.format(
                    "No replica for package \"%s\" was found in repository \"%s\".",
                    packageIdentifier,
                    repositoryName
                )
            );
        }
    }

    public void execute() {
        repositoryClient.purgeObject(packageIdentifier);
        replicaService.deleteReplica(replica);
    }
}
