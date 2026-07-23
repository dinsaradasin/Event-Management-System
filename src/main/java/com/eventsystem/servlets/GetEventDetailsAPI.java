package com.eventsystem.servlets;

import com.eventsystem.dao.EventDatabaseActions;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession; 

@WebServlet("/GetEventDetailsAPI")
public class GetEventDetailsAPI extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
    
        response.setContentType("application/json"); 
        
        try (PrintWriter out = response.getWriter()) {
            
       
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("activeEventId") == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("{\"error\": \"Unauthorized session\"}");
                return;
            }
            int eventId = (int) session.getAttribute("activeEventId");
            
            EventDatabaseActions db = new EventDatabaseActions();
            String[] details = db.getEventDetails(eventId);
            
      
            out.print("{");
            out.print("\"date\": \"" + (details != null && details[0] != null ? details[0] : "TBA") + "\", ");
            out.print("\"time\": \"" + (details != null && details[1] != null ? details[1] : "TBA") + "\", ");
            out.print("\"location\": \"" + (details != null && details[2] != null ? details[2] : "TBA") + "\"");
            out.print("}");
            
        } catch (Exception e) {
            System.out.println("Error in GetEventDetailsAPI: " + e.getMessage());
            response.getWriter().print("{\"error\": \"System error\"}");
        }
    }
}