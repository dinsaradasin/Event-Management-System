package com.eventsystem.servlets;

import com.eventsystem.dao.EventDatabaseActions;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession; 

@WebServlet("/DeleteAnnouncementAPI")
public class DeleteAnnouncementAPI extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
          
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("loggedUserId") == null) {
                response.getWriter().write("error");
                return;
            }

            int announcementId = Integer.parseInt(request.getParameter("announcementId"));
            
            EventDatabaseActions db = new EventDatabaseActions();
            db.deleteAnnouncement(announcementId);
            
       
            response.getWriter().write("success");
            
        } catch (Exception e) {
            System.out.println("Delete Error: " + e.getMessage());
            response.getWriter().write("error");
        }
    }
}