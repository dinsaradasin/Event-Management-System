package com.eventsystem.utils;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnector {
    
    public static Connection connectToXAMPP() {
        try {
         
            Class.forName("com.mysql.cj.jdbc.Driver");
            
         
            String databaseUrl = "jdbc:mysql://localhost:3306/event_management";
            String databaseUsername = "root";
            String databasePassword = ""; 
            
        
            return DriverManager.getConnection(databaseUrl, databaseUsername, databasePassword);
            
        } catch (Exception error) {
            System.out.println("Oops! Database connection failed: " + error.getMessage());
            return null;
        }
    }
}