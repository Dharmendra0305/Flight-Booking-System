package ui;

import db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

/**
 * ViewBookings - Displays all bookings for a specific flight.
 * Shows passenger details, seat info, payment status, and booking time.
 */
public class ViewBookings extends JFrame
{
    // Table model for bookings
    private final DefaultTableModel tableModel;

    // Constructor
    public ViewBookings(int flightId)
    {
        // Frame setup
        setTitle("Bookings for Flight ID: " + flightId);
        setSize(900, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Background image panel
        BackgroundImagePanel bgPanel = new BackgroundImagePanel("/ui/Icons/6.jpg");
        setContentPane(bgPanel);
        bgPanel.setLayout(new BorderLayout());

        // Title label
        JLabel title = new JLabel("Bookings Details", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        bgPanel.add(title, BorderLayout.NORTH);

        // Table setup
        tableModel = new DefaultTableModel();
        JTable bookingsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        bgPanel.add(scrollPane, BorderLayout.CENTER);

        // Table columns
        tableModel.setColumnIdentifiers(new String[]
                {
                        "Booking ID", "Passenger Name", "Email", "Seat Number", "Payment Status", "Booking Time"
                });

        // Load booking data from database
        loadBookingData(flightId);
    }

    /**
     * Loads booking data for the given flight ID and populates the table.
     */
    private void loadBookingData(int flightId)
    {
        try (Connection con = DBConnection.getConnection())
        {
            String q = "select * from bookings where flight_id = ?";
            PreparedStatement stm = con.prepareStatement(q);
            stm.setInt(1, flightId);
            ResultSet r = stm.executeQuery();

            while (r.next())
            {
                Vector<Object> row = new Vector<>();
                row.add(r.getInt("booking_id"));
                row.add(r.getString("passenger_name"));
                row.add(r.getString("email"));
                row.add(r.getString("seat_number"));
                row.add(r.getString("payment_status"));
                row.add(r.getTimestamp("booking_time"));
                tableModel.addRow(row);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading bookings.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
