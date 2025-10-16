package com.swiftshare.fileio.testing;

import com.swiftshare.fileio.core.*;
import java.io.*;

/**
 * Test program to verify file chunking works
 */
public class TestFileChunker {

    public static void main(String[] args) {
        System.out.println("=== FILE CHUNKER TEST ===\n");

        try {
            // Step 1: Create a test file
            String testFileName = "test.txt";
            int testFileSize = 200000; // 200KB
            createTestFile(testFileName, testFileSize);

            // Step 2: Chunk the file
            FileChunkManager chunker = new FileChunkManager();

            System.out.println("\n--- Starting Chunking ---\n");
            FileChunkData chunkData = chunker.chunkFile(testFileName);

            // Step 3: Verify results
            System.out.println("\n--- Verification ---");
            System.out.println("Original file size: " + testFileSize + " bytes");
            System.out.println("Chunk data file size: " + chunkData.fileSize + " bytes");
            System.out.println("Number of chunks: " + chunkData.totalChunks);
            System.out.println("Expected chunks: " +
                    Math.ceil((double)testFileSize / FileChunkManager.CHUNK_SIZE));

            // Calculate total size of all chunks
            long totalChunkSize = 0;
            for (byte[] chunk : chunkData.chunks) {
                totalChunkSize += chunk.length;
            }
            System.out.println("Total chunk data: " + totalChunkSize + " bytes");

            // Check if sizes match
            if (totalChunkSize == testFileSize) {
                System.out.println("\n✅ SUCCESS! Chunking works correctly!");
            } else {
                System.out.println("\n❌ FAILURE! Size mismatch!");
            }

        } catch (Exception e) {
            System.err.println("\n❌ ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Helper method to create a test file
     */
    static void createTestFile(String fileName, int sizeInBytes) throws IOException {
        System.out.println("Creating test file: " + fileName);
        System.out.println("Size: " + sizeInBytes + " bytes");

        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            byte[] data = new byte[sizeInBytes];

            // Fill with some pattern data
            for (int i = 0; i < sizeInBytes; i++) {
                data[i] = (byte) (i % 256);
            }

            fos.write(data);
        }

        System.out.println("✅ Test file created successfully");
    }
}