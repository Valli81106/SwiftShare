package com.swiftshare.network.core;

import java.net.*;
import java.io.*;

// handles connection to a single peer
public class PeerConnection {
    private Socket socket;
    private BufferedReader textReader;
    private PrintWriter textWriter;
    private DataInputStream dataReader;
    private DataOutputStream dataWriter;
    private String peerId;
    private boolean connected;

    // for when someone connects to us
    public PeerConnection(Socket socket) throws IOException {
        this.socket = socket;
        this.peerId = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
        this.connected = true;
        setupStreams();
    }

    // for when we connect to someone else
    public PeerConnection(String host, int port) throws IOException {
        this.socket = new Socket();
        this.socket.connect(new InetSocketAddress(host, port), 10000);
        this.peerId = host + ":" + port;
        this.connected = true;
        setupStreams();
    }

    private void setupStreams() throws IOException {
        // streams for text messages
        textReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        textWriter = new PrintWriter(
                new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),
                true
        );

        // streams for binary data (file chunks)
        dataReader = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        dataWriter = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }

    // send a text message
    public synchronized void sendMessage(Message message) throws IOException {
        if (!connected) {
            throw new IOException("Not connected");
        }
        textWriter.println(message.serialize());
        if (textWriter.checkError()) {
            throw new IOException("Error writing message");
        }
    }

    // wait for and receive a text message
    public Message receiveMessage() throws IOException {
        String raw = textReader.readLine();
        if (raw == null) {
            connected = false;
            throw new IOException("Connection closed");
        }
        return Message.deserialize(raw);
    }
    // send binary data (like file chunks)
    public synchronized void sendBytes(byte[] data) throws IOException {
        if (!connected) {
            throw new IOException("Not connected");
        }
        dataWriter.writeInt(data.length);
        dataWriter.write(data);
        dataWriter.flush();
    }
    // receive binary data
    public byte[] receiveBytes() throws IOException {
        int length = dataReader.readInt();
        // 'sanity check' to not allow huge chunks
        if (length < 0 || length > 10 * 1024 * 1024) {
            throw new IOException("Invalid chunk size: " + length);
        }
        byte[] data = new byte[length];
        dataReader.readFully(data);
        return data;
    }
    public boolean isConnected() {
        return connected && socket != null && !socket.isClosed();
    }
    public String getPeerId() {
        return peerId;
    }
    public String getPeerAddress() {
        if (socket != null) {
            return socket.getInetAddress().getHostAddress();
        }
        return "unknown";
    }
    public int getPeerPort() {
        if (socket != null) {
            return socket.getPort();
        }
        return -1;
    }
    public void close() {
        connected = false;
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}