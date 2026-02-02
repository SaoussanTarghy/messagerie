package com.isga.chat.websocket;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket server for browser clients.
 * Runs on port 8888 and handles both group and private messaging.
 */
public class WebSocketServer extends org.java_websocket.server.WebSocketServer {
    private static final int PORT = 8888;
    private static ConcurrentHashMap<WebSocket, WebSocketClientHandler> clients = new ConcurrentHashMap<>();

    public WebSocketServer() {
        super(new InetSocketAddress(PORT));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("New WebSocket client connected: " + conn.getRemoteSocketAddress());
        clients.put(conn, new WebSocketClientHandler(conn));
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("WebSocket client disconnected: " + conn.getRemoteSocketAddress());
        WebSocketClientHandler handler = clients.get(conn);
        if (handler != null) {
            handler.onClose();
            clients.remove(conn);
        }
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        WebSocketClientHandler handler = clients.get(conn);
        if (handler != null) {
            handler.processMessage(message);
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("WebSocket error: " + ex.getMessage());
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("WebSocket Server started on port " + PORT);
        System.out.println("Waiting for browser clients...");
    }

    // Broadcast message to all connected clients
    public static void broadcastToAll(String message) {
        for (WebSocketClientHandler handler : clients.values()) {
            handler.send(message);
        }
    }

    // Send message to a specific user (for private messages)
    public static void sendToUser(int userId, String message) {
        for (WebSocketClientHandler handler : clients.values()) {
            if (handler.getUser() != null && handler.getUser().getId() == userId) {
                handler.send(message);
                return;
            }
        }
    }

    // Disconnect a specific user (for ban functionality)
    public static void disconnectUser(int userId) {
        for (WebSocketClientHandler handler : clients.values()) {
            if (handler.getUser() != null && handler.getUser().getId() == userId) {
                handler.send(JsonProtocol.errorResponse("You have been banned."));
                handler.getConnection().close();
                break;
            }
        }
    }

    // Check if a user is online
    public static boolean isUserOnline(int userId) {
        for (WebSocketClientHandler handler : clients.values()) {
            if (handler.getUser() != null && handler.getUser().getId() == userId) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        WebSocketServer server = new WebSocketServer();
        server.start();
        System.out.println("╔═══════════════════════════════════════════╗");
        System.out.println("║     ISGA Chat WebSocket Server            ║");
        System.out.println("║     Group + Private Messaging             ║");
        System.out.println("╠═══════════════════════════════════════════╣");
        System.out.println("║  Port: " + PORT + "                               ║");
        System.out.println("║  Status: Running                          ║");
        System.out.println("╚═══════════════════════════════════════════╝");
    }
}
