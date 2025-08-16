package ui;

import db.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * AdminLogin - Login screen for administrators.
 * Connects to database and verifies credentials.
 */
public class AdminLogin extends JFrame
{
    // UI Components
    private final JTextField usernameField;
    private final JPasswordField passwordField;

    // Constructor
    public AdminLogin()
    {
        // Frame setup
        setTitle("Admin Login");
        setSize(350, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Background image panel
        BackgroundImagePanel backgroundPanel = new BackgroundImagePanel("/ui/Icons/2.jpeg");
        setContentPane(backgroundPanel);
        backgroundPanel.setLayout(new BorderLayout());

        // Title Label
        JLabel title  = new JLabel("Admin Login", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 25));
        title.setForeground(Color.WHITE); // Contrast on background
        backgroundPanel.add(title, BorderLayout.NORTH);

        // Form panel for username and password
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        backgroundPanel.add(formPanel, BorderLayout.CENTER);

        // Username field
        formPanel.add(new JLabel("Username:")
        {{
            setFont(new Font("Arial", Font.PLAIN, 20));
            setForeground(Color.YELLOW);
        }});
        usernameField = new JTextField();
        formPanel.add(usernameField);

        // Password field
        formPanel.add(new JLabel("Password:")
        {{
            setFont(new Font("Arial", Font.PLAIN, 20));
            setForeground(Color.YELLOW);
        }});
        passwordField = new JPasswordField();
        formPanel.add(passwordField);

        // Login button
        JButton loginBtn = new JButton("Login");
        backgroundPanel.add(loginBtn, BorderLayout.SOUTH);

        // Action listener for login
        loginBtn.addActionListener(_ -> handleLogin());
    }

    /**
     * Handles login logic by verifying credentials against the database.
     */
    private void handleLogin()
    {
        String username = usernameField.getText().trim();
        String password = String.valueOf(passwordField.getPassword());

        try (Connection conn = DBConnection.getConnection())
        {
            String q = "select * from admins where username = ? and password = ?";
            PreparedStatement stm = conn.prepareStatement(q);
            stm.setString(1, username);
            stm.setString(2, password);

            ResultSet r = stm.executeQuery();
            if (r.next())
            {
                JOptionPane.showMessageDialog(this, "Login successful!");
                dispose();
                new AdminDashboard().setVisible(true);
            }
            else
            {
                JOptionPane.showMessageDialog(this, "Invalid username or password", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error occurred", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
