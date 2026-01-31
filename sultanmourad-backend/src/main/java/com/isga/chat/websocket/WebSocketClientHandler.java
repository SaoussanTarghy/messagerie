package com.isga.chat.websocket;

import com.isga.chat.common.Message;
import com.isga.chat.common.User;
import com.isga.chat.dao.LogDAO;
import com.isga.chat.dao.MessageDAO;
import com.isga.chat.dao.UserDAO;
import org.java_websocket.WebSocket;

import java.util.List;
import java.util.Map;

/**
 * Handles WebSocket client connections.
 * Mirrors ClientHandler.java logic but for WebSocket with JSON protocol.
 */
public class WebSocketClientHandler {
    private WebSocket conn;
    private User user;

    private UserDAO userDAO = new UserDAO();
    private MessageDAO messageDAO = new MessageDAO();
    private LogDAO logDAO = new LogDAO();

    public WebSocketClientHandler(WebSocket conn) {
        this.conn = conn;
    }

    public void processMessage(String jsonMessage) {
        try {
            Map<String, Object> request = JsonProtocol.parseRequest(jsonMessage);
            String type = (String) request.get("type");

            switch (type) {
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
            }
        } catch (Exception e) {
            e.printStackTrace();
            send(JsonProtocol.errorResponse("Invalid request format"));
        }
    }

    @SuppressWarnings("unchecked")
    private void handleLogin(Map<String, Object> request) {
        Map<String, String> credentials = (Map<String, String>) request.get("payload");
        String username = credentials.get("username");
        String password = credentials.get("password");

        User authenticatedUser = userDAO.authenticate(username, password);
        if (authenticatedUser != null) {
            this.user = authenticatedUser;
            userDAO.updateStatus(user.getId(), "online");
            logDAO.log(user.getId(), "login");

            send(JsonProtocol.loginResponse(user));

            // Broadcast user list update to all clients
            WebSocketServer.broadcastToAll(JsonProtocol.userListUpdate(userDAO.getAllUsers()));

            // Send recent messages
            List<Message> recent = messageDAO.getRecentMessages(50);
            for (int i = recent.size() - 1; i >= 0; i--) {
                send(JsonProtocol.messageBroadcast(recent.get(i)));
            }
        } else {
            send(JsonProtocol.errorResponse("Invalid credentials or banned."));
        }
    }

    @SuppressWarnings("unchecked")
    private void handleRegister(Map<String, Object> request) {
        Map<String, String> data = (Map<String, String>) request.get("payload");
        String username = data.get("username");
        String email = data.get("email");
        String password = data.get("password");

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
