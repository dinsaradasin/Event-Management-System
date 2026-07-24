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

@WebServlet("/GetAdminAnalyticsAPI")
public class GetAdminAnalyticsAPI extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

      
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
          
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("activeEventId") == null) {
                out.print("{\"capacity\": 0, \"registered\": 0, \"live\": 0}");
                return;
            }
            int eventId = (int) session.getAttribute("activeEventId");
            
          
            EventDatabaseActions db = new EventDatabaseActions();
            int[] stats = db.getEventAnalytics(eventId);

        
            String json = "{\"capacity\": " + stats[0] + ", \"registered\": " + stats[1] + ", \"live\": " + stats[2] + "}";
            
            
            out.print(json);

        } catch (Exception e) {
         
            out.print("{\"capacity\": 0, \"registered\": 0, \"live\": 0}");
        }
    }
}