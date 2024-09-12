package edu.umich.lib.dor.replicaexperiment.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import at.favre.lib.bytes.Bytes;

import edu.umich.lib.dor.replicaexperiment.exception.ChecksumCalculationException;

public class ChecksumCalculator {
    public static String calculate(Path filePath) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            throw new ChecksumCalculationException(e.getMessage());
        }

        try {
            byte[] digest = md.digest(Files.readAllBytes(filePath));
            return Bytes.wrap(digest).encodeHex();
        } catch (IOException e) {
            throw new ChecksumCalculationException(e.getMessage());
        }
    }
}
