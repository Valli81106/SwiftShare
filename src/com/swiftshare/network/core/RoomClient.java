package com.swiftshare.network.core;

import java.io.*;
import java.util.concurrent.*;

// client that connects to a room server
public class RoomClient {
    // callback for events
    public interface ClientCallback {
        void onMessageReceived(Message message);
        void onDisconnected();
        void onError(String error);
    }
    private PeerConnection connection;
    private ExecutorService listenerThread;
    private ClientCallback callback;
    private boolean connected;

    public RoomClient(ClientCallback callback) {
        this.callback = callback;
        this.listenerThread = Executors.newSingleThreadExecutor();
    }
    public boolean connect(String host, int port) {
        try {
            System.out.println("Connecting to " + host + ":" + port + "...");

            connection = new PeerConnection(host, port);
            connected = true;

            System.out.println("Connected!");

            // start listening for messages in background
            listenerThread.submit(this::listenForMessages);

            return true;

        } catch (IOException e) {
            System.err.println("Connection failed: " + e.getMessage());
            if (callback != null) {
                callback.onError("Failed to connect: " + e.getMessage());
            }
            return false;
        }
    }
    private void listenForMessages() {
        try {
            while (connected && connection.isConnected()) {
                Message message = connection.receiveMessage();

                if (message == null) {
                    break;
                }

                System.out.println("Received message: " + message.getType());

                if (callback != null) {
                    callback.onMessageReceived(message);
                }
            }
        } catch (IOException e) {
            System.err.println("Connection lost: " + e.getMessage());
        }
        finally {
            disconnect();
            if (callback != null) {
                callback.onDisconnected();
            }
            private void startExpiryCheck() {
                new Thread(() -> {
                    while (isHost && server != null) {
                        try {
                            Thread.sleep(60000); // check every minute

                            if (System.currentTimeMillis() > roomExpiryTime) {
                                System.out.println("Room expired!");
                                if (callback != null) {
                                    callback.onError("Room expired after 24 hours");
                                }
                                disconnect();
                                break;
                            }
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                }).start();
            }

// GUI can call this to get remaining time
            public long getRemainingTime() {
                if (!isHost) return -1;
                long remaining = roomExpiryTime - System.currentTimeMillis();
                return remaining > 0 ? remaining : 0;
            }

// GUI can call this to format time nicely
            public String getRemainingTimeString() {
                long ms = getRemainingTime();
                if (ms <= 0) return "Expired";

                long hours = ms / (60 * 60 * 1000);
                long minutes = (ms % (60 * 60 * 1000)) / (60 * 1000);

                return hours + "h " + minutes + "m remaining";
            }

// Allow custom duration (optional)
            public boolean createRoom(int port, long durationMs) {
                boolean success = createRoom(port);
                if (success) {
                    roomExpiryTime = System.currentTimeMillis() + durationMs;
                }
                return success;
            }
        }
    }
    public void sendMessage(Message message) {
        if (!connected || connection == null) {
            System.err.println("Cannot send: not connected");
            return;
        }

        try {
            connection.sendMessage(message);
        } catch (IOException e) {
            System.err.println("Failed to send message: " + e.getMessage());
            if (callback != null) {
                callback.onError("Failed to send message");
            }
        }
    }
    public PeerConnection getConnection() {
        return connection;
    }
    public void disconnect() {
        connected = false;
        if (connection != null) {
            connection.close();
        }
        listenerThread.shutdown();
    }
    public boolean isConnected() {
        return connected && connection != null && connection.isConnected();
    }
}