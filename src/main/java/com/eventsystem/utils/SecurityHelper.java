package com.eventsystem.utils;

import java.security.MessageDigest;
import java.util.Base64;

public class SecurityHelper {
    
    /**
     * Hashes a plain text password using the built-in SHA-256 algorithm.
     * We use a 'static' method here so we can call SecurityHelper.hashPassword() 
     * anywhere in our project without needing to use the 'new' keyword.
     */
    public static String hashPassword(String plainTextPassword) {
        try {
          
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            
       
            byte[] hash = digest.digest(plainTextPassword.getBytes("UTF-8"));
            
         
            return Base64.getEncoder().encodeToString(hash);
            
        } catch (Exception e) {
    
            throw new RuntimeException("Critical Error: Failed to hash password", e);
        }
    }
}