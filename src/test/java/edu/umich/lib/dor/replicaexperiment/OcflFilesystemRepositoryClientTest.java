package edu.umich.lib.dor.replicaexperiment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.Paths;
import java.util.List;
import java.util.function.Consumer;

import io.ocfl.api.OcflObjectUpdater;
import io.ocfl.api.OcflOption;
import io.ocfl.api.OcflRepository;
import io.ocfl.api.model.ObjectVersionId;
import io.ocfl.api.model.User;
import io.ocfl.api.model.VersionInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import edu.umich.lib.dor.replicaexperiment.domain.Curator;
import edu.umich.lib.dor.replicaexperiment.service.OcflFilesystemRepositoryClient;
import edu.umich.lib.dor.replicaexperiment.service.RepositoryClient;

public class OcflFilesystemRepositoryClientTest {
    OcflRepository ocflRepositoryMock;
    OcflFilesystemRepositoryClient repositoryClient;

    @BeforeEach
    public void init() {
        ocflRepositoryMock = mock(OcflRepository.class);
    }

    @Test
    public void clientWithInjectedCreationWorks() {
        new OcflFilesystemRepositoryClient(ocflRepositoryMock);
    }

    @Test
    public void clientCreatesObject() {
        RepositoryClient repositoryClient = new OcflFilesystemRepositoryClient(ocflRepositoryMock);
        repositoryClient.createObject(
            "A",
            Paths.get("some/path/deposit_A"),
            new Curator("test", "test@example.edu"),
            "adding images and metadata"
        );

        verify(ocflRepositoryMock).putObject(
            ObjectVersionId.head("A"),
            Paths.get("some/path/deposit_A"),
            new VersionInfo().setUser(
                "test", "test@example.edu"
            ).setMessage("adding images and metadata")
        );
    }

    @Test
    public void clientUpdatesObject() {
        RepositoryClient repositoryClient = new OcflFilesystemRepositoryClient(ocflRepositoryMock);
        
        when(ocflRepositoryMock.updateObject(
            any(ObjectVersionId.class),
            any(VersionInfo.class),
            ArgumentMatchers.<Consumer<OcflObjectUpdater>>any()
        ))
            .then((invocationOnMock) -> {
                var args = invocationOnMock.getArguments();

                ObjectVersionId versionId = (ObjectVersionId) args[0];
                assertEquals("A", versionId.getObjectId());

                VersionInfo versionInfo = (VersionInfo) args[1];
                assertEquals("Updating files", versionInfo.getMessage());
                User user = versionInfo.getUser();
                assertEquals("test", user.getName());
                assertEquals("test@example.edu", user.getAddress());

                Consumer<OcflObjectUpdater> updaterLambda = invocationOnMock.getArgument(2);
                OcflObjectUpdater updaterMock = mock(OcflObjectUpdater.class);
                updaterLambda.accept(updaterMock);

                verify(updaterMock).addPath(
                    Paths.get("some/path/update_A/A.txt"),
                    "A.txt",
                    OcflOption.OVERWRITE
                );
                verify(updaterMock).addPath(
                    Paths.get("some/path/update_A/B/C.txt"),
                    "B/C.txt",
                    OcflOption.OVERWRITE
                );
                return versionId;
            });
        repositoryClient.updateObjectFiles(
            "A",
            Paths.get("some/path/update_A"),
            List.of(Paths.get("A.txt"), Paths.get("B/C.txt")),
            new Curator("test", "test@example.edu"),
            "Updating files"
        );
    }
}
