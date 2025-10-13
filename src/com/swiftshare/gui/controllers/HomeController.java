package com.swiftshare.gui.controllers;

import com.swiftshare.models.RoomInfo;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Controller for home screen operations
 * MOCK VERSION - No networking integration yet
 */
public class HomeController {
    
    public HomeController() {
        System.out.println("HomeController initialized (Mock Mode)");
    }
    
    /**
     * Create a new room (MOCK)
     * @return RoomInfo with mock data
     */
    public RoomInfo createRoom(String roomName, String password, int durationMinutes) {
        System.out.println("MOCK: Creating room: " + roomName);
        
        // Generate mock room ID
        String roomId = "ROOM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        // Calculate expiry time
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(durationMinutes);
        
        // Create RoomInfo
        RoomInfo roomInfo = new RoomInfo(roomId, roomName, expiryTime);
        roomInfo.setPasswordHash(password); // Store plaintext for now
        
        System.out.println("MOCK: Room created successfully - ID: " + roomId);
        return roomInfo;
    }
    
    /**
     * Join an existing room (MOCK)
     * @return RoomInfo with mock data
     */
    public RoomInfo joinRoom(String roomId, String password) {
        System.out.println("MOCK: Joining room: " + roomId);
        
        // Mock validation
        if (roomId == null || roomId.isEmpty()) {
            System.out.println("MOCK: Invalid room ID");
            return null;
        }
        
        // Create mock RoomInfo for joined room
        LocalDateTime expiryTime = LocalDateTime.now().plusHours(1);
        RoomInfo roomInfo = new RoomInfo(roomId, "Joined Room", expiryTime);
        
        System.out.println("MOCK: Joined room successfully");
        return roomInfo;
    }
    
    /**
     * Validate room ID format
     */
    public boolean isValidRoomId(String roomId) {
        if (roomId == null || roomId.isEmpty()) return false;
        // Accept any non-empty string for now
        return roomId.length() >= 4;
    }
    
    /**
     * Validate password strength
     */
    public boolean isValidPassword(String password) {
        return password != null && password.length() >= 4;
    }
}