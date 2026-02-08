package com.isga.chat.client;

import com.isga.chat.controller.ChatController;
import com.isga.chat.view.ChatView;
import javax.swing.SwingUtilities;

public class ClientMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ChatView view = new ChatView();
            new ChatController(view);
            view.setVisible(true);
        });
    }
}
