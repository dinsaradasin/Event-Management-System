package com.eventsystem.servlets;

import com.eventsystem.dao.UserDatabaseActions;
import com.eventsystem.models.User; 
import com.eventsystem.utils.SecurityHelper; 
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/RegisterServlet") 
public class UserRegistrationHandler extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String typedName = request.getParameter("full_name");
            String typedEmail = request.getParameter("email");
            String typedPassword = request.getParameter("password");
            String confirmPassword = request.getParameter("confirm_password");

         
            if (!typedPassword.equals(confirmPassword)) {
                response.sendRedirect("register.html?error=passwordMismatch");
                return;
            }
            
       
            String hashedPassword = SecurityHelper.hashPassword(typedPassword);
            
         
            User newUser = new User(typedName, typedEmail, hashedPassword);
            
            UserDatabaseActions database = new UserDatabaseActions();
          
            boolean isSuccess = database.registerNewUser(
                    newUser.getFullName(), 
                    newUser.getEmailAddress(), 
                    newUser.getHiddenPassword()
            );
            
            if (isSuccess) {
                response.sendRedirect("index.html?message=success");
            } else {
                response.sendRedirect("register.html?error=emailTaken");
            }
            
        } catch (Exception e) {
            System.out.println("Registration Error: " + e.getMessage());
            response.sendRedirect("register.html?error=SystemError");
        }
    }
}