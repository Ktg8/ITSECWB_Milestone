import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;
import at.favre.lib.crypto.bcrypt.BCrypt;

public class Login {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            createAndShowLoginScreen();
        });
    }

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

            String jdbcString = "jdbc:sqlite:D:/GITHUB/itsec/ITSECWB_Milestone/SQLite/usersdb.db"; // Change to your database location
            try (Connection connection = DriverManager.getConnection(jdbcString)) {

                String sql = "SELECT * FROM users WHERE email = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, email);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    String storedHash = resultSet.getString("password");
                    BCrypt.Verifyer verifyer = BCrypt.verifyer();
                    BCrypt.Result result = verifyer.verify(password.toCharArray(), storedHash);
                    if (result.verified) {
                        JOptionPane.showMessageDialog(frame, "Login successful!");
                        // Proceed to the next part of your application
                    } else {
                        JOptionPane.showMessageDialog(frame, "Invalid password");
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid email");
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error connecting to the database");
            }
        });

        registerButton.addActionListener((ActionEvent e) -> {
            frame.dispose(); // Close the login screen
            App.main(null); // Open the registration screen
        });

        frame.setVisible(true);
    }
}
