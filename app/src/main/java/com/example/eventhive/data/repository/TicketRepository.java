package com.example.eventhive.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.example.eventhive.data.local.AppDatabase;
import com.example.eventhive.data.local.dao.TicketDao;
import com.example.eventhive.data.local.entities.TicketEntity;
import com.example.eventhive.models.Ticket;
import com.example.eventhive.utils.TicketCodeGenerator;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Repository for Ticket data operations.
 * Mediates between ViewModel and TicketDao, handles ticket code generation.
 */
public class TicketRepository {

    private final TicketDao ticketDao;
    private final ExecutorService executorService;

    public TicketRepository(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        ticketDao = database.ticketDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    /**
     * Purchases a ticket for a user and event.
     * Generates a unique ticket code.
     * 
     * @param userId   User ID purchasing the ticket
     * @param eventId  Event ID for the ticket
     * @param callback Callback with ticket code if success
     */
    public void purchaseTicket(int userId, int eventId, UserRepository.RepositoryCallback<String> callback) {
        executorService.execute(() -> {
            try {
                // Generate unique ticket code
                String ticketCode;
                int attempts = 0;
                do {
                    ticketCode = TicketCodeGenerator.generateTicketCode(eventId);
                    attempts++;

                    // Safety check to prevent infinite loop
                    if (attempts > 10) {
                        callback.onError("Failed to generate unique ticket code");
                        return;
                    }
                } while (ticketDao.ticketCodeExists(ticketCode));

                // Create and insert ticket
                TicketEntity ticket = new TicketEntity();
                ticket.setUserId(userId);
                ticket.setEventId(eventId);
                ticket.setUniqueCode(ticketCode);

                long ticketId = ticketDao.insertTicket(ticket);

                if (ticketId > 0) {
                    callback.onSuccess(ticketCode);
                } else {
                    callback.onError("Failed to purchase ticket");
                }
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    /**
     * Gets all tickets for a user with event information.
     */
    public LiveData<List<Ticket>> getTicketsForUser(int userId) {
        return Transformations.map(ticketDao.getTicketsWithEventInfo(userId), ticketInfoList -> ticketInfoList.stream()
                .map(TicketDao.TicketWithEventInfo::toTicket).collect(Collectors.toList()));
    }

    /**
     * Gets ticket count for a user.
     */
    public LiveData<Integer> getTicketCountForUser(int userId) {
        return ticketDao.getTicketCountForUser(userId);
    }

    /**
     * Gets ticket count for an event.
     */
    /**
     * Gets ticket count for an event.
     */
    public LiveData<Integer> getTicketCountForEvent(int eventId) {
        return ticketDao.getTicketCountForEvent(eventId);
    }

    /**
     * Search tickets for a user.
     */
    public LiveData<List<Ticket>> searchTickets(int userId, String query, long minDate, long maxDate) {
        return Transformations.map(ticketDao.searchTickets(userId, query, minDate, maxDate),
                ticketInfoList -> ticketInfoList.stream()
                        .map(TicketDao.TicketWithEventInfo::toTicket).collect(Collectors.toList()));
    }
}
