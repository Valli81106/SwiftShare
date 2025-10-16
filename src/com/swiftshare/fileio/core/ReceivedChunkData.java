package com.swiftshare.fileio.core;

/**
 * Data structure for received chunks (RECEIVER side)
 */
public class ReceivedChunkData {
    public String fileName;
    public long expectedFileSize;
    public int totalChunks;
    public byte[][] chunks;           // Stores chunk data
    public boolean[] chunkReceived;   // Tracks which chunks we have
    
    /**
     * Constructor - initializes arrays
     */
    public ReceivedChunkData(String fileName, long fileSize, int totalChunks) {
        this.fileName = fileName;
        this.expectedFileSize = fileSize;
        this.totalChunks = totalChunks;
        this.chunks = new byte[totalChunks][];
        this.chunkReceived = new boolean[totalChunks];
    }
    
    /**
     * Called when a chunk arrives from network
     * @param chunkNumber Which chunk this is (0-indexed)
     * @param chunkData The actual chunk data
     */
    public void addChunk(int chunkNumber, byte[] chunkData) {
        if (chunkNumber >= 0 && chunkNumber < totalChunks) {
            chunks[chunkNumber] = chunkData;
            chunkReceived[chunkNumber] = true;
            System.out.println("Stored chunk " + (chunkNumber + 1) + "/" + totalChunks);
        } else {
            System.err.println("Invalid chunk number: " + chunkNumber);
        }
    }
    
    /**
     * Calculate how much of the file we've received
     * @return Progress as percentage (0.0 to 100.0)
     */
    public double getProgress() {
        int received = 0;
        for (boolean hasChunk : chunkReceived) {
            if (hasChunk) received++;
        }
        return (received * 100.0) / totalChunks;
    }
    
    /**
     * Check if we have all chunks
     * @return true if all chunks received
     */
    public boolean isComplete() {
        for (boolean hasChunk : chunkReceived) {
            if (!hasChunk) return false;
        }
        return true;
    }
    
    /**
     * Get list of missing chunk numbers
     * @return Array of missing chunk indices
     */
    public int[] getMissingChunks() {
        // Count missing chunks
        int missingCount = 0;
        for (boolean hasChunk : chunkReceived) {
            if (!hasChunk) missingCount++;
        }
        
        // Create array of missing indices
        int[] missing = new int[missingCount];
        int index = 0;
        for (int i = 0; i < totalChunks; i++) {
            if (!chunkReceived[i]) {
                missing[index++] = i;
            }
        }
        
        return missing;
    }
}
