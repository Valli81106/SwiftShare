package com.swiftshare.network.discovery;

import java.net.*;
import java.io.*;

// finds rooms on the local network
public class NetworkDiscovery {
    private static final int DISCOVERY_PORT = 8888;
    private static final String MAGIC_STRING = "SWIFTSHARE_ROOM:";
    private DatagramSocket socket;
    private boolean listening;

    // broadcast that we have a room
    public void announce(int roomPort) throws IOException {
        socket = new DatagramSocket();
        socket.setBroadcast(true);

        String message = MAGIC_STRING + roomPort;
        byte[] data = message.getBytes();

        InetAddress broadcast = InetAddress.getByName("255.255.255.255");
        DatagramPacket packet = new DatagramPacket(data, data.length, broadcast, DISCOVERY_PORT);

        socket.send(packet);
        System.out.println("Room announced on port " + roomPort);
        socket.close();
    }

    // listen for room announcements
    public String listen(int timeoutMs) throws IOException {
        DatagramSocket listenSocket = new DatagramSocket(DISCOVERY_PORT);
        listenSocket.setSoTimeout(timeoutMs);

        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        try {
            System.out.println("Listening for rooms...");
            listenSocket.receive(packet);

            String message = new String(packet.getData(), 0, packet.getLength());

            if (message.startsWith(MAGIC_STRING)) {
                String port = message.substring(MAGIC_STRING.length());
                String host = packet.getAddress().getHostAddress();

                System.out.println("Found room at " + host + ":" + port);
                listenSocket.close();
                return host + ":" + port;
            }
        } catch (SocketTimeoutException e) {
            System.out.println("No rooms found");
        }

        listenSocket.close();
        return null;
    }

    // keep listening in background
    public void startListening(DiscoveryCallback callback) {
        listening = true;
        new Thread(() -> {
            try {
                DatagramSocket listenSocket = new DatagramSocket(DISCOVERY_PORT);
                listenSocket.setSoTimeout(1000);

                byte[] buffer = new byte[1024];

                while (listening) {
                    try {
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                        listenSocket.receive(packet);

                        String message = new String(packet.getData(), 0, packet.getLength());

                        if (message.startsWith(MAGIC_STRING)) {
                            String port = message.substring(MAGIC_STRING.length());
                            String host = packet.getAddress().getHostAddress();

                            if (callback != null) {
                                callback.onRoomDiscovered(host, Integer.parseInt(port));
                            }
                        }
                    } catch (SocketTimeoutException e) {
                        // keep listening
                    }
                }

                listenSocket.close();
            } catch (IOException e) {
                System.err.println("Discovery error: " + e.getMessage());
            }
        }).start();
    }

    public void stopListening() {
        listening = false;
    }

    public interface DiscoveryCallback {
        void onRoomDiscovered(String host, int port);
    }
}