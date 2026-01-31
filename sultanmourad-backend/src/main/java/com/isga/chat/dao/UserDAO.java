package com.isga.chat.dao;

import com.isga.chat.common.User;
import com.isga.chat.server.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    public User authenticate(String usernameOrEmail, String passwordHash) {
        // Allow login with either username or email
        String query = "SELECT * FROM USER WHERE (USERNAME = ? OR EMAIL = ?) AND PASSWORD = ? AND IS_BANNED = 0";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, usernameOrEmail);
            stmt.setString(2, usernameOrEmail);
            stmt.setString(3, passwordHash);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User registerUser(String username, String email, String passwordHash) {
        // Check if username or email already exists
        String checkQuery = "SELECT COUNT(*) FROM USER WHERE USERNAME = ? OR EMAIL = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
            checkStmt.setString(1, username);
            checkStmt.setString(2, email);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return null; // User already exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        // Insert new user
        String insertQuery = "INSERT INTO USER (USERNAME, FIRST_NAME, LAST_NAME, EMAIL, PASSWORD, PERMISSION, STATUS) VALUES (?, ?, ?, ?, ?, 3, 'online')";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, username);
            stmt.setString(2, username); // Use username as first name
            stmt.setString(3, "User"); // Default last name
            stmt.setString(4, email);
            stmt.setString(5, passwordHash);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int newUserId = generatedKeys.getInt(1);
                    // Fetch and return the new user
                    return getUserById(newUserId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User getUserById(int userId) {
        String query = "SELECT * FROM USER WHERE ID = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateStatus(int userId, String status) {
        String query = "UPDATE USER SET STATUS = ?, LAST_CONNECTION_TIME = CURRENT_TIMESTAMP WHERE ID = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void banUser(int userId) {
        String query = "UPDATE USER SET IS_BANNED = TRUE, STATUS = 'offline' WHERE ID = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM USER";
        try (Connection conn = DBConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public void updateRole(int userId, int newPermission) {
        String query = "UPDATE USER SET PERMISSION = ? WHERE ID = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, newPermission);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("ID"));
        user.setUsername(rs.getString("USERNAME"));
        user.setFirstName(rs.getString("FIRST_NAME"));
        user.setLastName(rs.getString("LAST_NAME"));
        user.setEmail(rs.getString("EMAIL"));
        user.setPermission(rs.getInt("PERMISSION"));
        user.setLastConnectionTime(rs.getTimestamp("LAST_CONNECTION_TIME"));
        user.setStatus(rs.getString("STATUS"));
        user.setBanned(rs.getBoolean("IS_BANNED"));
        return user;
    }
}
