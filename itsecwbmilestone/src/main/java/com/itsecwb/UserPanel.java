package com.itsecwb;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class UserPanel {
    public static void createAndShowUserPanel(String fullName) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("User Panel");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(300, 200);
            frame.setLayout(new BorderLayout());

            JLabel welcomeLabel = new JLabel("Hello, " + fullName + "!", JLabel.CENTER);
            frame.add(welcomeLabel, BorderLayout.CENTER);

            JPanel panel = new JPanel();
            JButton logoutButton = new JButton("Logout");
            panel.add(logoutButton);
            frame.add(panel, BorderLayout.SOUTH);

            logoutButton.addActionListener((ActionEvent e) -> {
                frame.dispose(); // Close user panel
                Main.main(null); // Open login screen
            });

            frame.setVisible(true);
        });
    }
}
