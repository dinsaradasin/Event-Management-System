package com.eventsystem.models;

public class LogisticsStat {
    
    private String requirement;
    private int guestCount;

    
    public LogisticsStat(String requirement, int guestCount) {
        this.requirement = requirement;
        this.guestCount = guestCount;
    }

  
    public String getRequirement() { return requirement; }
    public int getGuestCount() { return guestCount; }

 
    public void setRequirement(String requirement) { this.requirement = requirement; }
    public void setGuestCount(int guestCount) { this.guestCount = guestCount; }

  
    @Override
    public String toString() {
        return "LogisticsStat[Requirement=" + requirement + ", Count=" + guestCount + "]";
    }
}