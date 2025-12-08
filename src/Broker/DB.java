package Broker;

import java.sql.*;

public class DB {
    private static final String JDBC_URL =
            "jdbc:mariadb://localhost:3306/cpsy200?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "";
    // INPUT THE PASSWORD. FOR SECURITY PURPOSES, IT IS LEFT EMPTY.

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USER, PASS);
    }

    public static void closeQuietly(AutoCloseable c) {
        if (c != null) {
            try { c.close(); } catch (Exception ignored) {}
        }
    }
}
