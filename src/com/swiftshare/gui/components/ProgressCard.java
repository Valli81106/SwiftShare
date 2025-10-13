package com.swiftshare.gui.components;

import com.swiftshare.gui.utils.UIConstants;
import com.swiftshare.models.TransferStatus;

import javax.swing.*;
import java.awt.*;

public class ProgressCard extends JPanel {
    private TransferStatus transferStatus;
    private JProgressBar progressBar;
    private JLabel statusLabel;
    private JLabel detailsLabel;
    
    public ProgressCard(TransferStatus status) {
        this.transferStatus = status;
        initComponents();
        updateProgress();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.BORDER_COLOR),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        // Top row: file name and status
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        
        JLabel fileNameLabel = new JLabel("File: " + getFileNameFromStatus());
        fileNameLabel.setFont(UIConstants.BODY_FONT);
        
        statusLabel = new JLabel();
        statusLabel.setFont(UIConstants.SMALL_FONT);
        
        topPanel.add(fileNameLabel, BorderLayout.WEST);
        topPanel.add(statusLabel, BorderLayout.EAST);
        
        // Progress bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setPreferredSize(new Dimension(300, 20));
        
        // Details row
        detailsLabel = new JLabel();
        detailsLabel.setFont(UIConstants.SMALL_FONT);
        detailsLabel.setForeground(Color.GRAY);
        
        // Control buttons
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controlPanel.setBackground(Color.WHITE);
        
        JButton pauseBtn = new JButton("Pause");
        JButton cancelBtn = new JButton("Cancel");
        
        pauseBtn.setFont(UIConstants.SMALL_FONT);
        cancelBtn.setFont(UIConstants.SMALL_FONT);
        
        controlPanel.add(pauseBtn);
        controlPanel.add(cancelBtn);
        
        // Layout
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(progressBar);
        centerPanel.add(Box.createVerticalStrut(5));
        centerPanel.add(detailsLabel);
        
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
    }
    
    private String getFileNameFromStatus() {
        // This would come from TransferStatus in real implementation
        return "example_file.txt";
    }
    
    public void updateProgress() {
        int progress = (int) transferStatus.getProgress();
        progressBar.setValue(progress);
        
        statusLabel.setText(transferStatus.getState().toString());
        statusLabel.setForeground(getStatusColor(transferStatus.getState()));
        
        String details = String.format("%d/%d chunks • %.2f MB/s • %s transferred",
            transferStatus.getChunksTransferred(),
            transferStatus.getTotalChunks(),
            transferStatus.getSpeedMBps(),
            formatBytes(transferStatus.getBytesTransferred())
        );
        detailsLabel.setText(details);
    }
    
    private Color getStatusColor(TransferStatus.State state) {
        switch (state) {
            case IN_PROGRESS: return UIConstants.PRIMARY_COLOR;
            case COMPLETED: return UIConstants.ACCENT_COLOR;
            case FAILED: return UIConstants.DANGER_COLOR;
            case PAUSED: return UIConstants.WARNING_COLOR;
            default: return Color.GRAY;
        }
    }
    
    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        else if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        else return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
    }
    
    public void setTransferStatus(TransferStatus status) {
        this.transferStatus = status;
        updateProgress();
    }
}