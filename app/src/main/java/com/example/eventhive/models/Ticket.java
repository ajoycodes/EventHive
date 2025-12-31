package com.example.eventhive.models;

public class Ticket {
    private int id;
    private int userId;
    private int eventId;
    private String uniqueCode;
    private long purchaseTimestamp; // Timestamp when ticket was purchased

    // Joined Data
    private String eventTitle;
    private String eventDate;
    private String eventLocation;

    public Ticket(int id, int userId, int eventId, String uniqueCode, String eventTitle, String eventDate,
            String eventLocation) {
        this.id = id;
        this.userId = userId;
        this.eventId = eventId;
        this.uniqueCode = uniqueCode;
        this.eventTitle = eventTitle;
        this.eventDate = eventDate;
        this.eventLocation = eventLocation;
        this.purchaseTimestamp = 0; // Default for existing tickets
    }

    // Constructor with timestamp
    public Ticket(int id, int userId, int eventId, String uniqueCode, long purchaseTimestamp,
            String eventTitle, String eventDate, String eventLocation) {
        this.id = id;
        this.userId = userId;
        this.eventId = eventId;
        this.uniqueCode = uniqueCode;
        this.purchaseTimestamp = purchaseTimestamp;
        this.eventTitle = eventTitle;
        this.eventDate = eventDate;
        this.eventLocation = eventLocation;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public int getEventId() {
        return eventId;
    }

    public String getUniqueCode() {
        return uniqueCode;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public String getEventDate() {
        return eventDate;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public long getPurchaseTimestamp() {
        return purchaseTimestamp;
    }
}
