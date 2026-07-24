package com.eventsystem.servlets;

import com.eventsystem.dao.EventDatabaseActions;
import com.eventsystem.models.Ticket;
import com.eventsystem.models.PaymentDetails; 
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession; 

@WebServlet("/PurchaseTicketAPI")
public class PurchaseTicketAPI extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
         
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("loggedUserId") == null || session.getAttribute("activeEventId") == null) {
                response.sendRedirect("dashboard.html");
                return;
            }
            
            int userId = (int) session.getAttribute("loggedUserId");
            int eventId = (int) session.getAttribute("activeEventId");
            
            String tierName = request.getParameter("tierName");
            
       
            String cardName = request.getParameter("demoName");
            String cardNumber = request.getParameter("demoNum");
            String expiryDate = request.getParameter("demoExp");
            String cvc = request.getParameter("demoCvc");
            
            PaymentDetails payment = new PaymentDetails(cardName, cardNumber, expiryDate, cvc);
            
            if (!payment.isValid()) {
        
                response.sendRedirect("participant_dashboard.html?tab=tickets&error=InvalidPayment");
                return; 
            }
            
            Ticket newTicket = new Ticket(0, eventId, tierName, "", "Pending");
            EventDatabaseActions db = new EventDatabaseActions();
            
            boolean success = db.purchaseTicket(
                userId, 
                newTicket.getEventId(),     
                newTicket.getTicketTier(),     
                "None",  
                "None"   
            );
            
            if (success) {
          
                response.sendRedirect("participant_dashboard.html?tab=tickets&message=TicketPurchased");
            } else {
                response.sendRedirect("participant_dashboard.html?tab=tickets&error=PurchaseFailed");
            }
            
        } catch (Exception e) {
            System.out.println("DEBUG: Purchase Error: " + e.getMessage());
            response.sendRedirect("participant_dashboard.html?tab=tickets&error=SystemError");
        }
    }
}