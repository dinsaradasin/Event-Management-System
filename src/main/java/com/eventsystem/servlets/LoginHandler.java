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
import javax.servlet.http.HttpSession;

@WebServlet("/LoginHandler") 
public class LoginHandler extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            String typedEmail = request.getParameter("email");
            String typedPassword = request.getParameter("password");
            String hashedPassword = SecurityHelper.hashPassword(typedPassword);
            
            UserDatabaseActions database = new UserDatabaseActions();
            User loggedInUser = database.attemptLogin(typedEmail, hashedPassword);
            
            if (loggedInUser != null) {
                HttpSession session = request.getSession();
                session.setAttribute("activeUser", loggedInUser);
                
             
                session.setAttribute("loggedUserId", loggedInUser.getUserId()); 
                
                response.sendRedirect("dashboard.html");
            } else {
                response.sendRedirect("index.html?error=InvalidCredentials");
            }
            
        } catch (Exception e) {
            System.out.println("Login Error: " + e.getMessage());
            response.sendRedirect("index.html?error=SystemError");
        }
    }
}