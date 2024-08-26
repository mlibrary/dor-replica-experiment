package edu.umich.lib.dor.replicaexperiment.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import io.ocfl.api.OcflOption;
import io.ocfl.api.OcflRepository;
import io.ocfl.api.model.ObjectDetails;
import io.ocfl.api.model.ObjectVersionId;
import io.ocfl.api.model.VersionInfo;
import io.ocfl.core.OcflRepositoryBuilder;
import io.ocfl.core.extension.storage.layout.config.HashedNTupleLayoutConfig;

import edu.umich.lib.dor.replicaexperiment.domain.User;
import edu.umich.lib.dor.replicaexperiment.exception.NoContentException;

public class OcflFilesystemRepositoryClient implements RepositoryClient {
	private static final Log log = LogFactory.getLog(OcflFilesystemRepositoryClient.class);

    private OcflRepository repo;

    public OcflFilesystemRepositoryClient(Path rootPath, Path workPath) {
        this.repo = new OcflRepositoryBuilder()
            .prettyPrintJson()
            .defaultLayoutConfig(new HashedNTupleLayoutConfig())
            .storage(storage -> storage.fileSystem(rootPath))
            .workDir(workPath)
            .build();
    }

    private VersionInfo createNewVersion(User user, String message) {
        return new VersionInfo().setUser(user.username(), user.email()).setMessage(message);
    }

    private void validatePath(Path path) {
        if (!Files.exists(path)) {
            throw new NoContentException(
                String.format("No content exists at path %s.", path.toString())
            );
        }
    }

    public RepositoryClient createObject(String id, Path inputPath, User user, String message) {
        validatePath(inputPath);
        repo.putObject(
            ObjectVersionId.head(id),
            inputPath,
            createNewVersion(user, message)
        );
        return this;
    }

    public RepositoryClient readObject(String id, Path outputPath) {
        repo.getObject(ObjectVersionId.head(id), outputPath);
        return this;
    }

    public RepositoryClient deleteObject(String id) {
        repo.purgeObject(id);
        return this;
    }

    public boolean hasObject(String id) {
        return repo.containsObject(id);
    }

    public List<String> getFilePaths(String id) {
        ObjectDetails objectDetails = repo.describeObject(id);
        var filePaths = objectDetails
            .getHeadVersion()
            .getFiles()
            .stream()
            .map(fileDetails -> { return fileDetails.getStorageRelativePath(); })
            .toList();
        log.debug(filePaths);
        return filePaths;
    }

    public RepositoryClient deleteObjectFile(String objectId, String filePath, User user, String message) {
        repo.updateObject(
            ObjectVersionId.head(objectId),
            createNewVersion(user, message),
            updater -> { updater.removeFile(filePath); }
        );
        return this;
    }

    public RepositoryClient updateObjectFile(
        String objectId, Path inputPath, String filePath, User user, String message
    ) {
        validatePath(inputPath);
        repo.updateObject(
            ObjectVersionId.head(objectId),
            createNewVersion(user, message),
            updater -> { updater.addPath(inputPath, filePath, OcflOption.OVERWRITE); }
        );
        return this;
    }

    public RepositoryClient importObject(Path inputPath) {
        // TO DO: Is MOVE_SOURCE OK? Keeps staging clear
        validatePath(inputPath);
        repo.importObject(inputPath, OcflOption.MOVE_SOURCE);
        return this;
    }

    public RepositoryClient exportObject(String objectId, Path outputPath) {
        repo.exportObject(objectId, outputPath);
        return this;
    }
}