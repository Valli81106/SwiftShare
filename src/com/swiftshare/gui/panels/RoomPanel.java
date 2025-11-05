package com.swiftshare.gui.panels;

import com.swiftshare.gui.frames.MainFrame;
import com.swiftshare.gui.utils.UIConstants;
import com.swiftshare.models.PeerInfo;
import com.swiftshare.models.RoomInfo;
import com.swiftshare.network.manager.NetworkManager;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class RoomPanel extends JPanel {
    private MainFrame parentFrame;
    private RoomInfo currentRoom;
    private NetworkManager networkManager;
    
    private JLabel roomIdLabel;
    private JLabel peerCountLabel;
    private JTextArea peerListArea;
    private JButton leaveButton;
    private List<PeerInfo> connectedPeers;

    public RoomPanel(MainFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.connectedPeers = new ArrayList<>();
        setupPanel();
        createComponents();
    }

    private void setupPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UIConstants.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    private void createComponents() {
        // Top panel with room info
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        topPanel.setBackground(UIConstants.PRIMARY_COLOR);
        topPanel.setPreferredSize(new Dimension(0, 80));

        roomIdLabel = new JLabel("Room ID: ----");
        roomIdLabel.setFont(UIConstants.HEADER_FONT);
        roomIdLabel.setForeground(Color.WHITE);

        peerCountLabel = new JLabel("Connected Peers: 0");
        peerCountLabel.setFont(UIConstants.NORMAL_FONT);
        peerCountLabel.setForeground(Color.WHITE);

        topPanel.add(roomIdLabel);
        topPanel.add(Box.createHorizontalStrut(50));
        topPanel.add(peerCountLabel);

        // Center panel with peer list
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createTitledBorder("Connected Peers"));

        peerListArea = new JTextArea();
        peerListArea.setEditable(false);
        peerListArea.setFont(UIConstants.NORMAL_FONT);
        peerListArea.setText("Waiting for peers to connect...");
        
        JScrollPane scrollPane = new JScrollPane(peerListArea);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom panel with buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(UIConstants.BACKGROUND_COLOR);

        leaveButton = new JButton("Leave Room");
        leaveButton.setFont(UIConstants.BUTTON_FONT);
        leaveButton.setBackground(UIConstants.DANGER_COLOR);
        leaveButton.setForeground(Color.WHITE);
        leaveButton.setFocusPainted(false);
        leaveButton.addActionListener(e -> handleLeaveRoom());

        bottomPanel.add(leaveButton);

        // Add all panels
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void setRoomInfo(RoomInfo roomInfo) {
        this.currentRoom = roomInfo;
        if (roomInfo != null) {
            roomIdLabel.setText("Room ID: " + roomInfo.getRoomId());
            updatePeerList();
        }
    }

    public void setNetworkManager(NetworkManager manager) {
        this.networkManager = manager;
        
        // Listen for peer events if needed
        System.out.println("Network manager set for room panel");
    }

    public void addPeer(PeerInfo peer) {
        if (!connectedPeers.contains(peer)) {
            connectedPeers.add(peer);
            updatePeerList();
            
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this, 
                    "Peer connected: " + peer.getPeerName(),
                    "Peer Connected",
                    JOptionPane.INFORMATION_MESSAGE);
            });
        }
    }

    public void removePeer(PeerInfo peer) {
        connectedPeers.remove(peer);
        updatePeerList();
        
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this,
                "Peer disconnected: " + peer.getPeerName(),
                "Peer Disconnected",
                JOptionPane.INFORMATION_MESSAGE);
        });
    }

    private void updatePeerList() {
        SwingUtilities.invokeLater(() -> {
            peerCountLabel.setText("Connected Peers: " + connectedPeers.size());
            
            if (connectedPeers.isEmpty()) {
                peerListArea.setText("Waiting for peers to connect...");
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("Connected Peers:\n\n");
                for (int i = 0; i < connectedPeers.size(); i++) {
                    PeerInfo peer = connectedPeers.get(i);
                    sb.append(String.format("%d. %s (%s:%d)\n", 
                        i + 1, 
                        peer.getPeerName(), 
                        peer.getIpAddress(), 
                        peer.getPort()));
                }
                peerListArea.setText(sb.toString());
            }
        });
    }

    private void handleLeaveRoom() {
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to leave this room?",
            "Leave Room",
            JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {
            if (networkManager != null) {
                networkManager.disconnect();
            }
            connectedPeers.clear();
            parentFrame.showHome();
        }
    }

    public void onPanelShown() {
        updatePeerList();
    }
}