package ui;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * BookingState - Holds shared state for the flight booking process.
 * Tracks flight details, passenger info, seat selections, and navigation callbacks.
 */
public class BookingState
{
    // Flight details
    public int flightId = -1;
    public String airlineName, source, destination;
    public Date flightDate;
    public Time departureTime, arrivalTime;
    public BigDecimal price;

    // Pricing
    public BigDecimal totalPrice = BigDecimal.ZERO;

    // Passenger details
    public String passengerName, passengerEmail;

    // Booking identifiers
    public List<Integer> bookingIds = new ArrayList<>();

    // Seat tracking
    public Set<String> bookedSeats = new HashSet<>();
    public Set<String> selectedSeats = new HashSet<>();

    // UI navigation callback
    private java.util.function.Consumer<String> showCard;

    /**
     * Sets the callback to switch UI cards by name.
     * @param showCard Consumer that accepts a card name to display
     */
    public void setShowCardListener(java.util.function.Consumer<String> showCard)
    {
        this.showCard = showCard;
    }

    /**
     * Triggers the card switch using the registered callback.
     * @param name Name of the card to show
     */
    public void showCard(String name)
    {
        if (showCard != null) showCard.accept(name);
    }
}
