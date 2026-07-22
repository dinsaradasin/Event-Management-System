package com.eventsystem.models;

public class AgendaItem {
   
    private int agendaId;
    private String title;
    private String date;
    private String startTime;
    private String endTime;

    
    public AgendaItem(int agendaId, String title, String date, String startTime, String endTime) {
        this.agendaId = agendaId;
        this.title = title;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    
    public int getAgendaId() { return agendaId; }
    public String getTitle() { return title; }
    public String getDate() { return date; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }

  
    public void setAgendaId(int agendaId) { this.agendaId = agendaId; }
    public void setTitle(String title) { this.title = title; }
    public void setDate(String date) { this.date = date; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

  
    @Override
    public String toString() {
        return "AgendaItem[ID=" + agendaId + ", Title=" + title + ", Time=" + startTime + "]";
    }
}