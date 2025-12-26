package com.example.eventhive.models;

import java.io.Serializable;

public class Event implements Serializable {
    // Status constants
    public static final String STATUS_ACTIVE = "Active";
    public static final String STATUS_HOLD = "Hold";
    public static final String STATUS_CANCELLED = "Cancelled";

    private int id;
    private String title;
    private String date; // Combined date/time string for simplicity
    private String location;
    private String description;
    private int imageResId;
    private String status; // Active, Hold, Cancelled

    public Event(int id, String title, String date, String location, String description, int imageResId,
            String status) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.location = location;
        this.description = description;
        this.imageResId = imageResId;
        this.status = status != null ? status : STATUS_ACTIVE;
    }

    // Constructor for creating new events (ID auto-generated)
    public Event(String title, String date, String location, String description, int imageResId) {
        this.title = title;
        this.date = date;
        this.location = location;
        this.description = description;
        this.imageResId = imageResId;
        this.status = STATUS_ACTIVE; // Default to active
    }

    // Constructor with status
    public Event(String title, String date, String location, String description, int imageResId, String status) {
        this.title = title;
        this.date = date;
        this.location = location;
        this.description = description;
        this.imageResId = imageResId;
        this.status = status != null ? status : STATUS_ACTIVE;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getStatus() {
        return status != null ? status : STATUS_ACTIVE;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setId(int id) {
        this.id = id;
    }
}