package com.isga.chat.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import com.isga.chat.common.User;
import com.isga.chat.common.Message;
import java.util.List;

public class ChatView extends JFrame {
    private JPanel cards;
    private CardLayout cardLayout;

    // Login Components
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    // Chat Components
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;
    private JList<User> userList;
    private DefaultListModel<User> userListModel;
    private JComboBox<String> statusCombo;
    private JButton banButton;

    public ChatView() {
        setTitle("ISGA Group Chat");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        cards.add(createLoginPanel(), "LOGIN");
        cards.add(createChatPanel(), "CHAT");

        add(cards);
        cardLayout.show(cards, "LOGIN");
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(15);
        panel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        panel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        loginButton = new JButton("Login");
        panel.add(loginButton, gbc);

        return panel;
    }

    private JPanel createChatPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Chat Area
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        panel.add(new JScrollPane(chatArea), BorderLayout.CENTER);

        // Sidebar (User List + Status)
        JPanel sidebar = new JPanel(new BorderLayout(5, 5));
        sidebar.setPreferredSize(new Dimension(200, 0));

        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        sidebar.add(new JScrollPane(userList), BorderLayout.CENTER);

        JPanel statusPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        statusCombo = new JComboBox<>(new String[] { "online", "away", "offline" });
        statusPanel.add(new JLabel("My Status:"));
        statusPanel.add(statusCombo);

        banButton = new JButton("Ban Selected User");
        banButton.setVisible(false); // Initially hidden, shown for admin/mod
        statusPanel.add(banButton);

        sidebar.add(statusPanel, BorderLayout.SOUTH);
        panel.add(sidebar, BorderLayout.EAST);

        // Bottom Panel (Message Input)
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        messageField = new JTextField();
        sendButton = new JButton("Send");
        bottomPanel.add(messageField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    public void showChat() {
        cardLayout.show(cards, "CHAT");
    }

    public void showLogin() {
        cardLayout.show(cards, "LOGIN");
    }

    // Getters for controller use
    public String getUsername() {
        return usernameField.getText();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public String getMessage() {
        return messageField.getText();
    }

    public void clearMessage() {
        messageField.setText("");
    }

    public User getSelectedUser() {
        return userList.getSelectedValue();
    }

    public String getSelectedStatus() {
        return (String) statusCombo.getSelectedItem();
    }

    public void appendChat(String text) {
        chatArea.append(text + "\n");
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    public void updateUserList(List<User> users) {
        userListModel.clear();
        for (User u : users)
            userListModel.addElement(u);
    }

    public void setBanButtonVisible(boolean visible) {
        banButton.setVisible(visible);
    }

    public void addLoginListener(ActionListener l) {
        loginButton.addActionListener(l);
    }

    public void addSendListener(ActionListener l) {
        sendButton.addActionListener(l);
        messageField.addActionListener(l);
    }

    public void addStatusListener(ActionListener l) {
        statusCombo.addActionListener(l);
    }

    public void addBanListener(ActionListener l) {
        banButton.addActionListener(l);
    }
}
