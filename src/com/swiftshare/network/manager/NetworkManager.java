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
import java.util.List;

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

    public boolean createRoom(int port) {
        try {
            System.out.println("Creating room on port " + port + "...");

            server = new RoomServer(port);
            roomExpiryTime = System.currentTimeMillis() + DEFAULT_ROOM_DURATION;
            startExpiryCheck();

            server.setCallback(new RoomServer.ServerCallback() {
                @Override
                public void onPeerConnected(PeerConnection peer) {
                    System.out.println("[SERVER] Peer connected: " + peer.getPeerAddress());
                    
                    PeerInfo peerInfo = new PeerInfo(
                            peer.getPeerId(),
                            peer.getPeerAddress(),
                            peer.getPeerPort()
                    );
                    
                    if (callback != null) {
                        callback.onPeerConnected(peerInfo);
                    }
                    
                    // Send current peer list to new peer
                    sendPeerListToNewPeer(peer);
                    
                    // Notify all OTHER peers about the new peer
                    Message newPeerMsg = new Message("PEER_JOINED", 
                        peer.getPeerId(), 
                        peer.getPeerAddress(), 
                        String.valueOf(peer.getPeerPort())
                    );
                    server.broadcastToOthers(peer, newPeerMsg);
                }

                @Override
                public void onPeerDisconnected(PeerConnection peer) {
                    System.out.println("[SERVER] Peer disconnected: " + peer.getPeerAddress());
                    
                    PeerInfo peerInfo = new PeerInfo(
                            peer.getPeerId(),
                            peer.getPeerAddress(),
                            peer.getPeerPort()
                    );
                    
                    if (callback != null) {
                        callback.onPeerDisconnected(peerInfo);
                    }
                    
                    // Notify all peers about disconnection
                    Message peerLeftMsg = new Message("PEER_LEFT", peer.getPeerId());
                    server.broadcast(peerLeftMsg);
                }

                @Override
                public void onMessageReceived(PeerConnection sender, Message message) {
                    handleIncomingMessage(message);
                    // Forward to all other peers
                    server.broadcastToOthers(sender, message);
                }
            });

            server.start();
            isHost = true;
            currentRoomId = "ROOM_" + port;

            if (callback != null) {
                callback.onRoomCreated(port);
            }

            System.out.println("[SERVER] Room created successfully!");
            return true;

        } catch (IOException e) {
            System.err.println("[SERVER] Failed to create room: " + e.getMessage());
            if (callback != null) {
                callback.onError("Failed to create room: " + e.getMessage());
            }
            return false;
        }
    }

    private void sendPeerListToNewPeer(PeerConnection newPeer) {
        try {
            List<PeerConnection> peers = server.getConnectedPeers();
            
            System.out.println("[SERVER] Sending peer list to new peer. Total peers: " + peers.size());
            
            // Send info about each existing peer
            for (PeerConnection existingPeer : peers) {
                if (existingPeer != newPeer) {
                    Message peerInfo = new Message("PEER_JOINED",
                        existingPeer.getPeerId(),
                        existingPeer.getPeerAddress(),
                        String.valueOf(existingPeer.getPeerPort())
                    );
                    newPeer.sendMessage(peerInfo);
                    System.out.println("[SERVER] Told new peer about: " + existingPeer.getPeerAddress());
                }
            }
        } catch (Exception e) {
            System.err.println("[SERVER] Error sending peer list: " + e.getMessage());
        }
    }

    public boolean joinRoom(String host, int port) {
        try {
            System.out.println("[CLIENT] Joining room at " + host + ":" + port + "...");

            client = new RoomClient(new RoomClient.ClientCallback() {
                @Override
                public void onMessageReceived(Message message) {
                    handleIncomingMessage(message);
                }

                @Override
                public void onDisconnected() {
                    System.out.println("[CLIENT] Disconnected from room");
                    if (callback != null) {
                        callback.onConnectionLost();
                    }
                }

                @Override
                public void onError(String error) {
                    System.err.println("[CLIENT] Error: " + error);
                    if (callback != null) {
                        callback.onError(error);
                    }
                }
            });

            boolean connected = client.connect(host, port);

            if (connected) {
                isHost = false;
                currentRoomId = "ROOM_" + host + "_" + port;

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

                // Send join message
                client.sendMessage(new Message(Message.JOIN_ROOM, currentRoomId));

                if (callback != null) {
                    callback.onRoomJoined(host, port);
                }

                System.out.println("[CLIENT] Joined room successfully!");
            }

            return connected;

        } catch (Exception e) {
            System.err.println("[CLIENT] Failed to join room: " + e.getMessage());
            if (callback != null) {
                callback.onError("Failed to join room: " + e.getMessage());
            }
            return false;
        }
    }

    private void handleIncomingMessage(Message message) {
        String type = message.getType();
        System.out.println("[NETWORK] Received message: " + type);

        switch (type) {
            case "PEER_JOINED":
                String peerId = message.getData(0);
                String peerAddress = message.getData(1);
                int peerPort = Integer.parseInt(message.getData(2));
                
                System.out.println("[NETWORK] New peer joined: " + peerAddress);
                
                PeerInfo newPeer = new PeerInfo(peerId, peerAddress, peerPort);
                if (callback != null) {
                    callback.onPeerConnected(newPeer);
                }
                break;

            case "PEER_LEFT":
                String leftPeerId = message.getData(0);
                System.out.println("[NETWORK] Peer left: " + leftPeerId);
                
                // Create a basic PeerInfo for the left peer
                PeerInfo leftPeer = new PeerInfo(leftPeerId, "unknown", 0);
                if (callback != null) {
                    callback.onPeerDisconnected(leftPeer);
                }
                break;

            case Message.JOIN_ROOM:
                System.out.println("[NETWORK] Peer joined the room");
                break;

            case Message.FILE_OFFER:
                String fileName = message.getData(0);
                long fileSize = Long.parseLong(message.getData(1));
                int totalChunks = Integer.parseInt(message.getData(2));
                String fileHash = message.getData(3);

                FileMetadata metadata = new FileMetadata(fileName, fileSize, fileHash, totalChunks);

                System.out.println("[NETWORK] File offer received: " + fileName +
                        " (" + formatFileSize(fileSize) + ")");

                if (callback != null) {
                    callback.onFileOfferReceived(metadata);
                }
                break;

            case Message.FILE_ACCEPT:
                System.out.println("[NETWORK] Peer accepted file transfer");
                break;

            case Message.FILE_REJECT:
                System.out.println("[NETWORK] Peer rejected file transfer");
                if (callback != null) {
                    callback.onError("File transfer rejected by peer");
                }
                break;

            case Message.CHUNK_META:
                System.out.println("[NETWORK] Receiving file chunk...");
                break;

            case Message.FILE_COMPLETE:
                String completedFile = message.getData(0);
                System.out.println("[NETWORK] File transfer completed: " + completedFile);
                if (callback != null) {
                    callback.onTransferComplete(completedFile);
                }
                break;

            case Message.HEARTBEAT:
                break;

            case Message.ERROR:
                String error = message.getData(0);
                System.err.println("[NETWORK] Error from peer: " + error);
                if (callback != null) {
                    callback.onError("Peer error: " + error);
                }
                break;

            default:
                System.out.println("[NETWORK] Unknown message type: " + type);
        }
    }

    public void sendFile(File file, byte[][] chunks, FileMetadata metadata) {
        if (client == null || !client.isConnected()) {
            System.err.println("[NETWORK] Not connected to a room!");
            if (callback != null) {
                callback.onError("Not connected to a room");
            }
            return;
        }

        System.out.println("[NETWORK] Initiating file transfer: " + file.getName());

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

    public void acceptFileOffer(String fileName) {
        if (client != null && client.isConnected()) {
            client.sendMessage(new Message(Message.FILE_ACCEPT, fileName));
            System.out.println("[NETWORK] Accepted file: " + fileName);
        }
    }

    public void rejectFileOffer(String fileName) {
        if (client != null && client.isConnected()) {
            client.sendMessage(new Message(Message.FILE_REJECT, fileName));
            System.out.println("[NETWORK] Rejected file: " + fileName);
        }
    }

    public void disconnect() {
        System.out.println("[NETWORK] Disconnecting...");

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

        System.out.println("[NETWORK] Disconnected");
    }

    private void startExpiryCheck() {
        new Thread(() -> {
            while (isHost && server != null) {
                try {
                    Thread.sleep(60000);

                    if (System.currentTimeMillis() > roomExpiryTime) {
                        System.out.println("[NETWORK] Room expired!");
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

    public long getRemainingTime() {
        if (!isHost) return -1;
        long remaining = roomExpiryTime - System.currentTimeMillis();
        return remaining > 0 ? remaining : 0;
    }

    public String getRemainingTimeString() {
        long ms = getRemainingTime();
        if (ms <= 0) return "Expired";

        long hours = ms / (60 * 60 * 1000);
        long minutes = (ms % (60 * 60 * 1000)) / (60 * 1000);

        return hours + "h " + minutes + "m remaining";
    }

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