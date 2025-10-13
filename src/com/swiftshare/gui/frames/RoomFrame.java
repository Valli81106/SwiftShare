package com.swiftshare.gui.frames;

import com.swiftshare.models.RoomInfo;
import com.swiftshare.gui.utils.UIConstants;

import javax.swing.*;
import java.awt.*;

public class RoomFrame extends JFrame {
    private RoomInfo roomInfo;
    private JLabel roomIdLabel;
    private JLabel timerLabel;
    private JLabel userCountLabel;
    
    public RoomFrame(RoomInfo roomInfo) {
        this.roomInfo = roomInfo;
        initializeFrame();
        setupUI();
    }
    
    private void initializeFrame() {
        setTitle("SwiftShare - Room: " + roomInfo.getRoomId());
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                handleRoomExit();
            }
        });
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        
        JPanel topBar = createTopBar();
        add(topBar, BorderLayout.NORTH);
        
        JPanel mainContent = new JPanel();
        mainContent.setBackground(UIConstants.BACKGROUND_COLOR);
        add(mainContent, BorderLayout.CENTER);
        
        JPanel bottomBar = createBottomBar();
        add(bottomBar, BorderLayout.SOUTH);
    }
    
    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        topBar.setBackground(UIConstants.PRIMARY_COLOR);
        topBar.setPreferredSize(new Dimension(0, 60));
        
        roomIdLabel = new JLabel("Room ID: " + roomInfo.getRoomId());
        roomIdLabel.setForeground(Color.WHITE);
        roomIdLabel.setFont(UIConstants.HEADER_FONT);
        
        timerLabel = new JLabel("Time Left: --:--");
        timerLabel.setForeground(Color.WHITE);
        timerLabel.setFont(UIConstants.NORMAL_FONT);
        
        userCountLabel = new JLabel("Users: 0");
        userCountLabel.setForeground(Color.WHITE);
        userCountLabel.setFont(UIConstants.NORMAL_FONT);
        
        topBar.add(roomIdLabel);
        topBar.add(Box.createHorizontalStrut(30));
        topBar.add(timerLabel);
        topBar.add(Box.createHorizontalStrut(30));
        topBar.add(userCountLabel);
        
        return topBar;
    }
    
    private JPanel createBottomBar() {
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        bottomBar.setBackground(UIConstants.BACKGROUND_COLOR);
        bottomBar.setPreferredSize(new Dimension(0, 60));
        
        JButton leaveButton = new JButton("Leave Room");
        leaveButton.setFont(UIConstants.BUTTON_FONT);
        leaveButton.setBackground(UIConstants.DANGER_COLOR);
        leaveButton.setForeground(Color.WHITE);
        leaveButton.addActionListener(e -> handleRoomExit());
        
        bottomBar.add(leaveButton);
        
        return bottomBar;
    }
    
    private void handleRoomExit() {
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to leave this room?",
            "Leave Room",
            JOptionPane.YES_NO_OPTION
        );
        
        if (choice == JOptionPane.YES_OPTION) {
            dispose();
        }
    }
    
    public void updateTimer(String timeLeft) {
        SwingUtilities.invokeLater(() -> {
            timerLabel.setText("Time Left: " + timeLeft);
        });
    }
    
    public void updateUserCount(int count) {
        SwingUtilities.invokeLater(() -> {
            userCountLabel.setText("Users: " + count);
        });
    }
}