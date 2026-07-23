package com.eventsystem.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/LogoutAPI")
public class LogoutAPI extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
         
            HttpSession session = request.getSession(false);
            
       
            if (session != null) {
                session.invalidate();
            }
            
       
            response.sendRedirect("index.html");
            
        } catch (Exception e) {
            System.out.println("Error during logout: " + e.getMessage());
            response.sendRedirect("index.html");
        }
    }
}