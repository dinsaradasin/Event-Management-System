package com.eventsystem.servlets;

import com.eventsystem.models.User;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/SessionAPI")
public class SessionAPI extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/plain;charset=UTF-8");
        
        try (PrintWriter out = response.getWriter()) {
            
        
            HttpSession session = request.getSession(false);
            User loggedInUser = (session != null) ? (User) session.getAttribute("activeUser") : null;
            
            if (loggedInUser != null) {
              
                out.print(loggedInUser.getFullName());
            } else {
           
                out.print("NO_USER");
            }
            
        } catch (Exception e) {
            System.out.println("Error in SessionAPI: " + e.getMessage());
            response.getWriter().print("NO_USER");
        }
    }
}