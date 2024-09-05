package edu.umich.lib.dor.replicaexperiment.service;

import java.nio.file.Path;
import java.util.List;

import edu.umich.lib.dor.replicaexperiment.domain.Curator;

public interface RepositoryClient {
    RepositoryClient createObject(String id, Path inputPath, Curator curator, String message);

    RepositoryClient readObject(String id, Path outputPath);

    RepositoryClient deleteObject(String id);

    boolean hasObject(String id);

    RepositoryClient deleteObjectFile(String objectId, String filePath, Curator curator, String message);

    RepositoryClient updateObjectFiles(
        String objectId, Path updatePackagePath, List<Path> inputPaths, Curator curator, String message
    );

    List<Path> getFilePaths(String id);

    List<Path> getStorageFilePaths(String id);

    RepositoryClient importObject(Path inputPath);

    RepositoryClient exportObject(String objectId, Path outputPath);
}
