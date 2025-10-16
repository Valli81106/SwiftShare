package com.swiftshare.gui.panels;

import com.swiftshare.gui.frames.MainFrame;
import com.swiftshare.gui.dialogs.CreateRoomDialog;
import com.swiftshare.gui.dialogs.JoinRoomDialog;
import com.swiftshare.gui.utils.UIConstants;
import com.swiftshare.gui.controllers.HomeController;
import com.swiftshare.models.RoomInfo;

import javax.swing.*;
import java.awt.*;

public class HomePanel extends JPanel {
    private MainFrame parentFrame;
    private HomeController homeController;
    private JButton createRoomButton;
    private JButton joinRoomButton;
    private JLabel titleLabel;
    private JLabel subtitleLabel;
    
    public HomePanel(MainFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.homeController = new HomeController();
        setupPanel();
        createComponents();
        layoutComponents();
    }
    
    private void setupPanel() {
        setLayout(new GridBagLayout());
        setBackground(UIConstants.BACKGROUND_COLOR);
    }
    
    private void createComponents() {
        titleLabel = new JLabel("SwiftShare");
        titleLabel.setFont(UIConstants.TITLE_FONT);
        titleLabel.setForeground(UIConstants.PRIMARY_COLOR);
        
        subtitleLabel = new JLabel("Ephemeral File Transfer Rooms");
        subtitleLabel.setFont(UIConstants.SUBTITLE_FONT);
        subtitleLabel.setForeground(UIConstants.TEXT_SECONDARY);
        
        createRoomButton = new JButton("Create New Room");
        createRoomButton.setFont(UIConstants.BUTTON_FONT);
        createRoomButton.setPreferredSize(new Dimension(250, 50));
        createRoomButton.setBackground(UIConstants.PRIMARY_COLOR);
        createRoomButton.setForeground(Color.WHITE);
        createRoomButton.setFocusPainted(false);
        createRoomButton.setBorderPainted(false);
        createRoomButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        createRoomButton.addActionListener(e -> handleCreateRoom());
        
        joinRoomButton = new JButton("Join Existing Room");
        joinRoomButton.setFont(UIConstants.BUTTON_FONT);
        joinRoomButton.setPreferredSize(new Dimension(250, 50));
        joinRoomButton.setBackground(UIConstants.SECONDARY_COLOR);
        joinRoomButton.setForeground(Color.WHITE);
        joinRoomButton.setFocusPainted(false);
        joinRoomButton.setBorderPainted(false);
        joinRoomButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        joinRoomButton.addActionListener(e -> handleJoinRoom());
        
        addHoverEffect(createRoomButton, UIConstants.PRIMARY_COLOR, UIConstants.PRIMARY_DARK);
        addHoverEffect(joinRoomButton, UIConstants.SECONDARY_COLOR, UIConstants.SECONDARY_DARK);
    }
    
    private void layoutComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 0, 10, 0);
        
        gbc.gridy = 0;
        add(titleLabel, gbc);
        
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 40, 0);
        add(subtitleLabel, gbc);
        
        gbc.gridy = 2;
        gbc.insets = new Insets(10, 0, 10, 0);
        add(createRoomButton, gbc);
        
        gbc.gridy = 3;
        add(joinRoomButton, gbc);
        
        gbc.gridy = 4;
        gbc.insets = new Insets(30, 0, 0, 0);
        JLabel infoLabel = new JLabel("Create temporary rooms that auto-destruct after time limit");
        infoLabel.setFont(UIConstants.SMALL_FONT);
        infoLabel.setForeground(UIConstants.TEXT_SECONDARY);
        add(infoLabel, gbc);
    }
    
    private void handleCreateRoom() {
        CreateRoomDialog dialog = new CreateRoomDialog(parentFrame, homeController); // MODIFIED
        dialog.setVisible(true);
        
        if (dialog.isRoomCreated()) {
            RoomInfo roomInfo = dialog.getRoomInfo();
            parentFrame.getRoomPanel().setRoomInfo(roomInfo);
            parentFrame.getRoomPanel().setNetworkManager(homeController.getNetworkManager());
            parentFrame.showRoom();
        }
    }
    
    private void handleJoinRoom() {
       JoinRoomDialog dialog = new JoinRoomDialog(parentFrame, homeController); // MODIFIED
        dialog.setVisible(true);
        
        if (dialog.isJoinSuccessful()) {
            RoomInfo roomInfo = dialog.getRoomInfo();
            parentFrame.getRoomPanel().setRoomInfo(roomInfo);
            parentFrame.getRoomPanel().setNetworkManager(homeController.getNetworkManager());
            parentFrame.showRoom();
        }
    }
    
    private void addHoverEffect(JButton button, Color normalColor, Color hoverColor) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(normalColor);
            }
        });
    }
    
    public void onPanelShown() {
        // Refresh panel if needed
    }
}