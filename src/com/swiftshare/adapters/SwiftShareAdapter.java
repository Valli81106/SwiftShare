package com.swiftshare.adapters;

import com.swiftshare.network.manager.NetworkCallback;
import com.swiftshare.gui.listeners.*;
import com.swiftshare.models.*;
import java.util.*;

/**
 * Unified adapter that bridges all components
 * Converts NetworkCallback to GUI listeners
 */
public class SwiftShareAdapter implements NetworkCallback {

    private List<NetworkEventListener> networkListeners;
    private List<FileTransferListener> fileTransferListeners;
    private List<RoomEventListener> roomListeners;

    // Track ongoing transfers
    private Map<String, TransferState> activeTransfers;

    public SwiftShareAdapter() {
        this.networkListeners = new ArrayList<>();
        this.fileTransferListeners = new ArrayList<>();
        this.roomListeners = new ArrayList<>();
        this.activeTransfers = new HashMap<>();
    }

    // Listener registration methods
    public void addNetworkListener(NetworkEventListener listener) {
        networkListeners.add(listener);
    }

    public void addFileTransferListener(FileTransferListener listener) {
        fileTransferListeners.add(listener);
    }

    public void addRoomListener(RoomEventListener listener) {
        roomListeners.add(listener);
    }

    // NetworkCallback implementation - converts to GUI events

    @Override
    public void onRoomCreated(int port) {
        System.out.println("[Adapter] Room created on port: " + port);
        // Notify network listeners
        for (NetworkEventListener listener : networkListeners) {
            listener.onConnected();
        }
    }

    @Override
    public void onRoomJoined(String host, int port) {
        System.out.println("[Adapter] Joined room at " + host + ":" + port);
        // Notify network listeners
        for (NetworkEventListener listener : networkListeners) {
            listener.onConnected();
        }
    }

    @Override
    public void onPeerConnected(PeerInfo peer) {
        System.out.println("[Adapter] Peer connected: " + peer.getPeerId());
        // Notify room listeners
        for (RoomEventListener listener : roomListeners) {
            listener.onPeerJoined(peer);
        }
    }

    @Override
    public void onPeerDisconnected(PeerInfo peer) {
        System.out.println("[Adapter] Peer disconnected: " + peer.getPeerId());
        // Notify room listeners
        for (RoomEventListener listener : roomListeners) {
            listener.onPeerLeft(peer);
        }
    }

    @Override
    public void onFileOfferReceived(FileMetadata metadata) {
        System.out.println("[Adapter] File offer received: " + metadata.getFileName());

        // Create unique file ID
        String fileId = "file_" + System.currentTimeMillis();

        // Create transfer state
        TransferState state = new TransferState(fileId, metadata.getFileName());
        activeTransfers.put(fileId, state);

        // Notify file transfer listeners
        for (FileTransferListener listener : fileTransferListeners) {
            listener.onTransferStarted(fileId, metadata.getFileName());
        }

        // Notify room listeners
        for (RoomEventListener listener : roomListeners) {
            listener.onFileReceived(metadata);
        }
    }

    @Override
    public void onTransferProgress(String fileName, double percent, String speed) {
        System.out.println("[Adapter] Transfer progress: " + fileName + " - " + percent + "%");

        // Find the transfer by filename
        String fileId = findFileIdByName(fileName);
        if (fileId == null) {
            fileId = "file_" + fileName.hashCode();
            activeTransfers.put(fileId, new TransferState(fileId, fileName));
        }

        // Create TransferStatus
        TransferStatus status = new TransferStatus();
        status.setState(TransferStatus.State.IN_PROGRESS);
        status.setSpeedMBps(parseSpeed(speed));

        // Calculate chunks from percentage (approximate)
        int estimatedTotalChunks = 100;
        int chunksTransferred = (int) (percent * estimatedTotalChunks / 100);
        status.setChunksTransferred(chunksTransferred);
        status.setTotalChunks(estimatedTotalChunks);

        // Update state
        TransferState state = activeTransfers.get(fileId);
        if (state != null) {
            state.status = status;
        }

        // Notify listeners
        for (FileTransferListener listener : fileTransferListeners) {
            listener.onProgressUpdate(status);
        }

        for (RoomEventListener listener : roomListeners) {
            listener.onTransferProgress(status);
        }
    }

    @Override
    public void onTransferComplete(String fileName) {
        System.out.println("[Adapter] Transfer complete: " + fileName);

        String fileId = findFileIdByName(fileName);
        if (fileId != null) {
            // Update state
            TransferState state = activeTransfers.get(fileId);
            if (state != null) {
                state.status.setState(TransferStatus.State.COMPLETED);
            }

            // Notify listeners
            for (FileTransferListener listener : fileTransferListeners) {
                listener.onTransferCompleted(fileId);
            }

            // Clean up
            activeTransfers.remove(fileId);
        }
    }

    @Override
    public void onConnectionLost() {
        System.out.println("[Adapter] Connection lost");

        // Notify network listeners
        for (NetworkEventListener listener : networkListeners) {
            listener.onDisconnected();
        }

        // Notify room listeners
        for (RoomEventListener listener : roomListeners) {
            listener.onRoomClosed("Connection lost");
        }

        // Cancel all active transfers
        for (Map.Entry<String, TransferState> entry : activeTransfers.entrySet()) {
            for (FileTransferListener listener : fileTransferListeners) {
                listener.onTransferCancelled(entry.getKey());
            }
        }
        activeTransfers.clear();
    }

    @Override
    public void onError(String error) {
        System.err.println("[Adapter] Error: " + error);

        // Notify network listeners
        for (NetworkEventListener listener : networkListeners) {
            listener.onNetworkError(error);
        }

        // Notify room listeners
        for (RoomEventListener listener : roomListeners) {
            listener.onError(error);
        }
    }

    // Helper methods

    private String findFileIdByName(String fileName) {
        for (Map.Entry<String, TransferState> entry : activeTransfers.entrySet()) {
            if (entry.getValue().fileName.equals(fileName)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private double parseSpeed(String speed) {
        try {
            // Parse "2.5 MB/s" to 2.5
            String[] parts = speed.split(" ");
            return Double.parseDouble(parts[0]);
        } catch (Exception e) {
            return 0.0;
        }
    }

    // Inner class to track transfer state
    private static class TransferState {
        String fileId;
        String fileName;
        TransferStatus status;

        TransferState(String fileId, String fileName) {
            this.fileId = fileId;
            this.fileName = fileName;
            this.status = new TransferStatus();
            this.status.setState(TransferStatus.State.WAITING);
        }
    }
}