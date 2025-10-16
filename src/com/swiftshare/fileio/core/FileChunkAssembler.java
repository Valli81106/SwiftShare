package com.swiftshare.fileio.core;
import java.io.*;

/**
 * Handles reassembling chunks back into original file (RECEIVER side)
 */
public class FileChunkAssembler {

    /**
     * Assemble received chunks into complete file
     * @param receivedData The chunks to assemble
     * @param outputPath Where to save the assembled file
     */
    public void assembleFile(ReceivedChunkData receivedData, String outputPath)
            throws IOException {

        System.out.println("\n=== Assembling File ===");
        System.out.println("File: " + receivedData.fileName);
        System.out.println("Expected size: " + receivedData.expectedFileSize + " bytes");
        System.out.println("Total chunks: " + receivedData.totalChunks);

        // Check if we have all chunks
        if (!receivedData.isComplete()) {
            int[] missing = receivedData.getMissingChunks();
            throw new IOException("Cannot assemble: Missing " + missing.length +
                    " chunks! First missing: " + missing[0]);
        }

        // Create output directory if it doesn't exist
        File outputFile = new File(outputPath);
        File parentDir = outputFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        // Write all chunks in order to output file
        try (FileOutputStream fos = new FileOutputStream(outputPath)) {

            for (int i = 0; i < receivedData.totalChunks; i++) {
                byte[] chunk = receivedData.chunks[i];

                if (chunk == null) {
                    throw new IOException("Chunk " + i + " is null!");
                }

                // Write this chunk to file
                fos.write(chunk);

                System.out.println("Wrote chunk " + (i+1) + "/" +
                        receivedData.totalChunks +
                        " (" + chunk.length + " bytes)");
            }

            // Ensure everything is written to disk
            fos.flush();
        }

        // Verify file size matches expected
        File assembled = new File(outputPath);
        long actualSize = assembled.length();

        if (actualSize != receivedData.expectedFileSize) {
            throw new IOException("File size mismatch! Expected: " +
                    receivedData.expectedFileSize +
                    ", Got: " + actualSize);
        }

        System.out.println("\n✅ File assembled successfully!");
        System.out.println("Saved to: " + outputPath);
        System.out.println("Size: " + actualSize + " bytes");
    }
}