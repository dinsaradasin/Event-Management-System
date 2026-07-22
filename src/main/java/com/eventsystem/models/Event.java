package com.eventsystem.models;

public class Event {
   
    private int eventId;
    private String eventTitle;
    private String location;
    private int capacity;
    private String eventCode;
    private String currentUserRole;

  
    public Event(int eventId, String eventTitle, String location, int capacity, String eventCode, String currentUserRole) {
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.location = location;
        this.capacity = capacity;
        this.eventCode = eventCode;
        this.currentUserRole = currentUserRole; 
    }

  
    public int getEventId() { return eventId; }
    public String getEventTitle() { return eventTitle; }
    public String getLocation() { return location; }
    public int getCapacity() { return capacity; }
    public String getEventCode() { return eventCode; }
    public String getCurrentUserRole() { return currentUserRole; } 

   
    public void setEventId(int eventId) { this.eventId = eventId; }
    public void setEventTitle(String eventTitle) { this.eventTitle = eventTitle; }
    public void setLocation(String location) { this.location = location; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public void setEventCode(String eventCode) { this.eventCode = eventCode; }
    public void setCurrentUserRole(String currentUserRole) { this.currentUserRole = currentUserRole; }

 
    @Override
    public String toString() {
        return "Event[ID=" + eventId + ", Title=" + eventTitle + ", Location=" + location + "]";
    }
}