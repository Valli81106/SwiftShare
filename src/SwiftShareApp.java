package com.swiftshare;

import com.swiftshare.gui.frames.MainFrame;
import com.swiftshare.models.*;
import com.swiftshare.network.manager.*;
import javax.swing.*;
import java.io.File;

/**
 * Main integration point for SwiftShare P2P File Sharing Application
 * This connects the networking layer with the GUI
 */
public class SwiftShareApp {
    private NetworkManager networkManager;
    private MainFrame mainFrame;

    public SwiftShareApp() {
        // Initialize networking with callbacks
        networkManager = new NetworkManager(new NetworkCallback() {
            @Override
            public void onRoomCreated(int port) {
                System.out.println("Room created on port: " + port);
                if (mainFrame != null) {
                    mainFrame.showInfo("Room created successfully on port " + port);
                }
            }

            @Override
            public void onRoomJoined(String host, int port) {
                System.out.println("Joined room at " + host + ":" + port);
                if (mainFrame != null) {
                    mainFrame.showInfo("Successfully joined room at " + host + ":" + port);
                }
            }

            @Override
            public void onPeerConnected(PeerInfo peer) {
                System.out.println("Peer connected: " + peer.getPeerName());
                if (mainFrame != null) {
                    mainFrame.showInfo("Peer connected: " + peer.getPeerName());
                }
            }

            @Override
            public void onPeerDisconnected(PeerInfo peer) {
                System.out.println("Peer disconnected: " + peer.getPeerName());
                if (mainFrame != null) {
                    mainFrame.showInfo("Peer disconnected: " + peer.getPeerName());
                }
            }

            @Override
            public void onFileOfferReceived(FileMetadata metadata) {
                System.out.println("File offer received: " + metadata.getFileName());
                if (mainFrame != null) {
                    int choice = JOptionPane.showConfirmDialog(
                            mainFrame,
                            "Accept file: " + metadata.getFileName() + " (" + metadata.getFormattedFileSize() + ")?",
                            "File Transfer Request",
                            JOptionPane.YES_NO_OPTION
                    );

                    if (choice == JOptionPane.YES_OPTION) {
                        networkManager.acceptFileOffer(metadata.getFileName());
                        receiveFile(metadata);
                    } else {
                        networkManager.rejectFileOffer(metadata.getFileName());
                    }
                }
            }

            @Override
            public void onTransferProgress(String fileName, double progress, String speed) {
                System.out.println("Transfer progress for " + fileName + ": " + progress + "% at " + speed);
            }

            @Override
            public void onTransferComplete(String fileName) {
                System.out.println("Transfer complete: " + fileName);
                if (mainFrame != null) {
                    mainFrame.showInfo("File transfer completed: " + fileName);
                }
            }

            @Override
            public void onError(String error) {
                System.err.println("Error: " + error);
                if (mainFrame != null) {
                    mainFrame.showError(error);
                }
            }

            @Override
            public void onConnectionLost() {
                System.out.println("Connection lost!");
                if (mainFrame != null) {
                    mainFrame.showError("Connection to room lost!");
                }
            }
        });
    }

    /**
     * Create a new room
     */
    public void createRoom(int port) {
        networkManager.createRoom(port);
    }

    /**
     * Join an existing room
     */
    public void joinRoom(String host, int port) {
        networkManager.joinRoom(host, port);
    }

    /**
     * Send a file (simplified - no chunking for now)
     */
    public void sendFile(File file) {
        try {
            // For now, use simple metadata without chunking
            FileMetadata metadata = new FileMetadata(
                    file.getName(),
                    file.length(),
                    "hash-" + System.currentTimeMillis(), // Simple hash placeholder
                    0 // No chunking for now
            );

            // Send via networking (simplified - network manager should handle the file)
            System.out.println("Sending file: " + file.getName());
            // networkManager.sendFile(file, new byte[0][0], metadata);

        } catch (Exception e) {
            System.err.println("Error sending file: " + e.getMessage());
            if (mainFrame != null) {
                mainFrame.showError("Failed to send file: " + e.getMessage());
            }
        }
    }

    /**
     * Receive file (placeholder implementation)
     */
    private void receiveFile(FileMetadata metadata) {
        try {
            System.out.println("Receiving file: " + metadata.getFileName());
            // TODO: Implement actual file reception
        } catch (Exception e) {
            System.err.println("Error receiving file: " + e.getMessage());
        }
    }

    /**
     * Disconnect from the current room
     */
    public void disconnect() {
        networkManager.disconnect();
    }

    /**
     * Set the GUI frame reference
     */
    public void setMainFrame(MainFrame frame) {
        this.mainFrame = frame;
    }

    /**
     * Get the network manager
     */
    public NetworkManager getNetworkManager() {
        return networkManager;
    }

    /**
     * Main entry point - launches the GUI
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