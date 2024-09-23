package edu.umich.lib.dor.replicaexperiment;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import io.ocfl.api.OcflObjectUpdater;
import io.ocfl.api.OcflOption;
import io.ocfl.api.OcflRepository;
import io.ocfl.api.model.FileDetails;
import io.ocfl.api.model.ObjectDetails;
import io.ocfl.api.model.ObjectVersionId;
import io.ocfl.api.model.User;
import io.ocfl.api.model.VersionDetails;
import io.ocfl.api.model.VersionInfo;
import io.ocfl.api.model.VersionNum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import edu.umich.lib.dor.replicaexperiment.domain.Curator;
import edu.umich.lib.dor.replicaexperiment.service.OcflFilesystemRepositoryClient;
import edu.umich.lib.dor.replicaexperiment.service.Package;

public class OcflFilesystemRepositoryClientTest {
    OcflRepository ocflRepositoryMock;
    OcflFilesystemRepositoryClient repositoryClient;
    Package sourcePackageMock;

    @BeforeEach
    public void init() {
        ocflRepositoryMock = mock(OcflRepository.class);
        repositoryClient = new OcflFilesystemRepositoryClient(ocflRepositoryMock);
        sourcePackageMock = mock(Package.class);
    }

    private ObjectDetails createObjectDetails() {
        var fileaDetails = new FileDetails();
        fileaDetails.setPath("test.txt");
        fileaDetails.setStorageRelativePath("storage/A/test.txt");

        var fileMap = new HashMap<String, FileDetails>();
        fileMap.put("A", fileaDetails);
        var versionDetails = new VersionDetails();
        versionDetails.setFileMap(fileMap);

        var versionMap = new HashMap<VersionNum, VersionDetails>();
        var versionNum = new VersionNum(1);
        versionMap.put(versionNum, versionDetails);
        var details = new ObjectDetails();
        details.setId("A");
        details.setHeadVersionNum(versionNum);
        details.setVersions(versionMap);
        return details;
    }

    @Test
    public void clientWithInjectedRepositoryWorks() {
        new OcflFilesystemRepositoryClient(ocflRepositoryMock);
    }

    @Test
    public void clientCreatesObject() {
        when(sourcePackageMock.getRootPath()).thenReturn(Paths.get("some/path/deposit_A"));

        repositoryClient.createObject(
            "A",
            sourcePackageMock,
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
        when(sourcePackageMock.getRootPath()).thenReturn(Paths.get("some/path/update_A"));
        when(sourcePackageMock.getFilePaths()).thenReturn(List.of(
            Paths.get("A.txt"), Paths.get("B/C.txt")
        ));

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
            sourcePackageMock,
            new Curator("test", "test@example.edu"),
            "Updating files"
        );
    }

    @Test
    public void clientGetsPackagePaths() {
        when(ocflRepositoryMock.describeObject("A"))
            .thenReturn(createObjectDetails());
        var filePaths = repositoryClient.getFilePaths("A");
        assertIterableEquals(List.of(Paths.get("test.txt")), filePaths);
    }

    @Test
    public void clientGetsStoragePackagePaths() {
        when(ocflRepositoryMock.describeObject("A"))
            .thenReturn(createObjectDetails());
        var filePaths = repositoryClient.getStorageFilePaths("A");
        assertIterableEquals(List.of(Paths.get("/storage/A/test.txt")), filePaths);
    }

    @Test
    public void clientExportsObject() {
        repositoryClient.exportObject("A", Paths.get("some/path/staging/A"));
        verify(ocflRepositoryMock).exportObject("A", Paths.get("some/path/staging/A"));
    }

    @Test
    public void clientImportsObject() {
        repositoryClient.importObject(Paths.get("some/path/staging/A"));
        verify(ocflRepositoryMock).importObject(
            Paths.get("some/path/staging/A"), OcflOption.MOVE_SOURCE
        );
    }

    @Test
    public void clientPurgesObject() {
        repositoryClient.purgeObject("A");
        verify(ocflRepositoryMock).purgeObject("A");
    }
}
