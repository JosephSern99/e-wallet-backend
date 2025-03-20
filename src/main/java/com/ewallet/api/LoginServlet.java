package com.ewallet.api;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        if (login(username, password)) {
            // Log successful login
            System.out.println("Login successful for user: " + username);
            // Redirect to index.html if login is successful
            resp.sendRedirect(req.getContextPath() + "/index.html");
        } else {
            // Log login failure
            System.out.println("Login failed for user: " + username);
            // Handle login failure
            req.setAttribute("error", "Invalid username or password");
            req.getRequestDispatcher("login.jsp").forward(req, resp);
        }
    }

    private boolean login(String username, String password) {
        // Implement your login logic here
        // For example, you can check the username and password against a database
        // Return true if the credentials are valid, otherwise return false
        return "user".equals(username) && "password".equals(password);
    }
}