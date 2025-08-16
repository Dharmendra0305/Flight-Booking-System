package ui;

import javax.swing.*;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * SeatSelectionPanel - Allows users to select seats for a flight.
 * Displays a grid of seats with visual indicators and pricing info.
 */
public class SeatSelectionPanel extends JPanel
{
    // Shared booking state
    private final BookingState state;

    // UI components
    private final Map<String, JToggleButton> seatButtons = new HashMap<>();
    private final JPanel gridPanel;
    private final JLabel lblInfo;

    // Seat layout configuration
    private final String[] cols = {"A", "B", "C", "D", "E", "F"};

    // Constructor
    public SeatSelectionPanel(BookingApp app, BookingState state)
    {
        this.state = state;
        setLayout(new BorderLayout(10, 10));

        // Title
        JLabel title = new JLabel("Select Seat", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        // Seat grid panel
        gridPanel = new JPanel(new GridBagLayout());
        add(new JScrollPane(gridPanel), BorderLayout.CENTER);

        // Bottom panel with navigation buttons
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        lblInfo = new JLabel("Selected: 0 seats");
        JButton btnBack = new JButton("Back to Search");
        JButton btnNext = new JButton("Proceed to Payment");

        bottom.add(lblInfo);
        bottom.add(btnBack);
        bottom.add(btnNext);
        bottom.setOpaque(false);
        add(bottom, BorderLayout.SOUTH);

        // Navigation actions
        btnBack.addActionListener(_ -> app.showSearch());
        btnNext.addActionListener(_ ->
        {
            if (state.selectedSeats.isEmpty())
            {
                JOptionPane.showMessageDialog(this, "Please select a seat.");
                return;
            }
            state.showCard("PAY");
        });
    }

    /**
     * Called when this panel becomes visible.
     * Rebuilds the seat grid based on current flight and booking state.
     */
    public void setVisible(boolean aFlag)
    {
        super.setVisible(aFlag);
        if (aFlag)
        {
            populateSeats();
        }
    }

    /**
     * Populates the seat grid with toggle buttons and visual indicators.
     */
    private void populateSeats()
    {
        gridPanel.removeAll();
        seatButtons.clear();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);

        int ROWS = 30;
        for (int r = 1; r <= ROWS; r++)
        {
            for (int c = 0; c < cols.length; c++)
            {
                int gridx = c;
                if (c >= 3) gridx = c + 1; // Aisle gap

                gbc.gridx = gridx;
                gbc.gridy = r - 1;

                // Insert aisle gap between C and D
                if (c == 3)
                {
                    GridBagConstraints aisleGBC = new GridBagConstraints();
                    aisleGBC.gridx = 3;
                    aisleGBC.gridy = r - 1;
                    aisleGBC.insets = new Insets(6, 6, 6, 6);

                    JPanel aisleGap = new JPanel();
                    aisleGap.setPreferredSize(new Dimension(20, 40));
                    aisleGap.setOpaque(false);
                    gridPanel.add(aisleGap, aisleGBC);
                }

                String seat = r + cols[c];
                JToggleButton btn = getJToggleButton(seat, r);
                seatButtons.put(seat, btn);
                gridPanel.add(btn, gbc);
            }
        }

        // Seat status legend and pricing info
        JPanel statusLegend = new JPanel();
        statusLegend.setLayout(new BoxLayout(statusLegend, BoxLayout.Y_AXIS));
        statusLegend.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 10));

        statusLegend.add(legendLabel("Available", new Color(120,200,120)));
        statusLegend.add(Box.createVerticalStrut(10));
        statusLegend.add(legendLabel("Booked", new Color(220,80,80)));
        statusLegend.add(Box.createVerticalStrut(10));
        statusLegend.add(legendLabel("Selected", new Color(255,220,120)));
        statusLegend.add(Box.createVerticalStrut(20));

        statusLegend.add(new JLabel(
                "<html> " + "\n" +
                        "<br><b>Seat Pricing:</b><br>" + "\n" +
                        "Base Price: ₹" + state.price.intValue() + "<br>" + "\n" +
                        "Business Class (Rows 1–5): ₹8000 <br>" + "\n" +
                        "Window Seat (A/F): +₹250 <br>" + "\n" +
                        "Middle Seat (B/E): +₹150 <br> " + "\n" +
                        "Aisle Seat (C/D): +₹200 " + "\n" +
                        "</html>")).setFont(new Font("SansSerif", Font.PLAIN, 14));

        add(statusLegend, BorderLayout.WEST);

        revalidate();
        repaint();
    }

    /**
     * Creates a toggle button for a seat with appropriate styling and behavior.
     */
    private JToggleButton getJToggleButton(String seat, int row)
    {
        JToggleButton btn = new JToggleButton(seat);
        btn.setPreferredSize(new Dimension(70, 40));
        btn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btn.setOpaque(true);

        if (state.bookedSeats.contains(seat))
        {
            // Booked seat
            btn.setEnabled(false);
            btn.setBackground(new Color(220, 80, 80));
            btn.setForeground(Color.WHITE);
            btn.setToolTipText("Booked");
        }
        else
        {
            // Available seat
            btn.setBackground(new Color(120, 200, 120));
            btn.addActionListener(_ ->
            {
                if (btn.isSelected())
                {
                    btn.setBackground(new Color(255, 220, 120));
                    state.selectedSeats.add(seat);
                }
                else
                {
                    btn.setBackground(new Color(120, 200, 120));
                    state.selectedSeats.remove(seat);
                }
                lblInfo.setText("Selected: " + state.selectedSeats.size() + " seats");
            });
        }

        // Business class visual styling
        if (row <= 5)
        {
            btn.setBorder(BorderFactory.createLineBorder(new Color(60,120,200), 2));
        }
        else
        {
            btn.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
        }
        return btn;
    }

    /**
     * Creates a label for the seat status legend.
     */
    private JLabel legendLabel(String text, Color color)
    {
        JLabel l = new JLabel(text);
        l.setOpaque(true);
        l.setBackground(color);
        l.setBorder(BorderFactory.createEmptyBorder(4,8,4,8));
        return l;
    }
}
