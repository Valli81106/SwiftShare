package com.swiftshare.gui.panels;

import com.swiftshare.gui.utils.UIConstants;

import javax.swing.*;
import java.awt.*;

public class SettingsPanel extends JPanel {
    
    public SettingsPanel() {
        setupPanel();
        createComponents();
    }
    
    private void setupPanel() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.BACKGROUND_COLOR);
    }
    
    private void createComponents() {
        JLabel titleLabel = new JLabel("Settings");
        titleLabel.setFont(UIConstants.HEADER_FONT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(titleLabel, BorderLayout.NORTH);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(Color.WHITE);
        
        contentPanel.add(createSettingRow("Download Location:", "Choose folder..."));
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(createSettingRow("Max File Size:", "100 MB"));
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(createSettingRow("Default Room Duration:", "30 minutes"));
        
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private JPanel createSettingRow(String label, String value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        row.setBackground(Color.WHITE);
        
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(UIConstants.NORMAL_FONT);
        labelComponent.setPreferredSize(new Dimension(200, 30));
        
        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(UIConstants.NORMAL_FONT);
        valueComponent.setForeground(UIConstants.TEXT_SECONDARY);
        
        row.add(labelComponent);
        row.add(valueComponent);
        
        return row;
    }
}