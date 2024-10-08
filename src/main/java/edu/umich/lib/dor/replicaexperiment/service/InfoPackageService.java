package edu.umich.lib.dor.replicaexperiment.service;

import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.umich.lib.dor.replicaexperiment.domain.InfoPackage;
import edu.umich.lib.dor.replicaexperiment.domain.InfoPackageRepository;

@Service
public class InfoPackageService {

    @Autowired
    InfoPackageRepository infoPackageRepo;

    public InfoPackage createInfoPackage(String identifier) {
        var infoPackage = new InfoPackage(identifier);
        infoPackage.setUpdatedAt(Instant.now());
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

    public InfoPackage updateInfoPackage(InfoPackage infoPackage) {
        infoPackage.setUpdatedAt(Instant.now());
        infoPackageRepo.save(infoPackage);
        return infoPackage;
    }

    public void deleteInfoPackage(InfoPackage infoPackage) {
        infoPackageRepo.delete(infoPackage);
    }
}
