package com.eventsystem.dao;

import com.eventsystem.models.User;


public interface UserDao {
    User attemptLogin(String enteredEmail, String enteredPassword);
    boolean registerNewUser(String newName, String newEmail, String newPassword);
}