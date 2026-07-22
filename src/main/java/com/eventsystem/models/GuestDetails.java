package com.eventsystem.models;

public class GuestDetails {
    private String guestName;
    private String ticketTier;
    private String status;

    public GuestDetails(String guestName, String ticketTier, String status) {
        this.guestName = guestName;
        this.ticketTier = ticketTier;
        this.status = status;
    }

    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }

    public String getTicketTier() { return ticketTier; }
    public void setTicketTier(String ticketTier) { this.ticketTier = ticketTier; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}