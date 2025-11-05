package com.swiftshare.fileio.core;

import java.io.*;

/**
 * Handles splitting files into chunks (SENDER side)
 */
public class FileChunkManager {

    // Chunk size: 64KB (good balance)
    public static final int CHUNK_SIZE = 64 * 1024;

    /**
     * Split a file into chunks
     * @param filePath Path to the file to chunk
     * @return FileChunkData containing all chunks
     */
    public FileChunkData chunkFile(String filePath) throws IOException {
        return chunkFile(filePath, null);
    }

    /**
     * Split a file into chunks with progress updates
     * @param filePath Path to the file to chunk
     * @param listener Listener for progress updates (can be null)
     * @return FileChunkData containing all chunks
     */
    public FileChunkData chunkFile(String filePath, ChunkProgressListener listener)
            throws IOException {

        File file = new File(filePath);

        // Validate file exists
        if (!file.exists()) {
            throw new IOException("File not found: " + filePath);
        }

        // Validate not a directory
        if (file.isDirectory()) {
            throw new IOException("Cannot chunk a directory: " + filePath);
        }

        // Validate file is readable
        if (!file.canRead()) {
            throw new IOException("No read permission for: " + filePath);
        }

        // Calculate file size and number of chunks needed
        long fileSize = file.length();

        if (fileSize == 0) {
            throw new IOException("Cannot chunk empty file: " + filePath);
        }

        int totalChunks = (int) Math.ceil((double) fileSize / CHUNK_SIZE);

        System.out.println("File: " + file.getName());
        System.out.println("Size: " + fileSize + " bytes");
        System.out.println("Chunks to create: " + totalChunks);
        System.out.println();

        // Create container for chunk data
        FileChunkData chunkData = new FileChunkData();
        chunkData.fileName = file.getName();
        chunkData.fileSize = fileSize;
        chunkData.totalChunks = totalChunks;
        chunkData.chunks = new byte[totalChunks][];

        // Read file and split into chunks
        try (FileInputStream fis = new FileInputStream(file)) {

            for (int i = 0; i < totalChunks; i++) {
                // Calculate this chunk's size
                int currentChunkSize = (int) Math.min(CHUNK_SIZE,
                        fileSize - (i * CHUNK_SIZE));

                // Create array for this chunk
                byte[] chunkBytes = new byte[currentChunkSize];

                // Read from file into chunk array
                int totalBytesRead = 0;
                while (totalBytesRead < currentChunkSize) {
                    int bytesRead = fis.read(chunkBytes, totalBytesRead,
                            currentChunkSize - totalBytesRead);

                    if (bytesRead == -1) {
                        throw new IOException("Unexpected end of file");
                    }

                    totalBytesRead += bytesRead;
                }

                // Store the chunk
                chunkData.chunks[i] = chunkBytes;

                System.out.println("Created chunk " + (i+1) + "/" + totalChunks +
                        " (" + currentChunkSize + " bytes)");

                // Notify listener if provided
                if (listener != null) {
                    listener.onChunkProcessed(i + 1, totalChunks);
                }
            }
        }

        System.out.println("\n✅ Chunking complete!");
        return chunkData;
    }
}