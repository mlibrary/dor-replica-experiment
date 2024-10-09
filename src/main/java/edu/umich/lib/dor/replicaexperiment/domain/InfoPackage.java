package edu.umich.lib.dor.replicaexperiment.domain;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.NaturalId;

@Entity
public class InfoPackage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NaturalId
    private String identifier;

    @CreationTimestamp()
    private Instant createdAt;

    private Instant updatedAt;

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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        // TO DO - add new fields to string formatting
        return String.format(
            "InfoPackage[id=%d, identifier='%s']",
            id,
            identifier
        );
    }
}
