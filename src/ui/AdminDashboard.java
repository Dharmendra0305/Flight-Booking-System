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
 * AdminDashboard - Flight management interface for administrators.
 * Allows viewing, adding, editing, deleting flights and viewing bookings.
 */
public class AdminDashboard extends JFrame
{
    // UI Components
    private final JTable flightTable;
    private final DefaultTableModel tableModel;

    // Constructor
    public AdminDashboard()
    {
        // Frame setup
        setTitle("Admin Dashboard");
        setSize(800, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Background image panel
        BackgroundImagePanel bgPanel = new BackgroundImagePanel("/ui/Icons/3.jpg");
        setContentPane(bgPanel);
        bgPanel.setLayout(new BorderLayout());

        // Title label
        JLabel title = new JLabel("Admin Dashboard - Flight Management", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(Color.DARK_GRAY);
        bgPanel.add(title, BorderLayout.NORTH);

        // Table setup
        tableModel = new DefaultTableModel();
        flightTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(flightTable);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        bgPanel.add(scrollPane, BorderLayout.CENTER);

        // Table columns
        tableModel.setColumnIdentifiers(new String[]
                {
                        "Flight ID", "Airline", "Source", "Destination", "Date", "Departure", "Arrival", "Price"
                });

        // Load flight data from database
        loadFlightData();

        // Button panel
        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.setOpaque(false);
        bgPanel.add(btnPanel, BorderLayout.SOUTH);

        // Buttons
        JButton addBtn = new JButton("Add Flight");
        JButton editBtn = new JButton("Edit Flight");
        JButton deleteBtn = new JButton("Delete Flight");
        JButton viewBookingsBtn = new JButton("View Bookings");

        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(viewBookingsBtn);

        // Button actions
        addBtn.addActionListener(_ -> new AddFlightForm(this).setVisible(true));

        editBtn.addActionListener(_ ->
        {
            int selectedRow = flightTable.getSelectedRow();
            if (selectedRow == -1)
            {
                JOptionPane.showMessageDialog(this, "please select a flight to edit.");
                return;
            }
            int flightId = (int) tableModel.getValueAt(selectedRow, 0);
            new EditFlightForm(flightId, AdminDashboard.this).setVisible(true);
        });

        deleteBtn.addActionListener(_ -> deleteSelectedFlight());

        viewBookingsBtn.addActionListener(_ ->
        {
            int selectedRow = flightTable.getSelectedRow();
            if (selectedRow == -1)
            {
                JOptionPane.showMessageDialog(this, "Please select a flight.");
                return;
            }
            int flightId = (int) tableModel.getValueAt(selectedRow, 0);
            new ViewBookings(flightId).setVisible(true);
        });
    }

    /**
     * Loads flight data from the database into the table.
     */
    public void loadFlightData()
    {
        tableModel.setRowCount(0); // Clear existing rows
        try (Connection con = DBConnection.getConnection())
        {
            String q = "select * from flights";
            PreparedStatement stm = con.prepareStatement(q);
            ResultSet r = stm.executeQuery();

            while (r.next())
            {
                Vector<Object> row = new Vector<>();
                row.add(r.getInt("flight_id"));
                row.add(r.getString("airline_name"));
                row.add(r.getString("source"));
                row.add(r.getString("destination"));
                row.add(r.getString("date"));
                row.add(r.getTime("departure_time"));
                row.add(r.getTime("arrival_time"));
                row.add(r.getDouble("price"));
                tableModel.addRow(row);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading flights.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Deletes the selected flight from the database and refreshes the table.
     */
    private void deleteSelectedFlight()
    {
        int row = flightTable.getSelectedRow();
        if (row == -1)
        {
            JOptionPane.showMessageDialog(this, "Please select a flight to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this flight?", "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION)
        {
            int flightID = (int) tableModel.getValueAt(row, 0);

            try (Connection con = DBConnection.getConnection())
            {
                PreparedStatement stm = con.prepareStatement("Delete from flights where flight_id = ?");
                stm.setInt(1, flightID);
                stm.executeUpdate();
                JOptionPane.showMessageDialog(this, "Flight deleted");
                loadFlightData(); // refresh table
            }
            catch (SQLException e)
            {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Could not delete flight.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        new AdminDashboard().setVisible(true);
    }
}
