package com.eventsystem.servlets;

import com.eventsystem.dao.EventDatabaseActions;
import com.eventsystem.models.Announcement; 
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession; 

@WebServlet("/SendAnnouncementAPI")
public class SendAnnouncementAPI extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
           
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("activeEventId") == null) {
                response.sendRedirect("dashboard.html");
                return;
            }
            int eventId = (int) session.getAttribute("activeEventId");
            
            String message = request.getParameter("message");
            
         
            Announcement newAnn = new Announcement(0, message, "");
            
            EventDatabaseActions db = new EventDatabaseActions();
         
            db.insertAnnouncement(eventId, newAnn.getMessage());
            
        
            response.sendRedirect("admin_dashboard.html?tab=broadcast");
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            response.sendRedirect("admin_dashboard.html?tab=broadcast&error=SystemError");
        }
    }
}