package com.swiftshare.gui.controllers;

import com.swiftshare.gui.listeners.RoomEventListener;
import com.swiftshare.models.FileMetadata;
import com.swiftshare.models.RoomInfo;
import com.swiftshare.network.manager.NetworkManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RoomController {
    
    private RoomInfo currentRoom;
    private List<RoomEventListener> listeners;
    private NetworkManager networkManager;
    
    public RoomController(NetworkManager networkManager) {
        this.networkManager = networkManager;
        this.listeners = new ArrayList<>();
        setupNetworkCallbacks();
    }
    
    /**
     * Setup callbacks to forward network events to GUI listeners
     */
    private void setupNetworkCallbacks() {
        // The NetworkManager already has callbacks set in HomeController
        // We just need to add our own listener forwarding
    }
    
    /**
     * Set the current room
     */
    public void setCurrentRoom(RoomInfo room) {
        this.currentRoom = room;
    }
    
    /**
     * Get current room info
     */
    public RoomInfo getCurrentRoom() {
        return currentRoom;
    }
    
    /**
     * Add a listener for room events
     */
    public void addRoomEventListener(RoomEventListener listener) {
        listeners.add(listener);
    }
    
    /**
     * Remove a listener
     */
    public void removeRoomEventListener(RoomEventListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Send a file to the room
     */
    public void sendFile(File file) {
        if (!networkManager.isConnected()) {
            notifyError("Not connected to a room");
            return;
        }
        
        // TODO: Get file chunks from Gauri's FileManager
        // For now, create a mock metadata
        FileMetadata metadata = new FileMetadata(
            file.getName(),
            file.length(),
            "hash-" + System.currentTimeMillis(),
            currentRoom.getRoomId()
        );
        
        // TODO: Get actual chunks from file I/O team
        byte[][] chunks = new byte[0][]; // Placeholder
        
        // Send file through network manager
        networkManager.sendFile(file, chunks, metadata);
        
        System.out.println("Sending file: " + file.getName());
    }
    
    /**
     * Accept an incoming file offer
     */
    public void acceptFile(String fileName) {
        networkManager.acceptFileOffer(fileName);
    }
    
    /**
     * Reject an incoming file offer
     */
    public void rejectFile(String fileName) {
        networkManager.rejectFileOffer(fileName);
    }
    
    /**
     * Leave the current room
     */
    public void leaveRoom() {
        networkManager.disconnect();
        currentRoom = null;
        System.out.println("Left the room");
    }
    
    /**
     * Get peer count
     */
    public int getPeerCount() {
        return networkManager.getConnectedPeerCount();
    }
    
    /**
     * Check if connected
     */
    public boolean isConnected() {
        return networkManager.isConnected();
    }
    
    /**
     * Notify all listeners of an error
     */
    private void notifyError(String message) {
        for (RoomEventListener listener : listeners) {
            listener.onError(message);
        }
    }
}