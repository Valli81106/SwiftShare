package com.swiftshare.network;

// tests the full NetworkManager API (what GUI will use)
public class IntegrationTest {
    public static void main(String[] args) throws Exception {
        System.out.println("=== SwiftShare Integration Test ===\n");

        // create host
        System.out.println("Step 1: Creating room (Host)...");
        NetworkManager host = new NetworkManager(new NetworkCallback() {
            @Override
            public void onRoomCreated(int port) {
                System.out.println("  Room created on port " + port);
            }

            @Override
            public void onRoomJoined(String host, int port) {}

            @Override
            public void onPeerConnected(PeerInfo peer) {
                System.out.println("  Peer connected: " + peer.getIpAddress());
            }

            @Override
            public void onPeerDisconnected(PeerInfo peer) {
                System.out.println("  Peer disconnected: " + peer.getIpAddress());
            }

            @Override
            public void onFileOfferReceived(FileMetadata metadata) {
                System.out.println("  File offer: " + metadata.getFileName());
            }

            @Override
            public void onTransferProgress(String fileName, double progress, String speed) {
                System.out.println("  Progress: " + progress + "% @ " + speed);
            }

            @Override
            public void onTransferComplete(String fileName) {
                System.out.println("  Transfer done: " + fileName);
            }

            @Override
            public void onError(String error) {
                System.out.println("  Error: " + error);
            }

            @Override
            public void onConnectionLost() {
                System.out.println("  Connection lost");
            }
        });

        host.createRoom(5000);
        Thread.sleep(2000);

        // create client
        System.out.println("\nStep 2: Joining room (Client)...");
        NetworkManager client = new NetworkManager(new NetworkCallback() {
            @Override
            public void onRoomCreated(int port) {}

            @Override
            public void onRoomJoined(String host, int port) {
                System.out.println("  Joined room at " + host + ":" + port);
            }

            @Override
            public void onPeerConnected(PeerInfo peer) {}

            @Override
            public void onPeerDisconnected(PeerInfo peer) {}

            @Override
            public void onFileOfferReceived(FileMetadata metadata) {
                System.out.println("  File offer received: " + metadata.getFileName());
            }

            @Override
            public void onTransferProgress(String fileName, double progress, String speed) {
                System.out.println("  Receiving: " + progress + "% @ " + speed);
            }

            @Override
            public void onTransferComplete(String fileName) {
                System.out.println("  File received: " + fileName);
            }

            @Override
            public void onError(String error) {
                System.out.println("  Error: " + error);
            }

            @Override
            public void onConnectionLost() {
                System.out.println("  Connection lost");
            }
        });

        client.joinRoom("localhost", 5000);
        Thread.sleep(2000);

        // simulate file transfer
        System.out.println("\nStep 3: Simulating file transfer...");
        FileMetadata metadata = new FileMetadata("test.pdf", 1024, "abc123", 16);
        byte[][] fakeChunks = new byte[16][64];
        for (int i = 0; i < 16; i++) {
            fakeChunks[i] = new byte[64];
            for (int j = 0; j < 64; j++) {
                fakeChunks[i][j] = (byte) i;
            }
        }

        // in real usage, File I/O team provides chunks
        // client.sendFile(new File("test.pdf"), fakeChunks, metadata);

        Thread.sleep(3000);

        // cleanup
        System.out.println("\nStep 4: Cleanup...");
        client.disconnect();
        Thread.sleep(500);
        host.disconnect();

        System.out.println("\n=== Integration test complete! ===");
    }
}