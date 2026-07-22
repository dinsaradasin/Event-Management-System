package com.eventsystem.models;

public class User {
    private int userId;
    private String fullName;
    private String emailAddress;
    private String hiddenPassword;

   
    public User(int userId, String fullName, String emailAddress, String hiddenPassword) {
        this.userId = userId;
        this.fullName = fullName;
        this.emailAddress = emailAddress;
        this.hiddenPassword = hiddenPassword;
    }

    
    public User(String fullName, String emailAddress, String hiddenPassword) {
        this.userId = 0; 
        this.fullName = fullName;
        this.emailAddress = emailAddress;
        this.hiddenPassword = hiddenPassword;
    }

    public int getUserId() { return userId; }
    public String getFullName() { return fullName; }
    public String getEmailAddress() { return emailAddress; }
    public String getHiddenPassword() { return hiddenPassword; }

    @Override
    public String toString() {
        return "User[Name=" + fullName + ", Email=" + emailAddress + "]";
    }
}