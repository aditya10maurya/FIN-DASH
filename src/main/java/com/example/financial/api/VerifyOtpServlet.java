package com.example.financial.api;

import org.json.JSONObject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/verify-otp")
public class VerifyOtpServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        JSONObject jsonResponse = new JSONObject();

        String email = req.getParameter("email");
        String otp = req.getParameter("otp");

        try {
            String storedOtp = LoginServlet.otpStorage.get(email);

            if (storedOtp != null && storedOtp.equals(otp)) {
                // --- OTP MATCHES ---
                // 1. Create a user session
                HttpSession session = req.getSession();
                session.setAttribute("userEmail", email); // Store email in session
                session.setMaxInactiveInterval(30 * 60); // 30-minute session timeout

                // 2. Remove OTP after use
                LoginServlet.otpStorage.remove(email);

                jsonResponse.put("status", "success");
                jsonResponse.put("message", "Verification successful");
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                jsonResponse.put("status", "error");
                jsonResponse.put("message", "Invalid or expired OTP");
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.put("status", "error");
            jsonResponse.put("message", "Error: " + e.getMessage());
        }

        out.print(jsonResponse.toString());
        out.flush();
    }
}