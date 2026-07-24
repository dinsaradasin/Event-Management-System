package com.eventsystem.servlets;

import com.eventsystem.dao.EventDatabaseActions;
import com.eventsystem.models.GuestDetails;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/GetCheckedInGuestsAPI")
public class GetCheckedInGuestsAPI extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        try (PrintWriter out = response.getWriter()) {
            
        
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("activeEventId") == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("<tr><td colspan='3' class='empty-state' style='color:red;'>Unauthorized session.</td></tr>");
                return;
            }
            int eventId = (int) session.getAttribute("activeEventId");
            
            EventDatabaseActions db = new EventDatabaseActions();
            List<GuestDetails> guests = db.getCheckedInGuests(eventId);
            
            if (guests.isEmpty()) {
              
                out.print("<tr><td colspan='3' class='empty-state'>No tickets have been sold yet.</td></tr>");
                return;
            }
            
            StringBuilder html = new StringBuilder();
            for (GuestDetails guest : guests) {
                html.append("<tr>");
                html.append("  <td style='font-weight: 600; color: var(--text-main);'>").append(guest.getGuestName()).append("</td>");
                html.append("  <td style='color: var(--text-muted);'>").append(guest.getTicketTier()).append("</td>");
                
                if ("Attended".equalsIgnoreCase(guest.getStatus())) {
                    html.append("  <td><span class='status-badge status-attended'>").append(guest.getStatus()).append("</span></td>");
                } else {
                    html.append("  <td><span class='status-badge status-registered'>").append(guest.getStatus()).append("</span></td>");
                }
                html.append("</tr>");
            }
            
            out.print(html.toString());
            
        } catch (Exception e) {
            System.out.println("Error fetching guest list API: " + e.getMessage());
        }
    }
}