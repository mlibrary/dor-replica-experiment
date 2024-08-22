package edu.umich.lib.dor.replicaexperiment.controllers.dtos;

import java.time.Instant;

import edu.umich.lib.dor.replicaexperiment.domain.Replica;

public class NestedReplicaDto {
    private Long id;
    private Instant createdAt;
    private NestedRepositoryDto repository;

    public NestedReplicaDto(Replica replica) {
        this.id = replica.getId();
        this.createdAt = replica.getCreatedAt();
        this.repository = new NestedRepositoryDto(replica.getRepository());
    }

    public Long getId() {
        return id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public NestedRepositoryDto getRepository() {
        return repository;
    }

}
