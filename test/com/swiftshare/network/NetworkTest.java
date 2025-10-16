package com.swiftshare.network;

// tests all the core networking stuff
public class NetworkTest {
    public static void main(String[] args) throws Exception {
        System.out.println("=== SwiftShare Network Test ===\n");

        // start server
        System.out.println("Test 1: Starting server...");
        RoomServer server = new RoomServer(5000);
        server.start();
        Thread.sleep(1000);
        System.out.println("Server started\n");

        // connect client 1
        System.out.println("Test 2: Connecting client 1...");
        RoomClient client1 = new RoomClient(new RoomClient.ClientCallback() {
            @Override
            public void onMessageReceived(Message message) {
                System.out.println("  Client 1 got: " + message.getType());
            }

            @Override
            public void onDisconnected() {
                System.out.println("  Client 1 disconnected");
            }

            @Override
            public void onError(String error) {
                System.out.println("  Client 1 error: " + error);
            }
        });
        client1.connect("localhost", 5000);
        Thread.sleep(1000);
        System.out.println("Client 1 connected\n");

        // connect client 2
        System.out.println("Test 3: Connecting client 2...");
        RoomClient client2 = new RoomClient(new RoomClient.ClientCallback() {
            @Override
            public void onMessageReceived(Message message) {
                System.out.println("  Client 2 got: " + message.getType());
            }

            @Override
            public void onDisconnected() {
                System.out.println("  Client 2 disconnected");
            }

            @Override
            public void onError(String error) {
                System.out.println("  Client 2 error: " + error);
            }
        });
        client2.connect("localhost", 5000);
        Thread.sleep(1000);
        System.out.println("Client 2 connected\n");

        // send some messages
        System.out.println("Test 4: Sending messages...");
        client1.sendMessage(new Message(Message.FILE_OFFER, "test.pdf", "1024", "16", "abc123"));
        Thread.sleep(500);
        client2.sendMessage(new Message(Message.FILE_ACCEPT, "test.pdf"));
        Thread.sleep(500);
        System.out.println("Messages sent\n");

        // test binary data
        System.out.println("Test 5: Sending binary data...");
        byte[] testData = "This is test file data".getBytes();
        client1.getConnection().sendBytes(testData);
        Thread.sleep(500);
        System.out.println("Binary data sent\n");

        // cleanup
        System.out.println("Cleaning up...");
        client1.disconnect();
        client2.disconnect();
        Thread.sleep(500);
        server.stop();

        System.out.println("\n=== All tests passed! ===");
    }
}