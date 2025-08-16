package ui;

import db.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * PaymentPanel - Handles passenger details input, payment simulation,
 * booking confirmation, and database insertion.
 */
public class PaymentPanel extends JPanel
{
    // Shared booking state
    private final BookingState state;

    // UI components
    private final List<JTextField> nameFields = new ArrayList<>();
    private final List<JTextField> ageFields = new ArrayList<>();
    private final JTextField emailFields = new JTextField();
    private final JLabel lblSummary;
    private final JPanel form;

    // Pricing surcharges
    private static final int BUSINESS_SURCHARGE = 8000;
    private static final int WINDOW_SURCHARGE = 250;
    private static final int AISLE_SURCHARGE = 200;
    private static final int MIDDLE_SURCHARGE = 150;

    // Constructor
    public PaymentPanel(BookingState state)
    {
        this.state = state;
        setLayout(new BorderLayout(8, 8));

        // Background image wrapper
        BackgroundImagePanel backgroundPanel = new BackgroundImagePanel("/ui/Icons/10.jpg");
        backgroundPanel.setLayout(new BorderLayout());
        add(backgroundPanel, BorderLayout.CENTER);

        // Title
        JLabel title = new JLabel("Passenger Details & Payment", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        backgroundPanel.add(title, BorderLayout.NORTH);

        // Form panel (scrollable)
        form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.setOpaque(false);
        form.setBorder(BorderFactory.createEmptyBorder(10, 120, 10, 120));
        form.setPreferredSize(new Dimension(400, 600));

        JScrollPane scrollPane = new JScrollPane(form);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        backgroundPanel.add(scrollPane, BorderLayout.CENTER);

        // Booking summary label
        lblSummary = new JLabel("<html>Summary will appear here</html>");
        lblSummary.setFont(new Font("SansSerif", Font.PLAIN, 16));
        lblSummary.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 0));
        lblSummary.setForeground(Color.YELLOW);
        backgroundPanel.add(lblSummary, BorderLayout.WEST);

        // Bottom navigation buttons
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnBack = new JButton("Back to Seats");
        JButton btnPay = new JButton("Confirm & Pay (Simulated)");
        bottom.add(btnBack);
        bottom.add(btnPay);
        bottom.setOpaque(false);
        backgroundPanel.add(bottom, BorderLayout.SOUTH);

        // Button actions
        btnBack.addActionListener(_ -> state.showCard("SEAT"));
        btnPay.addActionListener(_ -> doPayment());
    }

    /**
     * Called when this panel becomes visible.
     * Pre-fills passenger details and refreshes booking summary.
     */
    public void setVisible(boolean aFlag)
    {
        super.setVisible(aFlag);
        if (aFlag)
        {
            refreshForm();
            refreshSummary();
        }
    }

    /**
     * Dynamically builds the passenger form based on selected seats.
     */
    private void refreshForm()
    {
        form.removeAll();
        nameFields.clear();
        ageFields.clear();

        List<String> seats = new ArrayList<>(state.selectedSeats);
        for (String seat : seats)
        {
            form.add(new JLabel("Passenger for " + seat + " - Name:"));
            JTextField nameField = new JTextField();
            nameFields.add(nameField);
            form.add(nameField);

            form.add(new JLabel("Passenger for " + seat + " - Age:"));
            JTextField ageField = new JTextField();
            ageFields.add(ageField);
            form.add(ageField);
        }

        form.add(new JLabel("Passenger Email:"));
        emailFields.setText(state.passengerEmail != null ? state.passengerEmail : "");
        form.add(emailFields);

        // Initial payment method field
        form.add(new JLabel("Payment Method:"));
        form.add(new JComboBox<>(new String[]{"Card (Simulated)", "UPI (Simulated)"}));

        form.revalidate();
        form.repaint();
    }

    /**
     * Refreshes the booking summary and calculates total price.
     */
    private void refreshSummary()
    {
        StringBuilder sb = new StringBuilder("<html><b>Booking Summary</b><br>");
        sb.append("Flight: ").append(state.airlineName).append(" — ").append(state.source).append(" → ").append(state.destination).append("<br>");
        sb.append("Date: ").append(state.flightDate).append("<br>");
        sb.append("Selected seats: ").append(state.selectedSeats.toString()).append("<br><br>");

        BigDecimal total = BigDecimal.ZERO;
        List<String> seats = new ArrayList<>(state.selectedSeats);

        for (String s : seats)
        {
            int row = Integer.parseInt(s.replaceAll("[^0-9]", ""));
            char col = s.replaceAll("[0-9]","").charAt(0);
            int seatPrice = state.price.intValue();

            // Business class surcharge
            if (row <= 5) seatPrice = BUSINESS_SURCHARGE;

            // Column-based surcharges
           seatPrice += switch (col)
           {
               case 'A', 'F' -> WINDOW_SURCHARGE;
               case 'C', 'D' -> AISLE_SURCHARGE;
               case 'B', 'E' -> MIDDLE_SURCHARGE;
               default -> 0;
           };

            total = total.add(BigDecimal.valueOf(seatPrice));
            sb.append(s).append(": ₹").append(seatPrice).append("<br>");
        }

        state.totalPrice = total;
        sb.append("<br><b>Total: ₹").append(total).append("</b></html>");
        lblSummary.setText(sb.toString());
    }

    /**
     * Simulates payment and inserts booking records into the database.
     * Includes seat availability check and rollback on failure.
     */
    private void doPayment()
    {
        List<String> names = new ArrayList<>();
        List<String> ages = new ArrayList<>();
        String email = emailFields.getText().trim();

        // Validate passenger input
        for (int i = 0; i < nameFields.size(); i++)
        {
            String name = nameFields.get(i).getText().trim();
            String age = ageFields.get(i).getText().trim();

            if (name.isEmpty() || age.isEmpty())
            {
                JOptionPane.showMessageDialog(this,"Fill details for all passengers.");
                return;
            }

            names.add(name);
            ages.add(age);
        }

        if (email.isEmpty())
        {
            JOptionPane.showMessageDialog(this, "Enter passenger email.");
            return;
        }

        if (state.selectedSeats.isEmpty())
        {
            JOptionPane.showMessageDialog(this,"Select seats first.");
            return;
        }

        int c = JOptionPane.showConfirmDialog(this, "Simulate payment now?", "Payment", JOptionPane.YES_NO_OPTION);
        if (c != JOptionPane.YES_OPTION) return;

        try (Connection conn = DBConnection.getConnection())
        {
            conn.setAutoCommit(false);
            try
            {
                List<Integer> generatedIds = new ArrayList<>();
                List<String> seats = new ArrayList<>(state.selectedSeats);

                for (int i = 0; i < state.selectedSeats.size(); i++)
                {
                    String seat = seats.get(i);
                    String name = names.get(i);
                    String age = ages.get(i);

                    // Check if seat is already booked
                    PreparedStatement chk = conn.prepareStatement("SELECT COUNT(*) FROM bookings WHERE flight_id=? AND seat_number=?");
                    chk.setInt(1, state.flightId);
                    chk.setString(2, seat);
                    ResultSet rs = chk.executeQuery();

                    if (rs.next() && rs.getInt(1) > 0)
                    {
                        conn.rollback();
                        JOptionPane.showMessageDialog(this, "Seat " + seat + " was just booked. Please select different seats.");
                        reloadBookedSeats();
                        state.showCard("SEAT");
                        return;
                    }

                    // Insert booking
                    PreparedStatement ins = conn.prepareStatement(
                            "INSERT INTO bookings (flight_id, passenger_name, age, email, seat_number, payment_status) VALUES (?,?,?,?,?,?)",
                            Statement.RETURN_GENERATED_KEYS
                    );
                    ins.setInt(1, state.flightId);
                    ins.setString(2, name);
                    ins.setInt(3, Integer.parseInt(age));
                    ins.setString(4, email);
                    ins.setString(5, seat);
                    ins.setString(6, "PAID");

                    int affected = ins.executeUpdate();
                    if (affected == 0)
                    {
                        conn.rollback();
                        throw new SQLException("Insert failed for " + seat);
                    }

                    ResultSet k = ins.getGeneratedKeys();
                    if (k.next()) generatedIds.add(k.getInt(1));
                }

                conn.commit();

                // Update state
                state.passengerName = names.getFirst();
                state.passengerEmail = email;
                state.bookingIds.clear();
                state.bookingIds.addAll(generatedIds);
                state.bookedSeats.addAll(state.selectedSeats);

                JOptionPane.showMessageDialog(this, "Booking successful! Booking IDs: " + state.bookingIds);
                state.showCard("CONF");
            }
            catch (SQLException ex)
            {
                conn.rollback();
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Booking failed: " + ex.getMessage());
            }
            finally
            {
                conn.setAutoCommit(true);
            }
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage());
        }
    }

    /**
     * Reloads booked seats from the database to update local cache.
     */
    private void reloadBookedSeats()
    {
        state.bookedSeats.clear();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement("SELECT seat_number FROM bookings WHERE flight_id=?"))
        {
            pst.setInt(1, state.flightId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) state.bookedSeats.add(rs.getString("seat_number"));
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
        }
    }
}
