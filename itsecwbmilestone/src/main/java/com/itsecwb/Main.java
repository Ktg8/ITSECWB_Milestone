package com.itsecwb;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import net.jpountz.xxhash.XXHash32;
import net.jpountz.xxhash.XXHashFactory;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            createAndShowLoginScreen();
        });
    }
    //Login Attempt loop counter: locks login until app close
    static int attempts = 3;

    private static void createAndShowLoginScreen() {
        JFrame frame = new JFrame("User Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setLayout(new GridLayout(4, 2));

        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        frame.add(emailLabel);
        frame.add(emailField);
        frame.add(passwordLabel);
        frame.add(passwordField);
        frame.add(new JLabel());
        frame.add(loginButton);
        frame.add(new JLabel());
        frame.add(registerButton);

        
        loginButton.addActionListener((ActionEvent e) -> {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());

            
            String jdbcString = "jdbc:sqlite:./itsecwbmilestone/SQLite/usersdb.db"; 
            
                if (attempts == 0) {
                    //Login lock on too many attempts
                    JOptionPane.showMessageDialog(frame, "Too many login attempts!");
                }
                else {
                    try (Connection connection = DriverManager.getConnection(jdbcString)) {

                        String sql = "SELECT * FROM users WHERE email = ?";
                        PreparedStatement preparedStatement = connection.prepareStatement(sql);
                        preparedStatement.setString(1, email);
                        ResultSet resultSet = preparedStatement.executeQuery();
                        if (resultSet.next()) {
                            //SALTING
                            String storedHash = resultSet.getString("password");
                            XXHashFactory factory = XXHashFactory.fastestInstance();
                            XXHash32 hash32 = factory.hash32();
                            int hash = hash32.hash(password.getBytes(), 0, password.getBytes().length, 0);
                            //end of salting
                            
                            if (Integer.toString(hash).equals(storedHash)) {
                                JOptionPane.showMessageDialog(frame, "Login successful!");
                                // next which is admin
                            } else {
                                //login lock decrement
                                attempts--;
                                JOptionPane.showMessageDialog(frame, "Invalid Login!");
        
                            }
                        } else {
                            //login lock decrement
                            attempts--;
                            JOptionPane.showMessageDialog(frame, "Invalid Login!");
                        }
        
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(frame, "Error connecting to the database");
                    }
                
                }
        });

        registerButton.addActionListener((ActionEvent e) -> {
            frame.dispose(); // Close the login screen
            Register.createAndShowRegistrationScreen(); // Open the registration screen
        });

        frame.setVisible(true);
    }
}
