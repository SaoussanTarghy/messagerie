package com.isga.chat.dao;

import com.isga.chat.common.User;
import com.isga.chat.server.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    public User authenticate(String username, String passwordHash) {
        String query = "SELECT * FROM USER WHERE USERNAME = ? AND PASSWORD = ? AND IS_BANNED = FALSE";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, passwordHash);
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
