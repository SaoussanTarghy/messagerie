package com.isga.chat.server;

import com.isga.chat.common.Message;
import com.isga.chat.common.NetworkPackage;
import com.isga.chat.common.User;
import com.isga.chat.dao.LogDAO;
import com.isga.chat.dao.MessageDAO;
import com.isga.chat.dao.UserDAO;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;

public class ClientHandler implements Runnable {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private User user;

    private UserDAO userDAO = new UserDAO();
    private MessageDAO messageDAO = new MessageDAO();
    private LogDAO logDAO = new LogDAO();

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            while (true) {
                NetworkPackage request = (NetworkPackage) in.readObject();
                processRequest(request);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Client disconnected: " + (user != null ? user.getUsername() : "Unknown"));
        } finally {
            close();
        }
    }

    private void processRequest(NetworkPackage request) {
        switch (request.getType()) {
            case LOGIN_REQUEST:
                handleLogin(request);
                break;
            case MESSAGE_SEND:
                handleMessage(request);
                break;
            case STATUS_CHANGE_REQUEST:
                handleStatusChange(request);
                break;
            case BAN_REQUEST:
                handleBan(request);
                break;
            case LOGOUT_REQUEST:
                handleLogout();
                break;
            default:
                break;
        }
    }

    private void handleLogin(NetworkPackage request) {
        Map<String, String> credentials = (Map<String, String>) request.getPayload();
        String username = credentials.get("username");
        String password = credentials.get("password");

        User authenticatedUser = userDAO.authenticate(username, password);
        if (authenticatedUser != null) {
            this.user = authenticatedUser;
            userDAO.updateStatus(user.getId(), "online");
            logDAO.log(user.getId(), "login");

            send(new NetworkPackage(NetworkPackage.Type.LOGIN_RESPONSE, user));

            // Broadcast user list update
            ServerMain.broadcast(new NetworkPackage(NetworkPackage.Type.USER_LIST_UPDATE, userDAO.getAllUsers()));

            // Send recent messages
            List<Message> recent = messageDAO.getRecentMessages(50);
            for (int i = recent.size() - 1; i >= 0; i--) {
                send(new NetworkPackage(NetworkPackage.Type.MESSAGE_BROADCAST, recent.get(i)));
            }
        } else {
            send(new NetworkPackage(NetworkPackage.Type.ERROR, "Invalid credentials or banned."));
        }
    }

    private void handleMessage(NetworkPackage request) {
        if (user == null)
            return;
        String content = (String) request.getPayload();
        Message msg = new Message(user.getId(), user.getUsername(), content);
        messageDAO.saveMessage(msg);
        logDAO.log(user.getId(), "send message");
        ServerMain.broadcast(new NetworkPackage(NetworkPackage.Type.MESSAGE_BROADCAST, msg));
    }

    private void handleStatusChange(NetworkPackage request) {
        if (user == null)
            return;
        String newStatus = (String) request.getPayload();
        userDAO.updateStatus(user.getId(), newStatus);
        logDAO.log(user.getId(), "status change: " + newStatus);
        ServerMain.broadcast(new NetworkPackage(NetworkPackage.Type.USER_LIST_UPDATE, userDAO.getAllUsers()));
    }

    private void handleBan(NetworkPackage request) {
        if (user == null || user.getPermission() > 2)
            return; // Only mod/admin
        int targetUserId = (int) request.getPayload();
        userDAO.banUser(targetUserId);
        logDAO.log(user.getId(), "ban user: " + targetUserId);

        ServerMain.broadcast(new NetworkPackage(NetworkPackage.Type.USER_LIST_UPDATE, userDAO.getAllUsers()));
        ServerMain.disconnectUser(targetUserId);
    }

    private void handleLogout() {
        if (user != null) {
            userDAO.updateStatus(user.getId(), "offline");
            logDAO.log(user.getId(), "logout");
            ServerMain.broadcast(new NetworkPackage(NetworkPackage.Type.USER_LIST_UPDATE, userDAO.getAllUsers()));
        }
        close();
    }

    public synchronized void send(NetworkPackage pkg) {
        try {
            out.writeObject(pkg);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void forceDisconnect() {
        try {
            send(new NetworkPackage(NetworkPackage.Type.ERROR, "You have been banned."));
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void close() {
        try {
            ServerMain.removeClient(this);
            if (socket != null && !socket.isClosed())
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public User getUser() {
        return user;
    }
}
