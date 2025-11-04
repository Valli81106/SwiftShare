package com.swiftshare.network.utils;

import com.swiftshare.network.core.Message;
import com.swiftshare.network.core.RoomClient;

import java.util.concurrent.*;

// keeps connection alive by sending heartbeats
public class HeartbeatManager {
    private ScheduledExecutorService scheduler;
    private RoomClient client;
    private boolean running;
    private int intervalSeconds;

    public HeartbeatManager(RoomClient client, int intervalSeconds) {
        this.client = client;
        this.intervalSeconds = intervalSeconds;
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.running = false;
    }

    public void start() {
        if (running) {
            return;
        }

        running = true;
        scheduler.scheduleAtFixedRate(() -> {
            if (client != null && client.isConnected()) {
                try {
                    client.sendMessage(new Message(Message.HEARTBEAT));
                    System.out.println("Heartbeat sent");
                } catch (Exception e) {
                    System.err.println("Failed to send heartbeat: " + e.getMessage());
                }
            }
        }, intervalSeconds, intervalSeconds, TimeUnit.SECONDS);

        System.out.println("Heartbeat started (every " + intervalSeconds + "s)");
    }

    public void stop() {
        running = false;
        scheduler.shutdown();
        try {
            scheduler.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
        System.out.println("Heartbeat stopped");
    }

    public boolean isRunning() {
        return running;
    }
}