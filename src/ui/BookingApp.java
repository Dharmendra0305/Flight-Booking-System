package ui;

import javax.swing.*;
import java.awt.*;

/**
 * BookingApp - Main container for the airline booking workflow.
 * Uses CardLayout to switch between search, seat selection, payment, and confirmation panels.
 */
public class BookingApp extends JFrame
{
    // Layout and panel container
    private final CardLayout cardLayout;
    private final JPanel cardPanel;

    // Shared booking state
    BookingState state = new BookingState();

    // Constructor
    public BookingApp()
    {
        // Frame setup
        setTitle("Airline Booking System");
        setSize(900, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize layout and container
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Initialize panels
        SearchPanel search = new SearchPanel(this, state);
        SeatSelectionPanel seatSel = new SeatSelectionPanel(this, state);
        PaymentPanel payment = new PaymentPanel(state);
        ConfirmationPanel conf = new ConfirmationPanel(state);

        // Add panels to card container

        cardPanel.add(search, "SEARCH");
        cardPanel.add(seatSel, "SEAT");
        cardPanel.add(payment, "PAY");
        cardPanel.add(conf, "CONF");

        // Add card container to frame
        add(cardPanel);

        // Show initial panel
        cardLayout.show(cardPanel, "SEARCH");

        // Set navigation callback in shared state
        state.setShowCardListener(name -> cardLayout.show(cardPanel, name));
    }

    // Optional direct navigation methods
    public void showSearch() { cardLayout.show(cardPanel, "SEARCH"); }
    public void showSeatSelection() { cardLayout.show(cardPanel, "SEAT"); }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() -> new BookingApp().setVisible(true));
    }
}
