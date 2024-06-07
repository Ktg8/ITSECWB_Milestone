import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;
import java.io.File;
import java.io.FileInputStream;
import at.favre.lib.crypto.bcrypt.BCrypt;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            createAndShowRegistrationScreen();
        });
    }

    private static void createAndShowRegistrationScreen() {
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
            
            String jdbcString = "jdbc:sqlite:D:/GITHUB/itsec/ITSECWB_Milestone/SQLite/usersdb.db"; // Change to your database location
            try (Connection connection = DriverManager.getConnection(jdbcString);
                    FileInputStream fis = new FileInputStream(photoPath)) {
            
                String hashedPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray());
                
                String sql = "INSERT INTO users (full_name, email, phone_number, profile_photo, password) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, fullName);
                preparedStatement.setString(2, email);
                preparedStatement.setString(3, phoneNumber);
                preparedStatement.setBinaryStream(4, fis, (int) new File(photoPath).length());
                preparedStatement.setString(5, hashedPassword);
                
                preparedStatement.executeUpdate();
                JOptionPane.showMessageDialog(frame, "User registered successfully!");
                frame.dispose(); // Close registration screen
                Login.main(null); // Open login screen
                
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
}
