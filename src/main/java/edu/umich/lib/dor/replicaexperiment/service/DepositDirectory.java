package edu.umich.lib.dor.replicaexperiment.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DepositDirectory {
    private static final Log log = LogFactory.getLog(OcflFilesystemRepositoryClient.class);

    private Path depositPath;

    public DepositDirectory(Path depositPath) {
        this.depositPath = depositPath;
    }

    public Path getDepositPath() {
        return depositPath;
    }

    public List<Path> getFilePaths(Path packagePath) {
        var fullPath = depositPath.resolve(packagePath);
        try {
            List<Path> paths = Files.walk(fullPath)
                .filter(p -> Files.isRegularFile(p))
                .toList();
            return paths;
        } catch(IOException e) {
            var message = "IOException occurred: " + e.toString();
            log.error(message);
            throw new IllegalArgumentException(message);
        }
    }

    public List<Path> getPackageFilePaths(Path packagePath) {
        var fullPath = depositPath.resolve(packagePath);
        return getFilePaths(packagePath)
            .stream()
            .map(p -> fullPath.relativize(p))
            .toList();
    }
}
