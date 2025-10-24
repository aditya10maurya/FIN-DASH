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
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@WebServlet("/api/login")
public class LoginServlet extends HttpServlet {

    // --- OTP Storage (DEMO ONLY) ---
    // In production, use a timed cache like Redis or Memcached.
    public static final Map<String, String> otpStorage = new HashMap<>();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        JSONObject jsonResponse = new JSONObject();

        String email = req.getParameter("email");
        String password = req.getParameter("password");

        try {
            String passwordHash = SecurityUtil.hashPassword(password);
            String sql = "SELECT user_id, password_hash FROM users WHERE email = ?";

            try (Connection conn = DatabaseUtil.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setString(1, email);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    if (passwordHash.equals(storedHash)) {
                        // --- Password MATCHES ---
                        // 1. Generate OTP
                        String otp = String.format("%06d", new Random().nextInt(999999));
                        
                        // 2. Store OTP (for demo)
                        otpStorage.put(email, otp);

                        // 3. "Send" OTP (Simulate by printing to server console)
                        System.out.println("---- OTP FOR " + email + " ----");
                        System.out.println("---- OTP: " + otp + " ----");
                        
                        jsonResponse.put("status", "success");
                        jsonResponse.put("message", "OTP sent successfully");
                        resp.setStatus(HttpServletResponse.SC_OK);
                    } else {
                        // Password mismatch
                        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        jsonResponse.put("status", "error");
                        jsonResponse.put("message", "Invalid email or password");
                    }
                } else {
                    // User not found
                    resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    jsonResponse.put("status", "error");
                    jsonResponse.put("message", "Invalid email or password");
                }
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        out.print(jsonResponse.toString());
        out.flush();
    }
}