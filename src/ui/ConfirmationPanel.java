package ui;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * ConfirmationPanel - Displays final booking summary and allows user to finish or start a new search.
 */
public class ConfirmationPanel extends JPanel
{
    // Shared booking state
    private final BookingState state;

    // UI component to show summary
    private final JTextArea summary;

    // Constructor
    public ConfirmationPanel(BookingState state)
    {
        this.state = state;
        setLayout(new BorderLayout(10, 10));

        // Background image wrapper
        BackgroundImagePanel backgroundPanel = new BackgroundImagePanel("/ui/Icons/11.jpg");
        backgroundPanel.setLayout(new BorderLayout());
        add(backgroundPanel, BorderLayout.CENTER);

        // Title
        JLabel title = new JLabel("Booking Confirmation", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 50));
        backgroundPanel.add(title, BorderLayout.NORTH);

        // Summary text area
        summary = new JTextArea();
        summary.setEditable(false);
        summary.setFont(new Font("Monospaced", Font.BOLD, 22));
        summary.setOpaque(false);
        summary.setForeground(Color.RED);

        JScrollPane scrollPane = new JScrollPane(summary);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        backgroundPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom buttons
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnFinish = new JButton("Finish");
        JButton btnNew = new JButton("New Search");
        bottom.add(btnNew);
        bottom.add(btnFinish);
        bottom.setOpaque(false);
        backgroundPanel.add(bottom, BorderLayout.SOUTH);

        // Button actions
        btnFinish.addActionListener(_ -> SwingUtilities.getWindowAncestor(this).dispose());
        btnNew.addActionListener(_ -> {
            // Reset minimal state for new search
            state.selectedSeats.clear();
            state.bookingIds.clear();
            state.passengerEmail = null;
            state.passengerName = null;
            state.showCard("SEARCH");
        });

        // Render summary when panel becomes visible
        this.addComponentListener(new java.awt.event.ComponentAdapter()
        {
            public void componentShown(java.awt.event.ComponentEvent e)
            {
                renderSummary();
            }
        });
    }

    /**
     * Renders the final booking summary using current state.
     */
    private void renderSummary()
    {
        NumberFormat currency = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        BigDecimal totalPrice = state.totalPrice != null ? state.totalPrice : BigDecimal.ZERO;

        String sb = "Booking IDs: " + state.bookingIds.stream().map(Object::toString).collect(Collectors.joining(", ")) + "\n\n" +
                "Passenger: " + state.passengerName + " (" + state.passengerEmail + ")\n" +
                "Flight: " + state.airlineName + " | " + state.source + " -> " + state.destination + "\n" +
                "Date: " + state.flightDate + "\n" +
                "Departure: " + state.departureTime + "\n" +
                "Arrival: " + state.arrivalTime + "\n" +
                "Seats: " + state.selectedSeats + "\n" +
                "Total Price: " + currency.format(totalPrice) + "\n" +
                "\nPlease save your booking IDs for cancellation or reference.\n";

        summary.setText(sb);
    }
}
