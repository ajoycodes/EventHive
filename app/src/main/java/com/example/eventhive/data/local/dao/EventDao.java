package com.example.eventhive.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.eventhive.data.local.entities.EventEntity;

import java.util.List;

/**
 * Data Access Object for Event operations.
 * Provides methods to interact with the events table.
 */
@Dao
public interface EventDao {

    @Insert
    long insertEvent(EventEntity event);

    @Update
    int updateEvent(EventEntity event);

    @Delete
    void deleteEvent(EventEntity event);

    @Query("DELETE FROM events WHERE id = :eventId")
    void deleteEventById(int eventId);

    @Query("SELECT * FROM events WHERE id = :eventId LIMIT 1")
    EventEntity getEventById(int eventId);

    @Query("SELECT * FROM events")
    LiveData<List<EventEntity>> getAllEvents();

    @Query("SELECT * FROM events")
    List<EventEntity> getAllEventsSync();

    @Query("SELECT * FROM events WHERE status = :status")
    LiveData<List<EventEntity>> getEventsByStatus(String status);

    @Query("SELECT COUNT(*) FROM events")
    LiveData<Integer> getEventCount();

    @Query("SELECT COUNT(*) FROM events WHERE status = :status")
    LiveData<Integer> getEventCountByStatus(String status);

    @Query("SELECT COUNT(*) FROM events WHERE status = 'Active'")
    int getActiveEventCountSync();

    /**
     * Search events with filters.
     * 
     * @param query    Search text for title (case-insensitive)
     * @param location Search text for location (case-insensitive)
     * @param minDate  Minimum timestamp (inclusive)
     * @param maxDate  Maximum timestamp (inclusive)
     * @return filtered list of events
     */
    @Query("SELECT * FROM events WHERE " +
            "(:query IS NULL OR title LIKE '%' || :query || '%') AND " +
            "(:location IS NULL OR location LIKE '%' || :location || '%') AND " +
            "(:minDate = 0 OR timestamp >= :minDate) AND " +
            "(:maxDate = 0 OR timestamp <= :maxDate) " +
            "ORDER BY timestamp ASC")
    LiveData<List<EventEntity>> searchEvents(String query, String location, long minDate, long maxDate);
}
