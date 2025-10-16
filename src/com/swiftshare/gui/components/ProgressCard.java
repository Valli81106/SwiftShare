package com.swiftshare.gui.components;

import com.swiftshare.gui.utils.UIConstants;
import com.swiftshare.models.TransferStatus;

import javax.swing.*;
import java.awt.*;

public class ProgressCard extends JPanel {
    private TransferStatus status;
    private JLabel fileNameLabel;
    private JProgressBar progressBar;
    private JLabel statusLabel;
    private JLabel speedLabel;
    
    public ProgressCard(TransferStatus status) {
        this.status = status;
        setupPanel();
        createComponents();
        updateStatus(status);
    }
    
    private void setupPanel() {
        setLayout(new BorderLayout(10, 5));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.PRIMARY_COLOR, 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        setBackground(new Color(230, 240, 255));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
    }
    
    private void createComponents() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        
        fileNameLabel = new JLabel();
        fileNameLabel.setFont(UIConstants.NORMAL_FONT);
        
        statusLabel = new JLabel();
        statusLabel.setFont(UIConstants.SMALL_FONT);
        statusLabel.setForeground(UIConstants.TEXT_SECONDARY);
        
        topPanel.add(fileNameLabel, BorderLayout.WEST);
        topPanel.add(statusLabel, BorderLayout.EAST);
        
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(0, 25));
        progressBar.setForeground(UIConstants.PRIMARY_COLOR);
        
        speedLabel = new JLabel();
        speedLabel.setFont(UIConstants.SMALL_FONT);
        speedLabel.setForeground(UIConstants.TEXT_SECONDARY);
        speedLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        add(topPanel, BorderLayout.NORTH);
        add(progressBar, BorderLayout.CENTER);
        add(speedLabel, BorderLayout.SOUTH);
    }
    
    public void updateStatus(TransferStatus status) {
        this.status = status;
        
        fileNameLabel.setText(status.getFileName());
        progressBar.setValue(status.getProgressPercentage());
        progressBar.setString(status.getProgressPercentage() + "%");
        statusLabel.setText(status.getState().toString());
        speedLabel.setText(status.getFormattedSpeed());
        
        if (status.isComplete()) {
            progressBar.setForeground(UIConstants.SUCCESS_COLOR);
            statusLabel.setText("COMPLETED");
        } else if (status.getState() == TransferStatus.State.FAILED) {
            progressBar.setForeground(UIConstants.DANGER_COLOR);
        }
    }
}