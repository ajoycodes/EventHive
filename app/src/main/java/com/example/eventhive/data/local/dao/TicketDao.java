package com.example.eventhive.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.eventhive.data.local.entities.TicketEntity;
import com.example.eventhive.models.Ticket;

import java.util.List;

/**
 * Data Access Object for Ticket operations.
 * Provides methods to interact with the tickets table.
 */
@Dao
public interface TicketDao {

    @Insert
    long insertTicket(TicketEntity ticket);

    @Query("SELECT * FROM tickets WHERE user_id = :userId")
    LiveData<List<TicketEntity>> getTicketsForUser(int userId);

    @Query("SELECT t.id, t.user_id, t.event_id, t.unique_code, " +
            "e.title as eventTitle, e.date as eventDate, e.location as eventLocation " +
            "FROM tickets t " +
            "INNER JOIN events e ON t.event_id = e.id " +
            "WHERE t.user_id = :userId " +
            "ORDER BY t.id DESC")
    LiveData<List<TicketWithEventInfo>> getTicketsWithEventInfo(int userId);

    @Query("SELECT EXISTS(SELECT 1 FROM tickets WHERE unique_code = :ticketCode LIMIT 1)")
    boolean ticketCodeExists(String ticketCode);

    @Query("SELECT COUNT(*) FROM tickets WHERE user_id = :userId")
    LiveData<Integer> getTicketCountForUser(int userId);

    @Query("SELECT COUNT(*) FROM tickets WHERE event_id = :eventId")
    LiveData<Integer> getTicketCountForEvent(int eventId);

    /**
     * POJO class for ticket with event information.
     * Used for JOIN queries.
     */
    class TicketWithEventInfo {
        public int id;
        public int user_id;
        public int event_id;
        public String unique_code;
        public String eventTitle;
        public String eventDate;
        public String eventLocation;

        public Ticket toTicket() {
            return new Ticket(id, user_id, event_id, unique_code, eventTitle, eventDate, eventLocation);
        }
    }

    /**
     * Search tickets with filters joined with event details.
     *
     * @param userId  Current user ID
     * @param query   Search text for event title
     * @param minDate Minimum event timestamp
     * @param maxDate Maximum event timestamp
     * @return filtered list of tickets with event info
     */
    @Query("SELECT t.id, t.user_id, t.event_id, t.unique_code, " +
            "e.title as eventTitle, e.date as eventDate, e.location as eventLocation " +
            "FROM tickets t " +
            "INNER JOIN events e ON t.event_id = e.id " +
            "WHERE t.user_id = :userId AND " +
            "(:query IS NULL OR e.title LIKE '%' || :query || '%' OR e.location LIKE '%' || :query || '%') AND " +
            "(:minDate = 0 OR e.timestamp >= :minDate) AND " +
            "(:maxDate = 0 OR e.timestamp <= :maxDate) " +
            "ORDER BY t.id DESC")
    LiveData<List<TicketWithEventInfo>> searchTickets(int userId, String query, long minDate, long maxDate);
}
