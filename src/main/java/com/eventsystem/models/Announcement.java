package com.eventsystem.models;

public class Announcement {
  
    private int announcementId;
    private String message;
    private String timeSent;

    
    public Announcement(int announcementId, String message, String timeSent) {
        this.announcementId = announcementId;
        this.message = message;
        this.timeSent = timeSent;
    }

    
    public int getAnnouncementId() { return announcementId; }
    public String getMessage() { return message; }
    public String getTimeSent() { return timeSent; }

  
    public void setAnnouncementId(int announcementId) { this.announcementId = announcementId; }
    public void setMessage(String message) { this.message = message; }
    public void setTimeSent(String timeSent) { this.timeSent = timeSent; }

    
    @Override
    public String toString() {
        return "Announcement[ID=" + announcementId + ", Time=" + timeSent + "]";
    }
}