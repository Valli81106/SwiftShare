package com.swiftshare.gui.controllers;

import com.swiftshare.models.RoomInfo;
import com.swiftshare.models.PeerInfo;
import com.swiftshare.models.FileMetadata;
import com.swiftshare.network.manager.NetworkManager;
import com.swiftshare.network.manager.NetworkCallback;

import java.time.LocalDateTime;

public class HomeController {
    
    private NetworkManager networkManager;
    
    public HomeController() {
        // Initialize network manager with callback
        networkManager = new NetworkManager(new NetworkCallback() {
            @Override
            public void onRoomCreated(int port) {
                System.out.println("Room created on port: " + port);
            }
            
            @Override
            public void onRoomJoined(String host, int port) {
                System.out.println("Joined room at " + host + ":" + port);
            }
            
            @Override
            public void onPeerConnected(PeerInfo peer) {
                System.out.println("Peer connected: " + peer.getPeerName());
            }
            
            @Override
            public void onPeerDisconnected(PeerInfo peer) {
                System.out.println("Peer disconnected: " + peer.getPeerName());
            }
            
            @Override
            public void onFileOfferReceived(FileMetadata metadata) {
                System.out.println("File offer received: " + metadata.getFileName());
            }
            
            @Override
            public void onTransferProgress(String fileName, double percent, String speed) {
                System.out.println("Transfer progress: " + fileName + " - " + percent + "%");
            }
            
            @Override
            public void onTransferComplete(String fileName) {
                System.out.println("Transfer complete: " + fileName);
            }
            
            @Override
            public void onConnectionLost() {
                System.out.println("Connection lost!");
            }
            
            @Override
            public void onError(String error) {
                System.err.println("Error: " + error);
            }
        });
    }
    
    /**
     * Create a new room
     * @return RoomInfo if successful, null otherwise
     */
    public RoomInfo createRoom(String roomName, String password, int durationMinutes) {
        // Generate a port (you can make this configurable)
        int port = 8000 + (int)(Math.random() * 1000); // Random port 8000-9000
        
        boolean success = networkManager.createRoom(port);
        
        if (success) {
            // Create RoomInfo
            String roomId = "ROOM_" + port;
            LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(durationMinutes);
            RoomInfo roomInfo = new RoomInfo(roomId, roomName, expiryTime);
            roomInfo.setPasswordHash(password); // Store password (should be hashed by security team)
            
            return roomInfo;
        }
        
        return null;
    }
    
    /**
     * Join an existing room
     * @return RoomInfo if successful, null otherwise
     */
    public RoomInfo joinRoom(String roomId, String password) {
        // Parse room ID to get host and port
        // Format: "ROOM_8123" or "192.168.1.5:8123"
        
        String host = "localhost"; // Default to localhost
        int port = 8000;
        
        try {
            // If roomId contains ":", it's in format "host:port"
            if (roomId.contains(":")) {
                String[] parts = roomId.split(":");
                host = parts[0];
                port = Integer.parseInt(parts[1]);
            } else if (roomId.startsWith("ROOM_")) {
                // Extract port from "ROOM_8123" format
                port = Integer.parseInt(roomId.substring(5));
            } else {
                // Try to parse as just a port number
                port = Integer.parseInt(roomId);
            }
        } catch (Exception e) {
            System.err.println("Invalid room ID format: " + roomId);
            return null;
        }
        
        boolean success = networkManager.joinRoom(host, port);
        
        if (success) {
            // Create RoomInfo for joined room
            LocalDateTime expiryTime = LocalDateTime.now().plusHours(1); // Default 1 hour
            RoomInfo roomInfo = new RoomInfo(roomId, "Joined Room", expiryTime);
            
            return roomInfo;
        }
        
        return null;
    }
    
    /**
     * Get network manager instance
     */
    public NetworkManager getNetworkManager() {
        return networkManager;
    }
    
    /**
     * Validate room ID format
     */
    public boolean isValidRoomId(String roomId) {
        if (roomId == null || roomId.isEmpty()) return false;
        
        // Accept formats: "ROOM_8123", "8123", "192.168.1.5:8123"
        return roomId.matches("ROOM_\\d+") || 
               roomId.matches("\\d+") || 
               roomId.matches("\\d+\\.\\d+\\.\\d+\\.\\d+:\\d+") ||
               roomId.matches("localhost:\\d+");
    }
    
    /**
     * Validate password strength
     */
    public boolean isValidPassword(String password) {
        return password != null && password.length() >= 4;
    }
}