package com.itsecwb;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class UserPanel {
    private static final String JDBC_STRING = "jdbc:sqlite:./itsecwbmilestone/SQLite/usersdb.db";

    public static void createAndShowUserPanel(String fullName, String email) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("User Panel");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(300, 400);
            frame.setLayout(new BorderLayout());

            JPanel panel = new JPanel(new BorderLayout());
            JLabel welcomeLabel = new JLabel("Hello, " + fullName + "!", JLabel.CENTER);
            panel.add(welcomeLabel, BorderLayout.NORTH);

            JLabel photoLabel = new JLabel();
            panel.add(photoLabel, BorderLayout.CENTER);

            JButton logoutButton = new JButton("Logout");
            panel.add(logoutButton, BorderLayout.SOUTH);

            frame.add(panel);

            loadProfilePicture(email, photoLabel);

            logoutButton.addActionListener((ActionEvent e) -> {
                frame.dispose(); // Close user panel
                Main.main(null); // Open login screen
            });

            frame.setVisible(true);
        });
    }

    private static void loadProfilePicture(String email, JLabel photoLabel) {
        try (Connection connection = DriverManager.getConnection(JDBC_STRING)) {
            String sql = "SELECT profile_photo FROM users WHERE email = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                byte[] profilePhotoBytes = resultSet.getBytes("profile_photo");
                if (profilePhotoBytes != null) {
                    ByteArrayInputStream bais = new ByteArrayInputStream(profilePhotoBytes);
                    Image profilePhoto = ImageIO.read(bais);
                    if (profilePhoto != null) {
                        ImageIcon profileIcon = new ImageIcon(profilePhoto.getScaledInstance(100, 100, Image.SCALE_SMOOTH));
                        photoLabel.setIcon(profileIcon);
                    }
                }
            }
        } catch (SQLException | IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading profile picture.");
        }
    }
}
