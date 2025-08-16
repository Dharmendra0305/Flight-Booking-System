package ui;

import db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;

/**
 * SearchPanel - Allows users to search for flights by source, destination, and date.
 * Displays results in a table and lets users proceed to seat selection.
 */
public class SearchPanel extends JPanel
{
    // Shared booking state
    private final BookingState state;

    // UI components
    private final JComboBox<String> txtSource, txtDestination;
    private final JTextField txtDate;
    private final JTable table;
    private final DefaultTableModel model;

    // Constructor
    public SearchPanel(BookingApp app, BookingState state)
    {
        this.state = state;
        setLayout(new BorderLayout(8, 8));

        // Background panel
        BackgroundImagePanel backgroundPanel = new BackgroundImagePanel("/ui/Icons/9.jpg");
        backgroundPanel.setLayout(new BorderLayout());
        add(backgroundPanel, BorderLayout.CENTER);

        // Top form panel
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        top.setOpaque(false);

        txtSource = new JComboBox<>(new String[]{"Delhi", "Mumbai", "Bangalore", "Chennai", "Kolkata"});
        txtDestination = new JComboBox<>(new String[]{"Delhi", "Mumbai", "Bangalore", "Chennai", "Kolkata"});
        txtDate = new JTextField(10);
        txtDate.setText(LocalDate.now().toString());

        top.add(new JLabel("Source:"));
        top.add(txtSource);
        top.add(new JLabel("Destination:"));
        top.add(txtDestination);
        top.add(new JLabel("Date (YYYY-MM-DD):"));
        top.add(txtDate);

        JButton btnSearch = new JButton("Search");
        JButton btnClear = new JButton("Clear");
        top.add(btnSearch);
        top.add(btnClear);

        backgroundPanel.add(top, BorderLayout.NORTH);

        // Results table
        model = new DefaultTableModel(new String[]
                {
                        "Flight ID", "Airline", "Source", "Destination", "Date", "Departure", "Arrival", "Price"
                }, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        backgroundPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom panel with Book button
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnBook = new JButton("Book Selected Flight");
        bottom.add(btnBook);
        bottom.setOpaque(false);
        backgroundPanel.add(bottom, BorderLayout.SOUTH);

        // Button actions
        btnSearch.addActionListener(_ -> doSearch());

        btnClear.addActionListener(_ ->
        {
            txtSource.setSelectedItem(null);
            txtDestination.setSelectedItem(null);
            txtDate.setText(LocalDate.now().toString());
            model.setRowCount(0);
        });

        btnBook.addActionListener(_ ->
        {
            int r = table.getSelectedRow();
            if (r == -1)
            {
                JOptionPane.showMessageDialog(this, "Select a flight first.");
                return;
            }

            // Populate booking state
            state.flightId = (int) model.getValueAt(r, 0);
            state.airlineName = model.getValueAt(r, 1).toString();
            state.source = model.getValueAt(r, 2).toString();
            state.destination = model.getValueAt(r, 3).toString();
            state.flightDate = Date.valueOf(model.getValueAt(r, 4).toString());
            state.departureTime = Time.valueOf(model.getValueAt(r, 5).toString());
            state.arrivalTime = Time.valueOf(model.getValueAt(r, 6).toString());
            state.price = new BigDecimal(model.getValueAt(r, 7).toString());

            // Load booked seats
            loadBookedSeats(state.flightId);

            // Navigate to seat selection
            app.showSeatSelection();
        });

        // Double-click to book
        table.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                if (e.getClickCount() == 2)
                {
                    btnBook.doClick();
                }
            }
        });
    }

    /**
     * Performs flight search based on user input and populates the table.
     */
    private void doSearch()
    {
        String src = txtSource.getSelectedItem().toString();
        String dest = txtDestination.getSelectedItem().toString();
        String dateStr = txtDate.getText().trim();

        // Basic validation
        if (src.isEmpty() || dest.isEmpty() || dateStr.isEmpty())
        {
            JOptionPane.showMessageDialog(this, "Enter source, destination and date.");
            return;
        }
        if (src.equalsIgnoreCase(dest))
        {
            JOptionPane.showMessageDialog(this, "Source and Destination cannot be same.");
            return;
        }

        Date sqlDate;
        try
        {
            sqlDate = Date.valueOf(dateStr);
        }
        catch (Exception ex)
        {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD");
            return;
        }

        model.setRowCount(0);// Clear previous results

        // Try multiple query formats for compatibility
        try (Connection con = DBConnection.getConnection())
        {
            PreparedStatement pst;

            String[] candidates = new String[]
                    {
                            "Select * from flights where source = ? and destination = ? and flight_date = ?",
                            "Select * from flights where source = ? and destination = ? and date = ?",
                            "Select * from flights where source = ? and destination = ? and DATE(departure_time) = ?"
                    };

            SQLException lastEx = null;

            for (String q: candidates)
            {
                try
                {
                    pst = con.prepareStatement(q);
                    pst.setString(1, src);
                    pst.setString(2, dest);
                    pst.setDate(3, sqlDate);
                    ResultSet r = pst.executeQuery();

                    while (r.next())
                    {
                        int fid = r.getInt("flight_id");
                        String airline = r.getString("airline_name");
                        String source = r.getString("source");
                        String destination = r.getString("destination");

                        Date fdate = null;
                        try
                        {
                            fdate = r.getDate("flight_date");
                        }
                        catch (Exception _) {}
                        if (fdate == null)
                        {
                            try
                            {
                                fdate = r.getDate("date");
                            }
                            catch (Exception _) {}
                        }
                        if (fdate == null)
                        {
                            Time dep = r.getTime("departure_time");
                            if (dep != null)
                            {
                                fdate = new Date(dep.getTime());
                            }
                        }

                        Time depTs = null;
                        Time arrTs = null;
                        try
                        {
                            depTs = r.getTime("departure_time");
                        }
                        catch (Exception _) {}
                        try
                        {
                            arrTs = r.getTime("arrival_time");
                        }
                        catch (Exception _) {}
                        if (depTs == null)
                        {
                            depTs = new Time(sqlDate.getTime());
                        }
                        if (arrTs == null)
                        {
                            arrTs = new Time(sqlDate.getTime());
                        }

                        BigDecimal price = r.getBigDecimal("price");

                        model.addRow(new Object[]
                        {
                            fid, airline, source, destination,
                            (fdate != null ? fdate.toString():sqlDate.toString()),
                            depTs.toString(), arrTs.toString(),
                            price.toString()
                        });
                    }
                    break; // Exit loop if successful
                }
                catch (SQLException ex)
                {
                    lastEx = ex;
                }
            }

            if (model.getRowCount() == 0)
            {
                // It's ok to be zero results; show info
                if (lastEx != null)
                {
                    // but also print error (for dev)
                    System.out.println("Search query exceptions: " + lastEx.getMessage());
                }
                JOptionPane.showMessageDialog(this, "No flights found for given criteria.");
            }
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    /**
     * Loads booked seats for the selected flight into the shared state.
     */
    private void loadBookedSeats(int flightId)
    {
        state.bookedSeats.clear();
        try (Connection con = DBConnection.getConnection())
        {
            PreparedStatement pst = con.prepareStatement("Select seat_number from bookings where flight_id = ?");
            pst.setInt(1, flightId);
            ResultSet rs = pst.executeQuery();
            while (rs.next())
            {
                state.bookedSeats.add(rs.getString("seat_number"));
            }
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
    }
}
