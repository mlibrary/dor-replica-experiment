package edu.umich.lib.dor.replicaexperiment.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.commons.codec.digest.DigestUtils;

import edu.umich.lib.dor.replicaexperiment.exception.ChecksumCalculationException;

public class ChecksumCalculator {
    public static String calculate(Path filePath) {
        try {
            return DigestUtils.sha512Hex(new FileInputStream(filePath.toFile()));
        } catch (IOException e) {
            throw new ChecksumCalculationException(
                "Could not calculate checksum for path: " + filePath, e
            );
        }
    }
}
