package com.jamburger.kitter.components;

import com.google.firebase.Timestamp;

public class Notification {
    private String id;
    private String title;
    private String message;
    private String details;
    private String userName;
    private Timestamp timestamp;

    // Default constructor required for calls to DataSnapshot.getValue(Notification.class)
    public Notification() {
    }

    public Notification(String id, String title, String message, String details, String userName, Timestamp timestamp) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.details = details;
        this.userName = userName;
        this.timestamp = timestamp;
    }

    // Getters and setters for each field
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
