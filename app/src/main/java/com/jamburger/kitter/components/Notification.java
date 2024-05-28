package com.jamburger.kitter.components;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Notification {
    private String id;
    private String title;
    private String message;
    private String details;
    private Date timestamp;
    private String type;
    private String userId;
    private String userName;

    public Notification() {
        // Default constructor required for calls to DataSnapshot.getValue(Notification.class)
    }

    public Notification(String id, String title, String message, String details, Date timestamp, String type, String userId, String userName) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.details = details;
        this.timestamp = timestamp;
        this.type = type;
        this.userId = userId;
        this.userName = userName;
    }

    // Getters and setters for all fields, including userName
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("title", title);
        map.put("message", message);
        map.put("details", details);
        map.put("timestamp", timestamp);
        map.put("type", type);
        map.put("userId", userId);
        map.put("userName", userName);
        return map;
    }
}
