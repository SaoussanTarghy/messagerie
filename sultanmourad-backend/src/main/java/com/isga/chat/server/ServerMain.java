package com.isga.chat.server;

import com.isga.chat.common.NetworkPackage;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerMain {
    private static final int PORT = 12345;
    private static List<ClientHandler> clients = new ArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void broadcast(NetworkPackage pkg) {
        for (ClientHandler client : clients) {
            client.send(pkg);
        }
    }

    public static synchronized void removeClient(ClientHandler client) {
        clients.remove(client);
    }

    public static synchronized void disconnectUser(int userId) {
        for (ClientHandler client : clients) {
            if (client.getUser() != null && client.getUser().getId() == userId) {
                client.forceDisconnect();
                break;
            }
        }
    }
}
