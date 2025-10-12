package com.swiftshare.models;

import java.util.ArrayList;
import java.util.List;

// info about a file sharing room
public class RoomInfo {
    private String roomId;
    private String passwordHash;
    private int port;
    private long createdTime;
    private long expiryTime;
    private List<PeerInfo> connectedPeers;

    public RoomInfo(String roomId, int port) {
        this.roomId = roomId;
        this.port = port;
        this.createdTime = System.currentTimeMillis();
        this.expiryTime = createdTime + (24 * 60 * 60 * 1000); // 24 hours
        this.connectedPeers = new ArrayList<>();
    }

    // check if room has expired
    public boolean isExpired() {
        return System.currentTimeMillis() > expiryTime;
    }

    // get time left before expiry
    public long getRemainingTime() {
        long remaining = expiryTime - System.currentTimeMillis();
        return remaining > 0 ? remaining : 0;
    }
    public String getRoomId() {
        return roomId;
    }
    public String getPasswordHash() {
        return passwordHash;
    }
    public void setPasswordHash(String hash) {
        this.passwordHash = hash;
    }
    public int getPort() {
        return port;
    }
    public List<PeerInfo> getConnectedPeers() {
        return connectedPeers;
    }
    public void addPeer(PeerInfo peer) {
        connectedPeers.add(peer);
    }
    public void removePeer(PeerInfo peer) {
        connectedPeers.remove(peer);
    }
}