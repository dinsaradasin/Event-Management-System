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

@WebServlet("/GetEventCodeAPI")
public class GetEventCodeAPI extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
       
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();

        try {
       
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("activeEventId") == null) {
                out.print("ERROR");
                return;
            }
            int eventId = (int) session.getAttribute("activeEventId");
            
         
            EventDatabaseActions db = new EventDatabaseActions();
            String eventCode = db.getEventCode(eventId);
            
          
            out.print(eventCode);
            
        } catch (Exception e) {
            out.print("ERROR");
        }
    }
}