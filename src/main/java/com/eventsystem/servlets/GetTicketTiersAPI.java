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

@WebServlet("/GetTicketTiersAPI")
public class GetTicketTiersAPI extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        
        try (PrintWriter out = response.getWriter()) {
            
         
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("activeEventId") == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("<tr><td colspan='6' style='text-align: center; color: red;'>Unauthorized session.</td></tr>");
                return;
            }
            int eventId = (int) session.getAttribute("activeEventId");
            
            EventDatabaseActions db = new EventDatabaseActions();
            List<TicketTier> ticketList = db.getTicketTiers(eventId);
            
            StringBuilder html = new StringBuilder();
            
            if (ticketList.isEmpty()) {
                html.append("<tr><td colspan='6' style='text-align: center; padding: 32px; color: var(--text-muted); font-size: 14px;'>No tickets configured yet.</td></tr>");
            } else {
                for (TicketTier ticket : ticketList) {
               
                    int capacity = ticket.getTotalCapacity();
                    int sold = ticket.getTicketsSold();
                    int remaining = capacity - sold;
                    
                  
                    String remainingColor = (remaining <= 0) ? "color: #EF4444; font-weight: bold;" : "color: #10B981; font-weight: bold;";

                    html.append("<tr style='border-bottom: 1px solid var(--border-light);'>");
                    html.append("  <td style='padding: 16px 24px; font-weight: 600;'>").append(ticket.getTierName()).append("</td>");
                    
                    if (ticket.getPrice() == 0) {
                        html.append("  <td style='padding: 16px 24px; color: var(--success-text); font-weight: bold;'>FREE</td>");
                    } else {
                        html.append("  <td style='padding: 16px 24px; color: var(--brand-primary); font-weight: bold;'>LKR ").append(String.format("%.2f", ticket.getPrice())).append("</td>");
                    }
                    
                    html.append("  <td style='padding: 16px 24px; color: var(--text-muted); font-size: 13px;'>").append(ticket.getDescription()).append("</td>");
                    
                
                    html.append("  <td style='padding: 16px 24px; text-align: center; font-weight: 600;'>").append(sold).append(" / ").append(capacity).append("</td>");
                    html.append("  <td style='padding: 16px 24px; text-align: center; ").append(remainingColor).append("'>").append(remaining).append("</td>");

                    html.append("  <td style='padding: 16px 24px; text-align: right;'>");
                    html.append("    <button class='btn-danger' style='padding: 6px 12px; font-size: 12px;' onclick='deleteTicketTier(").append(ticket.getTierId()).append(", \"").append(ticket.getTierName()).append("\")'>Delete</button>");
                    html.append("  </td>");
                    html.append("</tr>");
                }
            }
            out.print(html.toString());
            
        } catch (Exception e) {
            System.out.println("Servlet Error: " + e.getMessage());
        }
    }
}