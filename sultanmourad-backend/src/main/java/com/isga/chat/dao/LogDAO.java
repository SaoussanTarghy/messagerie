package com.isga.chat.dao;

import com.isga.chat.server.DBConnection;
import java.sql.*;

public class LogDAO {
    public void log(int userId, String type) {
        String query = "INSERT INTO LOG (USER_ID, TYPE) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setString(2, type);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
