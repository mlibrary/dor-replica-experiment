package edu.umich.lib.dor.replicaexperiment;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.umich.lib.dor.replicaexperiment.domain.Curator;
import edu.umich.lib.dor.replicaexperiment.domain.InfoPackage;
import edu.umich.lib.dor.replicaexperiment.exception.EntityAlreadyExistsException;
import edu.umich.lib.dor.replicaexperiment.service.Deposit;
import edu.umich.lib.dor.replicaexperiment.service.DepositDirectory;
import edu.umich.lib.dor.replicaexperiment.service.DepositFactory;
import edu.umich.lib.dor.replicaexperiment.service.InfoPackageService;
import edu.umich.lib.dor.replicaexperiment.service.Package;
import edu.umich.lib.dor.replicaexperiment.service.RepositoryClient;

public class DepositTest {
    Curator testCurator = new Curator("test", "test@example.edu");

    InfoPackageService packageServiceMock;
    RepositoryClient repositoryClientMock;
    DepositDirectory depositDirMock;

    DepositFactory depositFactory;

    InfoPackage infoPackageMock;
    Package sourcePackageMock;

    @BeforeEach
    void init() {
        this.packageServiceMock = mock(InfoPackageService.class);
        this.repositoryClientMock = mock(RepositoryClient.class);
        this.depositDirMock = mock(DepositDirectory.class);

        this.depositFactory = new DepositFactory(
            packageServiceMock,
            repositoryClientMock,
            depositDirMock
        );

        this.infoPackageMock = mock(InfoPackage.class);
        this.sourcePackageMock = mock(Package.class);
    }

    @Test
    void depositCanBeCreated() {
        when(packageServiceMock.getInfoPackage("A")).thenReturn(null);
        when(depositDirMock.getPackage(Paths.get("something"))).thenReturn(sourcePackageMock);

        assertDoesNotThrow(() -> {
            depositFactory.create(
                testCurator,
                "A",
                Paths.get("something"),
                "we're good"
            );
        });
    }

    @Test
    void depositFailsWhenPackageAlreadyExists() {
        when(packageServiceMock.getInfoPackage("A")).thenReturn(infoPackageMock);

        assertThrows(EntityAlreadyExistsException.class, () -> {
            depositFactory.create(
                testCurator,
                "A",
                Paths.get("/something"),
                "did I already add this?"
            );
        });
    }

    @Test
    void depositExecutes() {
        when(packageServiceMock.getInfoPackage("A")).thenReturn(null);
        when(depositDirMock.getPackage(Paths.get("something"))).thenReturn(sourcePackageMock);

        final Deposit deposit = depositFactory.create(
            testCurator,
            "A",
            Paths.get("something"),
            "we're good"
        );

        deposit.execute();
        verify(repositoryClientMock).createObject(
            "A", sourcePackageMock, testCurator, "we're good"
        );
        verify(packageServiceMock).createInfoPackage("A");
    }
}
