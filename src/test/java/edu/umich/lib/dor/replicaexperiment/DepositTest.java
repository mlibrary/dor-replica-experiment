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

import edu.umich.lib.dor.replicaexperiment.domain.InfoPackage;
import edu.umich.lib.dor.replicaexperiment.domain.Replica;
import edu.umich.lib.dor.replicaexperiment.domain.Repository;
import edu.umich.lib.dor.replicaexperiment.domain.RepositoryType;
import edu.umich.lib.dor.replicaexperiment.domain.User;
import edu.umich.lib.dor.replicaexperiment.exception.EntityAlreadyExistsException;
import edu.umich.lib.dor.replicaexperiment.exception.NoEntityException;
import edu.umich.lib.dor.replicaexperiment.service.DepositFactory;
import edu.umich.lib.dor.replicaexperiment.service.InfoPackageService;
import edu.umich.lib.dor.replicaexperiment.service.OcflFilesystemRepositoryClient;
import edu.umich.lib.dor.replicaexperiment.service.ReplicaService;
import edu.umich.lib.dor.replicaexperiment.service.RepositoryClientRegistry;
import edu.umich.lib.dor.replicaexperiment.service.RepositoryService;

public class DepositTest {
    User testUser = new User("test", "test@example.edu");
    InfoPackageService packageServiceMock;
    RepositoryService repositoryServiceMock;
    ReplicaService replicaServiceMock;
    RepositoryClientRegistry registryMock;
    OcflFilesystemRepositoryClient clientMock;
    Replica replicaMock;

    Path depositPath;

    DepositFactory depositFactory;

    @BeforeEach
    void init() {
        this.packageServiceMock = mock(InfoPackageService.class);
        this.repositoryServiceMock = mock(RepositoryService.class);
        this.replicaServiceMock = mock(ReplicaService.class);
        this.registryMock = mock(RepositoryClientRegistry.class);
        this.clientMock = mock(OcflFilesystemRepositoryClient.class);
        this.replicaMock = mock(Replica.class);

        this.depositPath = Paths.get("/deposit");

        depositFactory = new DepositFactory(
            packageServiceMock,
            repositoryServiceMock,
            replicaServiceMock,
            registryMock,
            depositPath
        );
    }

    @Test
    void depositCanBeCreated() {
        when(packageServiceMock.getInfoPackage("A")).thenReturn(null);
        when(repositoryServiceMock.getRepository("some_repo")).thenReturn(
            new Repository("some_repo", RepositoryType.FILE_SYSTEM)
        );
        when(registryMock.getClient("some_repo")).thenReturn(clientMock);
    
        assertDoesNotThrow(() -> {
            depositFactory.create(
                testUser,
                "A",
                Paths.get("something"),
                "some_repo",
                "we're good"
            );
        });
    }

    @Test
    void depositSpecifiesPackageThatAlreadyExists() {
        when(packageServiceMock.getInfoPackage("A")).thenReturn(
            new InfoPackage("A")
        );

        assertThrows(EntityAlreadyExistsException.class, () -> {
            depositFactory.create(
                testUser,
                "A",
                Paths.get("/something"),
                "some_repo",
                "did I already add this?"
            );
        });
    }

    @Test
    void depositSpecifiesRepositoryThatDoesNotExist() {
        when(repositoryServiceMock.getRepository("some_repo")).thenReturn(null);

        assertThrows(NoEntityException.class, () -> {
            depositFactory.create(
                testUser,
                "A",
                Paths.get("/something"),
                "some_repo",
                "was there a some_repo?"
            );
        });
    }

    @Test
    void depositExecutes() {
        var repository = new Repository("some_repo", RepositoryType.FILE_SYSTEM);
        var infoPackage = new InfoPackage("A");

        when(packageServiceMock.getInfoPackage("A")).thenReturn(null);
        when(repositoryServiceMock.getRepository("some_repo")).thenReturn(repository);
        when(registryMock.getClient("some_repo")).thenReturn(clientMock);

        var deposit = depositFactory.create(
            testUser,
            "A",
            Paths.get("something"),
            "some_repo",
            "we're good"
        );

        when(packageServiceMock.createInfoPackage("A")).thenReturn(infoPackage);
        when(replicaServiceMock.createReplica(infoPackage, repository)).thenReturn(replicaMock);

        deposit.execute();

        verify(clientMock).createObject(
            "A", depositPath.resolve("something"), testUser, "we're good"
        );
        verify(packageServiceMock).createInfoPackage("A");
        verify(replicaServiceMock).createReplica(infoPackage, repository);
    }
}
