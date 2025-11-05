
package com.swiftshare.gui.controllers;

import com.swiftshare.models.RoomInfo;
import com.swiftshare.models.PeerInfo;
import com.swiftshare.models.FileMetadata;
import com.swiftshare.network.manager.NetworkManager;
import com.swiftshare.network.manager.NetworkCallback;
import com.swiftshare.network.discovery.NetworkDiscovery;

import java.net.*;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

public class HomeController {

    private NetworkManager networkManager;
    private NetworkDiscovery discovery;

    public HomeController() {
        discovery = new NetworkDiscovery();
        
        // Initialize network manager with callback
        networkManager = new NetworkManager(new NetworkCallback() {
            @Override
            public void onRoomCreated(int port) {
                System.out.println("Room created on port: " + port);
                // Announce the room on the network
                announceRoom(port);
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
     * Create a new room and announce it on the network
     */
    public RoomInfo createRoom(String roomName, String password, int durationMinutes) {
        // Generate a port (you can make this configurable)
        int port = 8000 + (int)(Math.random() * 1000); // Random port 8000-9000

        boolean success = networkManager.createRoom(port);

        if (success) {
            // Create RoomInfo
            String roomId = "ROOM_" + port;
            RoomInfo roomInfo = new RoomInfo(roomId, port);
            roomInfo.setPasswordHash(password); // Store password (should be hashed by security team)

            // Get and display the host IP for others to connect
            String hostIP = getLocalIPAddress();
            System.out.println("Room created! Share this information:");
            System.out.println("Room ID: " + roomId);
            System.out.println("Host IP: " + hostIP + ":" + port);
            System.out.println("Or clients can use: " + hostIP + ":" + port);

            return roomInfo;
        }

        return null;
    }

    /**
     * Join an existing room with improved host discovery
     */
    public RoomInfo joinRoom(String roomId, String password) {
        String host = null;
        int port = 8000;

        try {
            if (roomId.contains(":")) {
                // Format: "192.168.1.5:8123" or "localhost:8123"
                String[] parts = roomId.split(":");
                host = parts[0];
                port = Integer.parseInt(parts[1]);
                System.out.println("Parsed host:port format - " + host + ":" + port);
            } else if (roomId.startsWith("ROOM_")) {
                // Format: "ROOM_8123" - need to discover the host
                port = Integer.parseInt(roomId.substring(5));
                System.out.println("Discovering host for room port: " + port);
                host = discoverRoomHost(port);
                if (host == null) {
                    System.err.println("Could not find host for room: " + roomId);
                    System.err.println("Try using the format: IP_ADDRESS:" + port);
                    return null;
                }
            } else {
                // Just a port number - need to discover the host
                try {
                    port = Integer.parseInt(roomId);
                    System.out.println("Discovering host for port: " + port);
                    host = discoverRoomHost(port);
                    if (host == null) {
                        System.err.println("Could not find host for port: " + port);
                        System.err.println("Try using the format: IP_ADDRESS:" + port);
                        return null;
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Invalid room ID format: " + roomId);
                    return null;
                }
            }
        } catch (Exception e) {
            System.err.println("Invalid room ID format: " + roomId);
            return null;
        }

        System.out.println("Attempting to connect to: " + host + ":" + port);
        boolean success = networkManager.joinRoom(host, port);

        if (success) {
            // Create RoomInfo for joined room
            RoomInfo roomInfo = new RoomInfo(roomId, port);
            return roomInfo;
        } else {
            System.err.println("Failed to connect to " + host + ":" + port);
            System.err.println("Please verify:");
            System.err.println("1. The host IP address is correct");
            System.err.println("2. The port number is correct"); 
            System.err.println("3. Both devices are on the same network");
            System.err.println("4. No firewall is blocking the connection");
        }

        return null;
    }

    /**
     * Discover room host using multiple methods
     */
    private String discoverRoomHost(int port) {
        System.out.println("Starting room discovery for port " + port + "...");
        
        // Method 1: Network discovery (broadcast)
        try {
            System.out.println("Listening for room announcements...");
            String result = discovery.listen(5000); // 5 second timeout
            
            if (result != null && result.contains(":")) {
                String[] parts = result.split(":");
                int discoveredPort = Integer.parseInt(parts[1]);
                if (discoveredPort == port) {
                    System.out.println("Found room via network discovery: " + parts[0]);
                    return parts[0];
                }
            }
        } catch (Exception e) {
            System.err.println("Network discovery failed: " + e.getMessage());
        }
        
        // Method 2: Try localhost first
        System.out.println("Trying localhost...");
        if (testConnection("localhost", port)) {
            System.out.println("Found room on localhost");
            return "localhost";
        }
        
        // Method 3: Scan local network
        System.out.println("Scanning local network...");
        String localHost = scanLocalNetwork(port);
        if (localHost != null) {
            System.out.println("Found room on local network: " + localHost);
            return localHost;
        }
        
        System.err.println("Room discovery failed for port " + port);
        return null;
    }

    /**
     *