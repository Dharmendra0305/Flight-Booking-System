package ui;

import db.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * UserLoginForm - UI for user login and registration navigation.
 * Authenticates user credentials and opens the UserDashboard.
 */
public class UserLoginForm extends JFrame
{
    // Constructor
    public UserLoginForm()
    {
        // Frame setup
        setTitle("User Login");
        setSize(300, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(3, 2, 10, 10));

        // Background image panel
        BackgroundImagePanel backgroundPanel = new BackgroundImagePanel("/ui/Icons/7.jpg");
        setContentPane(backgroundPanel);
        backgroundPanel.setLayout(new BorderLayout());

        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Email and password fields
        formPanel.add(new JLabel("Email:"));
        JTextField emailField = new JTextField();
        formPanel.add(emailField);

        formPanel.add(new JLabel("Password:"));
        JPasswordField passField = new JPasswordField();
        formPanel.add(passField);

        backgroundPanel.add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);

        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");

        // Login button action
        loginBtn.addActionListener(_ ->
        {
            String email = emailField.getText().trim();
            String pass = new String(passField.getPassword());

            try (Connection con = DBConnection.getConnection())
            {
                String q = "select * from users where email = ? and password = ?";
                PreparedStatement stm = con.prepareStatement(q);
                stm.setString(1, email);
                stm.setString(2, pass);

                ResultSet r = stm.executeQuery();
                if (r.next())
                {
                    JOptionPane.showMessageDialog(this, "Login Successful!");
                    dispose();
                    new UserDashboard(r.getString("name")).setVisible(true);
                }
                else
                {
                    JOptionPane.showMessageDialog(this, "Invalid credentials.");
                }
            }
            catch (SQLException ex)
            {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Login failed.");
            }
        });

        // Register button action
        registerBtn.addActionListener(_ ->
        {
            dispose();
            new UserRegistrationForm().setVisible(true);
        });

        buttonPanel.add(loginBtn);
        buttonPanel.add(registerBtn);
        backgroundPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    public static void main(String[] args)
    {
        new UserLoginForm().setVisible(true);
    }
}
