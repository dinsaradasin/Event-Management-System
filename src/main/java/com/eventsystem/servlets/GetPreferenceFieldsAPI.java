package com.eventsystem.servlets;

import com.eventsystem.dao.EventDatabaseActions;
import com.eventsystem.models.EventPreferenceField;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/GetPreferenceFieldsAPI")
public class GetPreferenceFieldsAPI extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        try (PrintWriter out = response.getWriter()) {
            
        
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("activeEventId") == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("<tr><td colspan='4' class='empty-state' style='color:red;'>Unauthorized session.</td></tr>");
                return;
            }
            int eventId = (int) session.getAttribute("activeEventId");
            
            EventDatabaseActions db = new EventDatabaseActions();
            List<EventPreferenceField> fields = db.getPreferenceFields(eventId);
            
            if (fields.isEmpty()) {
                out.print("<tr><td colspan='4' class='empty-state'>No custom fields created.</td></tr>");
            } else {
                for (EventPreferenceField field : fields) {
                    out.print("<tr>");
                    out.print("<td>" + field.getFieldLabel() + "</td>");
                    out.print("<td>" + field.getFieldType() + "</td>");
                    out.print("<td>" + (field.getDropdownOptions() != null ? field.getDropdownOptions() : "-") + "</td>");
                    out.print("<td style='text-align: right;'><button class='btn-danger' onclick='deletePreferenceField(" + field.getFieldId() + ")'>Delete</button></td>");
                    out.print("</tr>");
                }
            }
        } catch (Exception e) {
            System.out.println("Error fetching preference fields: " + e.getMessage());
        }
    }
}