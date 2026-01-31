package com.isga.chat.websocket;

import com.isga.chat.common.Message;
import com.isga.chat.common.User;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for JSON ↔ Object conversion.
 * Mirrors the NetworkPackage types for WebSocket communication.
 */
public class JsonProtocol {
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    // Parse incoming JSON message
    public static Map<String, Object> parseRequest(String json) {
        JSONObject obj = new JSONObject(json);
        Map<String, Object> result = new HashMap<>();
        result.put("type", obj.getString("type"));
        
        if (obj.has("payload")) {
            Object payload = obj.get("payload");
            if (payload instanceof JSONObject) {
                JSONObject payloadObj = (JSONObject) payload;
                Map<String, String> payloadMap = new HashMap<>();
                for (String key : payloadObj.keySet()) {
                    payloadMap.put(key, payloadObj.getString(key));
                }
                result.put("payload", payloadMap);
            } else {
                result.put("payload", payload);
            }
        }
        return result;
    }

    // Create JSON for login response
    public static String loginResponse(User user) {
        JSONObject obj = new JSONObject();
        obj.put("type", "LOGIN_RESPONSE");
        obj.put("payload", userToJson(user));
        return obj.toString();
    }

    // Create JSON for error response
    public static String errorResponse(String message) {
        JSONObject obj = new JSONObject();
        obj.put("type", "ERROR");
        obj.put("payload", message);
        return obj.toString();
    }

    // Create JSON for message broadcast
    public static String messageBroadcast(Message msg) {
        JSONObject obj = new JSONObject();
        obj.put("type", "MESSAGE_BROADCAST");
        
        JSONObject payload = new JSONObject();
        payload.put("id", msg.getId());
        payload.put("userId", msg.getUserId());
        payload.put("username", msg.getUsername());
        payload.put("content", msg.getContent());
        if (msg.getTimestamp() != null) {
            payload.put("timestamp", DATE_FORMAT.format(msg.getTimestamp()));
        }
        obj.put("payload", payload);
        return obj.toString();
    }

    // Create JSON for user list update
    public static String userListUpdate(List<User> users) {
        JSONObject obj = new JSONObject();
        obj.put("type", "USER_LIST_UPDATE");
        
        JSONArray usersArray = new JSONArray();
        for (User user : users) {
            usersArray.put(userToJson(user));
        }
        obj.put("payload", usersArray);
        return obj.toString();
    }

    // Convert User to JSONObject
    private static JSONObject userToJson(User user) {
        JSONObject obj = new JSONObject();
        obj.put("id", user.getId());
        obj.put("username", user.getUsername());
        obj.put("firstName", user.getFirstName());
        obj.put("lastName", user.getLastName());
        obj.put("email", user.getEmail());
        obj.put("permission", user.getPermission());
        obj.put("status", user.getStatus());
        obj.put("isBanned", user.isBanned());
        if (user.getLastConnectionTime() != null) {
            obj.put("lastConnectionTime", DATE_FORMAT.format(user.getLastConnectionTime()));
        }
        return obj;
    }
}
