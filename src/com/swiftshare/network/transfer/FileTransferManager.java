package com.swiftshare.network.transfer;

import com.swiftshare.models.FileMetadata;
import com.swiftshare.network.core.Message;
import com.swiftshare.network.core.RoomClient;

import java.io.*;

// handles sending and receiving files
public class FileTransferManager {

    public interface TransferCallback {
        void onProgress(double percent, String speed);
        void onComplete(String fileName);
        void onError(String error);
    }

    private RoomClient client;
    private TransferStats stats;
    private TransferCallback callback;

    public FileTransferManager(RoomClient client) {
        this.client = client;
        this.stats = new TransferStats();
    }

    public void setCallback(TransferCallback callback) {
        this.callback = callback;
    }

    // send file in background thread
    public void sendFile(FileMetadata metadata, byte[][] chunks) {
        new Thread(() -> {
            try {
                System.out.println("Starting transfer: " + metadata.getFileName());
                stats.start(metadata.getFileSize());

                // send file offer first
                Message offer = new Message(Message.FILE_OFFER,
                        metadata.getFileName(),
                        String.valueOf(metadata.getFileSize()),
                        String.valueOf(metadata.getTotalChunks()),
                        metadata.getFileHash()
                );
                client.sendMessage(offer);

                Thread.sleep(500); // give them time to accept

                // send each chunk
                for (int i = 0; i < chunks.length; i++) {
                    // send chunk info
                    Message chunkMeta = new Message(Message.CHUNK_META,
                            metadata.getFileName(),
                            String.valueOf(i),
                            String.valueOf(chunks.length)
                    );
                    client.sendMessage(chunkMeta);

                    // send actual data
                    client.getConnection().sendBytes(chunks[i]);

                    // update progress
                    stats.addBytes(chunks[i].length);

                    if (callback != null) {
                        callback.onProgress(stats.getProgress(), stats.getSpeedString());
                    }

                    System.out.println("Sent chunk " + (i+1) + "/" + chunks.length +
                            " (" + String.format("%.1f", stats.getProgress()) + "% @ " +
                            stats.getSpeedString() + ")");

                    Thread.sleep(10);
                }

                // send completion message
                Message complete = new Message(Message.FILE_COMPLETE,
                        metadata.getFileName(),
                        metadata.getFileHash()
                );
                client.sendMessage(complete);

                System.out.println("File sent successfully!");

                if (callback != null) {
                    callback.onComplete(metadata.getFileName());
                }

            } catch (Exception e) {
                System.err.println("Error sending file: " + e.getMessage());
                e.printStackTrace();

                if (callback != null) {
                    callback.onError("Transfer failed: " + e.getMessage());
                }
            }
        }).start();
    }

    // receive file chunks
    public byte[][] receiveFile(int totalChunks) throws IOException {
        System.out.println("Starting to receive file chunks...");
        stats.start(totalChunks * 64 * 1024);

        byte[][] chunks = new byte[totalChunks][];

        for (int i = 0; i < totalChunks; i++) {
            chunks[i] = client.getConnection().receiveBytes();
            stats.addBytes(chunks[i].length);

            if (callback != null) {
                callback.onProgress(stats.getProgress(), stats.getSpeedString());
            }

            System.out.println("Received chunk " + (i+1) + "/" + totalChunks +
                    " (" + String.format("%.1f", stats.getProgress()) + "%)");
        }

        System.out.println("All chunks received!");
        return chunks;
    }
}