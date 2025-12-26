package com.example.eventhive.data.local.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Room entity for tickets table.
 * Stores ticket purchases with foreign key relationships to users and events.
 */
@Entity(tableName = "tickets", foreignKeys = {
        @ForeignKey(entity = UserEntity.class, parentColumns = "id", childColumns = "user_id", onDelete = ForeignKey.CASCADE),
        @ForeignKey(entity = EventEntity.class, parentColumns = "id", childColumns = "event_id", onDelete = ForeignKey.CASCADE)
}, indices = {
        @Index(value = "user_id"),
        @Index(value = "event_id"),
        @Index(value = "unique_code", unique = true)
})
public class TicketEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "user_id")
    private int userId;

    @ColumnInfo(name = "event_id")
    private int eventId;

    @ColumnInfo(name = "unique_code")
    private String uniqueCode;

    // Constructors
    public TicketEntity() {
    }

    @Ignore
    public TicketEntity(int id, int userId, int eventId, String uniqueCode) {
        this.id = id;
        this.userId = userId;
        this.eventId = eventId;
        this.uniqueCode = uniqueCode;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }
}
