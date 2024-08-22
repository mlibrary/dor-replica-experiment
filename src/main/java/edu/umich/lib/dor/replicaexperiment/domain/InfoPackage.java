package edu.umich.lib.dor.replicaexperiment.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import org.hibernate.annotations.NaturalId;

@Entity
public class InfoPackage {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @NaturalId
    private String identifier;

    @OneToMany(mappedBy="infoPackage", fetch=FetchType.LAZY)
    private Set<Replica> replicas = new HashSet<>();

    protected InfoPackage() {}

    public InfoPackage(String identifier) {
        this.identifier = identifier;
    }

    public Long getId() {
        return id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Set<Replica> getReplicas() {
        return replicas;
    }

    public void addReplica(Replica replica) {
        replicas.add(replica);
    }

    public int getNumReplicas() {
        return replicas.size();
    }

    public boolean hasAReplicaIn(String repositoryName) {
        List<String> repositoryNames = replicas.stream()
            .map(replica -> { return replica.getRepository().getName(); })
            .toList();
        return repositoryNames.contains(repositoryName);
    }

    @Override
    public String toString() {
        return String.format(
            "InfoPackage[id=%d, identifier='%s', numReplicas=%d]",
            id,
            identifier,
            getNumReplicas()
        );
    }
}
