package ui;

import db.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

public class UserRegistrationForm extends JFrame
{
    public UserRegistrationForm()
    {
        setTitle("User Registration");
        setSize(350, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(5, 2, 10, 10));

        add(new JLabel("Name:"));
        JTextField nameField = new JTextField();
        add(nameField);

        add(new JLabel("Email:"));
        JTextField emailField = new JTextField();
        add(emailField);

        add(new JLabel("Password:"));
        JPasswordField passField = new JPasswordField();
        add(passField);

        JButton registerBtn = new JButton("Register");
        JButton backBtn = new JButton("Back");

        registerBtn.addActionListener(_ ->
        {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String pass = new String((passField.getPassword()));

            if (name.isEmpty() || email.isEmpty() || pass.isEmpty())
            {
                JOptionPane.showMessageDialog(this, "All fields required");
                return;
            }

            try (Connection con = DBConnection.getConnection())
            {
                String q = "insert into users(name, email, password) values(?, ?, ?)";
                PreparedStatement stm = con.prepareStatement(q);
                stm.setString(1, name);
                stm.setString(2, email);
                stm.setString(3, pass);

                stm.executeUpdate();
                JOptionPane.showMessageDialog(this, "Registration Successful!");
                dispose();
                new UserLoginForm().setVisible(true);
            }
            catch (SQLIntegrityConstraintViolationException ex)
            {
                JOptionPane.showMessageDialog(this, "Email already registered");
            }
            catch (SQLException ex)
            {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Registration failed.");
            }
        });

        backBtn.addActionListener(_ ->
        {
            dispose();
            new UserLoginForm().setVisible(true);
        });

        add(registerBtn);
        add(backBtn);
    }
}
