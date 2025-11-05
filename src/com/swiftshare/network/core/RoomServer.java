package com.swiftshare.network.core;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

// server that hosts a file sharing room
public class RoomServer {

    // callback for when stuff happens
    public interface ServerCallback {
        void onPeerConnected(PeerConnection peer);
        void onPeerDisconnected(PeerConnection peer);
        void onMessageReceived(PeerConnection sender, Message message);
    }

    private int port;
    private ServerSocket serverSocket;
    private List<PeerConnection> connectedPeers;
    private boolean running;
    private ExecutorService threadPool;
    private ServerCallback callback;

    public RoomServer(int port) {
        this.port = port;
        this.connectedPeers = new CopyOnWriteArrayList<>();
        this.threadPool = Executors.newCachedThreadPool();
    }

    public void setCallback(ServerCallback callback) {
        this.callback = callback;
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        serverSocket.setReuseAddress(true);
        running = true;

        System.out.println("Room server started on port " + port);

        // start accepting connections in background
        threadPool.submit(this::acceptConnections);
    }

    private void acceptConnections() {
        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New connection from: " +
                        clientSocket.getInetAddress().getHostAddress());

                PeerConnection peer = new PeerConnection(clientSocket);
                connectedPeers.add(peer);

                if (callback != null) {
                    callback.onPeerConnected(peer);
                }

                // handle this peer in separate thread
                threadPool.submit(() -> handlePeer(peer));

            } catch (SocketException e) {
                if (running) {
                    System.err.println("Socket error: " + e.getMessage());
                }
            } catch (IOException e) {
                if (running) {
                    System.err.println("Accept error: " + e.getMessage());
                }
            }
        }
    }
    private void handlePeer(PeerConnection peer) {
        try {
            while (peer.isConnected() && running) {
                Message message = peer.receiveMessage();

                if (message == null) {
                    break;
                }

                System.out.println("Received " + message.getType() +
                        " from " + peer.getPeerAddress());

                if (callback != null) {
                    callback.onMessageReceived(peer, message);
                } else {
                    // default: just forward to everyone else
                    broadcastToOthers(peer, message);
                }
            }
        } catch (IOException e) {
            System.out.println("Peer disconnected: " + peer.getPeerAddress());
        } finally {
            connectedPeers.remove(peer);
            peer.close();

            if (callback != null) {
                callback.onPeerDisconnected(peer);
            }
        }
    }
    // send to everyone except the sender
    public void broadcastToOthers(PeerConnection sender, Message message) {
        for (PeerConnection peer : connectedPeers) {
            if (peer != sender && peer.isConnected()) {
                try {
                    peer.sendMessage(message);
                } catch (IOException e) {
                    System.err.println("Failed to send to peer: " + e.getMessage());
                }
            }
        }
    }
    // send to everyone including sender
    public void broadcast(Message message) {
        for (PeerConnection peer : connectedPeers) {
            if (peer.isConnected()) {
                try {
                    peer.sendMessage(message);
                } catch (IOException e) {
                    System.err.println("Failed to broadcast: " + e.getMessage());
                }
            }
        }
    }

    public void stop() {
        running = false;

        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing server: " + e.getMessage());
        }

        for (PeerConnection peer : connectedPeers) {
            peer.close();
        }
        connectedPeers.clear();

        threadPool.shutdown();
        try {
            threadPool.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
        }

        System.out.println("Room server stopped");
    }

    public int getConnectedPeerCount() {
        return connectedPeers.size();
    }
    public List<PeerConnection> getConnectedPeers() {
        return new ArrayList<>(connectedPeers);
    }
}