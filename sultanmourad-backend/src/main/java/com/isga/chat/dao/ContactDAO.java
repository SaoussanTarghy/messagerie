package com.isga.chat.dao;

import com.isga.chat.common.User;
import com.isga.chat.server.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for managing user contacts (WhatsApp-style contact list)
 */
public class ContactDAO {

    /**
     * Add a contact for a user
     */
    public boolean addContact(int userId, int contactUserId, String contactName) {
        // Don't allow adding yourself as contact
        if (userId == contactUserId) {
            return false;
        }

        String query = "INSERT OR IGNORE INTO CONTACT (USER_ID, CONTACT_USER_ID, CONTACT_NAME) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, contactUserId);
            stmt.setString(3, contactName);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Remove a contact
     */
    public boolean removeContact(int userId, int contactUserId) {
        String query = "DELETE FROM CONTACT WHERE USER_ID = ? AND CONTACT_USER_ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, contactUserId);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get all contacts for a user (returns User objects with contact info)
     */
    public List<ContactInfo> getContacts(int userId) {
        List<ContactInfo> contacts = new ArrayList<>();
        String query = """
            SELECT c.ID as CONTACT_ID, c.CONTACT_NAME, c.CREATED_AT,
                   u.ID, u.USERNAME, u.FIRST_NAME, u.LAST_NAME, u.EMAIL, 
                   u.PERMISSION, u.STATUS, u.IS_BANNED, u.LAST_CONNECTION_TIME
            FROM CONTACT c
            JOIN USER u ON c.CONTACT_USER_ID = u.ID
            WHERE c.USER_ID = ?
            ORDER BY c.CONTACT_NAME, u.USERNAME
            """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ContactInfo contact = new ContactInfo();
                contact.setContactId(rs.getInt("CONTACT_ID"));
                contact.setContactName(rs.getString("CONTACT_NAME"));
                contact.setUserId(rs.getInt("ID"));
                contact.setUsername(rs.getString("USERNAME"));
                contact.setFirstName(rs.getString("FIRST_NAME"));
                contact.setLastName(rs.getString("LAST_NAME"));
                contact.setEmail(rs.getString("EMAIL"));
                contact.setPermission(rs.getInt("PERMISSION"));
                contact.setStatus(rs.getString("STATUS"));
                contact.setBanned(rs.getBoolean("IS_BANNED"));
                contacts.add(contact);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contacts;
    }

    /**
     * Check if a contact exists
     */
    public boolean isContact(int userId, int contactUserId) {
        String query = "SELECT COUNT(*) FROM CONTACT WHERE USER_ID = ? AND CONTACT_USER_ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, contactUserId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Search users by username or email (for adding contacts)
     */
    public List<User> searchUsers(String searchTerm, int excludeUserId) {
        List<User> users = new ArrayList<>();
        String query = """
            SELECT * FROM USER 
            WHERE (USERNAME LIKE ? OR EMAIL LIKE ?) 
            AND ID != ? 
            AND IS_BANNED = 0
            LIMIT 20
            """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            String term = "%" + searchTerm + "%";
            stmt.setString(1, term);
            stmt.setString(2, term);
            stmt.setInt(3, excludeUserId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("ID"));
                user.setUsername(rs.getString("USERNAME"));
                user.setFirstName(rs.getString("FIRST_NAME"));
                user.setLastName(rs.getString("LAST_NAME"));
                user.setEmail(rs.getString("EMAIL"));
                user.setPermission(rs.getInt("PERMISSION"));
                user.setStatus(rs.getString("STATUS"));
                user.setBanned(rs.getBoolean("IS_BANNED"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    /**
     * Inner class to hold contact information
     */
    public static class ContactInfo {
        private int contactId;
        private String contactName;
        private int userId;
        private String username;
        private String firstName;
        private String lastName;
        private String email;
        private int permission;
        private String status;
        private boolean isBanned;
        private int unreadCount;

        // Getters and Setters
        public int getContactId() { return contactId; }
        public void setContactId(int contactId) { this.contactId = contactId; }
        public String getContactName() { return contactName; }
        public void setContactName(String contactName) { this.contactName = contactName; }
        public int getUserId() { return userId; }
        public void setUserId(int userId) { this.userId = userId; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public int getPermission() { return permission; }
        public void setPermission(int permission) { this.permission = permission; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public boolean isBanned() { return isBanned; }
        public void setBanned(boolean banned) { isBanned = banned; }
        public int getUnreadCount() { return unreadCount; }
        public void setUnreadCount(int unreadCount) { this.unreadCount = unreadCount; }
    }
}
