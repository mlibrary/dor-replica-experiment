package edu.umich.lib.dor.replicaexperiment.domain;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InfoPackageRepository extends ListCrudRepository<InfoPackage, Long> {
    InfoPackage findByIdentifier(String identifier);
}