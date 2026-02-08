package com.isga.chat.dao;

import com.isga.chat.server.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for managing private messages between users
 */
public class PrivateMessageDAO {

    /**
     * Save a private message
     */
    public PrivateMessage saveMessage(int senderId, int receiverId, String content) {
        String query = "INSERT INTO PRIVATE_MESSAGE (SENDER_ID, RECEIVER_ID, CONTENT) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, senderId);
            stmt.setInt(2, receiverId);
            stmt.setString(3, content);
            stmt.executeUpdate();
            
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                return getMessageById(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get a message by ID
     */
    public PrivateMessage getMessageById(int messageId) {
        String query = """
            SELECT pm.*, 
                   s.USERNAME as SENDER_USERNAME,
                   r.USERNAME as RECEIVER_USERNAME
            FROM PRIVATE_MESSAGE pm
            JOIN USER s ON pm.SENDER_ID = s.ID
            JOIN USER r ON pm.RECEIVER_ID = r.ID
            WHERE pm.ID = ?
            """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, messageId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToMessage(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get conversation between two users
     */
    public List<PrivateMessage> getConversation(int userId1, int userId2, int limit) {
        List<PrivateMessage> messages = new ArrayList<>();
        String query = """
            SELECT pm.*, 
                   s.USERNAME as SENDER_USERNAME,
                   r.USERNAME as RECEIVER_USERNAME
            FROM PRIVATE_MESSAGE pm
            JOIN USER s ON pm.SENDER_ID = s.ID
            JOIN USER r ON pm.RECEIVER_ID = r.ID
            WHERE (pm.SENDER_ID = ? AND pm.RECEIVER_ID = ?)
               OR (pm.SENDER_ID = ? AND pm.RECEIVER_ID = ?)
            ORDER BY pm.TIMESTAMP DESC
            LIMIT ?
            """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId1);
            stmt.setInt(2, userId2);
            stmt.setInt(3, userId2);
            stmt.setInt(4, userId1);
            stmt.setInt(5, limit);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                messages.add(mapResultSetToMessage(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    /**
     * Mark messages as read
     */
    public void markAsRead(int receiverId, int senderId) {
        String query = "UPDATE PRIVATE_MESSAGE SET IS_READ = TRUE WHERE RECEIVER_ID = ? AND SENDER_ID = ? AND IS_READ = FALSE";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, receiverId);
            stmt.setInt(2, senderId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get unread count for a user from a specific sender
     */
    public int getUnreadCount(int receiverId, int senderId) {
        String query = "SELECT COUNT(*) FROM PRIVATE_MESSAGE WHERE RECEIVER_ID = ? AND SENDER_ID = ? AND IS_READ = FALSE";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, receiverId);
            stmt.setInt(2, senderId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Get total unread count for a user
     */
    public int getTotalUnreadCount(int userId) {
        String query = "SELECT COUNT(*) FROM PRIVATE_MESSAGE WHERE RECEIVER_ID = ? AND IS_READ = FALSE";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Get all conversations for a user (latest message from each conversation)
     */
    public List<ConversationSummary> getConversations(int userId) {
        List<ConversationSummary> conversations = new ArrayList<>();
        String query = """
            SELECT 
                CASE WHEN pm.SENDER_ID = ? THEN pm.RECEIVER_ID ELSE pm.SENDER_ID END as OTHER_USER_ID,
                u.USERNAME as OTHER_USERNAME,
                u.STATUS as OTHER_STATUS,
                pm.CONTENT as LAST_MESSAGE,
                pm.TIMESTAMP as LAST_MESSAGE_TIME,
                (SELECT COUNT(*) FROM PRIVATE_MESSAGE 
                 WHERE RECEIVER_ID = ? AND SENDER_ID = u.ID AND IS_READ = FALSE) as UNREAD_COUNT
            FROM PRIVATE_MESSAGE pm
            JOIN USER u ON u.ID = CASE WHEN pm.SENDER_ID = ? THEN pm.RECEIVER_ID ELSE pm.SENDER_ID END
            WHERE pm.SENDER_ID = ? OR pm.RECEIVER_ID = ?
            GROUP BY OTHER_USER_ID
            HAVING pm.TIMESTAMP = (
                SELECT MAX(pm2.TIMESTAMP) 
                FROM PRIVATE_MESSAGE pm2 
                WHERE (pm2.SENDER_ID = ? AND pm2.RECEIVER_ID = OTHER_USER_ID)
                   OR (pm2.SENDER_ID = OTHER_USER_ID AND pm2.RECEIVER_ID = ?)
            )
            ORDER BY pm.TIMESTAMP DESC
            """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            stmt.setInt(3, userId);
            stmt.setInt(4, userId);
            stmt.setInt(5, userId);
            stmt.setInt(6, userId);
            stmt.setInt(7, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ConversationSummary summary = new ConversationSummary();
                summary.setOtherUserId(rs.getInt("OTHER_USER_ID"));
                summary.setOtherUsername(rs.getString("OTHER_USERNAME"));
                summary.setOtherStatus(rs.getString("OTHER_STATUS"));
                summary.setLastMessage(rs.getString("LAST_MESSAGE"));
                summary.setLastMessageTime(rs.getTimestamp("LAST_MESSAGE_TIME"));
                summary.setUnreadCount(rs.getInt("UNREAD_COUNT"));
                conversations.add(summary);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conversations;
    }

    private PrivateMessage mapResultSetToMessage(ResultSet rs) throws SQLException {
        PrivateMessage msg = new PrivateMessage();
        msg.setId(rs.getInt("ID"));
        msg.setSenderId(rs.getInt("SENDER_ID"));
        msg.setReceiverId(rs.getInt("RECEIVER_ID"));
        msg.setSenderUsername(rs.getString("SENDER_USERNAME"));
        msg.setReceiverUsername(rs.getString("RECEIVER_USERNAME"));
        msg.setContent(rs.getString("CONTENT"));
        msg.setTimestamp(rs.getTimestamp("TIMESTAMP"));
        msg.setRead(rs.getBoolean("IS_READ"));
        return msg;
    }

    /**
     * Private Message class
     */
    public static class PrivateMessage {
        private int id;
        private int senderId;
        private int receiverId;
        private String senderUsername;
        private String receiverUsername;
        private String content;
        private Timestamp timestamp;
        private boolean isRead;

        // Getters and Setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public int getSenderId() { return senderId; }
        public void setSenderId(int senderId) { this.senderId = senderId; }
        public int getReceiverId() { return receiverId; }
        public void setReceiverId(int receiverId) { this.receiverId = receiverId; }
        public String getSenderUsername() { return senderUsername; }
        public void setSenderUsername(String senderUsername) { this.senderUsername = senderUsername; }
        public String getReceiverUsername() { return receiverUsername; }
        public void setReceiverUsername(String receiverUsername) { this.receiverUsername = receiverUsername; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public Timestamp getTimestamp() { return timestamp; }
        public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
        public boolean isRead() { return isRead; }
        public void setRead(boolean read) { isRead = read; }
    }

    /**
     * Conversation Summary class
     */
    public static class ConversationSummary {
        private int otherUserId;
        private String otherUsername;
        private String otherStatus;
        private String lastMessage;
        private Timestamp lastMessageTime;
        private int unreadCount;

        // Getters and Setters
        public int getOtherUserId() { return otherUserId; }
        public void setOtherUserId(int otherUserId) { this.otherUserId = otherUserId; }
        public String getOtherUsername() { return otherUsername; }
        public void setOtherUsername(String otherUsername) { this.otherUsername = otherUsername; }
        public String getOtherStatus() { return otherStatus; }
        public void setOtherStatus(String otherStatus) { this.otherStatus = otherStatus; }
        public String getLastMessage() { return lastMessage; }
        public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }
        public Timestamp getLastMessageTime() { return lastMessageTime; }
        public void setLastMessageTime(Timestamp lastMessageTime) { this.lastMessageTime = lastMessageTime; }
        public int getUnreadCount() { return unreadCount; }
        public void setUnreadCount(int unreadCount) { this.unreadCount = unreadCount; }
    }
}
