package com.example.eventhive.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.eventhive.data.repository.EventRepository;
import com.example.eventhive.models.Event;

import java.util.List;

/**
 * ViewModel for Event operations.
 * Manages event data and provides LiveData for UI observation.
 */
public class EventViewModel extends AndroidViewModel {

    private final EventRepository eventRepository;
    private final MutableLiveData<FilterParams> filterParams = new MutableLiveData<>(new FilterParams());
    private final LiveData<List<Event>> events; // Replaces allEvents with filtered list
    private final MutableLiveData<OperationResult> operationResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public EventViewModel(@NonNull Application application) {
        super(application);
        eventRepository = new EventRepository(application);

        // Populate timestamps if missing
        eventRepository.ensureTimestampsPopulated();

        // React to filter changes
        events = androidx.lifecycle.Transformations.switchMap(filterParams,
                params -> eventRepository.searchEvents(params.query, params.location, params.minDate, params.maxDate));
    }

    /**
     * Updates search filters.
     * 
     * @param query      Title search
     * @param location   Location search
     * @param filterType "UPCOMING", "PAST", or "ALL"
     */
    public void setFilters(String query, String location, String filterType) {
        long minDate = 0;
        long maxDate = 0;
        long currentTime = System.currentTimeMillis();

        if ("UPCOMING".equals(filterType)) {
            minDate = currentTime;
        } else if ("PAST".equals(filterType)) {
            maxDate = currentTime;
        }

        FilterParams params = new FilterParams();
        params.query = (query != null && !query.isEmpty()) ? query : null;
        params.location = (location != null && !location.isEmpty()) ? location : null;
        params.minDate = minDate;
        params.maxDate = maxDate;

        filterParams.setValue(params);
    }

    // ... create/update/delete methods ...

    /**
     * Creates a new event.
     */
    public void createEvent(String title, String date, String location, String description) {
        // Validate inputs
        if (title == null || title.trim().isEmpty()) {
            operationResult.setValue(new OperationResult(false, "Title is required"));
            return;
        }

        if (date == null || date.trim().isEmpty()) {
            operationResult.setValue(new OperationResult(false, "Date is required"));
            return;
        }

        if (location == null || location.trim().isEmpty()) {
            operationResult.setValue(new OperationResult(false, "Location is required"));
            return;
        }

        if (description == null || description.trim().isEmpty()) {
            operationResult.setValue(new OperationResult(false, "Description is required"));
            return;
        }

        isLoading.setValue(true);

        Event event = new Event(title, date, location, description, 0);

        eventRepository.createEvent(event,
                new com.example.eventhive.data.repository.UserRepository.RepositoryCallback<Long>() {
                    @Override
                    public void onSuccess(Long eventId) {
                        isLoading.postValue(false);
                        operationResult.postValue(new OperationResult(true, "Event created successfully"));
                    }

                    @Override
                    public void onError(String error) {
                        isLoading.postValue(false);
                        operationResult.postValue(new OperationResult(false, error));
                    }
                });
    }

    /**
     * Updates an existing event.
     */
    public void updateEvent(Event event) {
        if (event == null) {
            operationResult.setValue(new OperationResult(false, "Invalid event"));
            return;
        }

        isLoading.setValue(true);

        eventRepository.updateEvent(event,
                new com.example.eventhive.data.repository.UserRepository.RepositoryCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean success) {
                        isLoading.postValue(false);
                        if (success) {
                            operationResult.postValue(new OperationResult(true, "Event updated successfully"));
                        } else {
                            operationResult.postValue(new OperationResult(false, "Failed to update event"));
                        }
                    }

                    @Override
                    public void onError(String error) {
                        isLoading.postValue(false);
                        operationResult.postValue(new OperationResult(false, error));
                    }
                });
    }

    /**
     * Deletes an event.
     */
    public void deleteEvent(int eventId) {
        isLoading.setValue(true);

        eventRepository.deleteEvent(eventId,
                new com.example.eventhive.data.repository.UserRepository.RepositoryCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        isLoading.postValue(false);
                        operationResult.postValue(new OperationResult(true, "Event deleted successfully"));
                    }

                    @Override
                    public void onError(String error) {
                        isLoading.postValue(false);
                        operationResult.postValue(new OperationResult(false, error));
                    }
                });
    }

    /**
     * Gets events by status.
     */
    public LiveData<List<Event>> getEventsByStatus(String status) {
        return eventRepository.getEventsByStatus(status);
    }

    /**
     * Gets active event count.
     */
    public void getActiveEventCount(
            com.example.eventhive.data.repository.UserRepository.RepositoryCallback<Integer> callback) {
        eventRepository.getActiveEventCount(callback);
    }

    // LiveData getters
    public LiveData<List<Event>> getAllEvents() {
        return events; // Returns the filtered list
    }

    public LiveData<OperationResult> getOperationResult() {
        return operationResult;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    /**
     * Result class for operations.
     */
    public static class OperationResult {
        public final boolean success;
        public final String message;

        public OperationResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }

    /**
     * Helper class to hold filter parameters.
     */
    private static class FilterParams {
        String query = null;
        String location = null;
        long minDate = 0;
        long maxDate = 0;
    }
}
