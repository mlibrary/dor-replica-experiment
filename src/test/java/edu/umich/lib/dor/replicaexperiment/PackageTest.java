package edu.umich.lib.dor.replicaexperiment;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.umich.lib.dor.replicaexperiment.service.DepositDirectory;
import edu.umich.lib.dor.replicaexperiment.service.Package;

public class PackageTest {
    static final Logger log = LoggerFactory.getLogger(PackageTest.class);

    private Path testDepositPath;

    @BeforeEach
    public void init() {
        this.testDepositPath = Paths.get("src", "test", "resources", "test_deposit");
        
        var emptyDepositPath = testDepositPath.resolve("empty_deposit");
        if (!Files.exists(emptyDepositPath)) {
            try {
                Files.createDirectory(emptyDepositPath);
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }

    @Test
    public void packageProvidesRootPath() {
        var pkg = new Package(
            new DepositDirectory(testDepositPath),
            Paths.get("deposit_one")
        );
        assertEquals(testDepositPath.resolve("deposit_one"), pkg.getRootPath());
    }

    @Test
    public void emptyPackageContainsNoFiles() {
        var pkg = new Package(
            new DepositDirectory(testDepositPath),
            Paths.get("empty_deposit")
        );
        assertTrue(pkg.getFilePaths().isEmpty());
    }

    @Test
    public void mixedPackageContainsExpectedFiles() {
        var pkg = new Package(
            new DepositDirectory(testDepositPath),
            Paths.get("deposit_one")
        );
        Set<Path> expectedSet = Set.of(
            Paths.get("A.txt"), Paths.get("B/B.txt"), Paths.get("C/D/D.txt")
        );

        assertEquals(expectedSet, Set.copyOf(pkg.getFilePaths()));
    }
}
