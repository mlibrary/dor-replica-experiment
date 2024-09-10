package edu.umich.lib.dor.replicaexperiment.controllers.dtos;

import java.time.Instant;

import edu.umich.lib.dor.replicaexperiment.domain.Replica;

public class NestedReplicaDto {
    private Long id;
    private Instant createdAt;
    private Instant updatedAt;
    private NestedRepositoryDto repository;

    public NestedReplicaDto(Replica replica) {
        this.id = replica.getId();
        this.createdAt = replica.getCreatedAt();
        this.updatedAt = replica.getUpdatedAt();
        this.repository = new NestedRepositoryDto(replica.getRepository());
    }

    public Long getId() {
        return id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public NestedRepositoryDto getRepository() {
        return repository;
    }

}
