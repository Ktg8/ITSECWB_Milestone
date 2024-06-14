package com.itsecwb;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
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
            File pfp = new File(photoPath);

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
            try {
                validateImage(pfp);

                try (Connection connection = DriverManager.getConnection(jdbcString);
                    //FOR UPLOADING PROFILE PIC
                    FileInputStream fis = new FileInputStream(renameFile(pfp,connection))) 
                    {
  
                        // Check for duplicate email
                    String checkEmailSql = "SELECT COUNT(*) FROM users WHERE email = ?";
                    PreparedStatement checkEmailStmt = connection.prepareStatement(checkEmailSql);
                    checkEmailStmt.setString(1, email);
                    ResultSet rs = checkEmailStmt.executeQuery();
                    if (rs.next() && rs.getInt(1) > 0) {
                        JOptionPane.showMessageDialog(frame, "Email already exists. Please use a different email.");
                        return;
                    }
                    //HASHING LOGIC   
                    XXHashFactory factory = XXHashFactory.fastestInstance();
                    XXHash32 hash32 = factory.hash32();
                    int hash = hash32.hash(password.getBytes(), 0, password.getBytes().length, 0);
                    //END HASH LOGIC

                    String sql = "INSERT INTO users (full_name, email, phone_number, profile_photo, password) VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement preparedStatement = connection.prepareStatement(sql);
                    preparedStatement.setString(1, fullName);
                    preparedStatement.setString(2, email);
                    preparedStatement.setString(3, phoneNumber);
                    preparedStatement.setBinaryStream(4, fis, (int) new File(pfp.getAbsolutePath()).length());
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

            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
            }
        });

        frame.setVisible(true);
    }

    // Validation methods
    private static boolean isValidName(String name) {
        return name != null && name.matches("^[\\p{L} .'-]+$");
    }

    private static boolean isValidEmail(String email) {
        return email != null && email.matches("^[0-9a-zA-Z!#$%&'*+/=?^_`{|}~-]{1,64}(?:\\.[0-9a-zA-Z!#$%&'*+/=?^_`{|}~-]+)*@(?:[0-9a-zA-Z](?:[0-9a-zA-Z-]{0,255}[0-9a-zA-Z])?\\.)+[a-zA-Z]{2,}$");
    }

    private static boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber != null && phoneNumber.matches("^\\+?\\d{10,15}$");
    }

    private static boolean isValidPassword(String password) {
        return password != null && password.matches("^(?=.*\\d)(?=.*[A-Z])(?=.*[a-z])(?=.*[^\\w\\s:]).{12,64}$");
    }
    //10 MB FILE LIMIT
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    //255 CHARACTER FILE NAME
    private static final int MAX_FILE_NAME_LENGTH = 255; 

    //VALIDATE IMAGE
    public static void validateImage(File file) throws IOException {

        if (file.getName().length() > MAX_FILE_NAME_LENGTH) {
            throw new IOException("The file name exceeds the 255 character limit.");
        }
        if (file.length() > MAX_FILE_SIZE) {
            throw new IOException("The file size exceeds the 10 MB limit.");
        }
        try { FileInputStream fis = new FileInputStream(file);
            BufferedImage image = ImageIO.read(fis);
            if (image == null) {
                throw new IOException("The provided input stream does not contain a valid image.");
            }
        } catch (IOException e) {
            if (e.getMessage().equals("End of stream has been reached")) {
                throw new IOException("The provided input stream does not contain a valid image.", e);
            } else {
                throw new IOException("Error: " + e.getMessage(), e);
            }
        }
    }
   //RENAME FILE
     public static File renameFile(File file, Connection conn) throws Exception {
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String newFileName = getNextId(conn) + timestamp;
        File newFile = new File(file.getParentFile(), newFileName);
        return file.renameTo(newFile) ? newFile : file;
    }
    private static int getNextId(Connection conn) throws SQLException {
        String sql = "SELECT MAX(id) FROM users";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            int maxId = resultSet.getInt(1);
            return maxId + 1;
       }
        return 1;
    }
}

