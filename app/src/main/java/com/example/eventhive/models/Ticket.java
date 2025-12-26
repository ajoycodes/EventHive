package com.example.eventhive.models;

public class Ticket {
    private int id;
    private int userId;
    private int eventId;
    private String uniqueCode;

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
}
