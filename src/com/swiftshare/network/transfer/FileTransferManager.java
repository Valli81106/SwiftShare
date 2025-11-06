package com.swiftshare.network.transfer;

import com.swiftshare.models.FileMetadata;
import com.swiftshare.network.core.Message;
import com.swiftshare.network.core.RoomClient;
import java.io.*;

public class FileTransferManager {

    public interface TransferCallback {
        void onProgress(double percent, String speed);
        void onComplete(String fileName);
        void onError(String error);
    }

    private RoomClient client;
    private TransferStats stats;
    private TransferCallback callback;
    private String downloadDirectory;

    public FileTransferManager(RoomClient client) {
        this.client = client;
        this.stats = new TransferStats();
        
        // Create downloads folder in user's home directory
        String userHome = System.getProperty("user.home");
        this.downloadDirectory = userHome + File.separator + "SwiftShare_Downloads";
        
        // Create the directory if it doesn't exist
        File downloadDir = new File(downloadDirectory);
        if (!downloadDir.exists()) {
            downloadDir.mkdirs();
            System.out.println("[FileTransfer] Created download directory: " + downloadDirectory);
        }
    }

    public void setCallback(TransferCallback callback) {
        this.callback = callback;
    }
    
    public void setDownloadDirectory(String path) {
        this.downloadDirectory = path;
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
    
    public String getDownloadDirectory() {
        return downloadDirectory;
    }

    // Send file in background thread
    public void sendFile(FileMetadata metadata, byte[][] chunks) {
        new Thread(() -> {
            try {
                System.out.println("[FileTransfer] Starting transfer: " + metadata.getFileName());
                stats.start(metadata.getFileSize());

                // Send file offer first
                Message offer = new Message(Message.FILE_OFFER,
                        metadata.getFileName(),
                        String.valueOf(metadata.getFileSize()),
                        String.valueOf(metadata.getTotalChunks()),
                        metadata.getFileHash());
                client.sendMessage(offer);

                Thread.sleep(500);

                // Send each chunk
                for (int i = 0; i < chunks.length; i++) {
                    // Send chunk info
                    Message chunkMeta = new Message(Message.CHUNK_META,
                            metadata.getFileName(),
                            String.valueOf(i),
                            String.valueOf(chunks.length));
                    client.sendMessage(chunkMeta);

                    // Send actual data
                    client.getConnection().sendBytes(chunks[i]);

                    // Update progress
                    stats.addBytes(chunks[i].length);

                    if (callback != null) {
                        callback.onProgress(stats.getProgress(), stats.getSpeedString());
                    }

                    System.out.println("[FileTransfer] Sent chunk " + (i+1) + "/" + chunks.length +
                            " (" + String.format("%.1f", stats.getProgress()) + "% @ " +
                            stats.getSpeedString() + ")");

                    Thread.sleep(10);
                }

                // Send completion message
                Message complete = new Message(Message.FILE_COMPLETE,
                        metadata.getFileName(),
                        metadata.getFileHash());
                client.sendMessage(complete);

                System.out.println("[FileTransfer] File sent successfully!");

                if (callback != null) {
                    callback.onComplete(metadata.getFileName());
                }

            } catch (Exception e) {
                System.err.println("[FileTransfer] Error sending file: " + e.getMessage());
                e.printStackTrace();

                if (callback != null) {
                    callback.onError("Transfer failed: " + e.getMessage());
                }
            }
        }).start();
    }

    // Receive file chunks and save to disk
    public void receiveFile(FileMetadata metadata) {
        new Thread(() -> {
            try {
                System.out.println("[FileTransfer] Starting to receive: " + metadata.getFileName());
                stats.start(metadata.getFileSize());

                byte[][] chunks = new byte[metadata.getTotalChunks()][];

                // Receive all chunks
                for (int i = 0; i < metadata.getTotalChunks(); i++) {
                    chunks[i] = client.getConnection().receiveBytes();
                    stats.addBytes(chunks[i].length);

                    if (callback != null) {
                        callback.onProgress(stats.getProgress(), stats.getSpeedString());
                    }

                    System.out.println("[FileTransfer] Received chunk " + (i+1) + "/" + 
                            metadata.getTotalChunks() + " (" + 
                            String.format("%.1f", stats.getProgress()) + "%)");
                }

                // Save the file
                String filePath = downloadDirectory + File.separator + metadata.getFileName();
                saveFile(filePath, chunks);

                System.out.println("[FileTransfer] File saved to: " + filePath);

                if (callback != null) {
                    callback.onComplete(metadata.getFileName());
                }

            } catch (Exception e) {
                System.err.println("[FileTransfer] Error receiving file: " + e.getMessage());
                e.printStackTrace();

                if (callback != null) {
                    callback.onError("Receive failed: " + e.getMessage());
                }
            }
        }).start();
    }

    // Save chunks to a file
    private void saveFile(String filePath, byte[][] chunks) throws IOException {
        File outputFile = new File(filePath);
        
        // If file exists, add number to filename
        if (outputFile.exists()) {
            String name = outputFile.getName();
            String baseName = name.substring(0, name.lastIndexOf('.'));
            String extension = name.substring(name.lastIndexOf('.'));
            
            int counter = 1;
            do {
                filePath = downloadDirectory + File.separator + 
                          baseName + " (" + counter + ")" + extension;
                outputFile = new File(filePath);
                counter++;
            } while (outputFile.exists());
            
            System.out.println("[FileTransfer] File exists, saving as: " + outputFile.getName());
        }

        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            for (byte[] chunk : chunks) {
                fos.write(chunk);
            }
            fos.flush();
        }

        System.out.println("[FileTransfer] File saved successfully: " + outputFile.getAbsolutePath());
    }
}