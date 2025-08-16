package ui;

import db.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * CancelBooking - Allows users to cancel a booking by entering Booking ID and optional email.
 */
public class CancelBooking extends JFrame
{
    // UI components
    private final JTextField bookingIdField;
    private final JTextField emailField;

    // Constructor
    public CancelBooking()
    {
        // Frame setup
        setTitle("Cancel Booking");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Background image panel
        BackgroundImagePanel bgPanel = new BackgroundImagePanel("/ui/Icons/12.jpg");
        setContentPane(bgPanel);
        bgPanel.setLayout(new BorderLayout());

        // Form panel
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setOpaque(false);

        panel.add(new JLabel("Booking ID:"));
        bookingIdField = new JTextField();
        panel.add(bookingIdField);

        panel.add(new JLabel("Email (optional):"));
        emailField = new JTextField();
        panel.add(emailField);

        panel.add(new JLabel("")); // Empty cell for layout
        JButton cancelBtn = new JButton("Cancel Booking");
        panel.add(cancelBtn);

        bgPanel.add(panel);

        // Styling components
        for (Component comp : panel.getComponents())
        {
            if (comp instanceof JLabel)
            {
                comp.setFont(new Font("Arial", Font.BOLD, 15));
                comp.setForeground(Color.YELLOW);
            }
            else if (comp instanceof JTextField)
            {
                comp.setFont(new Font("Arial", Font.PLAIN, 14));
            }
            else if (comp instanceof JButton)
            {
                comp.setFont(new Font("Arial", Font.BOLD, 13));
            }
        }

        // Button action
        cancelBtn.addActionListener(_ -> cancelBooking());
    }

    /**
     * Attempts to cancel the booking using the provided Booking ID and optional email.
     */
    private void cancelBooking()
    {
        String bookingId = bookingIdField.getText().trim();
        String email = emailField.getText().trim();

        if (bookingId.isEmpty())
        {
            JOptionPane.showMessageDialog(this, "Please enter a Booking ID.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection con = DBConnection.getConnection();
            PreparedStatement pst = email.isEmpty()
                    ? con.prepareStatement("Delete from bookings where booking_id = ?")
                    : con.prepareStatement("Delete from bookings where booking_id = ? and email = ?"))
        {
            pst.setInt(1, Integer.parseInt(bookingId));
            if (!email.isEmpty())
            {
                pst.setString(2, email);
            }

            int rowsAffected = pst.executeUpdate();

            if (rowsAffected > 0)
            {
                JOptionPane.showMessageDialog(this, "Booking cancelled successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
            else
            {
                JOptionPane.showMessageDialog(this, "Booking not found or email doesn't match.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        catch (NumberFormatException ex)
        {
            JOptionPane.showMessageDialog(this, "Invalid Booking ID.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() -> new CancelBooking().setVisible(true));
    }
}
