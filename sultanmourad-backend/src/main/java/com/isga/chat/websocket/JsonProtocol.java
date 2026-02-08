package com.isga.chat.websocket;

import com.isga.chat.common.Message;
import com.isga.chat.common.User;
import com.isga.chat.dao.ContactDAO.ContactInfo;
import com.isga.chat.dao.PrivateMessageDAO.PrivateMessage;
import com.isga.chat.dao.PrivateMessageDAO.ConversationSummary;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for JSON ↔ Object conversion.
 * Extended to support private messaging and contacts.
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
                Map<String, Object> payloadMap = new HashMap<>();
                for (String key : payloadObj.keySet()) {
                    payloadMap.put(key, payloadObj.get(key));
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

    // Create JSON for register response
    public static String registerResponse(User user) {
        JSONObject obj = new JSONObject();
        obj.put("type", "REGISTER_RESPONSE");
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

    // Create JSON for group message broadcast
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

    // ==================== NEW: Private Messaging ====================

    // Create JSON for private message
    public static String privateMessageResponse(PrivateMessage msg) {
        JSONObject obj = new JSONObject();
        obj.put("type", "PRIVATE_MESSAGE");
        obj.put("payload", privateMessageToJson(msg));
        return obj.toString();
    }

    // Create JSON for conversation history
    public static String conversationHistory(List<PrivateMessage> messages, int otherUserId) {
        JSONObject obj = new JSONObject();
        obj.put("type", "CONVERSATION_HISTORY");

        JSONObject payload = new JSONObject();
        payload.put("otherUserId", otherUserId);
        
        JSONArray messagesArray = new JSONArray();
        for (PrivateMessage msg : messages) {
            messagesArray.put(privateMessageToJson(msg));
        }
        payload.put("messages", messagesArray);
        obj.put("payload", payload);
        return obj.toString();
    }

    // Create JSON for conversations list
    public static String conversationsList(List<ConversationSummary> conversations) {
        JSONObject obj = new JSONObject();
        obj.put("type", "CONVERSATIONS_LIST");

        JSONArray conversationsArray = new JSONArray();
        for (ConversationSummary conv : conversations) {
            JSONObject convObj = new JSONObject();
            convObj.put("otherUserId", conv.getOtherUserId());
            convObj.put("otherUsername", conv.getOtherUsername());
            convObj.put("otherStatus", conv.getOtherStatus());
            convObj.put("lastMessage", conv.getLastMessage());
            if (conv.getLastMessageTime() != null) {
                convObj.put("lastMessageTime", DATE_FORMAT.format(conv.getLastMessageTime()));
            }
            convObj.put("unreadCount", conv.getUnreadCount());
            conversationsArray.put(convObj);
        }
        obj.put("payload", conversationsArray);
        return obj.toString();
    }

    // ==================== NEW: Contacts ====================

    // Create JSON for contacts list
    public static String contactsList(List<ContactInfo> contacts) {
        JSONObject obj = new JSONObject();
        obj.put("type", "CONTACTS_LIST");

        JSONArray contactsArray = new JSONArray();
        for (ContactInfo contact : contacts) {
            JSONObject contactObj = new JSONObject();
            contactObj.put("contactId", contact.getContactId());
            contactObj.put("contactName", contact.getContactName());
            contactObj.put("userId", contact.getUserId());
            contactObj.put("username", contact.getUsername());
            contactObj.put("firstName", contact.getFirstName());
            contactObj.put("lastName", contact.getLastName());
            contactObj.put("email", contact.getEmail());
            contactObj.put("status", contact.getStatus());
            contactObj.put("permission", contact.getPermission());
            contactObj.put("isBanned", contact.isBanned());
            contactObj.put("unreadCount", contact.getUnreadCount());
            contactsArray.put(contactObj);
        }
        obj.put("payload", contactsArray);
        return obj.toString();
    }

    // Create JSON for contact added response
    public static String contactAdded(ContactInfo contact) {
        JSONObject obj = new JSONObject();
        obj.put("type", "CONTACT_ADDED");
        
        JSONObject contactObj = new JSONObject();
        contactObj.put("contactId", contact.getContactId());
        contactObj.put("contactName", contact.getContactName());
        contactObj.put("userId", contact.getUserId());
        contactObj.put("username", contact.getUsername());
        contactObj.put("status", contact.getStatus());
        obj.put("payload", contactObj);
        return obj.toString();
    }

    // Create JSON for user search results
    public static String userSearchResults(List<User> users) {
        JSONObject obj = new JSONObject();
        obj.put("type", "USER_SEARCH_RESULTS");

        JSONArray usersArray = new JSONArray();
        for (User user : users) {
            JSONObject userObj = new JSONObject();
            userObj.put("id", user.getId());
            userObj.put("username", user.getUsername());
            userObj.put("firstName", user.getFirstName());
            userObj.put("lastName", user.getLastName());
            userObj.put("email", user.getEmail());
            userObj.put("status", user.getStatus());
            usersArray.put(userObj);
        }
        obj.put("payload", usersArray);
        return obj.toString();
    }

    // Create JSON for success response
    public static String successResponse(String message) {
        JSONObject obj = new JSONObject();
        obj.put("type", "SUCCESS");
        obj.put("payload", message);
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

    // Convert PrivateMessage to JSONObject
    private static JSONObject privateMessageToJson(PrivateMessage msg) {
        JSONObject obj = new JSONObject();
        obj.put("id", msg.getId());
        obj.put("senderId", msg.getSenderId());
        obj.put("receiverId", msg.getReceiverId());
        obj.put("senderUsername", msg.getSenderUsername());
        obj.put("receiverUsername", msg.getReceiverUsername());
        obj.put("content", msg.getContent());
        obj.put("isRead", msg.isRead());
        if (msg.getTimestamp() != null) {
            obj.put("timestamp", DATE_FORMAT.format(msg.getTimestamp()));
        }
        return obj;
    }
}
