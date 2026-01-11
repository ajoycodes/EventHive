package com.example.eventhive.models;

public class Ticket {
    private String firestoreId; // Document ID
    private String userId; // Firebase UID
    private String eventId; // Firestore Event Document ID
    private String uniqueCode;
    private long purchaseTimestamp;

    // Joined Data (Duplicated for easier display)
    private String eventTitle;
    private String eventDate;
    private String eventLocation;

    public Ticket() {
        // Required for Firestore serialization
    }

    public Ticket(String userId, String eventId, String uniqueCode, long purchaseTimestamp,
            String eventTitle, String eventDate, String eventLocation) {
        this.userId = userId;
        this.eventId = eventId;
        this.uniqueCode = uniqueCode;
        this.purchaseTimestamp = purchaseTimestamp;
        this.eventTitle = eventTitle;
        this.eventDate = eventDate;
        this.eventLocation = eventLocation;
    }

    public String getFirestoreId() {
        return firestoreId;
    }

    public void setFirestoreId(String firestoreId) {
        this.firestoreId = firestoreId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    public long getPurchaseTimestamp() {
        return purchaseTimestamp;
    }

    public void setPurchaseTimestamp(long purchaseTimestamp) {
        this.purchaseTimestamp = purchaseTimestamp;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }
}
