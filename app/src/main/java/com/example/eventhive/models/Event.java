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
    private int imageResId; // Legacy field, deprecated in favor of coverImagePath
    private String status; // Active, Hold, Cancelled

    // New fields for enhanced event management
    private double ticketPrice;
    private int ticketQuantity;
    private String coverImagePath; // File path to cover image
    private String galleryImagePaths; // Comma-separated file paths for gallery images
    private String eventType; // Event category: Concert, Seminar, Festival, etc.

    public Event(int id, String title, String date, String location, String description, int imageResId,
            String status) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.location = location;
        this.description = description;
        this.imageResId = imageResId;
        this.status = status != null ? status : STATUS_ACTIVE;
        // Initialize new fields with defaults
        this.ticketPrice = 0.0;
        this.ticketQuantity = 0;
        this.coverImagePath = "";
        this.galleryImagePaths = "";
        this.eventType = "Other";
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
        // Initialize new fields with defaults
        this.ticketPrice = 0.0;
        this.ticketQuantity = 0;
        this.coverImagePath = "";
        this.galleryImagePaths = "";
        this.eventType = "Other";
    }

    // Comprehensive constructor with all fields including new ones
    public Event(int id, String title, String date, String location, String description,
            String status, double ticketPrice, int ticketQuantity,
            String coverImagePath, String galleryImagePaths, String eventType) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.location = location;
        this.description = description;
        this.imageResId = 0; // Legacy field, not used
        this.status = status != null ? status : STATUS_ACTIVE;
        this.ticketPrice = ticketPrice;
        this.ticketQuantity = ticketQuantity;
        this.coverImagePath = coverImagePath;
        this.galleryImagePaths = galleryImagePaths;
        this.eventType = eventType;
    }

    // Constructor for creating new events with all fields
    public Event(String title, String date, String location, String description,
            String status, double ticketPrice, int ticketQuantity,
            String coverImagePath, String galleryImagePaths, String eventType) {
        this.title = title;
        this.date = date;
        this.location = location;
        this.description = description;
        this.imageResId = 0; // Legacy field
        this.status = status != null ? status : STATUS_ACTIVE;
        this.ticketPrice = ticketPrice;
        this.ticketQuantity = ticketQuantity;
        this.coverImagePath = coverImagePath;
        this.galleryImagePaths = galleryImagePaths;
        this.eventType = eventType;
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

    // Getters and setters for new fields
    public double getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(double ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public int getTicketQuantity() {
        return ticketQuantity;
    }

    public void setTicketQuantity(int ticketQuantity) {
        this.ticketQuantity = ticketQuantity;
    }

    public String getCoverImagePath() {
        return coverImagePath;
    }

    public void setCoverImagePath(String coverImagePath) {
        this.coverImagePath = coverImagePath;
    }

    public String getGalleryImagePaths() {
        return galleryImagePaths;
    }

    public void setGalleryImagePaths(String galleryImagePaths) {
        this.galleryImagePaths = galleryImagePaths;
    }

    public String getEventType() {
        return eventType != null && !eventType.isEmpty() ? eventType : "Other";
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
}