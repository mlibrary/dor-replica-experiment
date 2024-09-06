package edu.umich.lib.dor.replicaexperiment;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.umich.lib.dor.replicaexperiment.service.DepositDirectory;

public class DepositDirectoryTest {
    
    DepositDirectory depositDir;

    @BeforeEach
    public void init() {
        Path testDepositPath = Paths.get("src", "test", "resources", "test_deposit");

        this.depositDir = new DepositDirectory(testDepositPath);
    }

    @Test
    public void depositDirectoryListsFilePaths() {
        Set<Path> expectedSet = Set.of(
            Paths.get("A.txt"), Paths.get("B/B.txt"), Paths.get("C/D/D.txt")
        );
        List<Path> paths = depositDir.getPackageFilePaths(Paths.get("deposit_one"));
        var pathSet = Set.copyOf(paths);
        assertTrue(expectedSet.equals(pathSet));
    }
}
