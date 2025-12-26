package com.example.eventhive.data.local.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.eventhive.models.Event;

/**
 * Room entity for events table.
 * Stores event information including status.
 */
@Entity(tableName = "events")
public class EventEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "date")
    private String date;

    @ColumnInfo(name = "location")
    private String location;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "image_res_id")
    private int imageResId;

    @ColumnInfo(name = "status", defaultValue = "Active")
    private String status; // Active, Hold, Cancelled

    @ColumnInfo(name = "timestamp")
    private long timestamp;

    // Constructors
    public EventEntity() {
    }

    @Ignore
    public EventEntity(int id, String title, String date, String location, String description, int imageResId,
            String status, long timestamp) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.location = location;
        this.description = description;
        this.imageResId = imageResId;
        this.status = status != null ? status : Event.STATUS_ACTIVE;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public String getStatus() {
        return status != null ? status : Event.STATUS_ACTIVE;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    // Conversion methods
    public static EventEntity fromEvent(Event event) {
        // Note: Event model might not have timestamp yet, so we might need to parse it
        // or set to 0
        // Ideally Event model should also be updated, but strictly following "No UI
        // redesign"
        // implies minimize model changes if possible, but for DB it is needed.
        // Let's assume we parse it or set 0 for now in this helper.
        return new EventEntity(
                event.getId(),
                event.getTitle(),
                event.getDate(),
                event.getLocation(),
                event.getDescription(),
                event.getImageResId(),
                event.getStatus(),
                0); // Placeholder, logic will be mainly in Repository
    }

    public Event toEvent() {
        return new Event(id, title, date, location, description, imageResId, status);
    }
}
