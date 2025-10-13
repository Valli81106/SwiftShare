import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class RunGUI {
    public static void main(String[] args) {
        System.out.println("Starting SwiftShare GUI Test...");
        
        try {
            // Set dark theme colors
            UIManager.put("Panel.background", new Color(18, 18, 18));
            UIManager.put("OptionPane.background", new Color(30, 30, 30));
            UIManager.put("OptionPane.messageForeground", Color.WHITE);
            UIManager.put("TextField.background", new Color(50, 50, 50));
            UIManager.put("TextField.foreground", Color.WHITE);
            UIManager.put("TextField.caretForeground", Color.WHITE);
            UIManager.put("TextField.border", BorderFactory.createLineBorder(new Color(80, 80, 80)));
            
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Create and show the frame
            JFrame frame = new JFrame("SwiftShare - P2P File Sharing") {
                @Override
                public void paint(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    super.paint(g2);
                }
            };
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 800);
            frame.setLocationRelativeTo(null);
            
            // Create main panel with dark background
            JPanel mainPanel = new JPanel(new BorderLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
                }
            };
            mainPanel.setBackground(new Color(18, 18, 18));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
            
            // Create content panel with rounded corners
            RoundedPanel contentPanel = new RoundedPanel(30);
            contentPanel.setLayout(new GridBagLayout());
            contentPanel.setBackground(new Color(30, 30, 30));
            contentPanel.setBorder(BorderFactory.createEmptyBorder(60, 60, 60, 60));
            
            // Use GridBagLayout for better centering control
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.CENTER;
            
            // Title with bigger font and gradient effect
            JLabel title = new JLabel("SwiftShare", JLabel.CENTER) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    
                    // Create gradient for text
                    FontMetrics fm = g2.getFontMetrics();
                    int x = (getWidth() - fm.stringWidth(getText())) / 2;
                    int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                    
                    GradientPaint gradient = new GradientPaint(
                        x, y, new Color(0, 184, 148),
                        x + fm.stringWidth(getText()), y, new Color(0, 206, 201)
                    );
                    g2.setPaint(gradient);
                    g2.setFont(getFont());
                    g2.drawString(getText(), x, y);
                }
            };
            title.setFont(new Font("Segoe UI", Font.BOLD, 48));
            title.setForeground(Color.WHITE);
            gbc.insets = new Insets(0, 0, 10, 0);
            contentPanel.add(title, gbc);
            
            // Subtitle
            JLabel subtitle = new JLabel("Peer-to-Peer File Sharing", JLabel.CENTER);
            subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            subtitle.setForeground(new Color(180, 180, 180));
            gbc.insets = new Insets(0, 0, 60, 0);
            contentPanel.add(subtitle, gbc);
            
            // Button panel with centered, smaller buttons
            JPanel buttonPanel = new JPanel(new GridBagLayout());
            buttonPanel.setBackground(new Color(30, 30, 30));
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 40, 0));
            
            GridBagConstraints btnGbc = new GridBagConstraints();
            btnGbc.gridwidth = GridBagConstraints.REMAINDER;
            btnGbc.fill = GridBagConstraints.NONE;
            btnGbc.anchor = GridBagConstraints.CENTER;
            btnGbc.insets = new Insets(0, 0, 25, 0);
            
            // Create Room Button - Smaller and centered
            RoundedButton createBtn = new RoundedButton("Create New Room", 25);
            createBtn.setBackground(new Color(0, 184, 148));
            createBtn.setForeground(Color.WHITE);
            createBtn.setHoverColor(new Color(0, 206, 201));
            createBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
            createBtn.setPreferredSize(new Dimension(280, 55)); // Smaller size
            
            // Join Room Button - Smaller and centered
            RoundedButton joinBtn = new RoundedButton("Join Existing Room", 25);
            joinBtn.setBackground(new Color(45, 45, 45));
            joinBtn.setForeground(Color.WHITE);
            joinBtn.setHoverColor(new Color(65, 65, 65));
            joinBtn.setBorderColor(new Color(80, 80, 80));
            joinBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
            joinBtn.setPreferredSize(new Dimension(280, 55)); // Smaller size
            
            // Action listeners
            createBtn.addActionListener(e -> {
                showCustomDialog(frame, "Create Room", 
                    "Room created successfully!\n\n" +
                    "Room ID: ROOM-12345\n" +
                    "Password: ••••••••\n\n" +
                    "Share the Room ID with others to join!",
                    new Color(0, 184, 148));
            });
            
            joinBtn.addActionListener(e -> {
                showInputDialog(frame, "Join Room");
            });
            
            // Add buttons to panel
            buttonPanel.add(createBtn, btnGbc);
            btnGbc.insets = new Insets(0, 0, 0, 0); // Remove bottom margin for last button
            buttonPanel.add(joinBtn, btnGbc);
            
            // Add button panel to main content
            gbc.insets = new Insets(0, 0, 0, 0);
            gbc.fill = GridBagConstraints.NONE;
            contentPanel.add(buttonPanel, gbc);
            
            mainPanel.add(contentPanel, BorderLayout.CENTER);
            frame.add(mainPanel);
            frame.setVisible(true);
            
            System.out.println("Dark Mode GUI is running!");
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Custom rounded button class
    static class RoundedButton extends JButton {
        private int radius;
        private Color borderColor = new Color(80, 80, 80);
        private Color hoverColor;
        
        public RoundedButton(String text, int radius) {
            super(text);
            this.radius = radius;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            // Add hover effect
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (hoverColor != null) {
                        setBackground(hoverColor);
                    }
                    repaint();
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    setBackground(getBackground()); // Reset to original
                    repaint();
                }
            });
        }
        
        public void setHoverColor(Color color) {
            this.hoverColor = color;
        }
        
        public void setBorderColor(Color color) {
            this.borderColor = color;
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Fill rounded background
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            
            // Draw border
            g2.setColor(borderColor);
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, radius, radius);
            
            // Draw text
            g2.setColor(getForeground());
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            String text = getText();
            int x = (getWidth() - fm.stringWidth(text)) / 2;
            int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
            g2.drawString(text, x, y);
            
            g2.dispose();
        }
    }
    
    // Custom rounded panel class
    static class RoundedPanel extends JPanel {
        private int radius;
        
        public RoundedPanel(int radius) {
            this.radius = radius;
            setOpaque(false);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        }
    }
    
    // Custom styled dialog for messages - FIXED CENTERING
    private static void showCustomDialog(JFrame parent, String title, String message, Color accentColor) {
        JDialog dialog = new JDialog(parent, title, true);
        dialog.setSize(500, 300);
        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(new BorderLayout());
        
        // Main panel
        RoundedPanel mainPanel = new RoundedPanel(20);
        mainPanel.setLayout(new GridBagLayout()); // Use GridBagLayout for perfect centering
        mainPanel.setBackground(new Color(40, 40, 40));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        
        // Message label - PROPERLY CENTERED
        JTextArea messageArea = new JTextArea(message);
        messageArea.setEditable(false);
        messageArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        messageArea.setForeground(Color.WHITE);
        messageArea.setBackground(new Color(40, 40, 40));
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
        
        // Center the text in the text area
        messageArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        messageArea.setAlignmentY(Component.CENTER_ALIGNMENT);
        
        // Create a wrapper panel to ensure centering
        JPanel messageWrapper = new JPanel(new BorderLayout());
        messageWrapper.setBackground(new Color(40, 40, 40));
        messageWrapper.add(messageArea, BorderLayout.CENTER);
        
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(messageWrapper, gbc);
        
        // OK button
        RoundedButton okButton = new RoundedButton("OK", 15);
        okButton.setBackground(accentColor);
        okButton.setForeground(Color.WHITE);
        okButton.setHoverColor(accentColor.brighter());
        okButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        okButton.setPreferredSize(new Dimension(120, 45));
        okButton.addActionListener(e -> dialog.dispose());
        
        // Button panel - centered
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(40, 40, 40));
        buttonPanel.add(okButton);
        
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(buttonPanel, gbc);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    
    // Custom styled input dialog - FIXED CENTERING
    private static void showInputDialog(JFrame parent, String title) {
        JDialog dialog = new JDialog(parent, title, true);
        dialog.setSize(500, 250);
        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(new BorderLayout());
        
        // Main panel
        RoundedPanel mainPanel = new RoundedPanel(20);
        mainPanel.setLayout(new GridBagLayout()); // Use GridBagLayout for centering
        mainPanel.setBackground(new Color(40, 40, 40));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(5, 0, 5, 0);
        
        // Input panel - centered
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(new Color(40, 40, 40));
        
        JLabel promptLabel = new JLabel("Enter Room ID:");
        promptLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        promptLabel.setForeground(Color.WHITE);
        promptLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JTextField roomIdField = new JTextField(20); // Set columns for proper size
        roomIdField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        roomIdField.setBackground(new Color(60, 60, 60));
        roomIdField.setForeground(Color.WHITE);
        roomIdField.setCaretColor(Color.WHITE);
        roomIdField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80), 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Add components to input panel with centering
        GridBagConstraints inputGbc = new GridBagConstraints();
        inputGbc.gridwidth = GridBagConstraints.REMAINDER;
        inputGbc.fill = GridBagConstraints.HORIZONTAL;
        inputGbc.anchor = GridBagConstraints.CENTER;
        
        inputPanel.add(promptLabel, inputGbc);
        inputGbc.insets = new Insets(10, 0, 0, 0);
        inputPanel.add(roomIdField, inputGbc);
        
        gbc.weighty = 1.0;
        mainPanel.add(inputPanel, gbc);
        
        // Button panel - centered
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(new Color(40, 40, 40));
        
        RoundedButton cancelButton = new RoundedButton("Cancel", 15);
        cancelButton.setBackground(new Color(80, 80, 80));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setHoverColor(new Color(100, 100, 100));
        cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cancelButton.setPreferredSize(new Dimension(100, 40));
        cancelButton.addActionListener(e -> dialog.dispose());
        
        RoundedButton joinButton = new RoundedButton("Join", 15);
        joinButton.setBackground(new Color(0, 184, 148));
        joinButton.setForeground(Color.WHITE);
        joinButton.setHoverColor(new Color(0, 206, 201));
        joinButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        joinButton.setPreferredSize(new Dimension(100, 40));
        joinButton.addActionListener(e -> {
            String roomId = roomIdField.getText().trim();
            if (!roomId.isEmpty()) {
                dialog.dispose();
                showCustomDialog(parent, "Success", 
                    "Successfully joined room:\n\n" + roomId + "\n\nConnecting to peers...",
                    new Color(0, 184, 148));
            } else {
                roomIdField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(231, 76, 60), 2),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
            }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(joinButton);
        
        gbc.weighty = 0;
        mainPanel.add(buttonPanel, gbc);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
        
        // Focus on text field
        roomIdField.requestFocus();
    }
}