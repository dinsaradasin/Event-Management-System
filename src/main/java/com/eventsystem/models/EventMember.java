package com.eventsystem.models;


public class EventMember extends User {
    private String role;

    public EventMember(int userId, String name, String email, String role) {
   
        super(userId, name, email, ""); 
        this.role = role;
    }

    public String getRole() { return role; }

    @Override
    public String toString() {
        return "EventMember[" + getFullName() + ", Role=" + role + "]";
    }
}