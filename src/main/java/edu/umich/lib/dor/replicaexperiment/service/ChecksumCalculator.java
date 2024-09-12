package edu.umich.lib.dor.replicaexperiment.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;

import edu.umich.lib.dor.replicaexperiment.exception.ChecksumCalculationException;

public class ChecksumCalculator {
    public static String calculate(Path filePath) {
        MessageDigest messageDigest;
        byte[] digestBytes;

        try {
            messageDigest = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            throw new ChecksumCalculationException(e.getMessage());
        }
        try {
            digestBytes = messageDigest.digest(Files.readAllBytes(filePath));
        } catch (IOException e) {
            throw new ChecksumCalculationException(e.getMessage());
        }
        return Hex.encodeHexString(digestBytes);
    }
}
