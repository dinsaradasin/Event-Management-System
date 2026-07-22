package com.eventsystem.models;

public class Ticket {
    private int ticketId;
    private int eventId;
    private String ticketTier;
    private String secureToken;
    private String checkInStatus;

    
    public Ticket() {}

    
    public Ticket(int ticketId, int eventId, String ticketTier, String secureToken, String checkInStatus) {
        this.ticketId = ticketId;
        this.eventId = eventId;
        this.ticketTier = ticketTier;
        this.secureToken = secureToken;
        this.checkInStatus = checkInStatus;
    }

   

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getTicketTier() {
        return ticketTier;
    }

    public void setTicketTier(String ticketTier) {
        this.ticketTier = ticketTier;
    }

    public String getSecureToken() {
        return secureToken;
    }

    public void setSecureToken(String secureToken) {
        this.secureToken = secureToken;
    }

    public String getCheckInStatus() {
        return checkInStatus;
    }

    public void setCheckInStatus(String checkInStatus) {
        this.checkInStatus = checkInStatus;
    }
}