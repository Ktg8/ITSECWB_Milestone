package com.itsecwb;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class AdminPanel {
    private static final String JDBC_STRING = "jdbc:sqlite:./itsecwbmilestone/SQLite/usersdb.db";

    public static void createAndShowAdminPanel() {
        JFrame frame = new JFrame("Admin Panel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        DefaultTableModel model = new DefaultTableModel();
        JTable table = new JTable(model);
        model.addColumn("ID");
        model.addColumn("Full Name");
        model.addColumn("Email");
        model.addColumn("Phone Number");

        loadUserData(model);

        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel panel = new JPanel();
        JButton deleteButton = new JButton("Delete User");
        JButton logoutButton = new JButton("Logout");
        panel.add(deleteButton);
        panel.add(logoutButton);
        frame.add(panel, BorderLayout.SOUTH);

        deleteButton.addActionListener((ActionEvent e) -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                int userId = (int) model.getValueAt(selectedRow, 0);
                deleteUser(userId);
                model.removeRow(selectedRow);
            } else {
                JOptionPane.showMessageDialog(frame, "Please select a user to delete.");
            }
        });

        logoutButton.addActionListener((ActionEvent e) -> {
            frame.dispose(); // Close admin panel
            Main.main(null); // Open login screen
        });

        frame.setVisible(true);
    }

    private static void loadUserData(DefaultTableModel model) {
        try (Connection connection = DriverManager.getConnection(JDBC_STRING)) {
            String sql = "SELECT id, full_name, email, phone_number FROM users";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String fullName = resultSet.getString("full_name");
                String email = resultSet.getString("email");
                String phoneNumber = resultSet.getString("phone_number");

                model.addRow(new Object[]{id, fullName, email, phoneNumber});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading user data.");
        }
    }

    private static void deleteUser(int userId) {
        try (Connection connection = DriverManager.getConnection(JDBC_STRING)) {
            String sql = "DELETE FROM users WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, userId);
            preparedStatement.executeUpdate();
            JOptionPane.showMessageDialog(null, "User deleted successfully.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error deleting user.");
        }
    }
}
