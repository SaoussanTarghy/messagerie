package com.isga.chat.common;

import java.io.Serializable;
import java.sql.Timestamp;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private int userId;
    private String username; // Added for convenience in chat
    private Timestamp timestamp;
    private String content;

    public Message() {}

    public Message(int userId, String username, String content) {
        this.userId = userId;
        this.username = username;
        this.content = content;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
