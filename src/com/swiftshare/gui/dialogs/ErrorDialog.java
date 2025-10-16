package com.swiftshare.gui.dialogs;

import com.swiftshare.gui.utils.UIConstants;

import javax.swing.*;
import java.awt.*;

public class ErrorDialog extends JDialog {
    
    public ErrorDialog(Frame parent, String title, String message) {
        super(parent, title, true);
        setupDialog();
        createComponents(message);
    }
    
    private void setupDialog() {
        setSize(400, 200);
        setLocationRelativeTo(getParent());
        setResizable(false);
    }
    
    private void createComponents(String message) {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        
        // Error icon and message
        JLabel iconLabel = new JLabel("⚠️");
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 48));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JTextArea messageArea = new JTextArea(message);
        messageArea.setFont(UIConstants.NORMAL_FONT);
        messageArea.setEditable(false);
        messageArea.setOpaque(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        
        JButton okButton = new JButton("OK");
        okButton.setFont(UIConstants.BUTTON_FONT);
        okButton.setBackground(UIConstants.DANGER_COLOR);
        okButton.setForeground(Color.WHITE);
        okButton.setFocusPainted(false);
        okButton.addActionListener(e -> dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(okButton);
        
        mainPanel.add(iconLabel, BorderLayout.WEST);
        mainPanel.add(messageArea, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    public static void show(Frame parent, String title, String message) {
        ErrorDialog dialog = new ErrorDialog(parent, title, message);
        dialog.setVisible(true);
    }
}