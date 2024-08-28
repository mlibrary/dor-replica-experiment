package edu.umich.lib.dor.replicaexperiment;

import java.util.List;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import edu.umich.lib.dor.replicaexperiment.exception.RepositoryNotRegisteredException;
import edu.umich.lib.dor.replicaexperiment.service.OcflFilesystemRepositoryClient;
import edu.umich.lib.dor.replicaexperiment.service.RepositoryClientRegistry;

public class RepositoryClientRegistryTest {
    
    OcflFilesystemRepositoryClient mockRepoOne;
    OcflFilesystemRepositoryClient mockRepoTwo;

    @BeforeEach
    void init() {
        mockRepoOne = mock(OcflFilesystemRepositoryClient.class);
        mockRepoTwo = mock(OcflFilesystemRepositoryClient.class);
    }

    @Test
    void registryCanRegisterRepositoryClient() {
        assertDoesNotThrow(() -> {
            RepositoryClientRegistry registry = new RepositoryClientRegistry();
            registry.register("repo_one", mockRepoOne);
        });
    }

    @Test
    void registryCanListRegisteredClients() {
        RepositoryClientRegistry registry = new RepositoryClientRegistry();
        registry.register("repo_one", mockRepoOne);
        assertEquals(1, registry.listClients().size());
        assertEquals("repo_one", registry.listClients().get(0));
    }

    @Test
    void repositoryClientCanGetARegisteredRepository() {
        RepositoryClientRegistry registry = new RepositoryClientRegistry();
        registry.register("repo_one", mockRepoOne);
        assertEquals(mockRepoOne, registry.getClient("repo_one"));
    }

    @Test
    void repositoryClientThrowsErrorWhenRepositoryNotRegistered() {
        assertThrows(RepositoryNotRegisteredException.class, () -> {
            RepositoryClientRegistry registry = new RepositoryClientRegistry();
            registry.getClient("repo_one");
        });
    }
}
