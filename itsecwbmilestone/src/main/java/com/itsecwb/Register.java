package com.itsecwb;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import net.jpountz.xxhash.XXHash32;
import net.jpountz.xxhash.XXHashFactory;

public class Register {
    public static void createAndShowRegistrationScreen() {
        // PANG INITIALIZE NG GRAPHICS
        JFrame frame = new JFrame("User Registration");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 500);
        frame.setLayout(new GridLayout(7, 2));

        JLabel nameLabel = new JLabel("Full Name:");
        JTextField nameField = new JTextField();
        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField();
        JLabel phoneLabel = new JLabel("Phone Number:");
        JTextField phoneField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();
        JLabel photoLabel = new JLabel("Profile Photo:");
        JTextField photoField = new JTextField();
        JButton browseButton = new JButton("Browse");
        JButton registerButton = new JButton("Register");

        frame.add(nameLabel);
        frame.add(nameField);
        frame.add(emailLabel);
        frame.add(emailField);
        frame.add(phoneLabel);
        frame.add(phoneField);
        frame.add(passwordLabel);
        frame.add(passwordField);
        frame.add(photoLabel);
        frame.add(photoField);
        frame.add(browseButton);
        frame.add(new JLabel());
        frame.add(registerButton);

        // LOGIC FOR BROWSING FILES
        browseButton.addActionListener((ActionEvent e) -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                photoField.setText(selectedFile.getAbsolutePath());
            }
        });

        registerButton.addActionListener((ActionEvent e) -> {
            String fullName = nameField.getText();
            String email = emailField.getText();
            String phoneNumber = phoneField.getText();
            String password = new String(passwordField.getPassword());
            String photoPath = photoField.getText();

            // Validate inputs
            if (!isValidName(fullName)) {
                JOptionPane.showMessageDialog(frame, "Invalid full name. Please enter a valid name.");
                return;
            }
            if (!isValidEmail(email)) {
                JOptionPane.showMessageDialog(frame, "Invalid email. Please enter a valid email.");
                return;
            }
            if (!isValidPhoneNumber(phoneNumber)) {
                JOptionPane.showMessageDialog(frame, "Invalid phone number. Please enter a valid phone number.");
                return;
            }
            if (!isValidPassword(password)) {
                JOptionPane.showMessageDialog(frame,
                        "Invalid password. Password must be at least 12 characters long. Must contain at least 1 uppercase and 1 lowercase letter, 1 digit, and 1 special character.");
                return;
            }

            String jdbcString = "jdbc:sqlite:./itsecwbmilestone/SQLite/usersdb.db";
            try (Connection connection = DriverManager.getConnection(jdbcString);
                    // FOR UPLOADING PROFILE PIC
                    FileInputStream fis = new FileInputStream(photoPath)) {

                // Check for duplicate email
                String checkEmailSql = "SELECT COUNT(*) FROM users WHERE email = ?";
                PreparedStatement checkEmailStmt = connection.prepareStatement(checkEmailSql);
                checkEmailStmt.setString(1, email);
                ResultSet rs = checkEmailStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(frame, "Email already exists. Please use a different email.");
                    return;
                }

                // HASHING LOGIC
                XXHashFactory factory = XXHashFactory.fastestInstance();
                XXHash32 hash32 = factory.hash32();
                int hash = hash32.hash(password.getBytes(), 0, password.getBytes().length, 0);
                // END HASH LOGIC

                String sql = "INSERT INTO users (full_name, email, phone_number, profile_photo, password) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, fullName);
                preparedStatement.setString(2, email);
                preparedStatement.setString(3, phoneNumber);
                preparedStatement.setBinaryStream(4, fis, (int) new File(photoPath).length());
                preparedStatement.setString(5, Integer.toString(hash));

                preparedStatement.executeUpdate();
                JOptionPane.showMessageDialog(frame, "User registered successfully!");
                frame.dispose(); // Close registration screen
                Main.main(null); // Open login screen

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error connecting to the database");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error processing profile photo");
            }
        });

        frame.setVisible(true);
    }

    // Validation methods
    private static boolean isValidName(String name) {
        return name != null && name.matches("^[\\p{L} .'-]+$");
    }

    private static boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$");
    }

    private static boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber != null && phoneNumber.matches("^\\+?\\d{10,15}$");
    }

    private static boolean isValidPassword(String password) {
        return password != null && password.matches("^(?=.*\\d)(?=.*[A-Z])(?=.*[a-z])(?=.*[^\\w\\s:]).{12,64}$");
    }
}
