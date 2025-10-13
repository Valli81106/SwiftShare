package com.swiftshare.gui.controllers;

import com.swiftshare.gui.listeners.RoomEventListener;
import com.swiftshare.models.FileMetadata;
import com.swiftshare.models.RoomInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for room operations
 * MOCK VERSION - No networking integration yet
 */
public class RoomController {
    
    private RoomInfo currentRoom;
    private List<RoomEventListener> listeners;
    
    public RoomController() {
        this.listeners = new ArrayList<>();
        System.out.println("RoomController initialized (Mock Mode)");
    }
    
    /**
     * Set the current room
     */
    public void setCurrentRoom(RoomInfo room) {
        this.currentRoom = room;
        System.out.println("MOCK: Current room set to: " + room.getRoomId());
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
     * Send a file to the room (MOCK)
     */
    public void sendFile(File file) {
        System.out.println("MOCK: Sending file: " + file.getName());
        System.out.println("MOCK: File size: " + file.length() + " bytes");
        
        // TODO: Will integrate with networking and file I/O in next phase
    }
    
    /**
     * Leave the current room (MOCK)
     */
    public void leaveRoom() {
        System.out.println("MOCK: Leaving room: " + 
            (currentRoom != null ? currentRoom.getRoomId() : "none"));
        currentRoom = null;
    }
    
    /**
     * Get peer count (MOCK)
     */
    public int getPeerCount() {
        // Return mock count
        return 0;
    }
    
    /**
     * Check if connected (MOCK)
     */
    public boolean isConnected() {
        // Always return false in mock mode
        return false;
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