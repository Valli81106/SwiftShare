package com.swiftshare.gui.dialogs;

import com.swiftshare.models.RoomInfo;
import com.swiftshare.gui.controllers.HomeController;

import javax.swing.*;
import java.awt.*;

public class JoinRoomDialog extends JDialog {
    private JTextField roomIdField;
    private JPasswordField passwordField;
    private JButton joinButton;
    private JButton cancelButton;
    
    private boolean joinSuccessful = false;
    private RoomInfo roomInfo;
    private HomeController homeController; // ADD THIS
    
    public JoinRoomDialog(Frame parent, HomeController controller) { // MODIFIED
        super(parent, "Join Room", true);
        this.homeController = controller; // ADD THIS
        setupDialog();
        createComponents();
        layoutComponents();
    }
    
    // ... setup and create methods stay the same ...
    
    private void handleJoin() {
        String roomId = roomIdField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (roomId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a room ID", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!homeController.isValidRoomId(roomId)) {
            JOptionPane.showMessageDialog(this, 
                "Invalid room ID format.\nAccepted formats:\n- ROOM_8123\n- 8123\n- 192.168.1.5:8123",
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a password", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // MODIFIED: Use HomeController to join room
        roomInfo = homeController.joinRoom(roomId, password);
        
        if (roomInfo != null) {
            joinSuccessful = true;
            JOptionPane.showMessageDialog(this, 
                "Joined room successfully!",
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Failed to join room. Check room ID and try again.",
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Keep rest of methods...
    
    public boolean isJoinSuccessful() {
        return joinSuccessful;
    }
    
    public RoomInfo getRoomInfo() {
        return roomInfo;
    }
}
