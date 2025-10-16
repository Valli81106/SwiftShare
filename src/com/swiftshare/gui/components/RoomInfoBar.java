package com.swiftshare.gui.components;

import com.swiftshare.gui.utils.UIConstants;
import com.swiftshare.models.RoomInfo;

import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.time.LocalDateTime;

public class RoomInfoBar extends JPanel {
    private JLabel roomIdLabel;
    private JLabel timerLabel;
    private JLabel peerCountLabel;
    private Timer countdownTimer;
    private LocalDateTime expiryTime;
    
    public RoomInfoBar() {
        setupPanel();
        createComponents();
    }
    
    private void setupPanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 30, 15));
        setBackground(UIConstants.PRIMARY_COLOR);
        setPreferredSize(new Dimension(0, 60));
    }
    
    private void createComponents() {
        roomIdLabel = new JLabel("Room ID: ----");
        roomIdLabel.setForeground(Color.WHITE);
        roomIdLabel.setFont(UIConstants.HEADER_FONT);
        
        timerLabel = new JLabel("⏱ Time Left: --:--");
        timerLabel.setForeground(Color.WHITE);
        timerLabel.setFont(UIConstants.NORMAL_FONT);
        
        peerCountLabel = new JLabel("👥 Peers: 0");
        peerCountLabel.setForeground(Color.WHITE);
        peerCountLabel.setFont(UIConstants.NORMAL_FONT);
        
        add(roomIdLabel);
        add(Box.createHorizontalStrut(20));
        add(timerLabel);
        add(Box.createHorizontalStrut(20));
        add(peerCountLabel);
    }
    
    public void setRoomInfo(RoomInfo roomInfo) {
        roomIdLabel.setText("Room ID: " + roomInfo.getRoomId());
        this.expiryTime = roomInfo.getExpiryTime();
        updatePeerCount(0);
    }
    
    public void startTimer() {
        if (countdownTimer != null) {
            countdownTimer.stop();
        }
        
        countdownTimer = new Timer(1000, e -> updateTimer());
        countdownTimer.start();
        updateTimer();
    }
    
    public void stopTimer() {
        if (countdownTimer != null) {
            countdownTimer.stop();
            countdownTimer = null;
        }
    }
    
    private void updateTimer() {
        if (expiryTime == null) {
            timerLabel.setText("⏱ Time Left: --:--");
            return;
        }
        
        Duration remaining = Duration.between(LocalDateTime.now(), expiryTime);
        
        if (remaining.isNegative() || remaining.isZero()) {
            timerLabel.setText("⏱ Time Left: EXPIRED");
            timerLabel.setForeground(UIConstants.DANGER_COLOR);
            stopTimer();
            return;
        }
        
        long hours = remaining.toHours();
        long minutes = remaining.toMinutes() % 60;
        long seconds = remaining.getSeconds() % 60;
        
        String timeText;
        if (hours > 0) {
            timeText = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            timeText = String.format("%02d:%02d", minutes, seconds);
        }
        
        timerLabel.setText("⏱ Time Left: " + timeText);
        
        if (remaining.toMinutes() < 5) {
            timerLabel.setForeground(Color.YELLOW);
        } else {
            timerLabel.setForeground(Color.WHITE);
        }
    }
    
    public void updatePeerCount(int count) {
        peerCountLabel.setText("👥 Peers: " + count);
    }
}