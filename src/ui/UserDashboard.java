package ui;

import javax.swing.*;
import java.awt.*;

/**
 * UserDashboard - Main screen for logged-in users.
 * Provides options to book or cancel flights.
 */
public class UserDashboard extends JFrame
{
    // Constructor
    public UserDashboard(String name)
    {
        // Frame setup
        setTitle("User Dashboard -Name: " + name);
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Background image panel
        BackgroundImagePanel backgroundPanel = new BackgroundImagePanel("/ui/Icons/8.jpg");
        setContentPane(backgroundPanel);
        backgroundPanel.setLayout(new BorderLayout());

        // Title label
        JLabel titleLabel = new JLabel("User DashBoard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 19));
        backgroundPanel.add(titleLabel, BorderLayout.NORTH);

        // Button panel
        JPanel btnPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        btnPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        btnPanel.setOpaque(false);

        // Buttons
        JButton bookFlight = new JButton("Book Flight");
        JButton cancelFlight = new JButton("Cancel Flight");

        btnPanel.add(bookFlight);
        btnPanel.add(cancelFlight);
        backgroundPanel.add(btnPanel, BorderLayout.CENTER);

        // Button actions
        bookFlight.addActionListener(_ -> new BookingApp().setVisible(true));
        cancelFlight.addActionListener(_ -> new CancelBooking().setVisible(true));
    }
}
