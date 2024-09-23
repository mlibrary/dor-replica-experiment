package edu.umich.lib.dor.replicaexperiment.service;

import java.time.Instant;
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
        replica.setUpdatedAt(Instant.now());
        infoPackage.addReplica(replica);
        repository.addReplica(replica);
        replicaRepo.save(replica);
        return replica;
    }

    public List<Replica> getReplicas() {
        return replicaRepo.findAll();
    }

    public Replica getReplica(
        InfoPackage infoPackage, Repository repository
    ) {
        var matchingReplicas = replicaRepo.findAllByInfoPackage(infoPackage)
            .stream()
            .filter(r -> r.getRepository().getId() == repository.getId())
            .toList();
        if (matchingReplicas.size() == 0) {
            return null;
        }
        return matchingReplicas.getFirst();
    }

    public Replica updateReplica(
        InfoPackage infoPackage, Repository repository
    ) {
        var replica = getReplica(infoPackage, repository);
        replica.setUpdatedAt(Instant.now());
        replicaRepo.save(replica);
        return replica;
    }

    public void deleteReplica(Replica replica) {
        replicaRepo.delete(replica);
    }
}
