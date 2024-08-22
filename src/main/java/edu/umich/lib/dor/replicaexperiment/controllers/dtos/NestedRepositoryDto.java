package edu.umich.lib.dor.replicaexperiment.controllers.dtos;

import edu.umich.lib.dor.replicaexperiment.domain.Repository;

public class NestedRepositoryDto {
    private Long id;
    private String name;
    private String type;

    public NestedRepositoryDto(Repository repository) {
        this.id = repository.getId();
        this.name = repository.getName();
        this.type = repository.getType();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
