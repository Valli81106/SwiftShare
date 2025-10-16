package com.swiftshare.fileio.core;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Handles file validation using checksums
 */
public class FileValidator {
    
    /**
     * Calculate SHA-256 checksum of a file
     * This creates a unique "fingerprint" for the file
     * @param filePath Path to file
     * @return Checksum as hex string
     */
    public String calculateChecksum(String filePath) 
            throws IOException, NoSuchAlgorithmException {
        
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        
        try (FileInputStream fis = new FileInputStream(filePath)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            
            while ((bytesRead = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
        }
        
        // Convert hash bytes to hex string
        byte[] hashBytes = digest.digest();
        StringBuilder hexString = new StringBuilder();
        
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        
        return hexString.toString();
    }
    
    /**
     * Verify a file matches expected checksum
     * @param filePath Path to file to validate
     * @param expectedChecksum The checksum it should have
     * @return true if checksums match, false otherwise
     */
    public boolean validateFile(String filePath, String expectedChecksum) 
            throws IOException, NoSuchAlgorithmException {
        
        String actualChecksum = calculateChecksum(filePath);
        boolean isValid = actualChecksum.equalsIgnoreCase(expectedChecksum);
        
        System.out.println("\n=== File Validation ===");
        System.out.println("File: " + filePath);
        System.out.println("Expected checksum: " + expectedChecksum);
        System.out.println("Actual checksum:   " + actualChecksum);
        System.out.println("Valid: " + (isValid ? "✅ YES" : "❌ NO"));
        
        return isValid;
    }
}