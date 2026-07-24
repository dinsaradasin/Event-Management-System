package com.eventsystem.servlets;

import com.eventsystem.dao.EventDatabaseActions;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/DeleteTicketTierAPI")
public class DeleteTicketTierAPI extends HttpServlet {

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
            
        
            int tierId = Integer.parseInt(request.getParameter("tierId"));
            
            EventDatabaseActions db = new EventDatabaseActions();
            db.deleteTicketTier(eventId, tierId);
            
       
            response.sendRedirect("admin_dashboard.html?tab=tickets");
            
        } catch (Exception e) {
            System.out.println("Error deleting ticket: " + e.getMessage());
            response.sendRedirect("dashboard.html");
        }
    }
}