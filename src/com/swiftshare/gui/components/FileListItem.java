package com.swiftshare.gui.components;

import com.swiftshare.gui.utils.ComponentFactory;
import com.swiftshare.gui.utils.UIConstants;
import com.swiftshare.models.FileMetadata;

import javax.swing.*;
import java.awt.*;

public class FileListItem extends JPanel {
    private FileMetadata fileMetadata;
    
    public FileListItem(FileMetadata fileMetadata) {
        this.fileMetadata = fileMetadata;
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UIConstants.BORDER_COLOR),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        // File icon and name
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(Color.WHITE);
        
        JLabel fileNameLabel = new JLabel(fileMetadata.getFileName());
        fileNameLabel.setFont(UIConstants.SUBHEADER_FONT);
        
        JLabel fileDetailsLabel = new JLabel(getFileDetails());
        fileDetailsLabel.setFont(UIConstants.SMALL_FONT);
        fileDetailsLabel.setForeground(Color.GRAY);
        
        leftPanel.add(fileNameLabel, BorderLayout.NORTH);
        leftPanel.add(fileDetailsLabel, BorderLayout.SOUTH);
        
        // Download button
        JButton downloadBtn = ComponentFactory.createSecondaryButton("Download");
        downloadBtn.setFont(UIConstants.SMALL_FONT);
        
        add(leftPanel, BorderLayout.CENTER);
        add(downloadBtn, BorderLayout.EAST);
    }
    
    private String getFileDetails() {
        return String.format("%s • %d chunks • %s", 
            formatFileSize(fileMetadata.getFileSize()),
            fileMetadata.getTotalChunks(),
            fileMetadata.getSenderId() != null ? fileMetadata.getSenderId() : "Unknown"
        );
    }
    
    private String formatFileSize(long size) {
        if (size < 1024) return size + " B";
        else if (size < 1024 * 1024) return String.format("%.1f KB", size / 1024.0);
        else return String.format("%.1f MB", size / (1024.0 * 1024.0));
    }
    
    public FileMetadata getFileMetadata() {
        return fileMetadata;
    }
}