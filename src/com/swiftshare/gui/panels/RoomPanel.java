package com.swiftshare.gui.panels;

import com.swiftshare.gui.frames.MainFrame;
import com.swiftshare.gui.components.RoomInfoBar;
import com.swiftshare.gui.controllers.RoomController;
import com.swiftshare.gui.components.PeerListItem;
import com.swiftshare.gui.components.FileListItem;
import com.swiftshare.gui.components.ProgressCard;
import com.swiftshare.gui.utils.UIConstants;
import com.swiftshare.models.RoomInfo;
import com.swiftshare.models.PeerInfo;
import com.swiftshare.models.FileMetadata;
import com.swiftshare.models.TransferStatus;
import com.swiftshare.network.manager.NetworkManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class RoomPanel extends JPanel {
    private MainFrame parentFrame;
    private RoomInfo roomInfo;
    
    private NetworkManager networkManager;
    private RoomController roomController;
    
    private RoomInfoBar infoBar;
    private DefaultListModel<PeerInfo> peerListModel;
    private JList<PeerInfo> peerList;
    private JPanel filesPanel;
    private JScrollPane filesScrollPane;
    private JButton sendFileButton;
    private JButton leaveRoomButton;
    
    private Map<String, ProgressCard> activeTransfers;
    
    public RoomPanel(MainFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.activeTransfers = new HashMap<>();
        setupPanel();
        createComponents();
        layoutComponents();
    }

    public void setNetworkManager(NetworkManager networkManager) {
    this.networkManager = networkManager;
    this.roomController = new RoomController(networkManager);
    roomController.setCurrentRoom(roomInfo);
}
    
    private void setupPanel() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.BACKGROUND_COLOR);
    }
    
    private void createComponents() {
        infoBar = new RoomInfoBar();
        
        peerListModel = new DefaultListModel<>();
        peerList = new JList<>(peerListModel);
        peerList.setCellRenderer(new PeerListRenderer());
        peerList.setBackground(UIConstants.BACKGROUND_COLOR);
        peerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        filesPanel = new JPanel();
        filesPanel.setLayout(new BoxLayout(filesPanel, BoxLayout.Y_AXIS));
        filesPanel.setBackground(Color.WHITE);
        filesPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        filesScrollPane = new JScrollPane(filesPanel);
        filesScrollPane.setBorder(BorderFactory.createTitledBorder("Shared Files"));
        filesScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        sendFileButton = new JButton("Send File");
        sendFileButton.setFont(UIConstants.BUTTON_FONT);
        sendFileButton.setBackground(UIConstants.PRIMARY_COLOR);
        sendFileButton.setForeground(Color.WHITE);
        sendFileButton.setFocusPainted(false);
        sendFileButton.setPreferredSize(new Dimension(150, 40));
        sendFileButton.addActionListener(e -> handleSendFile());
        
        leaveRoomButton = new JButton("Leave Room");
        leaveRoomButton.setFont(UIConstants.BUTTON_FONT);
        leaveRoomButton.setBackground(UIConstants.DANGER_COLOR);
        leaveRoomButton.setForeground(Color.WHITE);
        leaveRoomButton.setFocusPainted(false);
        leaveRoomButton.setPreferredSize(new Dimension(150, 40));
        leaveRoomButton.addActionListener(e -> handleLeaveRoom());
    }
    
    private void layoutComponents() {
        add(infoBar, BorderLayout.NORTH);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(250);
        splitPane.setResizeWeight(0.25);
        
        JScrollPane peerScrollPane = new JScrollPane(peerList);
        peerScrollPane.setBorder(BorderFactory.createTitledBorder("Connected Peers"));
        splitPane.setLeftComponent(peerScrollPane);
        
        splitPane.setRightComponent(filesScrollPane);
        
        add(splitPane, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        bottomPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        bottomPanel.add(sendFileButton);
        bottomPanel.add(leaveRoomButton);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    public void setRoomInfo(RoomInfo roomInfo) {
        this.roomInfo = roomInfo;
        if (infoBar != null) {
            infoBar.setRoomInfo(roomInfo);
        }
    }
    
    public void onPanelShown() {
        if (roomInfo != null) {
            infoBar.setRoomInfo(roomInfo);
            infoBar.startTimer();
        }
        peerListModel.clear();
        filesPanel.removeAll();
        activeTransfers.clear();
    }
    
    public void addPeer(PeerInfo peer) {
        SwingUtilities.invokeLater(() -> {
            if (!peerListModel.contains(peer)) {
                peerListModel.addElement(peer);
                updatePeerCount();
            }
        });
    }
    
    public void removePeer(PeerInfo peer) {
        SwingUtilities.invokeLater(() -> {
            peerListModel.removeElement(peer);
            updatePeerCount();
        });
    }
    
    public void addFile(FileMetadata fileMetadata) {
        SwingUtilities.invokeLater(() -> {
            FileListItem fileItem = new FileListItem(fileMetadata);
            filesPanel.add(fileItem);
            filesPanel.add(Box.createVerticalStrut(10));
            filesPanel.revalidate();
            filesPanel.repaint();
        });
    }
    
    public void updateTransferProgress(TransferStatus status) {
        SwingUtilities.invokeLater(() -> {
            String fileId = status.getFileId();
            ProgressCard progressCard = activeTransfers.get(fileId);
            
            if (progressCard == null) {
                progressCard = new ProgressCard(status);
                activeTransfers.put(fileId, progressCard);
                filesPanel.add(progressCard);
                filesPanel.add(Box.createVerticalStrut(10));
            } else {
                progressCard.updateStatus(status);
            }
            
            filesPanel.revalidate();
            filesPanel.repaint();
            
            if (status.isComplete()) {
                Timer timer = new Timer(3000, e -> {
                    filesPanel.remove(progressCard);
                    activeTransfers.remove(fileId);
                    filesPanel.revalidate();
                    filesPanel.repaint();
                });
                timer.setRepeats(false);
                timer.start();
            }
        });
    }
    
    private void updatePeerCount() {
        infoBar.updatePeerCount(peerListModel.getSize());
    }
    
    private void handleSendFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select File to Send");
        
        int result = fileChooser.showOpenDialog(parentFrame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            
           if (roomController != null) {
            roomController.sendFile(selectedFile); // Use controller
        }
        }
    }
    
    private void handleLeaveRoom() {
        int choice = JOptionPane.showConfirmDialog(
            parentFrame,
            "Are you sure you want to leave this room?",
            "Leave Room",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (choice == JOptionPane.YES_OPTION) {
            if (roomController != null) {
            roomController.leaveRoom(); // Use controller
            }
            infoBar.stopTimer();
            parentFrame.showHome();
        }
    }
    
    private class PeerListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                                                     int index, boolean isSelected, boolean cellHasFocus) {
            if (value instanceof PeerInfo) {
                PeerInfo peer = (PeerInfo) value;
                PeerListItem item = new PeerListItem(peer);
                
                if (isSelected) {
                    item.setBackground(UIConstants.SELECTED_COLOR);
                } else {
                    item.setBackground(list.getBackground());
                }
                
                return item;
            }
            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
    }
}