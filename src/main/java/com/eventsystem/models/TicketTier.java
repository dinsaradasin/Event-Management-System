package com.eventsystem.models;

public class TicketTier {
    private int tierId;
    private String tierName;
    private double price;
    private String description;
    private int totalCapacity;
    private int maxPerUser;
    private int ticketsSold; // NEW: Tracks live sales

    // CONSTRUCTOR 1: Used when an Admin creates a brand new tier (Starts with 0 sales)
    public TicketTier(int tierId, String tierName, double price, String description, int totalCapacity, int maxPerUser) {
        this.tierId = tierId;
        this.tierName = tierName;
        this.price = price;
        this.description = description;
        this.totalCapacity = totalCapacity;
        this.maxPerUser = maxPerUser;
        this.ticketsSold = 0; 
    }

    // CONSTRUCTOR 2 (OVERLOADED): Used by the DAO to pull live stats from the database
    public TicketTier(int tierId, String tierName, double price, String description, int totalCapacity, int maxPerUser, int ticketsSold) {
        this.tierId = tierId;
        this.tierName = tierName;
        this.price = price;
        this.description = description;
        this.totalCapacity = totalCapacity;
        this.maxPerUser = maxPerUser;
        this.ticketsSold = ticketsSold;
    }

    // Getters
    public int getTierId() { return tierId; }
    public String getTierName() { return tierName; }
    public double getPrice() { return price; }
    public String getDescription() { return description; }
    public int getTotalCapacity() { return totalCapacity; }
    public int getMaxPerUser() { return maxPerUser; }
    public int getTicketsSold() { return ticketsSold; } // NEW
}