package edu.umich.lib.dor.replicaexperiment;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    Path depositPath;

    InfoPackage infoPackageMock;
    Repository repositoryMock;
    Replica replicaMock;
    OcflFilesystemRepositoryClient clientMock;
    DepositDirectory depositDir;

    UpdateFactory updateFactory;

    Path updatePackagePath;

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

        this.depositPath = Paths.get("/deposit");
        this.depositDir = mock(DepositDirectory.class);

        updateFactory = new UpdateFactory(
            packageServiceMock,
            repositoryServiceMock,
            replicaServiceMock,
            registryMock,
            depositDir
        );

        this.updatePackagePath = Paths.get("update_A");
    }

    @Test
	void updateCanBeCreated() {
        Path depositUpdatePath = Paths.get("update_A");

        when(packageServiceMock.getInfoPackage("A")).thenReturn(infoPackageMock);
        when(repositoryServiceMock.getRepository("some_repo")).thenReturn(repositoryMock);
        when(infoPackageMock.hasAReplicaIn("some_repo")).thenReturn(true);

        when(registryMock.getClient("some_repo")).thenReturn(clientMock);
        when(depositDir.getFilePaths(depositUpdatePath)).thenReturn(
            List.of(
                Paths.get("something.txt"),
                Paths.get("something_new.txt")
            )
        );
        when(depositDir.getDepositPath()).thenReturn(Paths.get("some/path"));
        when(clientMock.getFilePaths("A")).thenReturn(
            List.of(
                Paths.get("something.txt"),
                Paths.get("something_else.txt"),
                Paths.get("special.txt")
            )
        );
    
        assertDoesNotThrow(() -> {
            updateFactory.create(
                testCurator,
                "A",
                depositUpdatePath,
                "some_repo",
                "we're good"
            );
        });
    }

    @Test
	void updateFailsWhenInfoPackageDoesNotExist () {
        when(packageServiceMock.getInfoPackage("A")).thenReturn(null);

        assertThrows(NoEntityException.class, () -> {
            updateFactory.create(
                testCurator,
                "A",
                updatePackagePath,
                "some_repo",
                "did I not add this yet?"
            );
        });
    }


    @Test
	void updateFailsWhenRepositoryDoesNotExist () {
        when(packageServiceMock.getInfoPackage("A")).thenReturn(infoPackageMock);
        when(repositoryServiceMock.getRepository("some_repo")).thenReturn(null);

        assertThrows(NoEntityException.class, () -> {
            updateFactory.create(
                testCurator,
                "A",
                updatePackagePath,
                "some_repo",
                "was there a some_repo?"
            );
        });
    }

    @Test
	void updateFailsWhenPackageDoesNotExistHaveReplicaInRepository() {
        when(packageServiceMock.getInfoPackage("A")).thenReturn(infoPackageMock);
        when(repositoryServiceMock.getRepository("some_repo")).thenReturn(repositoryMock);
        when(registryMock.getClient("some_repo")).thenReturn(clientMock);
        when(infoPackageMock.hasAReplicaIn("some_repo")).thenReturn(false);

        assertThrows(NoEntityException.class, () -> {
            updateFactory.create(
                testCurator,
                "A",
                updatePackagePath,
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

        when(depositDir.getDepositPath()).thenReturn(Paths.get("some/path"));
        when(depositDir.getFilePaths(updatePackagePath)).thenReturn(newPackagePaths);
        when(clientMock.getFilePaths("A")).thenReturn(
            List.of(
                Paths.get("something.txt"),
                Paths.get("something_else.txt"),
                Paths.get("special.txt")
            )
        );

        Update update = updateFactory.create(
            testCurator,
            "A",
            updatePackagePath,
            "some_repo",
            "we're good"
        );

        update.execute();

        Path fullUpdatePackagePath = depositDir.getDepositPath().resolve(updatePackagePath);

        verify(clientMock).updateObjectFiles(
            "A", fullUpdatePackagePath, newPackagePaths, testCurator, "we're good"
        );
    }
}
