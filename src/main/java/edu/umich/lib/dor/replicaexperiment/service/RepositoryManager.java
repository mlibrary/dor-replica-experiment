package edu.umich.lib.dor.replicaexperiment.service;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.umich.lib.dor.replicaexperiment.domain.InfoPackage;
import edu.umich.lib.dor.replicaexperiment.domain.Replica;
import edu.umich.lib.dor.replicaexperiment.domain.Repository;
import edu.umich.lib.dor.replicaexperiment.domain.User;
import edu.umich.lib.dor.replicaexperiment.exception.EntityAlreadyExistsException;
import edu.umich.lib.dor.replicaexperiment.exception.NoEntityException;
import edu.umich.lib.dor.replicaexperiment.exception.RepositoryNotRegisteredException;

public class RepositoryManager {
	private static final Log log = LogFactory.getLog(RepositoryManager.class);

    RepositoryService repositoryService;
    InfoPackageService infoPackageService;
    ReplicaService replicaService;

    Path depositPath;
    Path stagingPath;
    HashMap<String, RepositoryClient> clientMap = new HashMap<>();

    public RepositoryManager(
        RepositoryService repositoryService,
        InfoPackageService infoPackageService,
        ReplicaService replicaService
    ) {
        this.repositoryService = repositoryService;
        this.infoPackageService = infoPackageService;
        this.replicaService = replicaService;
    }

    public void setStagingPath(Path stagingPath) {
        this.stagingPath = stagingPath;
    }

    private Path getStagingPath() {
        if (stagingPath == null) {
            throw new IllegalArgumentException("stagingPath must be set.");
        }
        return stagingPath;
    }

    public void setDepositPath(Path depositPath) {
        this.depositPath = depositPath;
    }

    private Path getDepositPath() {
        if (depositPath == null) {
            throw new IllegalArgumentException("depositPath must be set.");
        }
        return depositPath;
    }

    public void registerRepository(String name, RepositoryClient client) {
        clientMap.put(name, client);
        repositoryService.getOrCreateRepository(name);
    }

    public List<String> listRepositories(){
        return clientMap.keySet()
            .stream()
            .toList();
    }

    private RepositoryClient getRepositoryClient(String name) {
        RepositoryClient client = clientMap.get(name);
        if (client == null) {
            throw new RepositoryNotRegisteredException(
                String.format("\"%s\" is not a registered repository.", name)
            );
        }
        return client;
    }

    @Override
    public String toString() {
        List<String> repoClientNames = listRepositories();
        return String.format(
            (
                "RepositoryManager[" +
                "repositories=[%s], " +
                "depositPath=%s, " +
                "stagingPath=%s" +
                "]"
            ),
            String.join(", ", repoClientNames),
            depositPath == null ? "null" : depositPath.toString(),
            stagingPath == null ? "null" : stagingPath.toString()
        );
    }

    public void addPackageToRepository(
        User user,
        String packageIdentifier,
        Path sourcePath,
        String repositoryName,
        String message
    ) {
        var existingPackage = infoPackageService.getInfoPackage(packageIdentifier);
        if (existingPackage != null) {
            throw new EntityAlreadyExistsException(
                String.format(
                    "A package with identifier \"%s\" already exists",
                    packageIdentifier
                )
            );
        }

        var repository = repositoryService.getRepository(repositoryName);
        if (repository == null) {
            throw new NoEntityException(
                String.format(
                    "No repository with name \"%s\" was found.",
                    repositoryName
                )
            );
        }

        Path fullSourcePath = getDepositPath().resolve(sourcePath);
        var ocflRepoClient = getRepositoryClient(repositoryName);
        ocflRepoClient.createObject(packageIdentifier, fullSourcePath, user, message);

        var infoPackage = infoPackageService.createInfoPackage(packageIdentifier);
        Replica replica = replicaService.createReplica(infoPackage, repository);
        infoPackage.addReplica(replica);
        repository.addReplica(replica);
    }

    public void replicatePackageToAnotherRepository(
        User user, String packageIdentifier, String sourceRepoName, String targetRepoName
    ) {
        Path stagingPath = getStagingPath();
        Path objectPathInStaging = stagingPath.resolve(packageIdentifier);

        RepositoryClient sourceRepoClient = getRepositoryClient(sourceRepoName);
        RepositoryClient targetRepoClient = getRepositoryClient(targetRepoName);
        InfoPackage infoPackage = infoPackageService.getInfoPackage(packageIdentifier);
        if (infoPackage == null) {
            throw new NoEntityException(
                String.format(
                    "No packaged with identifier \"%s\" was found.",
                    packageIdentifier
                )
            );
        }

        sourceRepoClient.exportObject(packageIdentifier, objectPathInStaging);
        targetRepoClient.importObject(objectPathInStaging);
        Repository repository = repositoryService.getRepository(targetRepoName);
        Replica replica = replicaService.createReplica(infoPackage, repository);
        infoPackage.addReplica(replica);
        repository.addReplica(replica);
    };
}
