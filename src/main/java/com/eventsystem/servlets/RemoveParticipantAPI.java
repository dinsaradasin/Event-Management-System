package com.eventsystem.servlets;

import com.eventsystem.dao.EventDatabaseActions;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession; 

@WebServlet("/RemoveParticipantAPI")
public class RemoveParticipantAPI extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
        
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("activeEventId") == null) {
                response.sendRedirect("dashboard.html");
                return;
            }
            int eventId = (int) session.getAttribute("activeEventId");
            
            String targetEmail = request.getParameter("userEmail");
            
            EventDatabaseActions db = new EventDatabaseActions();
            db.removeParticipant(eventId, targetEmail);
            
       
            response.sendRedirect("admin_dashboard.html");
            
        } catch (Exception e) {
            System.out.println("Error removing participant: " + e.getMessage());
            response.sendRedirect("dashboard.html"); 
        }
    }
}