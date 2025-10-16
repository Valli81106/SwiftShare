package com.swiftshare.models;

public class FileMetadata {
    private String fileName;
    private long fileSize;
    private String fileHash;
    private int totalChunks;
    private String senderId;

    // Main constructor (what we use in networking)
    public FileMetadata(String fileName, long fileSize, String fileHash, int totalChunks) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileHash = fileHash;
        this.totalChunks = totalChunks;
    }

    // Alternative constructor without hash (GUI can use this if they don't have hash yet)
    public FileMetadata(String fileName, long fileSize, int totalChunks) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileHash = null; // Security team will fill this later
        this.totalChunks = totalChunks;
    }

    // Minimal constructor (just filename and size)
    public FileMetadata(String fileName, long fileSize) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileHash = null;
        this.totalChunks = 0; // Will be calculated later
    }

    // Full constructor with sender ID
    public FileMetadata(String fileName, long fileSize, String fileHash, int totalChunks, String senderId) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileHash = fileHash;
        this.totalChunks = totalChunks;
        this.senderId = senderId;
    }

    // ... rest of the existing getters/setters stay the same
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public long getFileSize() {
        return fileSize;
    }
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
    public String getFileHash() {
        return fileHash;
    }
    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }
    public int getTotalChunks() {
        return totalChunks;
    }
    public void setTotalChunks(int totalChunks) {
        this.totalChunks = totalChunks;
    }
    public String getSenderId() {
        return senderId;
    }
    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
    @Override
    public String toString() {
        return "File: " + fileName + ", Size: " + fileSize + " bytes, Chunks: " + totalChunks;
    }
}