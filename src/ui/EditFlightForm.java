package ui;

import db.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;

/**
 * EditFlightForm - UI form for editing an existing flight.
 * Loads flight data by ID and updates it in the database.
 */
public class EditFlightForm extends JFrame {
    // UI Components
    private final JTextField airlineField, sourceField, destField, dateField, departField, arriveField, priceField;
    private final int flightId;
    private final AdminDashboard dashboard;

    // Constructor
    public EditFlightForm(int flightId, AdminDashboard dashboard) {
        this.flightId = flightId;
        this.dashboard = dashboard;

        // Frame setup
        setTitle("Edit Flight ID: " + flightId);
        setSize(400, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(8, 2, 10, 10));

        // Background image panel
        BackgroundImagePanel bgPanel = new BackgroundImagePanel("/ui/Icons/5.jpg");
        setContentPane(bgPanel);
        bgPanel.setLayout(new BorderLayout());

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Form fields
        formPanel.add(new JLabel("Airline Name:"));
        airlineField = new JTextField();
        formPanel.add(airlineField);

        formPanel.add(new JLabel("Source:"));
        sourceField = new JTextField();
        formPanel.add(sourceField);

        formPanel.add(new JLabel("Destination:"));
        destField = new JTextField();
        formPanel.add(destField);

        formPanel.add(new JLabel("Date (yyyy-MM-dd):"));
        dateField = new JTextField();
        formPanel.add(dateField);

        formPanel.add(new JLabel("Departure (HH:mm):"));
        departField = new JTextField();
        formPanel.add(departField);

        formPanel.add(new JLabel("Arrival (HH:mm):"));
        arriveField = new JTextField();
        formPanel.add(arriveField);

        formPanel.add(new JLabel("Price:"));
        priceField = new JTextField();
        formPanel.add(priceField);

        // Update button
        JButton updateBtn = new JButton("Update Flight");
        updateBtn.addActionListener(_ -> updateFlight());
        formPanel.add(new JLabel()); // Empty cell for layout
        formPanel.add(updateBtn);

        // Styling components
        for (Component comp : formPanel.getComponents()) {
            if (comp instanceof JLabel) {
                comp.setFont(new Font("Arial", Font.BOLD, 15));
                comp.setForeground(Color.YELLOW);
            } else if (comp instanceof JTextField) {
                comp.setFont(new Font("Arial", Font.PLAIN, 14));
            } else if (comp instanceof JButton) {
                comp.setFont(new Font("Arial", Font.BOLD, 15));
            }
        }

        bgPanel.add(formPanel, BorderLayout.CENTER);

        // Load existing flight data
        loadFlightData();
    }

    /**
     * Loads flight data from the database and populates the form fields.
     */
    private void loadFlightData() {
        try (Connection con = DBConnection.getConnection()) {
            String q = "select * from flights where flight_id = ?";
            PreparedStatement stm = con.prepareStatement(q);
            stm.setInt(1, flightId);
            ResultSet r = stm.executeQuery();

            if (r.next()) {
                airlineField.setText(r.getString("airline_name"));
                sourceField.setText(r.getString("source"));
                destField.setText(r.getString("destination"));
                dateField.setText(r.getDate("date").toString());
                departField.setText(r.getTime("departure_time").toString().substring(0, 5));
                arriveField.setText(r.getTime("arrival_time").toString().substring(0, 5));
                priceField.setText(String.valueOf(r.getDouble("price")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading flight data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Validates input and updates the flight record in the database.
     */
    private void updateFlight() {
        String airline = airlineField.getText().trim();
        String source = sourceField.getText().trim();
        String dest = destField.getText().trim();
        String date = dateField.getText().trim();
        String depart = departField.getText().trim();
        String arrive = arriveField.getText().trim();
        String priceStr = priceField.getText().trim();

        // Basic validation
        if (airline.isEmpty() || source.isEmpty() || dest.isEmpty() || date.isEmpty() || depart.isEmpty()
                || arrive.isEmpty() || priceStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.");
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);
            Date sqlDate = Date.valueOf(date);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            Time depTime = new Time(sdf.parse(depart).getTime());
            Time arrTime = new Time(sdf.parse(arrive).getTime());

            Connection con = DBConnection.getConnection();
            String q = "update flights set airline_name = ?, source = ?, destination = ?, date = ?, departure_time = ?, arrival_time = ?, price = ? where flight_id = ?";
            PreparedStatement stm = con.prepareStatement(q);
            stm.setString(1, airline);
            stm.setString(2, source);
            stm.setString(3, dest);
            stm.setDate(4, sqlDate);
            stm.setTime(5, depTime);
            stm.setTime(6, arrTime);
            stm.setDouble(7, price);
            stm.setInt(8, flightId);

            int rows = stm.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Flight updated successfully!");
                dashboard.loadFlightData(); // Refresh table
                dispose();
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating flight.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
