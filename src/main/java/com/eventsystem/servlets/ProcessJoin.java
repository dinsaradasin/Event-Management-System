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

@WebServlet("/ProcessJoin")
public class ProcessJoin extends HttpServlet {

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
            
          
            String typedCode = request.getParameter("event_code");
            
          
            EventDatabaseActions database = new EventDatabaseActions();
            boolean success = database.joinEventWithCode(loggedInUser.getUserId(), typedCode);
            
         
            if (success) {
                response.sendRedirect("dashboard.html?join=success");
            } else {
                response.sendRedirect("dashboard.html?join=failed");
            }
            
        } catch (Exception e) {
            System.out.println("Error processing event join: " + e.getMessage());
            response.sendRedirect("dashboard.html?join=error");
        }
    }
}