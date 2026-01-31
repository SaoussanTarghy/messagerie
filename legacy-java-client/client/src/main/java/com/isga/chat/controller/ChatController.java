package com.isga.chat.controller;

import com.isga.chat.common.Message;
import com.isga.chat.common.NetworkPackage;
import com.isga.chat.common.User;
import com.isga.chat.view.ChatView;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatController {
    private ChatView view;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private User currentUser;
    private boolean running = true;

    public ChatController(ChatView view) {
        this.view = view;
        initListeners();
    }

    private void initListeners() {
        view.addLoginListener(e -> handleLogin());
        view.addSendListener(e -> handleSendMessage());
        view.addStatusListener(e -> handleStatusChange());
        view.addBanListener(e -> handleBan());
    }

    private void handleLogin() {
        String username = view.getUsername();
        String password = view.getPassword();
        String hashedPassword = hashPassword(password);

        try {
            socket = new Socket("localhost", 12345);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            Map<String, String> creds = new HashMap<>();
            creds.put("username", username);
            creds.put("password", hashedPassword);

            send(new NetworkPackage(NetworkPackage.Type.LOGIN_REQUEST, creds));

            // Start receiving thread
            new Thread(this::receiveLoop).start();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(view, "Could not connect to server: " + ex.getMessage());
        }
    }

    private void handleSendMessage() {
        String content = view.getMessage();
        if (!content.isEmpty()) {
            send(new NetworkPackage(NetworkPackage.Type.MESSAGE_SEND, content));
            view.clearMessage();
        }
    }

    private void handleStatusChange() {
        String status = view.getSelectedStatus();
        send(new NetworkPackage(NetworkPackage.Type.STATUS_CHANGE_REQUEST, status));
    }

    private void handleBan() {
        User target = view.getSelectedUser();
        if (target != null) {
            send(new NetworkPackage(NetworkPackage.Type.BAN_REQUEST, target.getId()));
        }
    }

    private void receiveLoop() {
        try {
            while (running) {
                NetworkPackage pkg = (NetworkPackage) in.readObject();
                SwingUtilities.invokeLater(() -> processResponse(pkg));
            }
        } catch (IOException | ClassNotFoundException e) {
            if (running) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(view, "Disconnected from server.");
                    view.showLogin();
                });
            }
        }
    }

    private void processResponse(NetworkPackage pkg) {
        switch (pkg.getType()) {
            case LOGIN_RESPONSE:
                currentUser = (User) pkg.getPayload();
                view.showChat();
                view.setBanButtonVisible(currentUser.getPermission() <= 2);
                break;
            case MESSAGE_BROADCAST:
                Message msg = (Message) pkg.getPayload();
                view.appendChat("[" + msg.getTimestamp().toString().substring(11, 16) + "] " + msg.getUsername() + ": "
                        + msg.getContent());
                break;
            case USER_LIST_UPDATE:
                List<User> users = (List<User>) pkg.getPayload();
                view.updateUserList(users);
                break;
            case ERROR:
                JOptionPane.showMessageDialog(view, (String) pkg.getPayload());
                if (pkg.getPayload().equals("You have been banned.")) {
                    running = false;
                    view.showLogin();
                }
                break;
            default:
                break;
        }
    }

    private void send(NetworkPackage pkg) {
        try {
            if (out != null) {
                out.writeObject(pkg);
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return password;
        }
    }
}
