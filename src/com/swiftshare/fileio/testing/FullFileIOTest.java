package com.swiftshare.fileio.testing;

import com.swiftshare.fileio.core.FileValidator;
import fileio.*;
import java.io.*;

/**
 * Complete test: Chunk → Receive → Assemble → Validate
 */
public class testing {

    public static void main(String[] args) {
        System.out.println("=== COMPLETE FILE I/O TEST ===\n");

        try {
            // 1. Create test file
            String testFile = "original.dat";
            int fileSize = 250000; // 250KB
            createTestFile(testFile, fileSize);

            // 2. Calculate original checksum
            System.out.println("\n--- Calculating Original Checksum ---");
            FileValidator validator = new FileValidator();
            String originalChecksum = validator.calculateChecksum(testFile);
            System.out.println("Original checksum: " + originalChecksum);

            // 3. SENDER SIDE: Chunk the file
            System.out.println("\n--- SENDER: Chunking File ---");
            FileChunkManager chunker = new FileChunkManager();
            FileChunkData chunkData = chunker.chunkFile(testFile);

            // 4. RECEIVER SIDE: Simulate receiving chunks
            System.out.println("\n--- RECEIVER: Receiving Chunks ---");
            ReceivedChunkData received = new ReceivedChunkData(
                    chunkData.fileName,
                    chunkData.fileSize,
                    chunkData.totalChunks
            );

            // Simulate chunks arriving over network
            for (int i = 0; i < chunkData.totalChunks; i++) {
                received.addChunk(i, chunkData.chunks[i]);
                System.out.println("Progress: " + String.format("%.1f", received.getProgress()) + "%");
            }

            // 5. RECEIVER SIDE: Assemble file
            System.out.println("\n--- RECEIVER: Assembling File ---");
            FileChunkAssembler assembler = new FileChunkAssembler();
            String outputFile = "received.dat";
            assembler.assembleFile(received, outputFile);

            // 6. Validate received file
            System.out.println("\n--- Validating Received File ---");
            boolean isValid = validator.validateFile(outputFile, originalChecksum);

            // 7. Final result
            if (isValid) {
                System.out.println("\n🎉🎉🎉 SUCCESS! 🎉🎉🎉");
                System.out.println("File transferred correctly!");
                System.out.println("Original: " + testFile);
                System.out.println("Received: " + outputFile);
            } else {
                System.out.println("\n❌ FAILURE! File is corrupted!");
            }

        } catch (Exception e) {
            System.err.println("\n❌ Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Create test file with random data
     */
    static void createTestFile(String fileName, int sizeInBytes) throws IOException {
        System.out.println("Creating test file: " + fileName + " (" + sizeInBytes + " bytes)");

        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            byte[] data = new byte[sizeInBytes];

            // Use random data to make it realistic
            java.util.Random random = new java.util.Random();
            random.nextBytes(data);

            fos.write(data);
        }

        System.out.println("✅ Test file created");
    }
}