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

@WebServlet("/GetParticipantListAPI")
public class GetParticipantListAPI extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        
        try (PrintWriter out = response.getWriter()) {
            
       
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("activeEventId") == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("<tr><td colspan='3' style='text-align: center; padding: 24px; color: red; font-size: 14px;'>Unauthorized session.</td></tr>");
                return;
            }
            int eventId = (int) session.getAttribute("activeEventId");
            
            EventDatabaseActions db = new EventDatabaseActions();
            List<EventMember> participantList = db.getParticipants(eventId);
            
            StringBuilder html = new StringBuilder();
            
            if (participantList.isEmpty()) {
                html.append("<tr><td colspan='3' style='text-align: center; padding: 24px; color: var(--text-muted); font-size: 14px;'>No participants found.</td></tr>");
            } else {
                for (EventMember user : participantList) {
                    html.append("<tr style='border-bottom: 1px solid var(--border-light);'>");
                    html.append("  <td style='padding: 16px 24px;'>")
                        .append("<div style='font-size: 14px; font-weight: 600;'>").append(user.getFullName()).append("</div>")
                        .append("<div style='font-size: 12px; color: var(--text-muted);'>").append(user.getEmailAddress()).append("</div>")
                        .append("</td>");
                    html.append("  <td style='padding: 16px 24px;'><span style='background: #E0E7FF; color: #4F46E5; padding: 4px 8px; border-radius: 4px; font-size: 11px; font-weight: bold;'>").append(user.getRole().toUpperCase()).append("</span></td>");
                    html.append("  <td style='padding: 16px 24px; text-align: right;'>");
                    html.append("    <button class='btn-danger' style='padding: 6px 12px; font-size: 12px;' onclick='removeParticipant(\"").append(user.getEmailAddress()).append("\", \"").append(user.getFullName()).append("\")'>Remove</button>");
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