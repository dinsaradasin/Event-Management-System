package com.eventsystem.servlets;

import com.eventsystem.dao.EventDatabaseActions;
import com.eventsystem.models.Ticket;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession; 

@WebServlet("/GetMyTicketsAPI")
public class GetMyTicketsAPI extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        try (PrintWriter out = response.getWriter()) {
            
         
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("loggedUserId") == null || session.getAttribute("activeEventId") == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("<div style='color: red; padding: 16px;'>Unauthorized session.</div>");
                return;
            }
            
            int userId = (int) session.getAttribute("loggedUserId");
            int eventId = (int) session.getAttribute("activeEventId");

            EventDatabaseActions db = new EventDatabaseActions();
            
        
            List<Ticket> myTickets = db.getMyTickets(userId, eventId);

            if (!myTickets.isEmpty()) {
           
                for (Ticket ticket : myTickets) {
                    String tier = ticket.getTicketTier();
                    String token = ticket.getSecureToken();
                    
                    out.print("<div style='background: linear-gradient(135deg, var(--brand-primary), #312E81); color: white; border-radius: 12px; padding: 24px; display: flex; justify-content: space-between; align-items: center; box-shadow: 0 10px 15px -3px rgba(0,0,0,0.1); width: 100%; margin-bottom: 16px;'>" +
                        "<div>" +
                            "<div style='background: rgba(255,255,255,0.2); padding: 4px 12px; border-radius: 99px; font-size: 12px; font-weight: bold; text-transform: uppercase; display: inline-block; margin-bottom: 12px;'>" + tier + " Pass</div>" +
                            "<h4 style='font-size: 22px; font-weight: 700; margin-bottom: 4px;'>Event Registration</h4>" +
                            "<p style='color: #cbd5e1; font-size: 14px; margin-bottom: 16px;'>Status: <span style='color: #4ade80; font-weight: bold;'>Confirmed & Paid</span></p>" +
                            "<div style='font-family: monospace; font-size: 18px; letter-spacing: 2px;'>" + token + "</div>" +
                        "</div>" +
                        "<div style='background: white; width: 90px; height: 90px; border-radius: 8px; display: flex; align-items: center; justify-content: center; flex-direction: column; color: var(--text-muted);'>" +
                            "<svg width='40' height='40' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'><rect x='3' y='3' width='18' height='18' rx='2' ry='2'></rect><rect x='7' y='7' width='3' height='3'></rect><rect x='14' y='7' width='3' height='3'></rect><rect x='7' y='14' width='3' height='3'></rect><rect x='14' y='14' width='3' height='3'></rect></svg>" +
                            "<span style='font-size: 9px; margin-top: 4px; font-weight: bold;'>ADMIT 1</span>" +
                        "</div>" +
                    "</div>");
                }
            }
        } catch (Exception e) {
            System.out.println("Error fetching user tickets: " + e.getMessage());
        }
    }
}