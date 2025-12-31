package com.example.eventhive.models;

/**
 * Notification model for in-app notification system.
 * Stores user notifications for various app actions like ticket purchases,
 * event creation, and event status changes.
 */
public class Notification {
    private int id;
    private String title;
    private String message;
    private long timestamp;
    private boolean isRead;
    private int userId; // User who receives this notification
    private int relatedEventId; // Optional: event this notification is about

    // Full constructor
    public Notification(int id, String title, String message, long timestamp, boolean isRead, int userId,
            int relatedEventId) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
        this.isRead = isRead;
        this.userId = userId;
        this.relatedEventId = relatedEventId;
    }

    // Constructor for creating new notifications (ID auto-generated)
    public Notification(String title, String message, long timestamp, boolean isRead, int userId, int relatedEventId) {
        this.title = title;
        this.message = message;
        this.timestamp = timestamp;
        this.isRead = isRead;
        this.userId = userId;
        this.relatedEventId = relatedEventId;
    }

    // Constructor for creating new notifications (simplified)
    public Notification(String title, String message, int userId, int relatedEventId) {
        this.title = title;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
        this.isRead = false; // New notifications are unread by default
        this.userId = userId;
        this.relatedEventId = relatedEventId;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isRead() {
        return isRead;
    }

    public int getUserId() {
        return userId;
    }

    public int getRelatedEventId() {
        return relatedEventId;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
