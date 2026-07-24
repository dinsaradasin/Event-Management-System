package com.eventsystem.servlets;

import com.eventsystem.dao.EventDatabaseActions;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/TogglePreferenceLockAPI")
public class TogglePreferenceLockAPI extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
       
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("activeEventId") == null) {
                response.sendRedirect("dashboard.html");
                return;
            }
            int eventId = (int) session.getAttribute("activeEventId");
            
            boolean lockStatus = Boolean.parseBoolean(request.getParameter("lock_status"));
            
            EventDatabaseActions db = new EventDatabaseActions();
            db.togglePreferenceLock(eventId, lockStatus);
            
        
            response.sendRedirect("admin_dashboard.html?tab=logistics");
            
        } catch (Exception e) {
            System.out.println("Error toggling preference lock: " + e.getMessage());
            response.sendRedirect("dashboard.html");
        }
    }
}