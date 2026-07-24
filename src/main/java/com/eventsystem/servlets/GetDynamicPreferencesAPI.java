package com.eventsystem.servlets;

import com.eventsystem.dao.EventDatabaseActions;
import com.eventsystem.models.EventPreferenceField;
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

@WebServlet("/GetDynamicPreferencesAPI")
public class GetDynamicPreferencesAPI extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        try (PrintWriter out = response.getWriter()) {
            
         
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("activeEventId") == null || session.getAttribute("loggedUserId") == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("<p style='color: #B91C1C; font-size: 14px; padding: 16px; background: #FEF2F2; border-radius: 8px;'>Error: Unauthorized session. Please return to the main dashboard.</p>");
                return;
            }
           
            

            int eventId = (int) session.getAttribute("activeEventId");
            int userId = (int) session.getAttribute("loggedUserId");
            
            EventDatabaseActions db = new EventDatabaseActions();
            List<EventPreferenceField> fields = db.getPreferenceFields(eventId);
            List<Ticket> userTickets = db.getMyTickets(userId, eventId); 
            
            if (fields.isEmpty()) {
                out.print("<p style='color: var(--text-muted); font-size: 14px;'>No logistics preferences required for this event.</p>");
                return;
            }
            if (userTickets.isEmpty()) {
                out.print("<p style='color: #B91C1C; font-size: 14px; padding: 16px; background: #FEF2F2; border-radius: 8px;'>You must purchase a ticket before you can fill out preferences.</p>");
                return;
            }
            
            StringBuilder html = new StringBuilder();
            
           
            boolean isLocked = db.isPreferenceLocked(eventId);
            if (isLocked) {
                html.append("<div style='background: #FFFBEB; border-left: 4px solid #F59E0B; padding: 16px; margin-bottom: 24px; border-radius: 4px; color: #92400E;'>");
                html.append("<strong>Preferences Locked</strong><br>The event organizer has finalized logistics. Your selections are secured and cannot be changed.");
                html.append("</div>");
             
                html.append("<style>#preferences-form button[type='submit'] { display: none !important; }</style>");
            }
            
            for (Ticket ticket : userTickets) {
                html.append("<div style='background: #F8FAFC; padding: 20px; border: 1px solid var(--border-light); border-radius: 8px; margin-bottom: 24px;'>");
                html.append("  <h4 style='margin-bottom: 16px; color: var(--brand-primary);'>Pass: ").append(ticket.getSecureToken()).append(" (").append(ticket.getTicketTier()).append(")</h4>");
                
                for (EventPreferenceField field : fields) {
               
                    String savedAnswer = db.getTicketPreferenceAnswer(ticket.getTicketId(), field.getFieldId());
                    
                    html.append("  <div class='form-group' style='margin-bottom: 16px;'>");
                    html.append("    <label class='form-label'>").append(field.getFieldLabel()).append("</label>");
                    
               
                    html.append("    <select name='tkt_").append(ticket.getTicketId()).append("_fld_").append(field.getFieldId()).append("' class='form-input' ");
                    if (isLocked) {
                        html.append("disabled ");
                    }
                    html.append("required>");
                    
                    if (field.getDropdownOptions() != null && !field.getDropdownOptions().isEmpty()) {
                        String[] options = field.getDropdownOptions().split(",");
                        for (String opt : options) {
                            String cleanOption = opt.trim();
                            
                     
                            if (cleanOption.equals(savedAnswer)) {
                                html.append("      <option value='").append(cleanOption).append("' selected>").append(cleanOption).append("</option>");
                            } else {
                                html.append("      <option value='").append(cleanOption).append("'>").append(cleanOption).append("</option>");
                            }
                        }
                    } else {
                        html.append("      <option value='N/A'>N/A</option>");
                    }
                    html.append("    </select>");
                    html.append("  </div>");
                }
                html.append("</div>");
            }
            out.print(html.toString());
            
        } catch (Exception e) {
            System.out.println("Error generating dynamic preferences: " + e.getMessage());
            response.getWriter().print("<p style='color: red;'>A system error occurred while loading preferences. Please try again.</p>");
        }
    }
}