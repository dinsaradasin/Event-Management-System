package com.eventsystem.servlets;

import com.eventsystem.dao.EventDatabaseActions;
import com.eventsystem.models.Event;
import com.eventsystem.models.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/UserEventsAPI")
public class UserEventsAPI extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        
        try (PrintWriter out = response.getWriter()) {
            
        
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("activeUser") == null) {
                out.print("<div class='empty-state'>Session expired. Please log in again.</div>");
                return;
            }
            
            User loggedInUser = (User) session.getAttribute("activeUser");

            EventDatabaseActions db = new EventDatabaseActions();
            List<Event> myEvents = db.getEventsForUser(loggedInUser.getUserId());

            if (myEvents.isEmpty()) {
                out.print("<div class='empty-state'>You haven't joined any events yet. Enter a code above to get started!</div>");
            } else {
               
                for (Event event : myEvents) {
                    String userRole = event.getCurrentUserRole();
                    String badgeStyle = "background-color: #E0E7FF; color: var(--primary);"; 
                    
                    if (userRole.equals("Admin")) {
                        badgeStyle = "background-color: #FEE2E2; color: #991B1B;"; 
                    } else if (userRole.equals("Organizer")) {
                        badgeStyle = "background-color: #FEF3C7; color: #92400E;"; 
                    }

                 
                    String targetUrl = "SelectEvent?eventId=" + event.getEventId() + "&role=" + userRole;

              
                    out.print("<a href='" + targetUrl + "' style='text-decoration: none; display: block;'>");
                    out.print("<div class='sample-event' style='transition: background 0.2s; padding: 16px; border-radius: 8px;'>");
                    out.print("  <div>");
                    out.print("    <h4 style='color: var(--text-main); margin-bottom: 4px;'>" + event.getEventTitle() + "</h4>");
                    out.print("    <p style='font-size: 13px; color: var(--text-muted); margin: 0;'>Location: " + event.getLocation() + "</p>");
                    out.print("  </div>");
                    
            
                    out.print("  <span style='" + badgeStyle + " padding: 6px 12px; border-radius: 6px; font-size: 12px; font-weight: 700; letter-spacing: 0.5px; text-transform: uppercase;'>" + userRole + "</span>");
                    
                    out.print("</div>");
                    out.print("</a>");
                }
            }
        } catch (Exception e) {
            System.out.println("Error in UserEventsAPI: " + e.getMessage());
        }
    }
}