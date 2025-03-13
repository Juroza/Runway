package group50.utils;

import java.sql.*;
public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:./database.db";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
            createTableIfNotExists();
            insertDefaultUsers();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("SQLite JDBC Driver not found!");
        }

        createTableIfNotExists();
        insertDefaultUsers();
    }

    private static void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL, " +
                "role TEXT NOT NULL CHECK(role IN ('admin', 'editor', 'viewer')))";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Users table initialized successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void insertDefaultUsers() {
        if (!userExists("admin")) {
            addUser("admin", "123456", "admin");
        }
        if (!userExists("bob")) {
            addUser("bob", "123456", "editor");
        }
        if (!userExists("timmy")) {
            addUser("timmy", "123456", "editor");
        }
        if (!userExists("nana")) {
            addUser("nana", "123456", "editor");
        }
        if (!userExists("taylor")) {
            addUser("taylor", "123456", "viewer");
        }
    }

    private static boolean userExists(String username) {
        String query = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void addUser(String username, String password, String role) {
        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, role);
            pstmt.executeUpdate();
            System.out.println("User " + username + " added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean validateUser(String username, String password) {
        String query = "SELECT role FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getUserRole(String username) {
        String query = "SELECT role FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() ? rs.getString("role") : null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

}
