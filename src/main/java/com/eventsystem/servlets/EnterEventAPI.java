package com.eventsystem.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/EnterEventAPI")
public class EnterEventAPI extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
     
        String eventId = request.getParameter("eventId");
        String role = request.getParameter("role"); 
        
        if (eventId != null && role != null) {
            
       
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.setAttribute("activeEventId", Integer.parseInt(eventId));
                session.setAttribute("activeRole", role);
            }
            
       
            if (role.equalsIgnoreCase("Admin")) {
                response.sendRedirect("admin_dashboard.html");
            } else if (role.equalsIgnoreCase("Organizer")) {
                response.sendRedirect("organizer_dashboard.html");
            } else {
                response.sendRedirect("participant_dashboard.html");
            }
            
        } else {
        
            response.sendRedirect("dashboard.html"); 
        }
    }
}