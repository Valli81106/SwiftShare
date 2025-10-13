package com.swiftshare.gui.utils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ComponentFactory {
    
    public static JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(UIConstants.BODY_FONT);
        button.setBackground(UIConstants.PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    public static JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(UIConstants.BODY_FONT);
        button.setBackground(Color.WHITE);
        button.setForeground(UIConstants.PRIMARY_COLOR);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.PRIMARY_COLOR),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(UIConstants.CARD_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UIConstants.BORDER_COLOR),
            BorderFactory.createEmptyBorder(UIConstants.PADDING, UIConstants.PADDING, UIConstants.PADDING, UIConstants.PADDING)
        ));
        return panel;
    }
    
    public static JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIConstants.TITLE_FONT);
        label.setForeground(UIConstants.PRIMARY_COLOR);
        return label;
    }
    
    public static JLabel createHeaderLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIConstants.HEADER_FONT);
        return label;
    }
}