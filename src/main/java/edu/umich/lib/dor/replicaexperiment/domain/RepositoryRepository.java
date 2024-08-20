package edu.umich.lib.dor.replicaexperiment.domain;

import org.springframework.data.repository.ListCrudRepository;

@org.springframework.stereotype.Repository
public interface RepositoryRepository extends ListCrudRepository<Repository, Long> {
    Repository findByName(String name);
}
