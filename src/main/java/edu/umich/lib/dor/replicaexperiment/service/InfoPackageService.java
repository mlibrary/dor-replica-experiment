package edu.umich.lib.dor.replicaexperiment.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.umich.lib.dor.replicaexperiment.domain.InfoPackage;
import edu.umich.lib.dor.replicaexperiment.domain.InfoPackageRepository;
import edu.umich.lib.dor.replicaexperiment.domain.Replica;

@Service
public class InfoPackageService {

    @Autowired
    InfoPackageRepository infoPackageRepo;

    public InfoPackage createInfoPackage(String identifier) {
        var infoPackage = new InfoPackage(identifier);
        infoPackageRepo.save(infoPackage);
        return infoPackage;
    }

    public InfoPackage getInfoPackage(String identifier) {
        InfoPackage infoPackage = infoPackageRepo.findByIdentifier(identifier);
        return infoPackage;
    }

    public List<InfoPackage> getAllInfoPackages() {
        return infoPackageRepo.findAll();
    }

    public void addReplicaToInfoPackage(InfoPackage infoPackage, Replica replica) {
        infoPackage.addReplica(replica);
        infoPackageRepo.save(infoPackage);
    }
}
