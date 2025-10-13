  package com.swiftshare.gui.components;

import com.swiftshare.gui.utils.UIConstants;
import com.swiftshare.models.PeerInfo;

import javax.swing.*;
import java.awt.*;

public class PeerListItem extends JPanel {
    private PeerInfo peerInfo;
    private JLabel nameLabel;
    private JLabel statusLabel;
    
    public PeerListItem(PeerInfo peerInfo) {
        this.peerInfo = peerInfo;
        setupPanel();
        createComponents();
    }
    
    private void setupPanel() {
        setLayout(new BorderLayout(10, 5));
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        setOpaque(true);
    }
    
    private void createComponents() {
        nameLabel = new JLabel(peerInfo.getPeerName());
        nameLabel.setFont(UIConstants.NORMAL_FONT);
        
        statusLabel = new JLabel("●");
        statusLabel.setForeground(peerInfo.isConnected() ? UIConstants.SUCCESS_COLOR : Color.GRAY);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        add(statusLabel, BorderLayout.WEST);
        add(nameLabel, BorderLayout.CENTER);
    }
}
