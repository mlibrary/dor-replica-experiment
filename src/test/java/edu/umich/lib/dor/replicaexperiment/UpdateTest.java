package edu.umich.lib.dor.replicaexperiment;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.umich.lib.dor.replicaexperiment.domain.InfoPackage;
import edu.umich.lib.dor.replicaexperiment.domain.Curator;
import edu.umich.lib.dor.replicaexperiment.exception.NoEntityException;
import edu.umich.lib.dor.replicaexperiment.service.DepositDirectory;
import edu.umich.lib.dor.replicaexperiment.service.InfoPackageService;
import edu.umich.lib.dor.replicaexperiment.service.Package;
import edu.umich.lib.dor.replicaexperiment.service.RepositoryClient;
import edu.umich.lib.dor.replicaexperiment.service.Update;
import edu.umich.lib.dor.replicaexperiment.service.UpdateFactory;

public class UpdateTest {
    Curator testCurator = new Curator("test", "test@example.edu");

    InfoPackageService packageServiceMock;
    RepositoryClient repositoryClientMock;
    DepositDirectory depositDirMock;

    UpdateFactory updateFactory;

    InfoPackage infoPackageMock;
    Package sourcePackageMock;

    @BeforeEach
    void init() {
        this.packageServiceMock = mock(InfoPackageService.class);
        this.repositoryClientMock = mock(RepositoryClient.class);
        this.depositDirMock = mock(DepositDirectory.class);

        this.updateFactory = new UpdateFactory(
            packageServiceMock,
            repositoryClientMock,
            depositDirMock
        );

        this.infoPackageMock = mock(InfoPackage.class);
        this.sourcePackageMock = mock(Package.class);
    }

    @Test
    void updateCanBeCreated() {
        when(packageServiceMock.getInfoPackage("A")).thenReturn(infoPackageMock);

        when(depositDirMock.getPackage(Paths.get("update_A"))).thenReturn(sourcePackageMock);
    
        assertDoesNotThrow(() -> {
            updateFactory.create(
                testCurator,
                "A",
                Paths.get("update_A"),
                "we're good"
            );
        });
    }

    @Test
    void updateFailsWhenInfoPackageDoesNotExist() {
        when(packageServiceMock.getInfoPackage("A")).thenReturn(null);

        assertThrows(NoEntityException.class, () -> {
            updateFactory.create(
                testCurator,
                "A",
                Paths.get("update_A"),
                "did I not add this yet?"
            );
        });
    }

    @Test
    void updateExecutes() {
        when(packageServiceMock.getInfoPackage("A")).thenReturn(infoPackageMock);

        when(depositDirMock.getPackage(Paths.get("update_A"))).thenReturn(sourcePackageMock);

        final Update update = updateFactory.create(
            testCurator,
            "A",
            Paths.get("update_A"),
            "we're good"
        );

        update.execute();
        verify(repositoryClientMock).updateObjectFiles(
            "A",
            sourcePackageMock,
            testCurator,
            "we're good"
        );
        verify(packageServiceMock).updateInfoPackage(infoPackageMock);
    }
}
