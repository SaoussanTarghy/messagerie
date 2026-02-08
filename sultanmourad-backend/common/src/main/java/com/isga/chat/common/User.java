package com.isga.chat.common;

import java.io.Serializable;
import java.sql.Timestamp;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private int permission;
    private Timestamp lastConnectionTime;
    private String status;
    private boolean isBanned;

    public User() {}

    public User(int id, String username, String firstName, String lastName, String email, int permission, String status, boolean isBanned) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.permission = permission;
        this.status = status;
        this.isBanned = isBanned;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
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
    public Timestamp getLastConnectionTime() { return lastConnectionTime; }
    public void setLastConnectionTime(Timestamp lastConnectionTime) { this.lastConnectionTime = lastConnectionTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public boolean isBanned() { return isBanned; }
    public void setBanned(boolean banned) { isBanned = banned; }

    @Override
    public String toString() {
        return username + " (" + status + ")";
    }
}
