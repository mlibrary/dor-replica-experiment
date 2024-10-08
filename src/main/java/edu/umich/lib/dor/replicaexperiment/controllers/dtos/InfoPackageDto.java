package edu.umich.lib.dor.replicaexperiment.controllers.dtos;

import java.time.Instant;

import edu.umich.lib.dor.replicaexperiment.domain.InfoPackage;

public class InfoPackageDto {
    private Long id;
    private String identifier;
    private Instant createdAt;
    private Instant updatedAt;

    public InfoPackageDto(InfoPackage infoPackage) {
        this.id = infoPackage.getId();
        this.identifier = infoPackage.getIdentifier();
        this.createdAt = infoPackage.getCreatedAt();
        this.updatedAt = infoPackage.getUpdatedAt();
    }

    public Long getId() {
        return id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
