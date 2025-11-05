package com.swiftshare.network.transfer;

// calculates transfer speed and progress
public class TransferStats {
    private long startTime;
    private long bytesTransferred;
    private long totalBytes;

    public void start(long totalBytes) {
        this.startTime = System.currentTimeMillis();
        this.bytesTransferred = 0;
        this.totalBytes = totalBytes;
    }

    public void addBytes(int bytes) {
        bytesTransferred += bytes;
    }

    public double getProgress() {
        if (totalBytes == 0) return 0;
        return (bytesTransferred * 100.0) / totalBytes;
    }

    public double getSpeedMBps() {
        long elapsed = System.currentTimeMillis() - startTime;
        if (elapsed == 0) return 0;

        double seconds = elapsed / 1000.0;
        double megabytes = bytesTransferred / (1024.0 * 1024.0);
        return megabytes / seconds;
    }

    public String getSpeedString() {
        double speed = getSpeedMBps();

        if (speed < 1) {
            return String.format("%.2f KB/s", speed * 1024);
        }
        return String.format("%.2f MB/s", speed);
    }

    public long getRemainingTime() {
        if (bytesTransferred == 0) return -1;

        long elapsed = System.currentTimeMillis() - startTime;
        long remaining = totalBytes - bytesTransferred;

        return (elapsed * remaining) / bytesTransferred;
    }

    public String getRemainingTimeString() {
        long ms = getRemainingTime();
        if (ms < 0) return "Calculating...";

        long seconds = ms / 1000;
        if (seconds < 60) return seconds + " seconds";

        long minutes = seconds / 60;
        if (minutes < 60) return minutes + " minutes";

        long hours = minutes / 60;
        return hours + " hours";
    }
}