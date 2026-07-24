package com.eventsystem.servlets;

import com.eventsystem.dao.EventDatabaseActions;
import com.eventsystem.models.EventMember;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession; 

@WebServlet("/GetRecentRegistrationsAPI")
public class GetRecentRegistrationsAPI extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        
        try (PrintWriter out = response.getWriter()) {
            
        
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("activeEventId") == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("<tr><td colspan='3' style='text-align: center; color: red;'>Unauthorized session.</td></tr>");
                return;
            }
            int eventId = (int) session.getAttribute("activeEventId");
            
            EventDatabaseActions db = new EventDatabaseActions();
            List<EventMember> recentList = db.getRecentRegistrations(eventId);
            
            StringBuilder html = new StringBuilder();
            
            if (recentList.isEmpty()) {
                html.append("<tr><td colspan='3' class='empty-state'>No registrations found yet.</td></tr>");
            } else {
                for (EventMember user : recentList) {
                    String role = user.getRole();
                    String badgeStyle = "background: #E0E7FF; color: #4F46E5;"; 
                    if (role.equalsIgnoreCase("Organizer")) badgeStyle = "background: #FEF3C7; color: #92400E;"; 
                    else if (role.equalsIgnoreCase("Admin")) badgeStyle = "background: #FEE2E2; color: #991B1B;"; 
                    
                    html.append("<tr style='border-bottom: 1px solid var(--border-light);'>");
                    html.append("  <td style='padding: 16px 24px; font-size: 14px; font-weight: 600;'>").append(user.getFullName()).append("</td>");
                    html.append("  <td style='padding: 16px 24px;'><span style='").append(badgeStyle).append(" padding: 4px 8px; border-radius: 4px; font-size: 11px; font-weight: bold;'>").append(role.toUpperCase()).append("</span></td>");
                    html.append("  <td style='padding: 16px 24px; text-align: right; color: var(--success-text); font-size: 14px; font-weight: 600;'>Registered</td>");
                    html.append("</tr>");
                }
            }
            out.print(html.toString());
            
        } catch (Exception e) {
            System.out.println("Servlet Error: " + e.getMessage());
        }
    }
}