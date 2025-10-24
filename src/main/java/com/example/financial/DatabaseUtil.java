package com.example.financial;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class DatabaseUtil {
    // !! IMPORTANT: Replace with your actual MySQL details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/financial_dashboard";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "@messiX10";

    static {
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load MySQL JDBC driver", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}
