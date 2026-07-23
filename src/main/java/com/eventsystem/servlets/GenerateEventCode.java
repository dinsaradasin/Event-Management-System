package com.eventsystem.servlets;

import com.eventsystem.dao.EventDatabaseActions;
import com.eventsystem.models.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/GenerateEventCode")
public class GenerateEventCode extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
       
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("activeUser") == null) {
                response.sendRedirect("index.html");
                return;
            }
            
            User loggedInUser = (User) session.getAttribute("activeUser");
            
          
            String title = request.getParameter("event_title");
            String date = request.getParameter("event_date");
            String time = request.getParameter("event_time");
            String location = request.getParameter("event_location");
            int capacity = Integer.parseInt(request.getParameter("capacity"));
            
          
            String eventDateTime = date + " " + time + ":00";
            
            // 3. Trigger Database Logic
            EventDatabaseActions database = new EventDatabaseActions();
            String generatedCode = database.createNewEvent(title, eventDateTime, location, capacity, loggedInUser.getUserId());
            
       
            if (generatedCode != null) {
           
                response.sendRedirect("create_event.html?code=" + generatedCode);
            } else {
                response.sendRedirect("create_event.html?error=failed");
            }
            
        } catch (Exception e) {
            System.out.println("Error generating event code: " + e.getMessage());
            response.sendRedirect("create_event.html?error=system");
        }
    }
}