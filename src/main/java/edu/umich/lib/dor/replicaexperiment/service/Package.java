package edu.umich.lib.dor.replicaexperiment.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import edu.umich.lib.dor.replicaexperiment.exception.NoContentException;

public class Package {
    protected final DepositDirectory depositDir;
    protected final Path packagePath;

    public Package(DepositDirectory depositDir, Path packagePath) {
        this.depositDir = depositDir;
        this.packagePath = packagePath;

        validatePath();
    }

    public Path getRootPath() {
        return depositDir.resolve(packagePath);
    }

    private void validatePath() {
        if (!Files.exists(getRootPath())) {
            throw new NoContentException(
                String.format("No content exists at path %s.", packagePath.toString())
            );
        }
    }

    public List<Path> getFilePaths() {
        var fullPath = getRootPath();
        try {
            List<Path> paths = Files.walk(fullPath)
                .filter(p -> Files.isRegularFile(p))
                .map(p -> fullPath.relativize(p))
                .toList();
            return paths;
        } catch (IOException e) {
            throw new RuntimeException(
                "Could not find file paths for root path: " + fullPath, e
            );
        }
    }
}
