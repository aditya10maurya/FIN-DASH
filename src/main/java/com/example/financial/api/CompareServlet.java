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

@WebServlet("/api/compare")
public class CompareServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        // --- Security Check (User must be logged in to compare) ---
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userEmail") == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"error\":\"Unauthorized. Please log in.\"}");
            out.flush();
            return;
        }

        JSONArray productsArray = new JSONArray();
        String sql = "SELECT bank_name, product_type, interest_rate, min_tenure_months FROM bank_products";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                JSONObject product = new JSONObject();
                product.put("bankName", rs.getString("bank_name"));
                product.put("productType", rs.getString("product_type"));
                product.put("interestRate", rs.getBigDecimal("interest_rate"));
                product.put("minTenure", rs.getInt("min_tenure_months") + " months");
                productsArray.put(product);
            }
            
            out.print(productsArray.toString());
            resp.setStatus(HttpServletResponse.SC_OK);

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\":\"" + e.getMessage() + "\"}");
            e.printStackTrace();
        }
        
        out.flush();
    }
}