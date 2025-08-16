package ui;

import db.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;

/**
 * AddFlightForm - UI form for adding a new flight to the system.
 * Validates input and inserts flight details into the database.
 */
public class AddFlightForm extends JFrame
{
    // UI Components
    private final JTextField airlineField, sourceField, destField, dateField, departField, arriveField, priceField;
    private final AdminDashboard dashboard;

    // Constructor
    public AddFlightForm(AdminDashboard dashboard)
    {
        this.dashboard = dashboard;

        // Frame setup
        setTitle("Add new Flight");
        setSize(400, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(8, 2, 10, 10));
        setResizable(false);

        // Background image panel
        BackgroundImagePanel bgPanel = new BackgroundImagePanel("/ui/Icons/4.jpg");
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

        formPanel.add(new JLabel("Price"));
        priceField = new JTextField();
        formPanel.add(priceField);

        // Add button
        JButton addButton = new JButton("Add Flight");
        addButton.addActionListener(_ -> insertFlight());
        formPanel.add(new JLabel()); // Empty cell for layout
        formPanel.add(addButton);

        // Styling components
        for (Component comp : formPanel.getComponents())
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
                comp.setFont(new Font("Arial", Font.BOLD, 15));
            }
        }

        bgPanel.add(formPanel, BorderLayout.CENTER);
    }

    /**
     * Validates input and inserts flight data into the database.
     */
    private void insertFlight()
    {
        String airline = airlineField.getText().trim();
        String source = sourceField.getText().trim();
        String dest = destField.getText().trim();
        String date = dateField.getText().trim();
        String depart = departField.getText().trim();
        String arrive = arriveField.getText().trim();
        String priceStr = priceField.getText().trim();

        // Basic validation
        if (airline.isEmpty() || source.isEmpty() || dest.isEmpty() || date.isEmpty() || depart.isEmpty() || arrive.isEmpty() || priceStr.isEmpty())
        {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try
        {
            double price = Double.parseDouble(priceStr);

            // Parse date
            Date sqlDate;
            try
            {
                sqlDate = Date.valueOf(date);
            }
            catch (IllegalArgumentException e)
            {
                JOptionPane.showMessageDialog(this, "Invalid date format. Use yyyy-MM-dd.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Parse time
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            java.sql.Time depSQL = new java.sql.Time(sdf.parse(depart).getTime());
            java.sql.Time arrSQL = new java.sql.Time(sdf.parse(arrive).getTime());

            // Insert into database
            Connection con = DBConnection.getConnection();
            String q = "Insert into flights(airline_name, source, destination, date, departure_time, arrival_time, price) values(?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stm = con.prepareStatement(q);
            stm.setString(1, airline);
            stm.setString(2, source);
            stm.setString(3, dest);
            stm.setDate(4, sqlDate);
            stm.setTime(5, depSQL);
            stm.setTime(6, arrSQL);
            stm.setDouble(7, price);

            int rows = stm.executeUpdate();
            if (rows > 0)
            {
                JOptionPane.showMessageDialog(this, "Flight added successfully!");
                dashboard.loadFlightData(); // Refresh dashboard
                dispose(); // Close form
            }
        }
        catch (NumberFormatException ex)
        {
            JOptionPane.showMessageDialog(this, "Invalid price format.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding flight", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
