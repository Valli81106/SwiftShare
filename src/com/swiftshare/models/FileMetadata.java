package com.swiftshare.models;

// stores basic file info for transfers
public class FileMetadata {
    private String fileName;
    private long fileSize;
    private String fileHash;
    private int totalChunks;
    private String senderId;

    public FileMetadata(String fileName, long fileSize, String fileHash, int totalChunks) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileHash = fileHash;
        this.totalChunks = totalChunks;
    }
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