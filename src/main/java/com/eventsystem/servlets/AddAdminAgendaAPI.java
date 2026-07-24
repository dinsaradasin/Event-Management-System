package com.eventsystem.servlets;

import com.eventsystem.dao.EventDatabaseActions;
import com.eventsystem.models.AgendaItem; 
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession; 

@WebServlet("/AddAdminAgendaAPI")
public class AddAdminAgendaAPI extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
         
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("activeEventId") == null) {
                response.sendRedirect("dashboard.html");
                return;
            }
            int eventId = (int) session.getAttribute("activeEventId");

            String title = request.getParameter("title");
            String agendaDate = request.getParameter("agenda_date");
            String startTime = request.getParameter("start_time");
            String endTime = request.getParameter("end_time");
            
            if (title == null || agendaDate == null || startTime == null || endTime == null) {
                throw new Exception("Missing form data");
            }
            
            String combinedStart = agendaDate + "T" + startTime;
            String combinedEnd = agendaDate + "T" + endTime;
            
         
            AgendaItem newItem = new AgendaItem(0, title, agendaDate, combinedStart, combinedEnd);
            
            EventDatabaseActions db = new EventDatabaseActions();
            
            boolean success = db.insertAgendaItem(
                eventId, 
                newItem.getTitle(),       
                newItem.getStartTime(),   
                newItem.getEndTime()     
            );
            
        
            response.sendRedirect("admin_dashboard.html?tab=timeline&lastDate=" + agendaDate + "&lastStart=" + startTime + "&lastEnd=" + endTime);
            
        } catch (Exception e) {
            System.out.println("DEBUG: Caught exception in Servlet: " + e.getMessage());
            e.printStackTrace(); 
            response.sendRedirect("dashboard.html"); 
        }
    }
}