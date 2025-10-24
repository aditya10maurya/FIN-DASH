package com.example.financial.api;

import com.example.financial.DatabaseUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/api/investments")
public class InvestmentServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        
        // --- Security Check ---
        HttpSession session = req.getSession(false); // Do not create a new session
        if (session == null || session.getAttribute("userEmail") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"error\":\"Unauthorized. Please log in.\"}");
            out.flush();
            return;
        }
        
        String userEmail = (String) session.getAttribute("userEmail");
        String investmentType = req.getParameter("type");

        JSONArray investmentsArray = new JSONArray();

        // Query to first get user_id from email, then get investments
        String sql = "SELECT bank_name, principal_amount, current_value, maturity_date " +
                     "FROM investments " +
                     "WHERE investment_type = ? AND user_id = (SELECT user_id FROM users WHERE email = ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, investmentType);
            stmt.setString(2, userEmail);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                JSONObject investment = new JSONObject();
                investment.put("bankName", rs.getString("bank_name"));
                investment.put("principalAmount", rs.getBigDecimal("principal_amount"));
                investment.put("currentValue", rs.getBigDecimal("current_value"));
                investment.put("maturityDate", rs.getDate("maturity_date"));
                investmentsArray.put(investment);
            }
            
            out.print(investmentsArray.toString());
            resp.setStatus(HttpServletResponse.SC_OK);

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"" + e.getMessage() + "\"}");
            e.printStackTrace();
        }
        
        out.flush();
    }
}