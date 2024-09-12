package edu.umich.lib.dor.replicaexperiment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import edu.umich.lib.dor.replicaexperiment.service.ChecksumCalculator;

public class ChecksumCalculatorTest {

    @Test
    public void calculatorGeneratesChecksum() {
        var testDepositPath = Paths.get("src", "test", "resources", "test_deposit");
        var filePath = testDepositPath.resolve("deposit_one/A.txt");
        var result = ChecksumCalculator.calculate(filePath);
        assertEquals(128, result.length());
        assertTrue(result.matches("^[0-9a-fA-F]+$"));
    }
}
