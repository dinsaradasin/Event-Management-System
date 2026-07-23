package com.eventsystem.dao;

import com.eventsystem.models.User;
import com.eventsystem.utils.DatabaseConnector;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class UserDatabaseActions implements UserDao {

    @Override
    public User attemptLogin(String enteredEmail, String enteredPassword) {
        String sqlQuery = "SELECT * FROM Users WHERE email = ? AND password_hash = ?";
        try {
            Connection myConnection = DatabaseConnector.connectToXAMPP();
            PreparedStatement statement = myConnection.prepareStatement(sqlQuery);
            statement.setString(1, enteredEmail);
            statement.setString(2, enteredPassword); 
            
            ResultSet results = statement.executeQuery();
            
            if (results.next()) {
                User validUser = new User(
                    results.getInt("user_id"),
                    results.getString("name"),
                    results.getString("email"),
                    results.getString("password_hash")
                );
                myConnection.close();
                return validUser;
            }
            myConnection.close();
            
        } catch (Exception error) {
            System.out.println("Something went wrong during login: " + error.getMessage());
        }
        return null; 
    }
    
    @Override
    public boolean registerNewUser(String newName, String newEmail, String newPassword) {
        String sqlQuery = "INSERT INTO Users (name, email, password_hash) VALUES (?, ?, ?)";
        try {
            Connection myConnection = DatabaseConnector.connectToXAMPP();
            PreparedStatement statement = myConnection.prepareStatement(sqlQuery);
            statement.setString(1, newName);
            statement.setString(2, newEmail);
            statement.setString(3, newPassword);
            
            int rowsAdded = statement.executeUpdate();
            
            if (rowsAdded > 0) {
                myConnection.close(); 
                return true;
            }
            myConnection.close();
            
        } catch (Exception error) {
            System.out.println("Registration failed: " + error.getMessage());
        }
        return false; 
    }
}