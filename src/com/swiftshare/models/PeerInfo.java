package com.swiftshare.models;

// info about a connected peer
public class PeerInfo {
    private String peerId;
    private String ipAddress;
    private int port;
    private String displayName;
    private boolean connected;

    public PeerInfo(String peerId, String ipAddress, int port) {
        this.peerId = peerId;
        this.ipAddress = ipAddress;
        this.port = port;
        this.connected = true;
    }
    public String getPeerId() {
        return peerId;
    }
    public String getIpAddress() {
        return ipAddress;
    }
    public int getPort() {
        return port;
    }
    public String getDisplayName() {
        return displayName;
    }
    public void setDisplayName(String name) {
        this.displayName = name;
    }
    public boolean isConnected() {
        return connected;
    }
    public void setConnected(boolean connected) {
        this.connected = connected;
    }
    @Override
    public String toString() {
        String name = displayName != null ? displayName : peerId;
        return name + " (" + ipAddress + ":" + port + ")";
    }
}