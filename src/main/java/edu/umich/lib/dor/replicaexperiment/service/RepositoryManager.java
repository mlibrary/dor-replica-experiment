package edu.umich.lib.dor.replicaexperiment.service;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

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
    RepositoryRepository repositoryRepo;
    ReplicaRepository replicaRepo;
    InfoPackageRepository infoPackageRepo;
    User user;
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

    @Override
    public String toString() {
        return "RepositoryManager[]";
    }

    public void setUser(User user) {
        this.user = user;
    }

    private Repository createRepository(String name) {
        var aRepository = new Repository(name, RepositoryType.FILE_SYSTEM);
        repositoryRepo.save(aRepository);
        return aRepository;
    }

    private InfoPackage createInfoPackage(String identifier) {
        var infoPackage = new InfoPackage(identifier);
        infoPackageRepo.save(infoPackage);
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

    public void registerRepositoryService(String name, RepositoryService service) {
        serviceMap.put(name, service);
        createRepository(name);
    }

    public Object[] listRepositoryServices(){
        return serviceMap.keySet().toArray();
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

    public void addPackageToRepository(
        String packageIdentifier, Path sourcePath, String repositoryName, String message
    ) {
        if (user == null) {
            throw new IllegalArgumentException("user must be set.");
        }
        var repository = repositoryRepo.findByName(repositoryName);
        createInfoPackage(packageIdentifier);
        var infoPackage = infoPackageRepo.findByIdentifier(packageIdentifier);

        var ocflRepoService = getRepositoryService(repositoryName);
        ocflRepoService.createObject(packageIdentifier, sourcePath, user, message);
        createReplica(infoPackage, repository);
    }

    public InfoPackage getInfoPackage(String identifier) {
        InfoPackage infoPackage = infoPackageRepo.findByIdentifier(identifier);
        return infoPackage;
    }

    public Repository getRepository(String name) {
        return repositoryRepo.findByName(name);
    }

    public List<Replica> getReplicas() {
        return replicaRepo.findAll();
    }
}
