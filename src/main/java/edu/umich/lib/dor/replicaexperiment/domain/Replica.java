package edu.umich.lib.dor.replicaexperiment.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

@Entity
public class Replica {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @ManyToOne(optional=false)
    private InfoPackage infoPackage;

    @ManyToOne(optional=false)
    private Repository repository;

    @CreationTimestamp()
    private Instant createdAt;

    public Replica() {}
    
    public void setInfoPackage(InfoPackage infoPackage) {
        this.infoPackage = infoPackage;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public Repository getRepository() {
        return repository;
    }

    public InfoPackage getInfoPackage() {
        return infoPackage;
    }

    @Override
    public String toString() {
        return String.format(
            "Replica[id=%d, createdAt=%s, infoPackage=%s, repository=%s]",
            id,
            createdAt.toString(),
            infoPackage.toString(),
            repository.toString()
        );
    }

    public Long getId() {
        return id;
    }
}
