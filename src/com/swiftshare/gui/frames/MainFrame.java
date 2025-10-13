package com.swiftshare.gui.frames;

import com.swiftshare.gui.panels.HomePanel;
import com.swiftshare.gui.panels.RoomPanel;
import com.swiftshare.gui.utils.UIConstants;

import javax.swing.*;
import java.awt.*;

/**
 * Main application window - uses CardLayout to switch between screens
 */
public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel contentPanel;
    
    // Panel instances
    private HomePanel homePanel;
    private RoomPanel roomPanel;
    
    // Card names
    public static final String HOME_CARD = "HOME";
    public static final String ROOM_CARD = "ROOM";
    
    public MainFrame() {
        initializeFrame();
        initializePanels();
        setupLayout();
    }
    
    private void initializeFrame() {
        setTitle("SwiftShare - Ephemeral File Rooms");
        setSize(UIConstants.WINDOW_WIDTH, UIConstants.WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen
        setMinimumSize(new Dimension(800, 600));
        
        // Set app icon (if you have one)
        // ImageIcon icon = new ImageIcon(getClass().getResource("/icons/app-icon.png"));
        // setIconImage(icon.getImage());
    }
    
    private void initializePanels() {
        // Create panels
        homePanel = new HomePanel(this);
        roomPanel = new RoomPanel(this);
    }
    
    private void setupLayout() {
        // Create CardLayout container
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        
        // Add panels to card layout
        contentPanel.add(homePanel, HOME_CARD);
        contentPanel.add(roomPanel, ROOM_CARD);
        
        // Add to frame
        add(contentPanel);
        
        // Show home panel initially
        showHome();
    }
    
    /**
     * Switch to home panel
     */
    public void showHome() {
        cardLayout.show(contentPanel, HOME_CARD);
        homePanel.onPanelShown(); // Refresh panel if needed
    }
    
    /**
     * Switch to room panel
     */
    public void showRoom() {
        cardLayout.show(contentPanel, ROOM_CARD);
        roomPanel.onPanelShown(); // Initialize room view
    }
    
    /**
     * Get the home panel instance
     */
    public HomePanel getHomePanel() {
        return homePanel;
    }
    
    /**
     * Get the room panel instance
     */
    public RoomPanel getRoomPanel() {
        return roomPanel;
    }
    
    /**
     * Show error message dialog
     */
    public void showError(String message) {
        JOptionPane.showMessageDialog(
            this,
            message,
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
    
    /**
     * Show info message dialog
     */
    public void showInfo(String message) {
        JOptionPane.showMessageDialog(
            this,
            message,
            "Information",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Main method to launch the application
     */
    public static void main(String[] args) {
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Create and show GUI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}