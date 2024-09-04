package edu.umich.lib.dor.replicaexperiment.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.ocfl.api.OcflOption;
import io.ocfl.api.OcflRepository;
import io.ocfl.api.model.FileDetails;
import io.ocfl.api.model.ObjectDetails;
import io.ocfl.api.model.ObjectVersionId;
import io.ocfl.api.model.VersionInfo;
import io.ocfl.core.OcflRepositoryBuilder;
import io.ocfl.core.extension.storage.layout.config.HashedNTupleLayoutConfig;

import edu.umich.lib.dor.replicaexperiment.domain.Curator;
import edu.umich.lib.dor.replicaexperiment.exception.NoContentException;

public class OcflFilesystemRepositoryClient implements RepositoryClient {
	private static final Logger log = LoggerFactory.getLogger(OcflFilesystemRepositoryClient.class);

    private OcflRepository repo;

    public OcflFilesystemRepositoryClient(Path rootPath, Path workPath) {
        this.repo = new OcflRepositoryBuilder()
            .prettyPrintJson()
            .defaultLayoutConfig(new HashedNTupleLayoutConfig())
            .storage(storage -> storage.fileSystem(rootPath))
            .workDir(workPath)
            .build();
    }

    private VersionInfo createNewVersion(Curator curator, String message) {
        return new VersionInfo().setUser(curator.username(), curator.email()).setMessage(message);
    }

    private void validatePath(Path path) {
        if (!Files.exists(path)) {
            throw new NoContentException(
                String.format("No content exists at path %s.", path.toString())
            );
        }
    }

    public RepositoryClient createObject(String id, Path inputPath, Curator curator, String message) {
        validatePath(inputPath);
        repo.putObject(
            ObjectVersionId.head(id),
            inputPath,
            createNewVersion(curator, message)
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

    private Collection<FileDetails> getFiles(String id) {
        ObjectDetails objectDetails = repo.describeObject(id);
        var files = objectDetails
            .getHeadVersion()
            .getFiles();
        return files;
    }

    public List<Path> getStorageFilePaths(String id) {
        var filePaths = getFiles(id)
            .stream()
            .map(fileDetails -> fileDetails.getStorageRelativePath())
            .map(p -> Paths.get(p))
            .toList();
        log.debug(filePaths);
        return filePaths;
    }

    public List<Path> getFilePaths(String id) {
        var filePaths = getFiles(id)
            .stream()
            .map(fileDetails -> fileDetails.getPath())
            .map(p -> Paths.get(p))
            .toList();
        log.debug(filePaths.toString());
        return filePaths;
    }

    public RepositoryClient deleteObjectFile(
        String objectId, String filePath, Curator curator, String message
    ) {
        repo.updateObject(
            ObjectVersionId.head(objectId),
            createNewVersion(curator, message),
            updater -> { updater.removeFile(filePath); }
        );
        return this;
    }

    public RepositoryClient updateObjectFiles(
        String objectId, Path updatePackagePath, List<Path> inputPaths, Curator curator, String message
    ) {
        repo.updateObject(
            ObjectVersionId.head(objectId),
            createNewVersion(curator, message),
            updater -> {
                for (Path inputPath : inputPaths) {
                    updater.addPath(
                        updatePackagePath.resolve(inputPath),
                        inputPath.toString(),
                        OcflOption.OVERWRITE
                    );
                }
            }
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