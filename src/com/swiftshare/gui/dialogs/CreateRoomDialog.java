package com.swiftshare.gui.dialogs;

import com.swiftshare.models.RoomInfo;
import com.swiftshare.gui.controllers.HomeController;

import javax.swing.*;
import java.awt.*;

public class CreateRoomDialog extends JDialog {
    private JTextField roomNameField;
    private JPasswordField passwordField;
    private JComboBox<String> durationCombo;
    private JButton createButton;
    private JButton cancelButton;
    
    private boolean roomCreated = false;
    private RoomInfo roomInfo;
    private HomeController homeController; // ADD THIS
    
    public CreateRoomDialog(Frame parent, HomeController controller) { // MODIFIED
        super(parent, "Create New Room", true);
        this.homeController = controller; // ADD THIS
        setupDialog();
        createComponents();
        layoutComponents();
    }
    
    // ... rest of the code stays the same until handleCreate()
    
    private void handleCreate() {
        String roomName = roomNameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (roomName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a room name", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a password", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Get duration in minutes
        int durationMinutes = getDurationInMinutes();
        
        // MODIFIED: Use HomeController to create room
        roomInfo = homeController.createRoom(roomName, password, durationMinutes);
        
        if (roomInfo != null) {
            roomCreated = true;
            JOptionPane.showMessageDialog(this, 
                "Room created successfully!\nRoom ID: " + roomInfo.getRoomId(),
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Failed to create room. Please try again.",
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private int getDurationInMinutes() {
        String selected = (String) durationCombo.getSelectedItem();
        switch (selected) {
            case "15 minutes": return 15;
            case "30 minutes": return 30;
            case "1 hour": return 60;
            case "2 hours": return 120;
            case "6 hours": return 360;
            case "24 hours": return 1440;
            default: return 30;
        }
    }
    
    // Keep rest of the methods...
    
    public boolean isRoomCreated() {
        return roomCreated;
    }
    
    public RoomInfo getRoomInfo() {
        return roomInfo;
    }
}