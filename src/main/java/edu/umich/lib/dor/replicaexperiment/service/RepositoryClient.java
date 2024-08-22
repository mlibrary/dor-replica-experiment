package edu.umich.lib.dor.replicaexperiment.service;

import java.nio.file.Path;
import java.util.List;

import edu.umich.lib.dor.replicaexperiment.domain.User;

public interface RepositoryClient {
    RepositoryClient createObject(String id, Path inputPath, User user, String message);

    RepositoryClient readObject(String id, Path outputPath);

    RepositoryClient deleteObject(String id);

    boolean hasObject(String id);

    RepositoryClient deleteObjectFile(String objectId, String filePath, User user, String message);

    RepositoryClient updateObjectFile(
        String objectId, Path inputPath, String filePath, User user, String message
    );

    List<String> getFilePaths(String id);

    RepositoryClient importObject(Path inputPath);

    RepositoryClient exportObject(String objectId, Path outputPath);
}
