package com.eventsystem.servlets;

import com.eventsystem.dao.EventDatabaseActions;
import com.eventsystem.models.PreferenceResult;
import com.eventsystem.models.PreferenceResult;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession; 

@WebServlet("/GetPreferenceStatsAPI")
public class GetPreferenceStatsAPI extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        try (PrintWriter out = response.getWriter()) {
            
         
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("activeEventId") == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("<div class='empty-state'>Unauthorized session.</div>");
                return;
            }
            int eventId = (int) session.getAttribute("activeEventId");
            
            EventDatabaseActions db = new EventDatabaseActions();
            List<PreferenceResult> stats = db.getPreferenceStatistics(eventId);
            
            if (stats.isEmpty()) {
                out.print("<div class='empty-state'>No logistics data to analyze yet. Add preference fields first.</div>");
                return;
            }
            
            StringBuilder html = new StringBuilder();
            String currentLabel = "";
            
            for (PreferenceResult stat : stats) {
            
                if (!stat.getFieldLabel().equals(currentLabel)) {
                    if (!currentLabel.isEmpty()) {
                        html.append("    </tbody></table></div>"); 
                    }
                    currentLabel = stat.getFieldLabel();
                    
                    html.append("<div style='margin-bottom: 24px;'>");
                
                    html.append("  <table class='stats-table'>");
                    
                
                    html.append("    <thead>");
                    html.append("      <tr class='stats-header-row'>");
                    html.append("        <td>").append(currentLabel).append("</td>");
                    html.append("        <td style='text-align: right;'>QUANTITY</td>");
                    html.append("      </tr>");
                    html.append("    </thead>");
                    html.append("    <tbody>");
                }
                
           
                html.append("      <tr class='stats-row'>");
                html.append("        <td>").append(stat.getAnswerText()).append("</td>");
                html.append("        <td>").append(stat.getVoteCount()).append("</td>");
                html.append("      </tr>");
            }
            
       
            if (!currentLabel.isEmpty()) {
                html.append("    </tbody></table></div>");
            }
            
            out.print(html.toString());
            
        } catch (Exception e) {
            System.out.println("Error generating stats: " + e.getMessage());
            response.getWriter().print("<div class='empty-state'>Error loading logistics analytics.</div>");
        }
    }
}