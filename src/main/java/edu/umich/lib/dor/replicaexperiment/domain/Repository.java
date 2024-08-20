package edu.umich.lib.dor.replicaexperiment.domain;

import java.util.Set;
import java.util.HashSet;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import org.hibernate.annotations.NaturalId;

@Entity
public class Repository {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @NaturalId
    private String name;

    @Enumerated(EnumType.STRING)
    private RepositoryType type;

    @OneToMany(mappedBy="repository", fetch=FetchType.EAGER)
    Set<Replica> replicas = new HashSet<>();

    public void addReplica(Replica replica) {
        replicas.add(replica);
    }

    protected Repository() {}

    public Repository(String name, RepositoryType type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String toString() {
        return String.format(
            "Repository[id=%d, name='%s', type='%s']",
            id,
            name,
            type.toString()
        );
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type.toString();
    }

    public Set<Replica> getReplicas() {
        return replicas;
    }
}
