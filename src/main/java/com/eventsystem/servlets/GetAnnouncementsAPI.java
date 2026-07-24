package com.eventsystem.servlets;

import com.eventsystem.dao.EventDatabaseActions;
import com.eventsystem.models.Announcement;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession; 

@WebServlet("/GetAnnouncementsAPI")
public class GetAnnouncementsAPI extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        
        try (PrintWriter out = response.getWriter()) {
            
          
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("activeEventId") == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("<div style='color: red; font-size: 14px; text-align: center; padding: 16px;'>Unauthorized session.</div>");
                return;
            }
            int eventId = (int) session.getAttribute("activeEventId");
            
          
            String role = request.getParameter("role"); 
            
            EventDatabaseActions db = new EventDatabaseActions();
            List<Announcement> announcements = db.getAnnouncements(eventId);
            
            StringBuilder html = new StringBuilder();
            
            if (announcements.isEmpty()) {
                html.append("<div style='color: var(--text-muted); font-size: 14px; text-align: center; padding: 16px;'>No announcements sent yet.</div>");
            } else {
                for (Announcement ann : announcements) {
                    html.append("<div style='padding: 12px 16px; border-bottom: 1px solid var(--border-light); background: #F8FAFC;'>");
                    
                 
                    html.append("  <div style='display: flex; justify-content: space-between; align-items: start; margin-bottom: 4px;'>");
                    html.append("    <div style='font-size: 11px; font-weight: bold; color: var(--brand-primary);'>").append(ann.getTimeSent()).append("</div>");
                    
                 
                    if ("admin".equals(role)) {
                        html.append("    <button onclick='deleteBroadcast(").append(ann.getAnnouncementId()).append(")' style='background: none; border: none; color: #EF4444; cursor: pointer; font-size: 12px; font-weight: bold;'>Delete</button>");
                    }
                    
                    html.append("  </div>");
                    html.append("  <div style='font-size: 14px; color: var(--text-main);'>").append(ann.getMessage()).append("</div>");
                    html.append("</div>");
                }
            }
            out.print(html.toString());
            
        } catch (Exception e) {
            System.out.println("Servlet Error: " + e.getMessage());
        }
    }
}