package com.eventsystem.servlets;

import com.eventsystem.dao.EventDatabaseActions;
import com.eventsystem.models.TicketTier; 
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession; 

@WebServlet("/AddTicketTierAPI")
public class AddTicketTierAPI extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
           
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("activeEventId") == null) {
                response.sendRedirect("dashboard.html");
                return;
            }
            int eventId = (int) session.getAttribute("activeEventId");

            String tierName = request.getParameter("tier_name");
            double price = Double.parseDouble(request.getParameter("price"));
            String description = request.getParameter("description");
            
          
            int totalCapacity = Integer.parseInt(request.getParameter("total_capacity"));
            int maxPerUser = Integer.parseInt(request.getParameter("max_per_user"));
            
            
            TicketTier newTier = new TicketTier(0, tierName, price, description, totalCapacity, maxPerUser);
            
            EventDatabaseActions db = new EventDatabaseActions();
            
            
            db.insertTicketTier(
                eventId, 
                newTier.getTierName(), 
                newTier.getPrice(), 
                newTier.getDescription(),
                newTier.getTotalCapacity(),
                newTier.getMaxPerUser()
            );
            
        
            response.sendRedirect("admin_dashboard.html?tab=tickets");
        } catch (Exception e) {
            response.sendRedirect("dashboard.html"); 
        }
    }
}