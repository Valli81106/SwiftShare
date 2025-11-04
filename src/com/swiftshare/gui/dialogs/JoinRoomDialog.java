package com.swiftshare.gui.dialogs;

import com.swiftshare.models.RoomInfo;
import com.swiftshare.gui.controllers.HomeController;

import javax.swing.*;
import java.awt.*;

public class JoinRoomDialog extends JDialog {
    private JTextField roomIdField;
    private JPasswordField passwordField;
    private JButton joinButton;
    private JButton cancelButton;

    private boolean joinSuccessful = false;
    private RoomInfo roomInfo;
    private HomeController homeController; // ADD THIS

    public JoinRoomDialog(Frame parent, HomeController controller) { // MODIFIED
        super(parent, "Join Room", true);
        this.homeController = controller; // ADD THIS
        setupDialog();
        createComponents();
        layoutComponents();
    }

    private void setupDialog() {
        setSize(400, 250);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void createComponents() {
        roomIdField = new JTextField(20);
        passwordField = new JPasswordField(20);
        joinButton = new JButton("Join Room");
        cancelButton = new JButton("Cancel");

        joinButton.addActionListener(e -> handleJoin());
        cancelButton.addActionListener(e -> dispose());
    }

    private void layoutComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Room ID:"), gbc);
        gbc.gridx = 1;
        panel.add(roomIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(joinButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        add(panel);
    }

    private void handleJoin() {
        String roomId = roomIdField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (roomId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a room ID", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!homeController.isValidRoomId(roomId)) {
            JOptionPane.showMessageDialog(this,
                    "Invalid room ID format.\nAccepted formats:\n- ROOM_8123\n- 8123\n- 192.168.1.5:8123",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a password", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // MODIFIED: Use HomeController to join room
        roomInfo = homeController.joinRoom(roomId, password);

        if (roomInfo != null) {
            joinSuccessful = true;
            JOptionPane.showMessageDialog(this,
                    "Joined room successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to join room. Check room ID and try again.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Keep rest of methods...

    public boolean isJoinSuccessful() {
        return joinSuccessful;
    }

    public RoomInfo getRoomInfo() {
        return roomInfo;
    }
}