package com.example.eventhive.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.eventhive.data.repository.TicketRepository;
import com.example.eventhive.models.Ticket;

import java.util.List;

/**
 * ViewModel for Ticket operations.
 * Manages ticket purchases and user ticket retrieval.
 */
public class TicketViewModel extends AndroidViewModel {

    private final TicketRepository ticketRepository;
    private final MutableLiveData<TicketFilterParams> filterParams = new MutableLiveData<>();
    private final LiveData<List<Ticket>> tickets; // Replaces direct call
    private final MutableLiveData<PurchaseResult> purchaseResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public TicketViewModel(@NonNull Application application) {
        super(application);
        ticketRepository = new TicketRepository(application);

        // React to filter changes
        tickets = androidx.lifecycle.Transformations.switchMap(filterParams,
                params -> ticketRepository.searchTickets(params.userId, params.query, params.minDate, params.maxDate));
    }

    /**
     * Sets filters for tickets. Must be called with userId at least once.
     */
    public void setFilters(int userId, String query, String filterType) {
        long minDate = 0;
        long maxDate = 0;
        long currentTime = System.currentTimeMillis();

        if ("UPCOMING".equals(filterType)) {
            minDate = currentTime;
        } else if ("PAST".equals(filterType)) {
            maxDate = currentTime;
        }

        TicketFilterParams params = new TicketFilterParams();
        params.userId = userId;
        params.query = (query != null && !query.isEmpty()) ? query : null;
        params.minDate = minDate;
        params.maxDate = maxDate;

        filterParams.setValue(params);
    }

    /**
     * Purchases a ticket for a user and event.
     */
    public void purchaseTicket(int userId, int eventId) {
        if (userId <= 0 || eventId <= 0) {
            purchaseResult.setValue(new PurchaseResult(false, "Invalid user or event", null));
            return;
        }

        isLoading.setValue(true);

        ticketRepository.purchaseTicket(userId, eventId,
                new com.example.eventhive.data.repository.UserRepository.RepositoryCallback<String>() {
                    @Override
                    public void onSuccess(String ticketCode) {
                        isLoading.postValue(false);
                        purchaseResult.postValue(new PurchaseResult(true, "Ticket purchased successfully", ticketCode));
                    }

                    @Override
                    public void onError(String error) {
                        isLoading.postValue(false);
                        purchaseResult.postValue(new PurchaseResult(false, error, null));
                    }
                });
    }

    /**
     * Gets all tickets for a user.
     * NOW: This just updates the filter param which triggers the LiveData update.
     * Compatible with old calls if we just return the LiveData.
     */
    public LiveData<List<Ticket>> getTicketsForUser(int userId) {
        // If not initialized or user changed, update params
        if (filterParams.getValue() == null || filterParams.getValue().userId != userId) {
            setFilters(userId, null, "ALL");
        }
        return tickets;
    }

    /**
     * Gets ticket count for a user.
     */
    public LiveData<Integer> getTicketCountForUser(int userId) {
        return ticketRepository.getTicketCountForUser(userId);
    }

    // LiveData getters
    public LiveData<PurchaseResult> getPurchaseResult() {
        return purchaseResult;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    /**
     * Result class for ticket purchase operations.
     */
    public static class PurchaseResult {
        public final boolean success;
        public final String message;
        public final String ticketCode;

        public PurchaseResult(boolean success, String message, String ticketCode) {
            this.success = success;
            this.message = message;
            this.ticketCode = ticketCode;
        }
    }

    /**
     * Helper class for ticket parameters.
     */
    private static class TicketFilterParams {
        int userId;
        String query = null;
        long minDate = 0;
        long maxDate = 0;
    }
}
