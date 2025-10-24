package com.example.financial.api;

import com.example.financial.DatabaseUtil;
import com.example.financial.SecurityUtil;
import org.json.JSONObject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;

@WebServlet("/api/register")
public class RegisterServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        JSONObject jsonResponse = new JSONObject();

        try {
            String name = req.getParameter("name");
            String phone = req.getParameter("phone");
            String account = req.getParameter("account");
            String email = req.getParameter("email");
            String password = req.getParameter("password");

            // 1. Hash the password
            String passwordHash = SecurityUtil.hashPassword(password);
            
            // 2. Encrypt sensitive data
            byte[] encryptedPhone = phone.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            byte[] encryptedAccount = account.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            //byte[] encryptedPhone = SecurityUtil.encrypt(phone);
            //byte[] encryptedAccount = SecurityUtil.encrypt(account);

            // 3. Save to database
            String sql = "INSERT INTO users (full_name, phone_number, account_number, email, password_hash) VALUES (?, ?, ?, ?, ?)";
            
            try (Connection conn = DatabaseUtil.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setString(1, name);
                stmt.setBytes(2, encryptedPhone);
                stmt.setBytes(3, encryptedAccount);
                stmt.setString(4, email);
                stmt.setString(5, passwordHash);
                
                int rowsAffected = stmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    jsonResponse.put("status", "success");
                    jsonResponse.put("message", "User registered successfully");
                    resp.setStatus(HttpServletResponse.SC_CREATED);
                } else {
                    throw new Exception("Registration failed, no rows affected.");
                }
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Error: " + e.getMessage());
            System.out.println("!!!!!!!!!!!!!! MY ERROR IS HERE !!!!!!!!!!!!!!");
            System.out.println("!!!!!!!!!!!!!! " + e.getMessage() + " !!!!!!!!!!!!!!");
            e.printStackTrace();
        }
        
        out.print(jsonResponse.toString());
        out.flush();
    }
}