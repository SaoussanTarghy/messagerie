package com.isga.chat.common;

import java.io.Serializable;

public class NetworkPackage implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Type {
        LOGIN_REQUEST, LOGIN_RESPONSE,
        LOGOUT_REQUEST,
        MESSAGE_SEND, MESSAGE_BROADCAST,
        STATUS_CHANGE_REQUEST, STATUS_CHANGE_BROADCAST,
        USER_LIST_UPDATE,
        BAN_REQUEST, BAN_BROADCAST,
        ERROR
    }

    private Type type;
    private Object payload;

    public NetworkPackage(Type type, Object payload) {
        this.type = type;
        this.payload = payload;
    }

    public Type getType() { return type; }
    public Object getPayload() { return payload; }
}
