package com.eventsystem.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/SelectEvent")
public class SelectEvent extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("activeUser") == null) {
                response.sendRedirect("index.html");
                return;
            }

            int eventId = Integer.parseInt(request.getParameter("eventId"));
            String role = request.getParameter("role");

        
            session.setAttribute("activeEventId", eventId);

           
            if ("Admin".equalsIgnoreCase(role)) {
                response.sendRedirect("admin_dashboard.html");
            } else if ("Organizer".equalsIgnoreCase(role)) {
                response.sendRedirect("organizer_dashboard.html");
            } else {
                response.sendRedirect("participant_dashboard.html");
            }
            
        } catch (Exception e) {
            System.out.println("Error selecting event: " + e.getMessage());
            response.sendRedirect("dashboard.html");
        }
    }
}