package edu.umich.lib.dor.replicaexperiment;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.umich.lib.dor.replicaexperiment.domain.InfoPackage;
import edu.umich.lib.dor.replicaexperiment.domain.Replica;
import edu.umich.lib.dor.replicaexperiment.domain.Repository;
import edu.umich.lib.dor.replicaexperiment.exception.NoEntityException;
import edu.umich.lib.dor.replicaexperiment.service.InfoPackageService;
import edu.umich.lib.dor.replicaexperiment.service.Purge;
import edu.umich.lib.dor.replicaexperiment.service.PurgeFactory;
import edu.umich.lib.dor.replicaexperiment.service.ReplicaService;
import edu.umich.lib.dor.replicaexperiment.service.RepositoryClient;
import edu.umich.lib.dor.replicaexperiment.service.RepositoryClientRegistry;
import edu.umich.lib.dor.replicaexperiment.service.RepositoryService;

public class PurgeTest {
    InfoPackageService packageServiceMock;
    RepositoryService repositoryServiceMock;
    ReplicaService replicaServiceMock;
    RepositoryClientRegistry registryMock;

    InfoPackage infoPackageMock;
    RepositoryClient clientMock;
    Replica replicaMock;
    Repository repositoryMock;

    PurgeFactory purgeFactory;

    @BeforeEach
    void init() {
        this.packageServiceMock = mock(InfoPackageService.class);
        this.repositoryServiceMock = mock(RepositoryService.class);
        this.replicaServiceMock = mock(ReplicaService.class);
        this.registryMock = mock(RepositoryClientRegistry.class);

        this.infoPackageMock = mock(InfoPackage.class);
        this.clientMock = mock(RepositoryClient.class);
        this.replicaMock = mock(Replica.class);
        this.repositoryMock = mock(Repository.class);

        this.purgeFactory = new PurgeFactory(
            packageServiceMock,
            repositoryServiceMock,
            replicaServiceMock,
            registryMock
        );
    }

    @Test
    void factoryCreatesPurge() {
        when(packageServiceMock.getInfoPackage("A")).thenReturn(infoPackageMock);
        when(repositoryServiceMock.getRepository("some_repo")).thenReturn(repositoryMock);
        when(registryMock.getClient("some_repo")).thenReturn(clientMock);
        when(replicaServiceMock.getReplica(infoPackageMock, repositoryMock))
            .thenReturn(replicaMock);

        purgeFactory.create("A", "some_repo");
    }

    @Test
    void purgeFailsWhenInfoPackageDoesNotExist() {
        when(packageServiceMock.getInfoPackage("A")).thenReturn(null);
    
        assertThrows(NoEntityException.class, () -> {
            purgeFactory.create("A", "some_repo");
        });
    }

    @Test
    void purgeFailsWhenSourceRepositoryDoesNotExist() {
        when(packageServiceMock.getInfoPackage("A")).thenReturn(infoPackageMock);
        when(repositoryServiceMock.getRepository("some_imaginary_repo")).thenReturn(null);

        assertThrows(NoEntityException.class, () -> {
            purgeFactory.create("A", "some_imaginary_repo");
        });
    }

    @Test
    void purgeFailsWhenInfoPackageDoesNotHaveReplicaInRepository() {
        Repository sourceRepositoryMock = mock(Repository.class);

        when(packageServiceMock.getInfoPackage("A")).thenReturn(infoPackageMock);
        when(repositoryServiceMock.getRepository("some_repo")).thenReturn(sourceRepositoryMock);
        when(registryMock.getClient("some_repo")).thenReturn(clientMock);
        when(infoPackageMock.hasAReplicaIn("some_repo")).thenReturn(false);

        assertThrows(NoEntityException.class, () -> {
            purgeFactory.create("A", "some_repo");
        });
    }

    @Test
    void purgeExecutes() {
        when(packageServiceMock.getInfoPackage("A")).thenReturn(infoPackageMock);
        when(repositoryServiceMock.getRepository("some_repo")).thenReturn(repositoryMock);
        when(registryMock.getClient("some_repo")).thenReturn(clientMock);
        when(replicaServiceMock.getReplica(infoPackageMock, repositoryMock))
            .thenReturn(replicaMock);

        Purge purge = purgeFactory.create("A", "some_repo");

        purge.execute();
        verify(clientMock).purgeObject("A");
        verify(replicaServiceMock).deleteReplica(replicaMock);
    }
}
