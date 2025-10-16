package com.swiftshare.gui.components;

import com.swiftshare.gui.utils.UIConstants;
import com.swiftshare.models.FileMetadata;

import javax.swing.*;
import java.awt.*;

public class FileListItem extends JPanel {
    private FileMetadata fileMetadata;
    private JLabel fileNameLabel;
    private JLabel fileSizeLabel;
    private JButton downloadButton;
    
    public FileListItem(FileMetadata fileMetadata) {
        this.fileMetadata = fileMetadata;
        setupPanel();
        createComponents();
    }
    
    private void setupPanel() {
        setLayout(new BorderLayout(10, 5));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.TEXT_SECONDARY, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        setBackground(Color.WHITE);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
    }
    
    private void createComponents() {
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        
        fileNameLabel = new JLabel(fileMetadata.getFileName());
        fileNameLabel.setFont(UIConstants.NORMAL_FONT);
        
        fileSizeLabel = new JLabel(fileMetadata.getFormattedFileSize());
        fileSizeLabel.setFont(UIConstants.SMALL_FONT);
        fileSizeLabel.setForeground(UIConstants.TEXT_SECONDARY);
        
        infoPanel.add(fileNameLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(fileSizeLabel);
        
        downloadButton = new JButton("Download");
        downloadButton.setFont(UIConstants.SMALL_FONT);
        downloadButton.setBackground(UIConstants.PRIMARY_COLOR);
        downloadButton.setForeground(Color.WHITE);
        downloadButton.setFocusPainted(false);
        downloadButton.addActionListener(e -> handleDownload());
        
        add(infoPanel, BorderLayout.CENTER);
        add(downloadButton, BorderLayout.EAST);
    }
    
    private void handleDownload() {
        // TODO: Integrate with networking layer
        JOptionPane.showMessageDialog(this, "Download started for: " + fileMetadata.getFileName());
    }
}