package com.swiftshare.gui.components;

import com.swiftshare.gui.utils.UIConstants;
import com.swiftshare.models.PeerInfo;

import javax.swing.*;
import java.awt.*;

public class PeerListItem extends JPanel {
    private PeerInfo peerInfo;
    
    public PeerListItem(PeerInfo peerInfo) {
        this.peerInfo = peerInfo;
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UIConstants.BORDER_COLOR),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        
        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.setBackground(Color.WHITE);
        
        String displayName = peerInfo.getDisplayName() != null ? 
            peerInfo.getDisplayName() : peerInfo.getPeerId();
        
        JLabel nameLabel = new JLabel(displayName);
        nameLabel.setFont(UIConstants.BODY_FONT);
        
        JLabel addressLabel = new JLabel(peerInfo.getIpAddress() + ":" + peerInfo.getPort());
        addressLabel.setFont(UIConstants.SMALL_FONT);
        addressLabel.setForeground(Color.GRAY);
        
        infoPanel.add(nameLabel);
        infoPanel.add(addressLabel);
        
        // Status indicator
        JLabel statusLabel = new JLabel(peerInfo.isConnected() ? "● Online" : "○ Offline");
        statusLabel.setFont(UIConstants.SMALL_FONT);
        statusLabel.setForeground(peerInfo.isConnected() ? UIConstants.ACCENT_COLOR : Color.GRAY);
        
        add(infoPanel, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.EAST);
    }
    
    public PeerInfo getPeerInfo() {
        return peerInfo;
    }
}