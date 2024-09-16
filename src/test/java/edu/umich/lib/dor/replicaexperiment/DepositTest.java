package edu.umich.lib.dor.replicaexperiment;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.umich.lib.dor.replicaexperiment.domain.Curator;
import edu.umich.lib.dor.replicaexperiment.domain.InfoPackage;
import edu.umich.lib.dor.replicaexperiment.domain.Replica;
import edu.umich.lib.dor.replicaexperiment.domain.Repository;
import edu.umich.lib.dor.replicaexperiment.exception.EntityAlreadyExistsException;
import edu.umich.lib.dor.replicaexperiment.exception.NoEntityException;
import edu.umich.lib.dor.replicaexperiment.service.DepositDirectory;
import edu.umich.lib.dor.replicaexperiment.service.DepositFactory;
import edu.umich.lib.dor.replicaexperiment.service.InfoPackageService;
import edu.umich.lib.dor.replicaexperiment.service.OcflFilesystemRepositoryClient;
import edu.umich.lib.dor.replicaexperiment.service.Package;
import edu.umich.lib.dor.replicaexperiment.service.ReplicaService;
import edu.umich.lib.dor.replicaexperiment.service.RepositoryClientRegistry;
import edu.umich.lib.dor.replicaexperiment.service.RepositoryService;

public class DepositTest {
    Curator testCurator = new Curator("test", "test@example.edu");
    InfoPackageService packageServiceMock;
    RepositoryService repositoryServiceMock;
    ReplicaService replicaServiceMock;
    RepositoryClientRegistry registryMock;
    Path depositPath;
    DepositDirectory depositDirMock;

    InfoPackage infoPackageMock;
    Repository repositoryMock;
    Replica replicaMock;
    OcflFilesystemRepositoryClient clientMock;
    Package sourcePackageMock;

    DepositFactory depositFactory;

    @BeforeEach
    void init() {
        this.packageServiceMock = mock(InfoPackageService.class);
        this.repositoryServiceMock = mock(RepositoryService.class);
        this.replicaServiceMock = mock(ReplicaService.class);
        this.registryMock = mock(RepositoryClientRegistry.class);
        this.depositDirMock = mock(DepositDirectory.class);
        this.sourcePackageMock = mock(Package.class);

        depositFactory = new DepositFactory(
            packageServiceMock,
            repositoryServiceMock,
            replicaServiceMock,
            registryMock,
            depositDirMock
        );

        this.infoPackageMock = mock(InfoPackage.class);
        this.repositoryMock = mock(Repository.class);
        this.clientMock = mock(OcflFilesystemRepositoryClient.class);
        this.replicaMock = mock(Replica.class);
    }

    @Test
    void depositCanBeCreated() {
        when(packageServiceMock.getInfoPackage("A")).thenReturn(null);
        when(repositoryServiceMock.getRepository("some_repo")).thenReturn(repositoryMock);
        when(registryMock.getClient("some_repo")).thenReturn(clientMock);
        when(depositDirMock.getPackage(Paths.get("something"))).thenReturn(sourcePackageMock);

        assertDoesNotThrow(() -> {
            depositFactory.create(
                testCurator,
                "A",
                Paths.get("something"),
                "some_repo",
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
                "some_repo",
                "did I already add this?"
            );
        });
    }

    @Test
    void depositFailsWhenRepositoryDoesNotExist() {
        when(packageServiceMock.getInfoPackage("A")).thenReturn(null);
        when(repositoryServiceMock.getRepository("some_repo")).thenReturn(null);

        assertThrows(NoEntityException.class, () -> {
            depositFactory.create(
                testCurator,
                "A",
                Paths.get("/something"),
                "some_repo",
                "was there a some_repo?"
            );
        });
    }

    @Test
    void depositExecutes() {
        when(packageServiceMock.getInfoPackage("A")).thenReturn(null);
        when(repositoryServiceMock.getRepository("some_repo")).thenReturn(repositoryMock);
        when(registryMock.getClient("some_repo")).thenReturn(clientMock);
        when(depositDirMock.getPackage(Paths.get("something"))).thenReturn(sourcePackageMock);

        final var deposit = depositFactory.create(
            testCurator,
            "A",
            Paths.get("something"),
            "some_repo",
            "we're good"
        );

        verify(sourcePackageMock).validatePath();

        when(depositDirMock.getPackage(Paths.get("something"))).thenReturn(sourcePackageMock);
        when(sourcePackageMock.getRootPath()).thenReturn(Paths.get("deposit/something"));
        when(packageServiceMock.createInfoPackage("A")).thenReturn(infoPackageMock);
        when(replicaServiceMock.createReplica(infoPackageMock, repositoryMock))
            .thenReturn(replicaMock);

        deposit.execute();

        verify(clientMock).createObject(
            "A", sourcePackageMock, testCurator, "we're good"
        );
        verify(packageServiceMock).createInfoPackage("A");
        verify(replicaServiceMock).createReplica(infoPackageMock, repositoryMock);
    }
}
