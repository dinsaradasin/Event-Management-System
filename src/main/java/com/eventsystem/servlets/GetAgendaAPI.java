package com.eventsystem.servlets;

import com.eventsystem.dao.EventDatabaseActions;
import com.eventsystem.models.AgendaItem;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession; 

@WebServlet("/GetAgendaAPI")
public class GetAgendaAPI extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        
        try (PrintWriter out = response.getWriter()) {
            
         
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("activeEventId") == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("<div style='text-align: center; padding: 32px; color: red; font-size: 14px;'>Unauthorized session.</div>");
                return;
            }
            int eventId = (int) session.getAttribute("activeEventId");
            
         
            String role = request.getParameter("role"); 
            
          
            EventDatabaseActions db = new EventDatabaseActions();
            List<AgendaItem> agendaList = db.getAgendaItems(eventId);
            
        
            StringBuilder html = new StringBuilder();
            
            if (agendaList.isEmpty()) {
                html.append("<div style='text-align: center; padding: 32px; color: var(--text-muted); font-size: 14px;'>No agenda items scheduled yet.</div>");
            } else {
                String currentDate = "";
                
                for (AgendaItem item : agendaList) {
                
                    if (!item.getDate().equals(currentDate)) { 
                        html.append("<div style='background: #F1F5F9; padding: 12px 24px; font-weight: 600; color: #334155; font-size: 14px; border-bottom: 2px solid var(--border-light); text-transform: uppercase; letter-spacing: 1px;'>");
                        html.append(item.getDate());
                        html.append("</div>");
                        currentDate = item.getDate();
                    }
                    
                   
                    html.append("<div style='display: flex; padding: 20px 24px; border-bottom: 1px solid var(--border-light); align-items: center;'>");
                    
                   
                    html.append("  <div style='width: 150px; flex-shrink: 0;'>");
                    html.append("    <div style='font-weight: 700; color: var(--brand-primary); font-size: 15px;'>").append(item.getStartTime()).append("</div>");
                    html.append("    <div style='font-size: 12px; color: var(--text-muted); margin-top: 4px;'>to ").append(item.getEndTime()).append("</div>");
                    html.append("  </div>");
                    
                  
                    html.append("  <div style='flex-grow: 1; padding-left: 24px; border-left: 2px solid var(--border-light); margin-left: 16px;'>");
                    html.append("    <div style='font-weight: 600; font-size: 16px; color: var(--text-main);'>").append(item.getTitle()).append("</div>");
                    html.append("  </div>");
                    
                 
                    if (role == null || !role.equals("guest")) {
                        html.append("  <div style='margin-left: 16px;'>");
                        html.append("    <button class='btn-danger' style='padding: 6px 12px; font-size: 12px; background: #EF4444; color: white; border: none; border-radius: 6px; cursor: pointer;' onclick='deleteAgendaItem(").append(item.getAgendaId()).append(")'>Remove</button>");
                        html.append("  </div>");
                    }
                    
                    html.append("</div>");
                }
            }
            
           
            out.print(html.toString());
            
        } catch (Exception e) {
            System.out.println("Servlet Error: " + e.getMessage());
        }
    }
}