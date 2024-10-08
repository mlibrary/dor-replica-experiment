package edu.umich.lib.dor.replicaexperiment;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.umich.lib.dor.replicaexperiment.domain.InfoPackage;
import edu.umich.lib.dor.replicaexperiment.exception.NoEntityException;
import edu.umich.lib.dor.replicaexperiment.service.InfoPackageService;
import edu.umich.lib.dor.replicaexperiment.service.Purge;
import edu.umich.lib.dor.replicaexperiment.service.PurgeFactory;
import edu.umich.lib.dor.replicaexperiment.service.RepositoryClient;

public class PurgeTest {
    InfoPackageService packageServiceMock;
    RepositoryClient repositoryClientMock;

    InfoPackage infoPackageMock;

    PurgeFactory purgeFactory;

    @BeforeEach
    void init() {
        this.packageServiceMock = mock(InfoPackageService.class);
        this.repositoryClientMock = mock(RepositoryClient.class);

        this.infoPackageMock = mock(InfoPackage.class);

        this.purgeFactory = new PurgeFactory(
            packageServiceMock,
            repositoryClientMock
        );
    }

    @Test
    void factoryCreatesPurge() {
        when(packageServiceMock.getInfoPackage("A")).thenReturn(infoPackageMock);

        purgeFactory.create("A");
    }

    @Test
    void purgeFailsWhenInfoPackageDoesNotExist() {
        when(packageServiceMock.getInfoPackage("?")).thenReturn(null);
    
        assertThrows(NoEntityException.class, () -> {
            purgeFactory.create("?");
        });
    }

    @Test
    void purgeExecutes() {
        when(packageServiceMock.getInfoPackage("A")).thenReturn(infoPackageMock);

        Purge purge = purgeFactory.create("A");

        purge.execute();
        verify(repositoryClientMock).purgeObject("A");
        verify(packageServiceMock).deleteInfoPackage(infoPackageMock);
    }
}
