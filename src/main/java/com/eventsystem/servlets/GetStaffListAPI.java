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

@WebServlet("/GetStaffListAPI")
public class GetStaffListAPI extends HttpServlet {

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
            List<EventMember> staffList = db.getStaffMembers(eventId);
            
            StringBuilder html = new StringBuilder();
            
            if (staffList.isEmpty()) {
                html.append("<tr><td colspan='3' style='text-align: center; padding: 24px; color: var(--text-muted); font-size: 14px;'>No staff members found.</td></tr>");
            } else {
                for (EventMember staff : staffList) {
                    String role = staff.getRole();
                    String badgeStyle = "background: #FEF3C7; color: #92400E;"; 
                    if (role.equalsIgnoreCase("Admin")) badgeStyle = "background: #FEE2E2; color: #991B1B;"; 
                    
                    html.append("<tr style='border-bottom: 1px solid var(--border-light);'>");
                    html.append("  <td style='padding: 16px 24px;'>")
                        .append("<div style='font-size: 14px; font-weight: 600;'>").append(staff.getFullName()).append("</div>")
                        .append("<div style='font-size: 12px; color: var(--text-muted);'>").append(staff.getEmailAddress()).append("</div>")
                        .append("</td>");
                    html.append("  <td style='padding: 16px 24px;'><span style='").append(badgeStyle).append(" padding: 4px 8px; border-radius: 4px; font-size: 11px; font-weight: bold;'>").append(role.toUpperCase()).append("</span></td>");
                    
                    if (role.equalsIgnoreCase("Admin")) {
                        html.append("  <td style='padding: 16px 24px; text-align: right; color: var(--text-muted); font-size: 12px; font-weight: bold;'>Event Owner</td>");
                    } else {
                        html.append("  <td style='padding: 16px 24px; text-align: right;'>");
                        html.append("    <button class='btn-danger' style='padding: 6px 12px; font-size: 12px;' onclick='demoteOrganizer(\"").append(staff.getEmailAddress()).append("\", \"").append(staff.getFullName()).append("\")'>Remove</button>");
                        html.append("  </td>");
                    }
                    html.append("</tr>");
                }
            }
            out.print(html.toString());
            
        } catch (Exception e) {
            System.out.println("Servlet Error: " + e.getMessage());
        }
    }
}