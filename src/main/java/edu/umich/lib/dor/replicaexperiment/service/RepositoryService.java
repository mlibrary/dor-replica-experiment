package edu.umich.lib.dor.replicaexperiment.service;

import java.nio.file.Path;
import java.util.List;

import edu.umich.lib.dor.replicaexperiment.domain.User;

public interface RepositoryService {
    RepositoryService createObject(String id, Path inputPath, User user, String message);

    RepositoryService readObject(String id, Path outputPath);

    RepositoryService deleteObject(String id);

    boolean hasObject(String id);

    RepositoryService deleteObjectFile(String objectId, String filePath, User user, String message);

    RepositoryService updateObjectFile(
        String objectId, Path inputPath, String filePath, User user, String message
    );

    List<String> getFilePaths(String id);

    RepositoryService importObject(Path inputPath);

    RepositoryService exportObject(String objectId, Path outputPath);
}
