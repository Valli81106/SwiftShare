package com.swiftshare.gui.utils;

import java.awt.*;

/**
 * Central place for all UI constants - colors, fonts, sizes
 */
public class UIConstants {
    
    // Window Dimensions
    public static final int WINDOW_WIDTH = 1200;
    public static final int WINDOW_HEIGHT = 800;
    
    // Colors
    public static final Color PRIMARY_COLOR = new Color(41, 128, 185);      // Blue
    public static final Color PRIMARY_DARK = new Color(31, 97, 141);        // Darker blue
    public static final Color SECONDARY_COLOR = new Color(52, 152, 219);    // Light blue
    public static final Color SECONDARY_DARK = new Color(41, 128, 185);     // Darker light blue
    public static final Color BACKGROUND_COLOR = new Color(236, 240, 241);  // Light gray
    public static final Color DANGER_COLOR = new Color(231, 76, 60);        // Red
    public static final Color SUCCESS_COLOR = new Color(46, 204, 113);      // Green
    public static final Color WARNING_COLOR = new Color(241, 196, 15);      // Yellow
    public static final Color TEXT_PRIMARY = new Color(44, 62, 80);         // Dark gray
    public static final Color TEXT_SECONDARY = new Color(127, 140, 141);    // Medium gray
    public static final Color SELECTED_COLOR = new Color(189, 195, 199);    // Selection gray
    
    // Fonts
    public static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 48);
    public static final Font SUBTITLE_FONT = new Font("Arial", Font.PLAIN, 18);
    public static final Font HEADER_FONT = new Font("Arial", Font.BOLD, 20);
    public static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 16);
    public static final Font NORMAL_FONT = new Font("Arial", Font.PLAIN, 14);
    public static final Font SMALL_FONT = new Font("Arial", Font.PLAIN, 12);
    
    // Component Sizes
    public static final int BUTTON_HEIGHT = 40;
    public static final int INPUT_HEIGHT = 35;
    public static final int DIALOG_WIDTH = 450;
    public static final int DIALOG_HEIGHT = 300;
    
    // Spacing
    public static final int PADDING_SMALL = 5;
    public static final int PADDING_MEDIUM = 10;
    public static final int PADDING_LARGE = 20;
    
    // Private constructor to prevent instantiation
    private UIConstants() {
        throw new AssertionError("Cannot instantiate UIConstants");
    }
}