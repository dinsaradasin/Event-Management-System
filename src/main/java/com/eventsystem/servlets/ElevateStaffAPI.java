package com.eventsystem.servlets;

import com.eventsystem.dao.EventDatabaseActions;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession; 

@WebServlet("/ElevateStaffAPI")
public class ElevateStaffAPI extends HttpServlet {

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
            
         
            String targetEmail = request.getParameter("user_email");
            
           
            EventDatabaseActions db = new EventDatabaseActions();
            boolean success = db.promoteUserToOrganizer(eventId, targetEmail);
            
     
            response.sendRedirect("admin_dashboard.html?tab=staff");
            
        } catch (Exception e) {
            System.out.println("Elevate Error: " + e.getMessage());
            response.sendRedirect("dashboard.html"); 
        }
    }
}