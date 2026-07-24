package com.eventsystem.servlets;

import com.eventsystem.dao.EventDatabaseActions;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession; 

@WebServlet("/DeleteAgendaItemAPI")
public class DeleteAgendaItemAPI extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
          
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("activeEventId") == null) {
                response.sendRedirect("dashboard.html");
                return;
            }
            int eventId = (int) session.getAttribute("activeEventId");
            
         
            int agendaId = Integer.parseInt(request.getParameter("agendaId"));
            
            EventDatabaseActions db = new EventDatabaseActions();
            db.deleteAgendaItem(agendaId);
            
        
            response.sendRedirect("admin_dashboard.html?tab=timeline");
            
        } catch (Exception e) {
            System.out.println("Error deleting agenda item: " + e.getMessage());
            response.sendRedirect("dashboard.html");
        }
    }
}