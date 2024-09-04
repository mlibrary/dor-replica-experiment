package edu.umich.lib.dor.replicaexperiment.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.umich.lib.dor.replicaexperiment.domain.Repository;
import edu.umich.lib.dor.replicaexperiment.domain.RepositoryRepository;
import edu.umich.lib.dor.replicaexperiment.domain.RepositoryType;

@Service
public class RepositoryService {
	private static final Logger log = LoggerFactory.getLogger(RepositoryService.class);

    @Autowired
    RepositoryRepository repositoryRepo;

    public Repository getOrCreateRepository(String name) {
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
}
