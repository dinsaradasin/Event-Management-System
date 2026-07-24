package com.eventsystem.servlets;

import com.eventsystem.dao.EventDatabaseActions;
import com.eventsystem.models.TicketTier;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession; 

@WebServlet("/GetTicketUserAPI")
public class GetTicketUserAPI extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        
        try (PrintWriter out = response.getWriter()) {
            
         
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("activeEventId") == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("<div style='color: red;'>Unauthorized session.</div>");
                return;
            }
            int eventId = (int) session.getAttribute("activeEventId");
            
           
            EventDatabaseActions dbActions = new EventDatabaseActions();
            
        
            List<TicketTier> availableTiers = dbActions.getTicketTiers(eventId);
            StringBuilder html = new StringBuilder();
            
        
            for (TicketTier tier : availableTiers) {
                
          
                String tierName = tier.getTierName();
                double price = tier.getPrice();
                String description = tier.getDescription();
                
          
                html.append("<div style='border: 1px solid var(--border-light); border-radius: 8px; padding: 24px; text-align: center; background: #FFFFFF; display: flex; flex-direction: column;'>");
                html.append("  <h4 style='font-size: 18px; margin-bottom: 8px; color: var(--text-main);'>").append(tierName).append("</h4>");
                html.append("  <div style='font-size: 32px; font-weight: 800; color: var(--text-main); margin-bottom: 16px;'>LKR ").append(price).append("</div>");
                html.append("  <p style='color: var(--text-muted); font-size: 14px; margin-bottom: 24px; flex-grow: 1;'>").append(description != null ? description : "Standard event access.").append("</p>");
                
                html.append("  <button class='btn-primary' style='width: 100%;' onclick=\"openCheckout('").append(tierName).append("')\">Select Ticket</button>");
                html.append("</div>");
            }
            
            out.print(html.toString()); 
            
        } catch (Exception e) {
            System.out.println("Error fetching user tickets: " + e.getMessage());
        }
    }
}