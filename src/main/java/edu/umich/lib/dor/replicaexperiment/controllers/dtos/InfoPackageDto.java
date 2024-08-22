package edu.umich.lib.dor.replicaexperiment.controllers.dtos;

import java.util.List;

import edu.umich.lib.dor.replicaexperiment.domain.InfoPackage;

public class InfoPackageDto {
    private Long id;
    private String identifier;
    private List<NestedReplicaDto> replicas;

    public InfoPackageDto(InfoPackage infoPackage) {
        this.id = infoPackage.getId();
        this.identifier = infoPackage.getIdentifier();
        this.replicas = infoPackage
            .getReplicas()
            .stream()
            .map(replica -> { return new NestedReplicaDto(replica); })
            .toList();
    }

    public Long getId() {
        return id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public List<NestedReplicaDto> getReplicas() {
        return replicas;
    }
};
