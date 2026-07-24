package com.eventsystem.servlets;

import com.eventsystem.dao.EventDatabaseActions;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession; 

@WebServlet("/AddAdminPreferenceFieldAPI")
public class AddAdminPreferenceFieldAPI extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
         
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("activeEventId") == null) {
                response.sendRedirect("dashboard.html");
                return;
            }
            int eventId = (int) session.getAttribute("activeEventId");

            String label = request.getParameter("field_label");
            String type = request.getParameter("field_type");
            String options = request.getParameter("dropdown_options");

            EventDatabaseActions db = new EventDatabaseActions();
            db.insertPreferenceField(eventId, label, type, options);
            
         
            response.sendRedirect("admin_dashboard.html?tab=logistics");
            
        } catch (Exception e) {
            response.sendRedirect("dashboard.html");
        }
    }
}