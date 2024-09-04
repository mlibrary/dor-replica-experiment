package edu.umich.lib.dor.replicaexperiment.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.umich.lib.dor.replicaexperiment.domain.InfoPackage;
import edu.umich.lib.dor.replicaexperiment.domain.Replica;
import edu.umich.lib.dor.replicaexperiment.domain.ReplicaRepository;
import edu.umich.lib.dor.replicaexperiment.domain.Repository;

@Service
public class ReplicaService {

    @Autowired
    ReplicaRepository replicaRepo;

    public Replica createReplica(InfoPackage infoPackage, Repository repository) {
        var replica = new Replica();
        replica.setInfoPackage(infoPackage);
        replica.setRepository(repository);
        infoPackage.addReplica(replica);
        repository.addReplica(replica);
        replicaRepo.save(replica);
        return replica;
    }

    public List<Replica> getReplicas() {
        return replicaRepo.findAll();
    }
}
