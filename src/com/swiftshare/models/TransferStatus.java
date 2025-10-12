package com.swiftshare.models;

// keeps track of file transfer progress
public class TransferStatus {

    public enum State {
        WAITING,
        IN_PROGRESS,
        PAUSED,
        COMPLETED,
        FAILED,
        CANCELLED
    }

    private State state;
    private int chunksTransferred;
    private int totalChunks;
    private double speedMBps;
    private long bytesTransferred;
    private String errorMessage;

    public TransferStatus() {
        this.state = State.WAITING;
        this.chunksTransferred = 0;
        this.totalChunks = 0;
        this.speedMBps = 0.0;
        this.bytesTransferred = 0;
    }
    // calculate percentage done
    public double getProgress() {
        if (totalChunks == 0) return 0.0;
        return (chunksTransferred * 100.0) / totalChunks;
    }
    public State getState() {
        return state;
    }
    public void setState(State state) {
        this.state = state;
    }
    public int getChunksTransferred() {
        return chunksTransferred;
    }
    public void setChunksTransferred(int chunks) {
        this.chunksTransferred = chunks;
    }
    public int getTotalChunks() {
        return totalChunks;
    }
    public void setTotalChunks(int chunks) {
        this.totalChunks = chunks;
    }
    public double getSpeedMBps() {
        return speedMBps;
    }
    public void setSpeedMBps(double speed) {
        this.speedMBps = speed;
    }
    public long getBytesTransferred() {
        return bytesTransferred;
    }
    public void setBytesTransferred(long bytes) {
        this.bytesTransferred = bytes;
    }
    public String getErrorMessage() {
        return errorMessage;
    }
    public void setErrorMessage(String msg) {
        this.errorMessage = msg;
    }
}