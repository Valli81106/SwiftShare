package com.swiftshare.gui.frames;

import com.swiftshare.gui.panels.HomePanel;
import com.swiftshare.gui.panels.RoomPanel;
import com.swiftshare.gui.utils.UIConstants;
import com.swiftshare.gui.listeners.RoomEventListener;
import com.swiftshare.models.RoomInfo;
import com.swiftshare.models.PeerInfo;
import com.swiftshare.models.FileMetadata;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame implements RoomEventListener {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private HomePanel homePanel;
    private RoomPanel roomPanel;
    
    public MainFrame() {
        initComponents();
        setupFrame();
    }
    
    private void initComponents() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        
        homePanel = new HomePanel(this);
        roomPanel = new RoomPanel(this);
        
        mainPanel.add(homePanel, "HOME");
        mainPanel.add(roomPanel, "ROOM");
        
        add(mainPanel);
    }
    
    private void setupFrame() {
        setTitle("SwiftShare - P2P File Sharing");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(UIConstants.WINDOW_WIDTH, UIConstants.WINDOW_HEIGHT);
        setLocationRelativeTo(null); // Center on screen
        setResizable(true);
    }
    
    public void showHome() {
        cardLayout.show(mainPanel, "HOME");
        setTitle("SwiftShare - P2P File Sharing");
    }
    
    public void showRoom() {
        cardLayout.show(mainPanel, "ROOM");
        setTitle("SwiftShare - Room: ROOM-12345");
    }
    
    // RoomEventListener implementation
    @Override
    public void onRoomCreated(RoomInfo roomInfo) {
        System.out.println("Room created: " + roomInfo.getRoomId());
        showRoom();
    }
    
    @Override
    public void onRoomJoined(RoomInfo roomInfo) {
        System.out.println("Room joined: " + roomInfo.getRoomId());
        showRoom();
    }
    
    @Override
    public void onRoomLeft() {
        System.out.println("Left room");
        showHome();
    }
    
    @Override
    public void onPeerJoined(PeerInfo peerInfo) {
        System.out.println("Peer joined: " + peerInfo.getPeerId());
    }
    
    @Override
    public void onPeerLeft(PeerInfo peerInfo) {
        System.out.println("Peer left: " + peerInfo.getPeerId());
    }
    
    @Override
    public void onFileAdded(FileMetadata fileMetadata) {
        System.out.println("File added: " + fileMetadata.getFileName());
    }
    
    @Override
    public void onFileRemoved(FileMetadata fileMetadata) {
        System.out.println("File removed: " + fileMetadata.getFileName());
    }
    
    @Override
    public void onError(String errorMessage) {
        JOptionPane.showMessageDialog(this, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    // Main method to run the application
    public static void main(String[] args) {
        // Set system look and feel - FIXED VERSION
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Error setting look and feel: " + e.getMessage());
            // Continue with default look and feel
        }
        
        // Create and show GUI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                MainFrame mainFrame = new MainFrame();
                mainFrame.setVisible(true);
                System.out.println("SwiftShare GUI started successfully!");
            } catch (Exception e) {
                System.err.println("Error starting application: " + e.getMessage());
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                    "Failed to start application: " + e.getMessage(), 
                    "Startup Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}