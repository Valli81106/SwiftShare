package com.swiftshare.network.core;

public class Message {

    // Message types
    public static final String JOIN_ROOM = "JOIN_ROOM";
    public static final String LEAVE_ROOM = "LEAVE_ROOM";
    public static final String PEER_JOINED = "PEER_JOINED";  // NEW
    public static final String PEER_LEFT = "PEER_LEFT";      // NEW
    public static final String FILE_OFFER = "FILE_OFFER";
    public static final String FILE_ACCEPT = "FILE_ACCEPT";
    public static final String FILE_REJECT = "FILE_REJECT";
    public static final String CHUNK_META = "CHUNK_META";
    public static final String CHUNK_ACK = "CHUNK_ACK";
    public static final String FILE_COMPLETE = "FILE_COMPLETE";
    public static final String HEARTBEAT = "HEARTBEAT";
    public static final String ERROR = "ERROR";

    private String type;
    private String[] data;
    private long timestamp;

    public Message(String type, String... data) {
        this.type = type;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    public String serialize() {
        StringBuilder sb = new StringBuilder();
        sb.append(type);

        if (data != null) {
            for (String item : data) {
                sb.append("|");
                if (item != null) {
                    sb.append(item);
                }
            }
        }
        return sb.toString();
    }

    public static Message deserialize(String raw) {
        if (raw == null || raw.isEmpty()) {
            return null;
        }
        String[] parts = raw.split("\\|", -1);
        String type = parts[0];

        String[] data = new String[parts.length - 1];
        for (int i = 0; i < data.length; i++) {
            data[i] = parts[i + 1];
        }
        return new Message(type, data);
    }

    public String getType() {
        return type;
    }

    public String[] getData() {
        return data;
    }

    public String getData(int index) {
        if (data != null && index < data.length) {
            return data[index];
        }
        return null;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "Message[" + type + "] at " + timestamp;
    }
}