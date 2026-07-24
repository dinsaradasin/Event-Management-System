package com.eventsystem.servlets;

import com.eventsystem.dao.EventDatabaseActions;
import java.io.IOException;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession; 

@WebServlet("/SaveUserPreferencesAPI")
public class SaveUserPreferencesAPI extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
        
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("activeEventId") == null || session.getAttribute("loggedUserId") == null) {
                response.sendRedirect("dashboard.html");
                return;
            }
            
            EventDatabaseActions db = new EventDatabaseActions();
            Enumeration<String> paramNames = request.getParameterNames();
            
            while (paramNames.hasMoreElements()) {
                String paramName = paramNames.nextElement();
                
            
                if (paramName.startsWith("tkt_") && paramName.contains("_fld_")) {
              
                    String cleanString = paramName.replace("tkt_", ""); 
                    String[] parts = cleanString.split("_fld_"); 
                    
                    int ticketId = Integer.parseInt(parts[0]);
                    int fieldId = Integer.parseInt(parts[1]);
                    String answerValue = request.getParameter(paramName);
                    
                    if (answerValue != null && !answerValue.trim().isEmpty()) {
                        db.saveTicketPreferenceAnswer(ticketId, fieldId, answerValue.trim());
                    }
                }
            }
            
         
            response.sendRedirect("participant_dashboard.html?tab=preferences&message=Saved");
            
        } catch (Exception e) {
            System.out.println("Error saving preferences: " + e.getMessage());
        
            response.sendRedirect("participant_dashboard.html?tab=preferences&error=SystemError");
        }
    }
}