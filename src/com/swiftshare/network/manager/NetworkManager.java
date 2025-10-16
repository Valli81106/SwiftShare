package com.swiftshare.network.manager;

import com.swiftshare.models.FileMetadata;
import com.swiftshare.models.PeerInfo;
import com.swiftshare.network.core.Message;
import com.swiftshare.network.core.PeerConnection;
import com.swiftshare.network.core.RoomClient;
import com.swiftshare.network.core.RoomServer;
import com.swiftshare.network.transfer.FileTransferManager;

import java.io.File;
import java.io.IOException;

// main API for GUI and other teams
public class NetworkManager {
    private RoomServer server;
    private RoomClient client;
    private FileTransferManager transferManager;
    private NetworkCallback callback;
    private String currentRoomId;
    private boolean isHost;
    private long roomExpiryTime;
    private static final long DEFAULT_ROOM_DURATION = 24 * 60 * 60 * 1000;

    public NetworkManager(NetworkCallback callback) {
        this.callback = callback;
        this.isHost = false;
    }

    // create a new room (become host)
    public boolean createRoom(int port) {
        try {
            System.out.println("Creating room on port " + port + "...");

            server = new RoomServer(port);

            // Set expiry time
            roomExpiryTime = System.currentTimeMillis() + DEFAULT_ROOM_DURATION;

            // Start expiry check in background
            startExpiryCheck();

            // setup callbacks for server events
            server.setCallback(new RoomServer.ServerCallback() {
                @Override
                public void onPeerConnected(PeerConnection peer) {
                    System.out.println("Peer connected: " + peer.getPeerAddress());
                    PeerInfo peerInfo = new PeerInfo(
                            peer.getPeerId(),
                            peer.getPeerAddress(),
                            peer.getPeerPort()
                    );
                    if (callback != null) {
                        callback.onPeerConnected(peerInfo);
                    }
                }

                @Override
                public void onPeerDisconnected(PeerConnection peer) {
                    System.out.println("Peer disconnected: " + peer.getPeerAddress());
                    PeerInfo peerInfo = new PeerInfo(
                            peer.getPeerId(),
                            peer.getPeerAddress(),
                            peer.getPeerPort()
                    );
                    if (callback != null) {
                        callback.onPeerDisconnected(peerInfo);
                    }
                }

                @Override
                public void onMessageReceived(PeerConnection sender, Message message) {
                    handleIncomingMessage(message);
                }
            });

            server.start();
            isHost = true;
            currentRoomId = "ROOM_" + port;

            if (callback != null) {
                callback.onRoomCreated(port);
            }

            System.out.println("Room created successfully!");
            return true;

        } catch (IOException e) {
            System.err.println("Failed to create room: " + e.getMessage());
            if (callback != null) {
                callback.onError("Failed to create room: " + e.getMessage());
            }
            return false;
        }
    }
    public boolean createRoom(int port, String password) {
        boolean success = createRoom(port);
        if (success && password != null && !password.isEmpty()) {
            System.out.println("Room created with password protection");
        }
        return success;
    }
    // join an existing room
    public boolean joinRoom(String host, int port) {
        try {
            System.out.println("Joining room at " + host + ":" + port + "...");

            client = new RoomClient(new RoomClient.ClientCallback() {
                @Override
                public void onMessageReceived(Message message) {
                    handleIncomingMessage(message);
                }

                @Override
                public void onDisconnected() {
                    System.out.println("Disconnected from room");
                    if (callback != null) {
                        callback.onConnectionLost();
                    }
                }

                @Override
                public void onError(String error) {
                    System.err.println("Error: " + error);
                    if (callback != null) {
                        callback.onError(error);
                    }
                }
            });

            boolean connected = client.connect(host, port);

            if (connected) {
                isHost = false;
                currentRoomId = "ROOM_" + host + "_" + port;

                // setup file transfer manager
                transferManager = new FileTransferManager(client);
                transferManager.setCallback(new FileTransferManager.TransferCallback() {
                    @Override
                    public void onProgress(double percent, String speed) {
                        if (callback != null) {
                            callback.onTransferProgress("current_file", percent, speed);
                        }
                    }

                    @Override
                    public void onComplete(String fileName) {
                        if (callback != null) {
                            callback.onTransferComplete(fileName);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        if (callback != null) {
                            callback.onError(error);
                        }
                    }
                });

                // send join message
                client.sendMessage(new Message(Message.JOIN_ROOM, currentRoomId));

                if (callback != null) {
                    callback.onRoomJoined(host, port);
                }

                System.out.println("Joined room successfully!");
            }

            return connected;

        } catch (Exception e) {
            System.err.println("Failed to join room: " + e.getMessage());
            if (callback != null) {
                callback.onError("Failed to join room: " + e.getMessage());
            }
            return false;
        }
    }
    public boolean joinRoom(String host, int port, String password) {
        // Send password when joining
        boolean connected = joinRoom(host, port);
        if (connected && password != null) {
            // Send password verification message
            client.sendMessage(new Message("PASSWORD_CHECK", password));
        }
        return connected;
    }
    // send a file to all peers
    public void sendFile(File file, byte[][] chunks, FileMetadata metadata) {
        if (client == null || !client.isConnected()) {
            System.err.println("Not connected to a room!");
            if (callback != null) {
                callback.onError("Not connected to a room");
            }
            return;
        }

        System.out.println("Initiating file transfer: " + file.getName());

        if (transferManager == null) {
            transferManager = new FileTransferManager(client);
            transferManager.setCallback(new FileTransferManager.TransferCallback() {
                @Override
                public void onProgress(double percent, String speed) {
                    if (callback != null) {
                        callback.onTransferProgress(file.getName(), percent, speed);
                    }
                }

                @Override
                public void onComplete(String fileName) {
                    if (callback != null) {
                        callback.onTransferComplete(fileName);
                    }
                }

                @Override
                public void onError(String error) {
                    if (callback != null) {
                        callback.onError(error);
                    }
                }
            });
        }

        transferManager.sendFile(metadata, chunks);
    }

    // handle messages from other peers
    private void handleIncomingMessage(Message message) {
        String type = message.getType();

        switch (type) {
            case Message.JOIN_ROOM:
                System.out.println("Peer joined the room");
                break;

            case Message.FILE_OFFER:
                // parse file info
                String fileName = message.getData(0);
                long fileSize = Long.parseLong(message.getData(1));
                int totalChunks = Integer.parseInt(message.getData(2));
                String fileHash = message.getData(3);

                FileMetadata metadata = new FileMetadata(fileName, fileSize, fileHash, totalChunks);

                System.out.println("File offer received: " + fileName +
                        " (" + formatFileSize(fileSize) + ")");

                if (callback != null) {
                    callback.onFileOfferReceived(metadata);
                }
                break;

            case Message.FILE_ACCEPT:
                System.out.println("Peer accepted file transfer");
                break;

            case Message.FILE_REJECT:
                System.out.println("Peer rejected file transfer");
                if (callback != null) {
                    callback.onError("File transfer rejected by peer");
                }
                break;

            case Message.CHUNK_META:
                System.out.println("Receiving file chunk...");
                break;

            case Message.FILE_COMPLETE:
                String completedFile = message.getData(0);
                System.out.println("File transfer completed: " + completedFile);
                if (callback != null) {
                    callback.onTransferComplete(completedFile);
                }
                break;

            case Message.HEARTBEAT:
                // just keepalive, nothing to do
                break;

            case Message.ERROR:
                String error = message.getData(0);
                System.err.println("Error from peer: " + error);
                if (callback != null) {
                    callback.onError("Peer error: " + error);
                }
                break;

            default:
                System.out.println("Unknown message type: " + type);
        }
    }

    // accept a file from another peer
    public void acceptFileOffer(String fileName) {
        if (client != null && client.isConnected()) {
            client.sendMessage(new Message(Message.FILE_ACCEPT, fileName));
            System.out.println("Accepted file: " + fileName);
        }
    }

    // reject a file offer
    public void rejectFileOffer(String fileName) {
        if (client != null && client.isConnected()) {
            client.sendMessage(new Message(Message.FILE_REJECT, fileName));
            System.out.println("Rejected file: " + fileName);
        }
    }

    // disconnect and cleanup
    public void disconnect() {
        System.out.println("Disconnecting...");

        if (client != null) {
            client.sendMessage(new Message(Message.LEAVE_ROOM));
            client.disconnect();
            client = null;
        }

        if (server != null) {
            server.stop();
            server = null;
        }

        if (transferManager != null) {
            transferManager = null;
        }

        isHost = false;
        currentRoomId = null;

        System.out.println("Disconnected");
    }
    // Add after disconnect() method

    // start checking if room expired
    private void startExpiryCheck() {
        new Thread(() -> {
            while (isHost && server != null) {
                try {
                    Thread.sleep(60000); // check every minute

                    if (System.currentTimeMillis() > roomExpiryTime) {
                        System.out.println("Room expired!");
                        if (callback != null) {
                            callback.onError("Room expired after 24 hours");
                        }
                        disconnect();
                        break;
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }
        }).start();
    }

    // GUI can call this to get remaining time
    public long getRemainingTime() {
        if (!isHost) return -1;
        long remaining = roomExpiryTime - System.currentTimeMillis();
        return remaining > 0 ? remaining : 0;
    }

    // GUI can call this to format time nicely
    public String getRemainingTimeString() {
        long ms = getRemainingTime();
        if (ms <= 0) return "Expired";

        long hours = ms / (60 * 60 * 1000);
        long minutes = (ms % (60 * 60 * 1000)) / (60 * 1000);

        return hours + "h " + minutes + "m remaining";
    }

    // Allow custom duration (optional)
    public boolean createRoom(int port, long durationMs) {
        boolean success = createRoom(port);
        if (success) {
            roomExpiryTime = System.currentTimeMillis() + durationMs;
        }
        return success;
    }

    public boolean isConnected() {
        if (isHost) {
            return server != null;
        } else {
            return client != null && client.isConnected();
        }
    }

    public String getCurrentRoomId() {
        return currentRoomId;
    }

    public boolean isHost() {
        return isHost;
    }

    public int getConnectedPeerCount() {
        if (isHost && server != null) {
            return server.getConnectedPeerCount();
        }
        return 0;
    }

    // helper to format file sizes nicely
    private String formatFileSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", bytes / (1024.0 * 1024.0));
        } else {
            return String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0));
        }
    }
}