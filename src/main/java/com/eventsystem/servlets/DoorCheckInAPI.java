package com.eventsystem.servlets;

import com.eventsystem.dao.EventDatabaseActions;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession; 

@WebServlet("/DoorCheckInAPI")
public class DoorCheckInAPI extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("activeEventId") == null) {
                response.sendRedirect("dashboard.html");
                return;
            }
            int eventId = (int) session.getAttribute("activeEventId");
            
            String token = request.getParameter("ticket_token").trim(); 
            
            EventDatabaseActions db = new EventDatabaseActions();
            String result = db.processDoorCheckIn(eventId, token);
            
           
            response.sendRedirect("organizer_dashboard.html?tab=door&checkin=" + result);
            
        } catch (Exception e) {
            System.out.println("Error processing door check-in: " + e.getMessage());
            response.sendRedirect("dashboard.html");
        }
    }
}