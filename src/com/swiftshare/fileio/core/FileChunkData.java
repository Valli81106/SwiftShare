package com.swiftshare.fileio.core;

/**
 * Data structure to hold all information about chunked file
 */
public class FileChunkData {
    public String fileName;        // "photo.jpg"
    public long fileSize;          // 1048576 (1MB in bytes)
    public int totalChunks;        // Number of chunks created
    public byte[][] chunks;        // Array of byte arrays - each is one chunk

    // Constructor
    public FileChunkData() {
    }

    // Helper method to print info
    public void printInfo() {
        System.out.println("File: " + fileName);
        System.out.println("Size: " + fileSize + " bytes");
        System.out.println("Chunks: " + totalChunks);
    }
}