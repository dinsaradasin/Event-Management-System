package com.eventsystem.servlets;

import com.eventsystem.dao.EventDatabaseActions;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession; 

@WebServlet("/CheckPreferenceLockAPI")
public class CheckPreferenceLockAPI extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json"); 
        try (PrintWriter out = response.getWriter()) {
            
       
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("activeEventId") == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("{\"locked\": false, \"error\": \"Unauthorized\"}");
                return;
            }
            int eventId = (int) session.getAttribute("activeEventId");
            
            EventDatabaseActions db = new EventDatabaseActions();
            boolean isLocked = db.isPreferenceLocked(eventId);
            
       
            out.print("{\"locked\": " + isLocked + "}");
            
        } catch (Exception e) {
            System.out.println("Error checking lock status: " + e.getMessage());
        }
    }
}