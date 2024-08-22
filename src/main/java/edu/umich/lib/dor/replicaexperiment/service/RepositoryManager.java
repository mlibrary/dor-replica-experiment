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

public class RepositoryManager {
	private static final Log log = LogFactory.getLog(RepositoryManager.class);

    RepositoryService repositoryService;
    InfoPackageService infoPackageService;
    ReplicaService replicaService;

    User user;
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

    public void setUser(User user) {
        this.user = user;
    }

    private User getUser() {
        if (user == null) {
            throw new IllegalArgumentException("user must be set.");
        }
        return user;
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
            throw new IllegalArgumentException(
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
                "user=%s, " +
                "depositPath=%s, " +
                "stagingPath=%s" +
                "]"
            ),
            String.join(", ", repoClientNames),
            user == null ? "null" : user.toString(),
            depositPath == null ? "null" : depositPath.toString(),
            stagingPath == null ? "null" : stagingPath.toString()
        );
    }

    public void addPackageToRepository(
        String packageIdentifier, Path sourcePath, String repositoryName, String message
    ) {
        Path fullSourcePath = getDepositPath().resolve(sourcePath);
        var repository = repositoryService.getRepository(repositoryName);
        infoPackageService.createInfoPackage(packageIdentifier);
        var infoPackage = infoPackageService.getInfoPackage(packageIdentifier);

        var ocflRepoClient = getRepositoryClient(repositoryName);
        ocflRepoClient.createObject(packageIdentifier, fullSourcePath, getUser(), message);
        Replica replica = replicaService.createReplica(infoPackage, repository);
        infoPackage.addReplica(replica);
        repository.addReplica(replica);
    }

    public void replicatePackageToAnotherRepository(
        String packageIdentifier, String sourceRepoName, String targetRepoName
    ) {
        Path stagingPath = getStagingPath();
        Path objectPathInStaging = stagingPath.resolve(packageIdentifier);
        RepositoryClient sourceRepoClient = getRepositoryClient(sourceRepoName);
        RepositoryClient targetRepoClient = getRepositoryClient(targetRepoName);

        sourceRepoClient.exportObject(packageIdentifier, objectPathInStaging);
        targetRepoClient.importObject(objectPathInStaging);
        InfoPackage infoPackage = infoPackageService.getInfoPackage(packageIdentifier);
        Repository repository = repositoryService.getRepository(targetRepoName);
        Replica replica = replicaService.createReplica(infoPackage, repository);
        infoPackage.addReplica(replica);
        repository.addReplica(replica);
    };
}
