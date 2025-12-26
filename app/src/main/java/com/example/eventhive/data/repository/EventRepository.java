package com.example.eventhive.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.example.eventhive.data.local.AppDatabase;
import com.example.eventhive.data.local.dao.EventDao;
import com.example.eventhive.data.local.entities.EventEntity;
import com.example.eventhive.models.Event;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Repository for Event data operations.
 * Mediates between ViewModel and EventDao.
 */
public class EventRepository {

    private final EventDao eventDao;
    private final ExecutorService executorService;
    private final LiveData<List<Event>> allEvents;

    public EventRepository(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        eventDao = database.eventDao();
        executorService = Executors.newSingleThreadExecutor();

        // Transform LiveData<List<EventEntity>> to LiveData<List<Event>>
        allEvents = Transformations.map(eventDao.getAllEvents(),
                entities -> entities.stream().map(EventEntity::toEvent).collect(Collectors.toList()));
    }

    /**
     * Creates a new event.
     */
    /**
     * Creates a new event.
     */
    public void createEvent(Event event, UserRepository.RepositoryCallback<Long> callback) {
        executorService.execute(() -> {
            try {
                EventEntity entity = EventEntity.fromEvent(event);

                // Set timestamp for ordering
                if (event.getDate() != null) {
                    entity.setTimestamp(parseDateStringToTimestamp(event.getDate()));
                }

                long eventId = eventDao.insertEvent(entity);
                callback.onSuccess(eventId);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    /**
     * Updates an existing event.
     */
    public void updateEvent(Event event, UserRepository.RepositoryCallback<Boolean> callback) {
        executorService.execute(() -> {
            try {
                EventEntity entity = EventEntity.fromEvent(event);

                // Update timestamp for ordering
                if (event.getDate() != null) {
                    entity.setTimestamp(parseDateStringToTimestamp(event.getDate()));
                }

                int rowsAffected = eventDao.updateEvent(entity);
                callback.onSuccess(rowsAffected > 0);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    /**
     * Deletes an event by ID.
     */
    public void deleteEvent(int eventId, UserRepository.RepositoryCallback<Void> callback) {
        executorService.execute(() -> {
            try {
                eventDao.deleteEventById(eventId);
                callback.onSuccess(null);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    /**
     * Gets all events as LiveData.
     */
    public LiveData<List<Event>> getAllEvents() {
        return allEvents;
    }

    /**
     * Gets events by status as LiveData.
     */
    public LiveData<List<Event>> getEventsByStatus(String status) {
        return Transformations.map(eventDao.getEventsByStatus(status),
                entities -> entities.stream().map(EventEntity::toEvent).collect(Collectors.toList()));
    }

    /**
     * Gets event by ID.
     */
    public void getEventById(int eventId, UserRepository.RepositoryCallback<Event> callback) {
        executorService.execute(() -> {
            try {
                EventEntity entity = eventDao.getEventById(eventId);
                if (entity != null) {
                    callback.onSuccess(entity.toEvent());
                } else {
                    callback.onError("Event not found");
                }
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    /**
     * Gets active event count synchronously (for dashboard stats).
     */
    public void getActiveEventCount(UserRepository.RepositoryCallback<Integer> callback) {
        executorService.execute(() -> {
            try {
                int count = eventDao.getActiveEventCountSync();
                callback.onSuccess(count);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    /**
     * Gets event count by status as LiveData.
     */
    /**
     * Gets event count by status as LiveData.
     */
    public LiveData<Integer> getEventCountByStatus(String status) {
        return eventDao.getEventCountByStatus(status);
    }

    /**
     * Search events with filters.
     */
    public LiveData<List<Event>> searchEvents(String query, String location, long minDate, long maxDate) {
        return Transformations.map(eventDao.searchEvents(query, location, minDate, maxDate),
                entities -> entities.stream().map(EventEntity::toEvent).collect(Collectors.toList()));
    }

    /**
     * Updates timestamps for legacy events that have 0 timestamp.
     * This is a migration helper.
     */
    public void ensureTimestampsPopulated() {
        executorService.execute(() -> {
            List<EventEntity> events = eventDao.getAllEventsSync();
            for (EventEntity event : events) {
                if (event.getTimestamp() == 0 && event.getDate() != null) {
                    long timestamp = parseDateStringToTimestamp(event.getDate());
                    if (timestamp > 0) {
                        event.setTimestamp(timestamp);
                        eventDao.updateEvent(event);
                    }
                }
            }
        });
    }

    private long parseDateStringToTimestamp(String dateStr) {
        try {
            // Format: "12 Dec - 10 PM"
            // We need to add a year to make it parseable
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            int currentYear = calendar.get(java.util.Calendar.YEAR);
            String dateWithYear = dateStr + " " + currentYear; // "12 Dec - 10 PM 2024"

            // Simple parsing strategy using standard SimpleDateFormat
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("d MMM - h a yyyy",
                    java.util.Locale.ENGLISH);
            java.util.Date date = sdf.parse(dateWithYear);

            if (date != null) {
                return date.getTime();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
