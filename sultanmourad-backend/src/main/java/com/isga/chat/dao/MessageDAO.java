package com.isga.chat.dao;

import com.isga.chat.common.Message;
import com.isga.chat.server.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {
    public void saveMessage(Message msg) {
        String query = "INSERT INTO MESSAGE (USER_ID, CONTENT) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, msg.getUserId());
            stmt.setString(2, msg.getContent());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                msg.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Message> getRecentMessages(int limit) {
        List<Message> messages = new ArrayList<>();
        String query = "SELECT m.*, u.USERNAME FROM MESSAGE m JOIN USER u ON m.USER_ID = u.ID ORDER BY m.TIMESTAMP DESC LIMIT ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Message msg = new Message();
                msg.setId(rs.getInt("ID"));
                msg.setUserId(rs.getInt("USER_ID"));
                msg.setUsername(rs.getString("USERNAME"));
                msg.setTimestamp(rs.getTimestamp("TIMESTAMP"));
                msg.setContent(rs.getString("CONTENT"));
                messages.add(msg);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }
}
