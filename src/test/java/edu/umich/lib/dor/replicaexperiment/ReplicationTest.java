package edu.umich.lib.dor.replicaexperiment;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.umich.lib.dor.replicaexperiment.domain.InfoPackage;
import edu.umich.lib.dor.replicaexperiment.domain.Replica;
import edu.umich.lib.dor.replicaexperiment.domain.Repository;
import edu.umich.lib.dor.replicaexperiment.exception.NoEntityException;
import edu.umich.lib.dor.replicaexperiment.service.InfoPackageService;
import edu.umich.lib.dor.replicaexperiment.service.OcflFilesystemRepositoryClient;
import edu.umich.lib.dor.replicaexperiment.service.ReplicaService;
import edu.umich.lib.dor.replicaexperiment.service.Replication;
import edu.umich.lib.dor.replicaexperiment.service.ReplicationFactory;
import edu.umich.lib.dor.replicaexperiment.service.RepositoryClientRegistry;
import edu.umich.lib.dor.replicaexperiment.service.RepositoryService;

public class ReplicationTest {
    InfoPackageService packageServiceMock;
    RepositoryService repositoryServiceMock;
    ReplicaService replicaServiceMock;
    RepositoryClientRegistry registryMock;
    InfoPackage infoPackageMock;
    Path stagingPath;

    OcflFilesystemRepositoryClient sourceClientMock;
    OcflFilesystemRepositoryClient targetClientMock;
    Replica replicaMock;
    Repository sourceRepositoryMock;
    Repository targetRepositoryMock;

    ReplicationFactory replicationFactory;

    @BeforeEach
    void init() {
        this.packageServiceMock = mock(InfoPackageService.class);
        this.repositoryServiceMock = mock(RepositoryService.class);
        this.replicaServiceMock = mock(ReplicaService.class);
        this.registryMock = mock(RepositoryClientRegistry.class);
        this.infoPackageMock = mock(InfoPackage.class);
        this.stagingPath = Paths.get("staging");

        this.replicationFactory = new ReplicationFactory(
            packageServiceMock,
            repositoryServiceMock,
            replicaServiceMock,
            registryMock,
            stagingPath
        );

        this.sourceClientMock = mock(OcflFilesystemRepositoryClient.class);
        this.targetClientMock = mock(OcflFilesystemRepositoryClient.class);
        this.replicaMock = mock(Replica.class);
        this.sourceRepositoryMock = mock(Repository.class);
        this.targetRepositoryMock = mock(Repository.class);
    }

    @Test
    void replicationCanBeCreated() {
        Repository sourceRepositoryMock = mock(Repository.class);
        Repository targetRepositoryMock = mock(Repository.class);

        when(packageServiceMock.getInfoPackage("A")).thenReturn(infoPackageMock);
        when(repositoryServiceMock.getRepository("some_repo")).thenReturn(sourceRepositoryMock);
        when(registryMock.getClient("some_repo")).thenReturn(sourceClientMock);
        when(infoPackageMock.hasAReplicaIn("some_repo")).thenReturn(true);
        when(repositoryServiceMock.getRepository("some_other_repo")).thenReturn(targetRepositoryMock);
        when(registryMock.getClient("some_other_repo")).thenReturn(targetClientMock);

        assertDoesNotThrow(() -> {
            replicationFactory.create(
                "A",
                "some_repo",
                "some_other_repo"
            );
        });
    }

    @Test
    void replicationSpecifiesInfoPackageThatDoesNotExist() {
        when(packageServiceMock.getInfoPackage("A")).thenReturn(null);
    
        assertThrows(NoEntityException.class, () -> {
            replicationFactory.create(
                "A",
                "some_repo",
                "some_other_repo"
            );
        });
    }

    @Test
    void replicationSpecifiesSourceRepositoryThatDoesNotExist() {
        when(packageServiceMock.getInfoPackage("A")).thenReturn(infoPackageMock);
        when(repositoryServiceMock.getRepository("some_repo")).thenReturn(null);

        assertThrows(NoEntityException.class, () -> {
            replicationFactory.create(
                "A",
                "some_repo",
                "some_other_repo"
            );
        });
    }

    @Test
    void replicationSpecifiesInfoPackageWithoutReplicaInSourceRepository() {
        Repository sourceRepositoryMock = mock(Repository.class);

        when(packageServiceMock.getInfoPackage("A")).thenReturn(infoPackageMock);
        when(repositoryServiceMock.getRepository("some_repo")).thenReturn(sourceRepositoryMock);
        when(registryMock.getClient("some_repo")).thenReturn(sourceClientMock);
        when(infoPackageMock.hasAReplicaIn("some_repo")).thenReturn(false);

        assertThrows(NoEntityException.class, () -> {
            replicationFactory.create(
                "A",
                "some_repo",
                "some_other_repo"
            );
        });
    }

    @Test
    void replicationSpecifiesTargetRepositoryThatDoesNotExist() {
        when(packageServiceMock.getInfoPackage("A")).thenReturn(infoPackageMock);
        when(repositoryServiceMock.getRepository("some_repo")).thenReturn(sourceRepositoryMock);
        when(registryMock.getClient("some_repo")).thenReturn(sourceClientMock);
        when(infoPackageMock.hasAReplicaIn("some_repo")).thenReturn(true);
        when(repositoryServiceMock.getRepository("some_other_repo")).thenReturn(null);

        assertThrows(NoEntityException.class, () -> {
            replicationFactory.create(
                "A",
                "some_repo",
                "some_other_repo"
            );
        });
    }

    @Test
    void replicationExecutes() {
        Path objectPathInStaging = stagingPath.resolve("A");

        when(packageServiceMock.getInfoPackage("A")).thenReturn(infoPackageMock);
        when(infoPackageMock.hasAReplicaIn("some_repo")).thenReturn(true);
        when(repositoryServiceMock.getRepository("some_repo")).thenReturn(sourceRepositoryMock);
        when(registryMock.getClient("some_repo")).thenReturn(sourceClientMock);
        when(repositoryServiceMock.getRepository("some_other_repo")).thenReturn(targetRepositoryMock);
        when(registryMock.getClient("some_other_repo")).thenReturn(targetClientMock);

        Replication replication = replicationFactory.create(
            "A",
            "some_repo",
            "some_other_repo"
        );

        when(replicaServiceMock.createReplica(infoPackageMock, targetRepositoryMock))
            .thenReturn(replicaMock);

        replication.execute();

        verify(sourceClientMock).exportObject("A", objectPathInStaging);
        verify(targetClientMock).importObject(objectPathInStaging);
        verify(replicaServiceMock).createReplica(infoPackageMock, targetRepositoryMock);
    }
}
