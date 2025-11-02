package com.swiftshare;

import com.swiftshare.network.manager.*;
import com.swiftshare.models.*;
import com.swiftshare.gui.frames.MainFrame;
import com.swiftshare.fileio.core.*;
import javax.swing.*;

public class SwiftShareApp {
    private NetworkManager networkManager;
    private FileChunkManager fileChunkManager;
    private MainFrame mainFrame;

    public SwiftShareApp() {
        fileChunkManager = new FileChunkManager();

        networkManager = new NetworkManager(new NetworkCallback() {
            @Override
            public void onRoomCreated(int port) {
                System.out.println("Room created on port: " + port);
            }
            @Override
            public void onRoomJoined(String host, int port) {
                System.out.println("Joined room at " + host + ":" + port);
            }
            @Override
            public void onPeerConnected(PeerInfo peer) {
                System.out.println("Peer connected: " + peer);
            }
            @Override
            public void onPeerDisconnected(PeerInfo peer) {
                System.out.println("Peer disconnected: " + peer);
            }
            @Override
            public void onFileOfferReceived(FileMetadata metadata) {
                System.out.println("File offer: " + metadata.getFileName());
            }
            @Override
            public void onTransferProgress(String fileName, double percent, String speed) {
                System.out.println("Progress: " + fileName + " - " + percent + "%");
            }
            @Override
            public void onTransferComplete(String fileName) {
                System.out.println("Complete: " + fileName);
            }
            @Override
            public void onConnectionLost() {
                System.out.println("Connection lost");
            }
            @Override
            public void onError(String error) {
                System.err.println("Error: " + error);
            }
        });

        SwingUtilities.invokeLater(() -> {
            mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }

    public static void main(String[] args) {
        System.out.println("Starting SwiftShare...");
        new SwiftShareApp();
    }
}
