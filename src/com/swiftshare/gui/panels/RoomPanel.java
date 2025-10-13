package com.swiftshare.gui.panels;

import com.swiftshare.gui.utils.ComponentFactory;
import com.swiftshare.gui.utils.UIConstants;
import com.swiftshare.gui.components.FileListItem;
import com.swiftshare.gui.components.PeerListItem;
import com.swiftshare.gui.components.ProgressCard;
import com.swiftshare.gui.listeners.RoomEventListener;
import com.swiftshare.models.FileMetadata;
import com.swiftshare.models.PeerInfo;
import com.swiftshare.models.TransferStatus;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RoomPanel extends JPanel {
    private RoomEventListener roomEventListener;
    private JPanel filesPanel;
    private JPanel peersPanel;
    private JPanel transfersPanel;
    
    // Mock data
    private List<FileMetadata> mockFiles = new ArrayList<>();
    private List<PeerInfo> mockPeers = new ArrayList<>();
    private List<TransferStatus> mockTransfers = new ArrayList<>();
    
    public RoomPanel(RoomEventListener listener) {
        this.roomEventListener = listener;
        initMockData();
        initComponents();
    }
    
    private void initMockData() {
        // Mock files
        mockFiles.add(new FileMetadata("document.pdf", 2048576, "abc123", 5));
        mockFiles.add(new FileMetadata("image.jpg", 1048576, "def456", 3));
        mockFiles.add(new FileMetadata("presentation.pptx", 5242880, "ghi789", 12));
        
        // Mock peers
        mockPeers.add(new PeerInfo("peer1", "192.168.1.101", 8080));
        mockPeers.add(new PeerInfo("peer2", "192.168.1.102", 8080));
        mockPeers.get(0).setDisplayName("Alice");
        mockPeers.get(1).setDisplayName("Bob");
        
        // Mock transfers
        TransferStatus transfer1 = new TransferStatus();
        transfer1.setTotalChunks(10);
        transfer1.setChunksTransferred(7);
        transfer1.setBytesTransferred(7340032);
        transfer1.setSpeedMBps(2.5);
        transfer1.setState(TransferStatus.State.IN_PROGRESS);
        mockTransfers.add(transfer1);
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.BACKGROUND_COLOR);
        
        // Top bar with room info and leave button
        JPanel topBar = createTopBar();
        
        // Main content area with tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIConstants.BODY_FONT);
        
        // Files tab
        tabbedPane.addTab("Files", createFilesPanel());
        
        // Peers tab
        tabbedPane.addTab("Peers", createPeersPanel());
        
        // Transfers tab
        tabbedPane.addTab("Transfers", createTransfersPanel());
        
        add(topBar, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel createTopBar() {
        JPanel topBar = ComponentFactory.createCardPanel();
        topBar.setLayout(new BorderLayout());
        
        JLabel roomInfoLabel = new JLabel("Room: ROOM-12345 • 2 peers connected • Expires in 23:59:59");
        roomInfoLabel.setFont(UIConstants.BODY_FONT);
        
        JButton leaveBtn = ComponentFactory.createSecondaryButton("Leave Room");
        leaveBtn.addActionListener(e -> leaveRoom());
        
        JButton addFileBtn = ComponentFactory.createPrimaryButton("Add File");
        addFileBtn.addActionListener(e -> addFile());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(addFileBtn);
        buttonPanel.add(leaveBtn);
        
        topBar.add(roomInfoLabel, BorderLayout.WEST);
        topBar.add(buttonPanel, BorderLayout.EAST);
        
        return topBar;
    }
    
    private JPanel createFilesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIConstants.BACKGROUND_COLOR);
        
        filesPanel = new JPanel();
        filesPanel.setLayout(new BoxLayout(filesPanel, BoxLayout.Y_AXIS));
        filesPanel.setBackground(Color.WHITE);
        
        // Add mock files
        for (FileMetadata file : mockFiles) {
            filesPanel.add(new FileListItem(file));
        }
        
        JScrollPane scrollPane = new JScrollPane(filesPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createPeersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIConstants.BACKGROUND_COLOR);
        
        peersPanel = new JPanel();
        peersPanel.setLayout(new BoxLayout(peersPanel, BoxLayout.Y_AXIS));
        peersPanel.setBackground(Color.WHITE);
        
        // Add mock peers
        for (PeerInfo peer : mockPeers) {
            peersPanel.add(new PeerListItem(peer));
        }
        
        JScrollPane scrollPane = new JScrollPane(peersPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createTransfersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIConstants.BACKGROUND_COLOR);
        
        transfersPanel = new JPanel();
        transfersPanel.setLayout(new BoxLayout(transfersPanel, BoxLayout.Y_AXIS));
        transfersPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        
        // Add mock transfers
        for (TransferStatus transfer : mockTransfers) {
            transfersPanel.add(new ProgressCard(transfer));
            transfersPanel.add(Box.createVerticalStrut(10));
        }
        
        JScrollPane scrollPane = new JScrollPane(transfersPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
    
    private void leaveRoom() {
        int result = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to leave the room?", 
            "Leave Room", 
            JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            if (roomEventListener != null) {
                roomEventListener.onRoomLeft();
            }
        }
    }
    
    private void addFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            // Mock file addition
            FileMetadata newFile = new FileMetadata(
                fileChooser.getSelectedFile().getName(),
                fileChooser.getSelectedFile().length(),
                "mock_hash_" + System.currentTimeMillis(),
                8
            );
            
            mockFiles.add(newFile);
            filesPanel.add(new FileListItem(newFile));
            filesPanel.revalidate();
            filesPanel.repaint();
            
            if (roomEventListener != null) {
                roomEventListener.onFileAdded(newFile);
            }
        }
    }
}