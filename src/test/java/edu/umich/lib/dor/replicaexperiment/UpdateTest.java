package edu.umich.lib.dor.replicaexperiment;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.umich.lib.dor.replicaexperiment.domain.InfoPackage;
import edu.umich.lib.dor.replicaexperiment.domain.Replica;
import edu.umich.lib.dor.replicaexperiment.domain.Repository;
import edu.umich.lib.dor.replicaexperiment.domain.Curator;
import edu.umich.lib.dor.replicaexperiment.exception.NoEntityException;
import edu.umich.lib.dor.replicaexperiment.service.DepositDirectory;
import edu.umich.lib.dor.replicaexperiment.service.InfoPackageService;
import edu.umich.lib.dor.replicaexperiment.service.OcflFilesystemRepositoryClient;
import edu.umich.lib.dor.replicaexperiment.service.Package;
import edu.umich.lib.dor.replicaexperiment.service.ReplicaService;
import edu.umich.lib.dor.replicaexperiment.service.RepositoryClientRegistry;
import edu.umich.lib.dor.replicaexperiment.service.RepositoryService;
import edu.umich.lib.dor.replicaexperiment.service.Update;
import edu.umich.lib.dor.replicaexperiment.service.UpdateFactory;

public class UpdateTest {
    Curator testCurator = new Curator("test", "test@example.edu");

    InfoPackageService packageServiceMock;
    RepositoryService repositoryServiceMock;
    ReplicaService replicaServiceMock;
    RepositoryClientRegistry registryMock;

    InfoPackage infoPackageMock;
    Repository repositoryMock;
    Replica replicaMock;
    OcflFilesystemRepositoryClient clientMock;
    DepositDirectory depositDirMock;
    Package sourcePackageMock;

    UpdateFactory updateFactory;

    @BeforeEach
    void init() {
        this.packageServiceMock = mock(InfoPackageService.class);
        this.repositoryServiceMock = mock(RepositoryService.class);
        this.replicaServiceMock = mock(ReplicaService.class);
        this.registryMock = mock(RepositoryClientRegistry.class);
        
        this.infoPackageMock = mock(InfoPackage.class);
        this.repositoryMock = mock(Repository.class);
        this.replicaMock = mock(Replica.class);
        this.clientMock = mock(OcflFilesystemRepositoryClient.class);

        this.depositDirMock = mock(DepositDirectory.class);
        this.sourcePackageMock = mock(Package.class);

        this.updateFactory = new UpdateFactory(
            packageServiceMock,
            repositoryServiceMock,
            replicaServiceMock,
            registryMock,
            depositDirMock
        );
    }

    @Test
    void updateCanBeCreated() {
        when(packageServiceMock.getInfoPackage("A")).thenReturn(infoPackageMock);
        when(repositoryServiceMock.getRepository("some_repo")).thenReturn(repositoryMock);
        when(infoPackageMock.hasAReplicaIn("some_repo")).thenReturn(true);

        when(registryMock.getClient("some_repo")).thenReturn(clientMock);
        when(depositDirMock.getPackage(Paths.get("update_A"))).thenReturn(sourcePackageMock);
    
        assertDoesNotThrow(() -> {
            updateFactory.create(
                testCurator,
                "A",
                Paths.get("update_A"),
                "some_repo",
                "we're good"
            );
        });
        verify(sourcePackageMock).validatePath();
    }

    @Test
    void updateFailsWhenInfoPackageDoesNotExist() {
        when(packageServiceMock.getInfoPackage("A")).thenReturn(null);

        assertThrows(NoEntityException.class, () -> {
            updateFactory.create(
                testCurator,
                "A",
                Paths.get("update_A"),
                "some_repo",
                "did I not add this yet?"
            );
        });
    }


    @Test
    void updateFailsWhenRepositoryDoesNotExist() {
        when(packageServiceMock.getInfoPackage("A")).thenReturn(infoPackageMock);
        when(repositoryServiceMock.getRepository("some_repo")).thenReturn(null);

        assertThrows(NoEntityException.class, () -> {
            updateFactory.create(
                testCurator,
                "A",
                Paths.get("update_A"),
                "some_repo",
                "was there a some_repo?"
            );
        });
    }

    @Test
    void updateFailsWhenPackageDoesNotHaveReplicaInRepository() {
        when(packageServiceMock.getInfoPackage("A")).thenReturn(infoPackageMock);
        when(repositoryServiceMock.getRepository("some_repo")).thenReturn(repositoryMock);
        when(registryMock.getClient("some_repo")).thenReturn(clientMock);
        when(infoPackageMock.hasAReplicaIn("some_repo")).thenReturn(false);

        assertThrows(NoEntityException.class, () -> {
            updateFactory.create(
                testCurator,
                "A",
                Paths.get("update_A"),
                "some_repo",
                "did I not add this yet?"
            );
        });
    }

    @Test
    void updateExecutes() {
        when(packageServiceMock.getInfoPackage("A")).thenReturn(infoPackageMock);
        when(repositoryServiceMock.getRepository("some_repo")).thenReturn(repositoryMock);
        when(registryMock.getClient("some_repo")).thenReturn(clientMock);
        when(infoPackageMock.hasAReplicaIn("some_repo")).thenReturn(true);

        List<Path> newPackagePaths = List.of(
            Paths.get("something.txt"),
            Paths.get("something_new.txt")
        );

        when(depositDirMock.getPackage(Paths.get("update_A"))).thenReturn(sourcePackageMock);
        when(sourcePackageMock.getRootPath()).thenReturn(Paths.get("some/path/update_A"));
        when(sourcePackageMock.getFilePaths()).thenReturn(newPackagePaths);

        Update update = updateFactory.create(
            testCurator,
            "A",
            Paths.get("update_A"),
            "some_repo",
            "we're good"
        );

        update.execute();

        verify(clientMock).updateObjectFiles(
            "A",
            Paths.get("some/path/update_A"),
            newPackagePaths,
            testCurator,
            "we're good"
        );
        verify(replicaServiceMock).updateReplica(infoPackageMock, repositoryMock);
    }
}
