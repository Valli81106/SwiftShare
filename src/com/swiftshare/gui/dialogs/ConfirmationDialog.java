package com.swiftshare.gui.dialogs;

import com.swiftshare.gui.utils.UIConstants;

import javax.swing.*;
import java.awt.*;

public class ConfirmationDialog extends JDialog {
    private boolean confirmed = false;
    
    public ConfirmationDialog(Frame parent, String title, String message) {
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
        
        // Question icon and message
        JLabel iconLabel = new JLabel("❓");
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 48));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JTextArea messageArea = new JTextArea(message);
        messageArea.setFont(UIConstants.NORMAL_FONT);
        messageArea.setEditable(false);
        messageArea.setOpaque(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        
        JButton yesButton = new JButton("Yes");
        yesButton.setFont(UIConstants.BUTTON_FONT);
        yesButton.setBackground(UIConstants.PRIMARY_COLOR);
        yesButton.setForeground(Color.WHITE);
        yesButton.setFocusPainted(false);
        yesButton.addActionListener(e -> {
            confirmed = true;
            dispose();
        });
        
        JButton noButton = new JButton("No");
        noButton.setFont(UIConstants.BUTTON_FONT);
        noButton.setBackground(UIConstants.TEXT_SECONDARY);
        noButton.setForeground(Color.WHITE);
        noButton.setFocusPainted(false);
        noButton.addActionListener(e -> dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(noButton);
        buttonPanel.add(yesButton);
        
        mainPanel.add(iconLabel, BorderLayout.WEST);
        mainPanel.add(messageArea, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public static boolean show(Frame parent, String title, String message) {
        ConfirmationDialog dialog = new ConfirmationDialog(parent, title, message);
        dialog.setVisible(true);
        return dialog.isConfirmed();
    }
}