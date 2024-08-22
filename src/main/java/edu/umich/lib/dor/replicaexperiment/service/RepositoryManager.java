package edu.umich.lib.dor.replicaexperiment.service;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.umich.lib.dor.replicaexperiment.domain.InfoPackage;
import edu.umich.lib.dor.replicaexperiment.domain.InfoPackageRepository;
import edu.umich.lib.dor.replicaexperiment.domain.Replica;
import edu.umich.lib.dor.replicaexperiment.domain.ReplicaRepository;
import edu.umich.lib.dor.replicaexperiment.domain.Repository;
import edu.umich.lib.dor.replicaexperiment.domain.RepositoryRepository;
import edu.umich.lib.dor.replicaexperiment.domain.RepositoryType;
import edu.umich.lib.dor.replicaexperiment.domain.User;

@Service
public class RepositoryManager {
	private static final Log log = LogFactory.getLog(RepositoryManager.class);

    RepositoryRepository repositoryRepo;
    ReplicaRepository replicaRepo;
    InfoPackageRepository infoPackageRepo;
    User user;
    Path depositPath;
    Path stagingPath;
    HashMap<String, RepositoryService> serviceMap = new HashMap<>();

    @Autowired
    public RepositoryManager(
        RepositoryRepository repositoryRepo,
        InfoPackageRepository infoPackageRepo,
        ReplicaRepository replicaRepo
    ) {
        this.repositoryRepo = repositoryRepo;
        this.infoPackageRepo = infoPackageRepo;
        this.replicaRepo = replicaRepo;
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

    private Repository createRepositoryIfNotExists(String name) {
        Repository repository = repositoryRepo.findByName(name);
        if (repository != null) {
            return repository;
        }
        log.debug(
            String.format("Creating new repository record in database with name \"%s\"", name)
        );
        var newRepository = new Repository(name, RepositoryType.FILE_SYSTEM);
        repositoryRepo.save(newRepository);
        return newRepository;
    }

    public Repository getRepository(String name) {
        return repositoryRepo.findByName(name);
    }

    private InfoPackage createInfoPackage(String identifier) {
        var infoPackage = new InfoPackage(identifier);
        infoPackageRepo.save(infoPackage);
        return infoPackage;
    }

    public InfoPackage getInfoPackage(String identifier) {
        InfoPackage infoPackage = infoPackageRepo.findByIdentifier(identifier);
        return infoPackage;
    }

    private void createReplica(InfoPackage infoPackage, Repository repository) {
        var replica = new Replica();
        replica.setInfoPackage(infoPackage);
        replica.setRepository(repository);
        replicaRepo.save(replica);
        infoPackage.addReplica(replica);
        infoPackageRepo.save(infoPackage);
        repository.addReplica(replica);
        repositoryRepo.save(repository);
    }

    public List<Replica> getReplicas() {
        return replicaRepo.findAll();
    }

    public void registerRepositoryService(String name, RepositoryService service) {
        serviceMap.put(name, service);
        createRepositoryIfNotExists(name);
    }

    public List<String> listRepositoryServices(){
        return serviceMap.keySet()
            .stream()
            .toList();
    }

    private RepositoryService getRepositoryService(String name) {
        RepositoryService service = serviceMap.get(name);
        if (service == null) {
            throw new IllegalArgumentException(
                String.format("\"%s\" is not a registered repository.", name)
            );
        }
        return service;
    }

    @Override
    public String toString() {
        List<String> repoServices = listRepositoryServices();
        return String.format(
            "RepositoryManager[repoServices=[%s], " +
                "user=%s, " +
                "depositPath=%s" +
                "stagingPath=%s",
            String.join(", ", repoServices),
            user == null ? "null" : user.toString(),
            depositPath == null ? "null" : depositPath.toString(),
            stagingPath == null ? "null" : stagingPath.toString()
        );
    }

    public void addPackageToRepository(
        String packageIdentifier, Path sourcePath, String repositoryName, String message
    ) {
        Path fullSourcePath = getDepositPath().resolve(sourcePath);
        var repository = repositoryRepo.findByName(repositoryName);
        createInfoPackage(packageIdentifier);
        var infoPackage = infoPackageRepo.findByIdentifier(packageIdentifier);

        var ocflRepoService = getRepositoryService(repositoryName);
        ocflRepoService.createObject(packageIdentifier, fullSourcePath, getUser(), message);
        createReplica(infoPackage, repository);
    }

    public void replicatePackageToAnotherRepository(
        String packageIdentifier, String sourceRepoName, String targetRepoName
    ) {
        Path stagingPath = getStagingPath();
        Path objectPathInStaging = stagingPath.resolve(packageIdentifier);
        RepositoryService sourceRepoService = getRepositoryService(sourceRepoName);
        RepositoryService targetRepoService = getRepositoryService(targetRepoName);

        sourceRepoService.exportObject(packageIdentifier, objectPathInStaging);
        targetRepoService.importObject(objectPathInStaging);
        InfoPackage infoPackage = infoPackageRepo.findByIdentifier(packageIdentifier);
        Repository repository = repositoryRepo.findByName(targetRepoName);
        createReplica(infoPackage, repository);
    };
}
