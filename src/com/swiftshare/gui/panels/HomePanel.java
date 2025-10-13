package com.swiftshare.gui.panels;

import com.swiftshare.gui.utils.ComponentFactory;
import com.swiftshare.gui.utils.UIConstants;
import com.swiftshare.gui.listeners.RoomEventListener;

import javax.swing.*;
import java.awt.*;

public class HomePanel extends JPanel {
    private RoomEventListener roomEventListener;
    private JButton createRoomBtn;
    private JButton joinRoomBtn;
    
    public HomePanel(RoomEventListener listener) {
        this.roomEventListener = listener;
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.BACKGROUND_COLOR);
        
        // Main content panel
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Title
        JLabel titleLabel = ComponentFactory.createTitleLabel("SwiftShare");
        JLabel subtitleLabel = new JLabel("Peer-to-Peer File Sharing");
        subtitleLabel.setFont(UIConstants.SUBHEADER_FONT);
        subtitleLabel.setForeground(Color.GRAY);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 0, 15));
        buttonPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(40, 100, 40, 100));
        
        createRoomBtn = ComponentFactory.createPrimaryButton("Create New Room");
        joinRoomBtn = ComponentFactory.createSecondaryButton("Join Existing Room");
        
        createRoomBtn.addActionListener(e -> showCreateRoomDialog());
        joinRoomBtn.addActionListener(e -> showJoinRoomDialog());
        
        buttonPanel.add(createRoomBtn);
        buttonPanel.add(joinRoomBtn);
        
        // Layout
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 10, 0);
        contentPanel.add(titleLabel, gbc);
        
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 40, 0);
        contentPanel.add(subtitleLabel, gbc);
        
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        contentPanel.add(buttonPanel, gbc);
        
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private void showCreateRoomDialog() {
        // Mock room creation
        JOptionPane.showMessageDialog(this, 
            "Room created successfully!\nRoom ID: ROOM-12345\nPassword: (set)", 
            "Room Created", 
            JOptionPane.INFORMATION_MESSAGE);
        
        // Simulate room creation callback
        if (roomEventListener != null) {
            // In real implementation, this would come from RoomManager
            // roomEventListener.onRoomCreated(roomInfo);
        }
    }
    
    private void showJoinRoomDialog() {
        String roomId = JOptionPane.showInputDialog(this, "Enter Room ID:", "Join Room", JOptionPane.QUESTION_MESSAGE);
        if (roomId != null && !roomId.trim().isEmpty()) {
            // Mock room joining
            JOptionPane.showMessageDialog(this, 
                "Joined room successfully!", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // Simulate room join callback
            if (roomEventListener != null) {
                // roomEventListener.onRoomJoined(roomInfo);
            }
        }
    }
}