package com.eventsystem.servlets;

import com.eventsystem.dao.EventDatabaseActions;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession; 

@WebServlet("/DeletePreferenceFieldAPI")
public class DeletePreferenceFieldAPI extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        try {
          
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("activeEventId") == null) {
                response.sendRedirect("dashboard.html");
                return;
            }
            
         
            int fieldId = Integer.parseInt(request.getParameter("fieldId"));
            
         
            EventDatabaseActions db = new EventDatabaseActions();
            db.deletePreferenceField(fieldId);
            
       
            response.sendRedirect("admin_dashboard.html?tab=logistics");
            
        } catch (Exception e) {
            System.out.println("Error deleting preference field: " + e.getMessage());
            response.sendRedirect("dashboard.html");
        }
    }
}