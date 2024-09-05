package edu.umich.lib.dor.replicaexperiment.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;

import edu.umich.lib.dor.replicaexperiment.exception.ChecksumCalculationException;

public class ChecksumCalculator {
    public static String calculate(Path filePath) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] digest = md.digest(Files.readAllBytes(filePath));
            return new String(digest, "US-ASCII");
        } catch(Exception e) {
            throw new ChecksumCalculationException(e.toString());
        }
    }
}
