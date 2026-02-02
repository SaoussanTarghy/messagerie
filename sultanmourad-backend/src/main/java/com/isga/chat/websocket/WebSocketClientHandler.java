package com.isga.chat.websocket;

import com.isga.chat.common.Message;
import com.isga.chat.common.User;
import com.isga.chat.dao.ContactDAO;
import com.isga.chat.dao.ContactDAO.ContactInfo;
import com.isga.chat.dao.LogDAO;
import com.isga.chat.dao.MessageDAO;
import com.isga.chat.dao.PrivateMessageDAO;
import com.isga.chat.dao.PrivateMessageDAO.PrivateMessage;
import com.isga.chat.dao.UserDAO;
import org.java_websocket.WebSocket;

import java.util.List;
import java.util.Map;

/**
 * Handles WebSocket client connections.
 * Extended to support private messaging and contacts.
 */
public class WebSocketClientHandler {
    private WebSocket conn;
    private User user;

    private UserDAO userDAO = new UserDAO();
    private MessageDAO messageDAO = new MessageDAO();
    private LogDAO logDAO = new LogDAO();
    private ContactDAO contactDAO = new ContactDAO();
    private PrivateMessageDAO privateMessageDAO = new PrivateMessageDAO();

    public WebSocketClientHandler(WebSocket conn) {
        this.conn = conn;
    }

    public void processMessage(String jsonMessage) {
        try {
            Map<String, Object> request = JsonProtocol.parseRequest(jsonMessage);
            String type = (String) request.get("type");

            switch (type) {
                // ===== Existing functionality =====
                case "LOGIN_REQUEST":
                    handleLogin(request);
                    break;
                case "REGISTER_REQUEST":
                    handleRegister(request);
                    break;
                case "MESSAGE_SEND":
                    handleMessage(request);
                    break;
                case "STATUS_CHANGE_REQUEST":
                    handleStatusChange(request);
                    break;
                case "BAN_REQUEST":
                    handleBan(request);
                    break;
                case "LOGOUT_REQUEST":
                    handleLogout();
                    break;

                // ===== NEW: Private Messaging =====
                case "PRIVATE_MESSAGE_SEND":
                    handlePrivateMessage(request);
                    break;
                case "GET_CONVERSATION":
                    handleGetConversation(request);
                    break;
                case "GET_CONVERSATIONS":
                    handleGetConversations();
                    break;
                case "MARK_AS_READ":
                    handleMarkAsRead(request);
                    break;

                // ===== NEW: Contacts =====
                case "ADD_CONTACT":
                    handleAddContact(request);
                    break;
                case "REMOVE_CONTACT":
                    handleRemoveContact(request);
                    break;
                case "GET_CONTACTS":
                    handleGetContacts();
                    break;
                case "SEARCH_USERS":
                    handleSearchUsers(request);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            send(JsonProtocol.errorResponse("Invalid request format: " + e.getMessage()));
        }
    }

    // ==================== Existing Handlers ====================

    @SuppressWarnings("unchecked")
    private void handleLogin(Map<String, Object> request) {
        Map<String, Object> credentials = (Map<String, Object>) request.get("payload");
        String username = (String) credentials.get("username");
        String password = (String) credentials.get("password");

        User authenticatedUser = userDAO.authenticate(username, password);
        if (authenticatedUser != null) {
            this.user = authenticatedUser;
            userDAO.updateStatus(user.getId(), "online");
            logDAO.log(user.getId(), "login");

            send(JsonProtocol.loginResponse(user));

            // Broadcast user list update to all clients
            WebSocketServer.broadcastToAll(JsonProtocol.userListUpdate(userDAO.getAllUsers()));

            // Send recent group messages
            List<Message> recent = messageDAO.getRecentMessages(50);
            for (int i = recent.size() - 1; i >= 0; i--) {
                send(JsonProtocol.messageBroadcast(recent.get(i)));
            }

            // Send contacts list
            sendContactsWithUnread();

            // Send conversations list
            send(JsonProtocol.conversationsList(privateMessageDAO.getConversations(user.getId())));
        } else {
            send(JsonProtocol.errorResponse("Invalid credentials or banned."));
        }
    }

    @SuppressWarnings("unchecked")
    private void handleRegister(Map<String, Object> request) {
        Map<String, Object> data = (Map<String, Object>) request.get("payload");
        String username = (String) data.get("username");
        String email = (String) data.get("email");
        String password = (String) data.get("password");

        User newUser = userDAO.registerUser(username, email, password);
        if (newUser != null) {
            this.user = newUser;
            logDAO.log(user.getId(), "register");

            send(JsonProtocol.registerResponse(user));

            // Broadcast user list update to all clients
            WebSocketServer.broadcastToAll(JsonProtocol.userListUpdate(userDAO.getAllUsers()));
        } else {
            send(JsonProtocol.errorResponse("Username or email already exists."));
        }
    }

    private void handleMessage(Map<String, Object> request) {
        if (user == null)
            return;
        String content = (String) request.get("payload");
        Message msg = new Message(user.getId(), user.getUsername(), content);
        messageDAO.saveMessage(msg);
        logDAO.log(user.getId(), "send message");
        WebSocketServer.broadcastToAll(JsonProtocol.messageBroadcast(msg));
    }

    private void handleStatusChange(Map<String, Object> request) {
        if (user == null)
            return;
        String newStatus = (String) request.get("payload");
        userDAO.updateStatus(user.getId(), newStatus);
        logDAO.log(user.getId(), "status change: " + newStatus);
        WebSocketServer.broadcastToAll(JsonProtocol.userListUpdate(userDAO.getAllUsers()));
    }

    private void handleBan(Map<String, Object> request) {
        if (user == null || user.getPermission() > 2)
            return; // Only mod/admin
        int targetUserId = ((Number) request.get("payload")).intValue();
        userDAO.banUser(targetUserId);
        logDAO.log(user.getId(), "ban user: " + targetUserId);

        WebSocketServer.broadcastToAll(JsonProtocol.userListUpdate(userDAO.getAllUsers()));
        WebSocketServer.disconnectUser(targetUserId);
    }

    private void handleLogout() {
        if (user != null) {
            userDAO.updateStatus(user.getId(), "offline");
            logDAO.log(user.getId(), "logout");
            WebSocketServer.broadcastToAll(JsonProtocol.userListUpdate(userDAO.getAllUsers()));
        }
    }

    // ==================== NEW: Private Messaging Handlers ====================

    @SuppressWarnings("unchecked")
    private void handlePrivateMessage(Map<String, Object> request) {
        if (user == null) return;
        
        Map<String, Object> payload = (Map<String, Object>) request.get("payload");
        int receiverId = ((Number) payload.get("receiverId")).intValue();
        String content = (String) payload.get("content");

        // Save the message
        PrivateMessage msg = privateMessageDAO.saveMessage(user.getId(), receiverId, content);
        
        if (msg != null) {
            logDAO.log(user.getId(), "private message to: " + receiverId);
            
            // Send to sender (confirmation)
            send(JsonProtocol.privateMessageResponse(msg));
            
            // Send to receiver if online
            WebSocketServer.sendToUser(receiverId, JsonProtocol.privateMessageResponse(msg));
            
            // Update conversations for both users
            send(JsonProtocol.conversationsList(privateMessageDAO.getConversations(user.getId())));
            WebSocketServer.sendToUser(receiverId, 
                JsonProtocol.conversationsList(privateMessageDAO.getConversations(receiverId)));
        }
    }

    @SuppressWarnings("unchecked")
    private void handleGetConversation(Map<String, Object> request) {
        if (user == null) return;
        
        int otherUserId = ((Number) request.get("payload")).intValue();
        List<PrivateMessage> messages = privateMessageDAO.getConversation(user.getId(), otherUserId, 100);
        
        // Mark messages as read
        privateMessageDAO.markAsRead(user.getId(), otherUserId);
        
        send(JsonProtocol.conversationHistory(messages, otherUserId));
        
        // Update contacts to reflect read status
        sendContactsWithUnread();
    }

    private void handleGetConversations() {
        if (user == null) return;
        send(JsonProtocol.conversationsList(privateMessageDAO.getConversations(user.getId())));
    }

    @SuppressWarnings("unchecked")
    private void handleMarkAsRead(Map<String, Object> request) {
        if (user == null) return;
        
        int senderId = ((Number) request.get("payload")).intValue();
        privateMessageDAO.markAsRead(user.getId(), senderId);
        
        // Update contacts to reflect read status
        sendContactsWithUnread();
    }

    // ==================== NEW: Contacts Handlers ====================

    @SuppressWarnings("unchecked")
    private void handleAddContact(Map<String, Object> request) {
        if (user == null) return;
        
        Map<String, Object> payload = (Map<String, Object>) request.get("payload");
        int contactUserId = ((Number) payload.get("userId")).intValue();
        String contactName = payload.get("contactName") != null ? 
            (String) payload.get("contactName") : null;

        boolean success = contactDAO.addContact(user.getId(), contactUserId, contactName);
        
        if (success) {
            logDAO.log(user.getId(), "add contact: " + contactUserId);
            sendContactsWithUnread();
            send(JsonProtocol.successResponse("Contact added successfully"));
        } else {
            send(JsonProtocol.errorResponse("Failed to add contact. User may already be in your contacts."));
        }
    }

    private void handleRemoveContact(Map<String, Object> request) {
        if (user == null) return;
        
        int contactUserId = ((Number) request.get("payload")).intValue();
        boolean success = contactDAO.removeContact(user.getId(), contactUserId);
        
        if (success) {
            logDAO.log(user.getId(), "remove contact: " + contactUserId);
            sendContactsWithUnread();
            send(JsonProtocol.successResponse("Contact removed successfully"));
        } else {
            send(JsonProtocol.errorResponse("Failed to remove contact."));
        }
    }

    private void handleGetContacts() {
        if (user == null) return;
        sendContactsWithUnread();
    }

    private void handleSearchUsers(Map<String, Object> request) {
        if (user == null) return;
        
        String searchTerm = (String) request.get("payload");
        List<User> results = contactDAO.searchUsers(searchTerm, user.getId());
        send(JsonProtocol.userSearchResults(results));
    }

    // Helper to send contacts with unread counts
    private void sendContactsWithUnread() {
        List<ContactInfo> contacts = contactDAO.getContacts(user.getId());
        // Add unread counts
        for (ContactInfo contact : contacts) {
            int unread = privateMessageDAO.getUnreadCount(user.getId(), contact.getUserId());
            contact.setUnreadCount(unread);
        }
        send(JsonProtocol.contactsList(contacts));
    }

    // ==================== Connection Methods ====================

    public void onClose() {
        if (user != null) {
            userDAO.updateStatus(user.getId(), "offline");
            logDAO.log(user.getId(), "logout");
            WebSocketServer.broadcastToAll(JsonProtocol.userListUpdate(userDAO.getAllUsers()));
        }
    }

    public void send(String message) {
        if (conn != null && conn.isOpen()) {
            conn.send(message);
        }
    }

    public User getUser() {
        return user;
    }

    public WebSocket getConnection() {
        return conn;
    }
}
