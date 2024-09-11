package edu.umich.lib.dor.replicaexperiment.service;

import java.nio.file.Path;

public class DepositDirectory {
    private Path depositPath;

    public DepositDirectory(Path depositPath) {
        this.depositPath = depositPath;
    }

    public Path getDepositPath() {
        return depositPath;
    }

    public Path resolve(Path packagePath) {
        return depositPath.resolve(packagePath);
    }

    public Package getPackage(Path packagePath) {
        return new Package(this, packagePath);
    }
}
