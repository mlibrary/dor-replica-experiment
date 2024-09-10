package edu.umich.lib.dor.replicaexperiment.domain;

import java.util.List;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReplicaRepository extends ListCrudRepository<Replica, Long> {
    List<Replica> findAllByInfoPackage(InfoPackage infoPackage);
}
