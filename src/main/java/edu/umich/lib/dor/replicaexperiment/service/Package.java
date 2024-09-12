package edu.umich.lib.dor.replicaexperiment.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Package {
    protected final DepositDirectory depositDir;
    protected final Path packagePath;

    public Package(DepositDirectory depositDir, Path packagePath) {
        this.depositDir = depositDir;
        this.packagePath = packagePath;
    }

    public Path getRootPath() {
        return depositDir.resolve(packagePath);
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
