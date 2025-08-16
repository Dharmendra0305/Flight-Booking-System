package ui;

import javax.swing.*;
import java.awt.*;

/**
 * HomeScreen - Entry point for the Airline Booking System UI.
 * Displays a welcome screen with background image and login options.
 */
public class HomeScreen extends JFrame
{
    // Constructor
    public HomeScreen()
    {
        // Frame setup
        setTitle("Airline Booking System");
        setSize(400, 280);
        setLocationRelativeTo(null); // Center window
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Background image panel
        BackgroundImagePanel backgroundPanel = new BackgroundImagePanel("/ui/Icons/1.jpg");
        setContentPane(backgroundPanel);
        backgroundPanel.setLayout(new BorderLayout());

        // Title label
        JLabel titleLabel = new JLabel("Welcome to Elina Kutir Airline Booking", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE); // Visible on background
        backgroundPanel.add(titleLabel, BorderLayout.NORTH);

        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        buttonPanel.setOpaque(false); // Let background show through
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        // Buttons
        JButton adminLoginBtn = new JButton("Admin Login");
        JButton userLoginBtn = new JButton("User Login");

        buttonPanel.add(adminLoginBtn);
        buttonPanel.add(userLoginBtn);
        backgroundPanel.add(buttonPanel, BorderLayout.CENTER);

        // Button actions

        adminLoginBtn.addActionListener(_ -> new AdminLogin().setVisible(true));
        userLoginBtn.addActionListener(_ -> new UserLoginForm().setVisible(true));
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() -> new HomeScreen().setVisible(true));
    }
}
