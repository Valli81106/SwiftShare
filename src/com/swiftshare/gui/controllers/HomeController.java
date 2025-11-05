package com.swiftshare.gui.controllers;

import com.swiftshare.models.FileMetadata;
import com.swiftshare.models.PeerInfo;
import com.swiftshare.models.RoomInfo;
import com.swiftshare.network.discovery.NetworkDiscovery;
import com.swiftshare.network.manager.NetworkCallback;
import com.swiftshare.network.manager.NetworkManager;
import java.io.*;
import java.net.*;
import java.util.*;

public class HomeController {

    private NetworkManager networkManager;
    private NetworkDiscovery discovery;
    private NetworkCallback guiCallback;

    public HomeController() {
        discovery = new NetworkDiscovery();
    }

    /**
     * Set the GUI callback that will receive all network events
     */
    public void setGuiCallback(NetworkCallback callback) {
        this.guiCallback = callback;
        
        // Create network manager with the GUI callback
        networkManager = new NetworkManager(new NetworkCallback() {
            @Override
            public void onRoomCreated(int port) {
                System.out.println("Room created on port: " + port);
                announceRoom(port);
                if (guiCallback != null) {
                    guiCallback.onRoomCreated(port);
                }
            }

            @Override
            public void onRoomJoined(String host, int port) {
                System.out.println("Joined room at " + host + ":" + port);
                if (guiCallback != null) {
                    guiCallback.onRoomJoined(host, port);
                }
            }

            @Override
            public void onPeerConnected(PeerInfo peer) {
                System.out.println("Peer connected: " + peer.getPeerName());
                if (guiCallback != null) {
                    guiCallback.onPeerConnected(peer);
                }
            }

            @Override
            public void onPeerDisconnected(PeerInfo peer) {
                System.out.println("Peer disconnected: " + peer.getPeerName());
                if (guiCallback != null) {
                    guiCallback.onPeerDisconnected(peer);
                }
            }

            @Override
            public void onFileOfferReceived(FileMetadata metadata) {
                System.out.println("File offer received: " + metadata.getFileName());
                if (guiCallback != null) {
                    guiCallback.onFileOfferReceived(metadata);
                }
            }

            @Override
            public void onTransferProgress(String fileName, double percent, String speed) {
                System.out.println("Transfer progress: " + fileName + " - " + percent + "%");
                if (guiCallback != null) {
                    guiCallback.onTransferProgress(fileName, percent, speed);
                }
            }

            @Override
            public void onTransferComplete(String fileName) {
                System.out.println("Transfer complete: " + fileName);
                if (guiCallback != null) {
                    guiCallback.onTransferComplete(fileName);
                }
            }

            @Override
            public void onConnectionLost() {
                System.out.println("Connection lost!");
                if (guiCallback != null) {
                    guiCallback.onConnectionLost();
                }
            }

            @Override
            public void onError(String error) {
                System.err.println("Error: " + error);
                if (guiCallback != null) {
                    guiCallback.onError(error);
                }
            }
        });
    }

    /**
     * Get the network manager instance
     */
    public NetworkManager getNetworkManager() {
        return networkManager;
    }

    /**
     * Create a new room and announce it on the network
     */
    public RoomInfo createRoom(String roomName, String password, int durationMinutes) {
        if (networkManager == null) {
            System.err.println("NetworkManager not initialized! Call setGuiCallback first.");
            return null;
        }

        int port = 8000 + (int)(Math.random() * 1000);

        boolean success = networkManager.createRoom(port);

        if (success) {
            String roomId = "ROOM_" + port;
            RoomInfo roomInfo = new RoomInfo(roomId, port);
            roomInfo.setPasswordHash(password);

            String hostIP = getLocalIPAddress();
            System.out.println("Room created! Share this information:");
            System.out.println("Room ID: " + roomId);
            System.out.println("Host IP: " + hostIP + ":" + port);

            return roomInfo;
        }

        return null;
    }

    /**
     * Join an existing room
     */
    public RoomInfo joinRoom(String roomId, String password) {
        if (networkManager == null) {
            System.err.println("NetworkManager not initialized! Call setGuiCallback first.");
            return null;
        }

        String host = null;
        int port = 8000;

        try {
            if (roomId.contains(":")) {
                String[] parts = roomId.split(":");
                host = parts[0];
                port = Integer.parseInt(parts[1]);
                System.out.println("Parsed host:port format - " + host + ":" + port);
            } else if (roomId.startsWith("ROOM_")) {
                port = Integer.parseInt(roomId.substring(5));
                System.out.println("Discovering host for room port: " + port);
                host = discoverRoomHost(port);
                if (host == null) {
                    System.err.println("Could not find host. Try format: IP_ADDRESS:" + port);
                    return null;
                }
            } else {
                try {
                    port = Integer.parseInt(roomId);
                    System.out.println("Discovering host for port: " + port);
                    host = discoverRoomHost(port);
                    if (host == null) {
                        System.err.println("Could not find host. Try format: IP_ADDRESS:" + port);
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
            RoomInfo roomInfo = new RoomInfo(roomId, port);
            return roomInfo;
        } else {
            System.err.println("Failed to connect to " + host + ":" + port);
        }

        return null;
    }

    /**
     * Discover room host using multiple methods
     */
    private String discoverRoomHost(int port) {
        System.out.println("Starting room discovery for port " + port + "...");
        
        try {
            System.out.println("Listening for room announcements...");
            String result = discovery.listen(5000);
            
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
        
        System.out.println("Trying localhost...");
        if (testConnection("localhost", port)) {
            System.out.println("Found room on localhost");
            return "localhost";
        }
        
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
     * Test if we can connect to a host
     */
    private boolean testConnection(String host, int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 1000);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Scan local network for the room
     */
    private String scanLocalNetwork(int port) {
        try {
            String localIP = getLocalIPAddress();
            String subnet = localIP.substring(0, localIP.lastIndexOf('.'));
            
            for (int i = 1; i < 255; i++) {
                String testIP = subnet + "." + i;
                if (testConnection(testIP, port)) {
                    return testIP;
                }
            }
        } catch (Exception e) {
            System.err.println("Network scan failed: " + e.getMessage());
        }
        return null;
    }

    /**
     * Announce room on network
     */
    private void announceRoom(int port) {
        try {
            discovery.announce(port);
        } catch (IOException e) {
            System.err.println("Failed to announce room: " + e.getMessage());
        }
    }

    /**
     * Get local IP address
     */
    private String getLocalIPAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                
                if (iface.isLoopback() || !iface.isUp()) {
                    continue;
                }
                
                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    
                    if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to get local IP: " + e.getMessage());
        }
        return "127.0.0.1";
    }

    /**
     * Validate room ID format
     */
    public boolean isValidRoomId(String roomId) {
        if (roomId == null || roomId.trim().isEmpty()) {
            return false;
        }
        
        if (roomId.contains(":")) {
            String[] parts = roomId.split(":");
            if (parts.length != 2) return false;
            try {
                Integer.parseInt(parts[1]);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        
        if (roomId.startsWith("ROOM_")) {
            try {
                Integer.parseInt(roomId.substring(5));
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        
        try {
            Integer.parseInt(roomId);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}