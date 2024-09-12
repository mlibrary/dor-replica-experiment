package edu.umich.lib.dor.replicaexperiment;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.umich.lib.dor.replicaexperiment.service.DepositDirectory;

public class DepositDirectoryTest {
    Path testDepositPath;
    DepositDirectory depositDir;

    @BeforeEach
    public void init() {
        this.testDepositPath = Paths.get("src", "test", "resources", "test_deposit");
        this.depositDir = new DepositDirectory(testDepositPath);
    }

    @Test
    public void depositDirectoryResolvesPackagePathsToFullOnes() {
        var packagePath = Paths.get("some_package");
        assertEquals(testDepositPath.resolve(packagePath), depositDir.resolve(packagePath));
    }

    @Test
    public void depositDirectoryProvidesAccessToPackages() {
        var packagePath = Paths.get("some_package");
        depositDir.getPackage(packagePath);
    }
}
