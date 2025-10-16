package com.swiftshare;

import com.swiftshare.network.manager.*;
import com.swiftshare.models.*;
import com.swiftshare.gui.*; // GUI team's main class
import com.swiftshare.fileio.*; // File I/O team's classes
import java.io.File;

// Main integration point
public class SwiftShareApp {
    private NetworkManager networkManager;
    private FileIOManager fileIOManager; // From File I/O team
    private MainGUI mainGUI; // From GUI team

    public SwiftShareApp() {
        // Initialize networking with callbacks
        networkManager = new NetworkManager(new NetworkCallback() {
            @Override
            public void onRoomCreated(int port) {
                // Tell GUI room was created
                if (mainGUI != null) {
                    mainGUI.showRoomCreated(port);
                }
            }

            @Override
            public void onRoomJoined(String host, int port) {
                if (mainGUI != null) {
                    mainGUI.showRoomJoined(host, port);
                }
            }

            @Override
            public void onPeerConnected(PeerInfo peer) {
                if (mainGUI != null) {
                    mainGUI.addPeerToList(peer);
                }
            }

            @Override
            public void onPeerDisconnected(PeerInfo peer) {
                if (mainGUI != null) {
                    mainGUI.removePeerFromList(peer);
                }
            }

            @Override
            public void onFileOfferReceived(FileMetadata metadata) {
                if (mainGUI != null) {
                    // Ask user if they want to accept
                    boolean accept = mainGUI.showFileOfferDialog(metadata);
                    if (accept) {
                        networkManager.acceptFileOffer(metadata.getFileName());
                        // Start receiving
                        receiveFile(metadata);
                    } else {
                        networkManager.rejectFileOffer(metadata.getFileName());
                    }
                }
            }

            @Override
            public void onTransferProgress(String fileName, double progress, String speed) {
                if (mainGUI != null) {
                    mainGUI.updateProgress(progress, speed);
                }
            }

            @Override
            public void onTransferComplete(String fileName) {
                if (mainGUI != null) {
                    mainGUI.showTransferComplete(fileName);
                }
            }

            @Override
            public void onError(String error) {
                if (mainGUI != null) {
                    mainGUI.showError(error);
                }
            }

            @Override
            public void onConnectionLost() {
                if (mainGUI != null) {
                    mainGUI.showDisconnected();
                }
            }
        });

        // Initialize File I/O
        fileIOManager = new FileIOManager(); // From File I/O team
    }

    // GUI calls this when user clicks "Create Room"
    public void createRoom(int port) {
        networkManager.createRoom(port);
    }

    // GUI calls this when user clicks "Join Room"
    public void joinRoom(String host, int port) {
        networkManager.joinRoom(host, port);
    }

    // GUI calls this when user selects file to send
    public void sendFile(File file) {
        try {
            // Get chunks from File I/O team
            byte[][] chunks = fileIOManager.readFileChunks(file, 64 * 1024);

            // Get hash from Security team (for now, use placeholder)
            String hash = "placeholder_hash"; // TODO: Security team

            // Create metadata
            FileMetadata metadata = new FileMetadata(
                    file.getName(),
                    file.length(),
                    hash,
                    chunks.length
            );

            // Send via networking
            networkManager.sendFile(file, chunks, metadata);

        } catch (Exception e) {
            System.err.println("Error sending file: " + e.getMessage());
            if (mainGUI != null) {
                mainGUI.showError("Failed to send file: " + e.getMessage());
            }
        }
    }

    // Receive file
    private void receiveFile(FileMetadata metadata) {
        try {
            // TODO: This will be implemented when File I/O team provides receive method
            System.out.println("Receiving file: " + metadata.getFileName());
        } catch (Exception e) {
            System.err.println("Error receiving file: " + e.getMessage());
        }
    }

    // GUI calls this on exit
    public void disconnect() {
        networkManager.disconnect();
    }

    // Set GUI reference (GUI team calls this)
    public void setGUI(MainGUI gui) {
        this.mainGUI = gui;
    }

    public static void main(String[] args) {
        SwiftShareApp app = new SwiftShareApp();
        // GUI team will create their GUI and link it here
        // MainGUI gui = new MainGUI(app);
        // app.setGUI(gui);
        // gui.show();
    }
}