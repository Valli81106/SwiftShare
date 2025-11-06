package com.swiftshare.gui.panels;

import com.swiftshare.gui.frames.MainFrame;
import com.swiftshare.gui.utils.UIConstants;
import com.swiftshare.models.RoomInfo;
import com.swiftshare.models.PeerInfo;
import com.swiftshare.models.FileMetadata;
import com.swiftshare.network.manager.NetworkManager;
import com.swiftshare.network.manager.NetworkCallback;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RoomPanel extends JPanel {
    private MainFrame parentFrame;
    private RoomInfo currentRoom;
    private NetworkManager networkManager;
    private NetworkCallback networkCallback;
    
    private JLabel roomIdLabel;
    private JLabel peerCountLabel;
    private JTextArea peerListArea;
    private JTextArea activityLogArea;
    private JButton leaveButton;
    private List<PeerInfo> connectedPeers;

    public RoomPanel(MainFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.connectedPeers = new ArrayList<>();
        initializeNetworkCallback();
        setupPanel();
        createComponents();
    }

    private void setupPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UIConstants.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    private void createComponents() {
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

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        centerPanel.setBackground(UIConstants.BACKGROUND_COLOR);

        JPanel peerPanel = new JPanel(new BorderLayout());
        peerPanel.setBackground(Color.WHITE);
        peerPanel.setBorder(BorderFactory.createTitledBorder("Connected Peers"));

        peerListArea = new JTextArea();
        peerListArea.setEditable(false);
        peerListArea.setFont(UIConstants.NORMAL_FONT);
        peerListArea.setText("Waiting for peers to connect...");
        
        JScrollPane peerScrollPane = new JScrollPane(peerListArea);
        peerPanel.add(peerScrollPane, BorderLayout.CENTER);

        JPanel activityPanel = new JPanel(new BorderLayout());
        activityPanel.setBackground(Color.WHITE);
        activityPanel.setBorder(BorderFactory.createTitledBorder("Activity Log"));

        activityLogArea = new JTextArea();
        activityLogArea.setEditable(false);
        activityLogArea.setFont(new Font("Monospace", Font.PLAIN, 12));
        activityLogArea.setText("Room created. Waiting for activity...\n");
        
        JScrollPane activityScrollPane = new JScrollPane(activityLogArea);
        activityPanel.add(activityScrollPane, BorderLayout.CENTER);

        centerPanel.add(peerPanel);
        centerPanel.add(activityPanel);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(UIConstants.BACKGROUND_COLOR);

        leaveButton = new JButton("Leave Room");
        leaveButton.setFont(UIConstants.BUTTON_FONT);
        leaveButton.setBackground(UIConstants.DANGER_COLOR);
        leaveButton.setForeground(Color.WHITE);
        leaveButton.setFocusPainted(false);
        leaveButton.addActionListener(e -> handleLeaveRoom());

        bottomPanel.add(leaveButton);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void setRoomInfo(RoomInfo roomInfo) {
        this.currentRoom = roomInfo;
        if (roomInfo != null) {
            roomIdLabel.setText("Room ID: " + roomInfo.getRoomId());
            logActivity("[+] Room created: " + roomInfo.getRoomId());
            updatePeerList();
        }
    }

    public void setNetworkManager(NetworkManager manager) {
        this.networkManager = manager;
        logActivity("[+] Network manager initialized");
        System.out.println("[GUI] Network manager set for room panel");
    }

    public NetworkCallback getNetworkCallback() {
        return this.networkCallback;
    }

    private void initializeNetworkCallback() {
        this.networkCallback = new NetworkCallback() {
            @Override
            public void onRoomCreated(int port) {
                logActivity("[+] Room created on port: " + port);
            }

            @Override
            public void onRoomJoined(String host, int port) {
                logActivity("[+] Joined room at " + host + ":" + port);
            }

            @Override
            public void onPeerConnected(PeerInfo peer) {
                System.out.println("[GUI] Peer connected callback: " + peer.getPeerName());
                addPeer(peer);
                logActivity("[+] Peer connected: " + peer.getPeerName() + " (" + peer.getIpAddress() + ")");
            }

            @Override
            public void onPeerDisconnected(PeerInfo peer) {
                System.out.println("[GUI] Peer disconnected callback: " + peer.getPeerName());
                removePeer(peer);
                logActivity("[-] Peer disconnected: " + peer.getPeerName());
            }

            @Override
            public void onFileOfferReceived(FileMetadata metadata) {
                System.out.println("[GUI] File offer received: " + metadata.getFileName());
                logActivity("[FILE] File offered: " + metadata.getFileName() + 
                           " (" + metadata.getFormattedFileSize() + ")");
                
                SwingUtilities.invokeLater(() -> {
                    int choice = JOptionPane.showConfirmDialog(
                        RoomPanel.this,
                        "Accept file: " + metadata.getFileName() + 
                        " (" + metadata.getFormattedFileSize() + ")?",
                        "File Transfer Request",
                        JOptionPane.YES_NO_OPTION
                    );

                    if (choice == JOptionPane.YES_OPTION) {
                        networkManager.acceptFileOffer(metadata.getFileName());
                        logActivity("[+] Accepted file: " + metadata.getFileName());
                    } else {
                        networkManager.rejectFileOffer(metadata.getFileName());
                        logActivity("[-] Rejected file: " + metadata.getFileName());
                    }
                });
            }

            @Override
            public void onTransferProgress(String fileName, double progress, String speed) {
                logActivity(String.format("[TRANSFER] %s - %.1f%% @ %s", 
                    fileName, progress, speed));
            }

            @Override
            public void onTransferComplete(String fileName) {
                System.out.println("[GUI] Transfer complete: " + fileName);
                logActivity("[+] Transfer complete: " + fileName);
                
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(
                        RoomPanel.this,
                        "File received successfully: " + fileName,
                        "Transfer Complete",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                });
            }

            @Override
            public void onError(String error) {
                System.err.println("[GUI] Error: " + error);
                logActivity("[ERROR] " + error);
                
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(
                        RoomPanel.this,
                        error,
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                });
            }

            @Override
            public void onConnectionLost() {
                System.out.println("[GUI] Connection lost");
                logActivity("[ERROR] Connection lost!");
                
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(
                        RoomPanel.this,
                        "Connection to room was lost",
                        "Connection Lost",
                        JOptionPane.WARNING_MESSAGE
                    );
                });
            }
        };
    }

    private void addPeer(PeerInfo peer) {
        SwingUtilities.invokeLater(() -> {
            boolean exists = false;
            for (PeerInfo p : connectedPeers) {
                if (p.getPeerId().equals(peer.getPeerId())) {
                    exists = true;
                    break;
                }
            }
            
            if (!exists) {
                connectedPeers.add(peer);
                updatePeerList();
                System.out.println("[GUI] Added peer to list: " + peer.getPeerName());
            }
        });
    }

    private void removePeer(PeerInfo peer) {
        SwingUtilities.invokeLater(() -> {
            connectedPeers.removeIf(p -> p.getPeerId().equals(peer.getPeerId()));
            updatePeerList();
            System.out.println("[GUI] Removed peer from list: " + peer.getPeerName());
        });
    }

    private void updatePeerList() {
        SwingUtilities.invokeLater(() -> {
            peerCountLabel.setText("Connected Peers: " + connectedPeers.size());
            
            if (connectedPeers.isEmpty()) {
                peerListArea.setText("Waiting for peers to connect...\n\n" +
                    "Share your Room ID with others:\n" +
                    (currentRoom != null ? currentRoom.getRoomId() : "----"));
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("Connected Peers:\n\n");
                for (int i = 0; i < connectedPeers.size(); i++) {
                    PeerInfo peer = connectedPeers.get(i);
                    sb.append(String.format("%d. %s\n   %s:%d\n   Status: %s\n\n", 
                        i + 1, 
                        peer.getPeerName(), 
                        peer.getIpAddress(), 
                        peer.getPort(),
                        peer.isConnected() ? "[CONNECTED]" : "[DISCONNECTED]"));
                }
                peerListArea.setText(sb.toString());
            }
        });
    }

    private void logActivity(String message) {
        SwingUtilities.invokeLater(() -> {
            String timestamp = new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date());
            activityLogArea.append("[" + timestamp + "] " + message + "\n");
            activityLogArea.setCaretPosition(activityLogArea.getDocument().getLength());
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
            logActivity("[-] Leaving room...");
            
            if (networkManager != null) {
                networkManager.disconnect();
            }
            
            connectedPeers.clear();
            parentFrame.showHome();
        }
    }

    public void onPanelShown() {
        logActivity("[INFO] Room panel shown");
        updatePeerList();
    }
}
