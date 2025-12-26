package com.example.eventhive.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventhive.R;
import com.example.eventhive.activities.EventDetailsActivity;
import com.example.eventhive.models.Event;

import java.util.ArrayList;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private Context context;
    private List<Event> eventList;

    public EventAdapter(Context context, List<Event> list) {
        this.context = context;
        this.eventList = new ArrayList<>(list);
    }

    /**
     * Updates the event list using DiffUtil for efficient updates.
     */
    public void updateEvents(List<Event> newEvents) {
        EventDiffCallback diffCallback = new EventDiffCallback(this.eventList, newEvents);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        this.eventList.clear();
        this.eventList.addAll(newEvents);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.event_card, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event e = eventList.get(position);

        // ===== NULL-SAFE Date Parsing for Badge =====
        String rawDate = e.getDate();
        if (rawDate != null && !rawDate.isEmpty()) {
            String[] parts = rawDate.split(" ");
            if (parts.length >= 2) {
                holder.tvDate.setText(parts[0]); // "12"
                holder.tvMonth.setText(parts[1]); // "Dec"
            } else {
                holder.tvDate.setText(rawDate);
                holder.tvMonth.setText("");
            }
        } else {
            // Handle null/empty date gracefully
            holder.tvDate.setText("--");
            holder.tvMonth.setText("TBA");
        }

        // ===== NULL-SAFE Location =====
        String location = e.getLocation();
        if (location != null && !location.isEmpty()) {
            holder.tvVenue.setText(location);
            holder.tvLocation.setText("Dhaka, Bangladesh"); // Or extract from location if comma-separated
        } else {
            holder.tvVenue.setText("Location TBA");
            holder.tvLocation.setText("");
        }

        // ===== NULL-SAFE Title =====
        String title = e.getTitle();
        if (title != null && !title.isEmpty()) {
            holder.tvTitle.setText(title);
        } else {
            holder.tvTitle.setText("Untitled Event");
        }

        // Placeholder organizer (would come from Event model if added)
        holder.tvOrganizer.setText("Prime Wave Communication");

        // ===== NULL-SAFE Status =====
        String status = e.getStatus();
        if (status == null || status.isEmpty()) {
            status = Event.STATUS_ACTIVE; // Default fallback
        }
        holder.tvStatus.setText(status);

        // Set status badge color based on status
        int statusBgColor;
        if (Event.STATUS_ACTIVE.equals(status)) {
            statusBgColor = context.getResources().getColor(R.color.brand_primary, null);
        } else if (Event.STATUS_HOLD.equals(status)) {
            statusBgColor = context.getResources().getColor(android.R.color.holo_orange_dark, null);
        } else if (Event.STATUS_CANCELLED.equals(status)) {
            statusBgColor = context.getResources().getColor(R.color.destructive, null);
        } else {
            statusBgColor = context.getResources().getColor(R.color.brand_primary, null);
        }
        holder.tvStatus.setBackgroundColor(statusBgColor);

        holder.itemView.setOnClickListener(v -> {
            android.util.Log.d("EventAdapter", "Card clicked for event: " + (title != null ? title : "unknown"));
            Intent intent = new Intent(context, EventDetailsActivity.class);
            intent.putExtra("EVENT", e);
            if (context != null) {
                context.startActivity(intent);
            } else {
                android.util.Log.e("EventAdapter", "Context is null");
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {

        TextView tvDate, tvMonth, tvVenue, tvLocation, tvOrganizer, tvTitle, tvStatus;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDate = itemView.findViewById(R.id.tvDate);
            tvMonth = itemView.findViewById(R.id.tvMonth);
            tvVenue = itemView.findViewById(R.id.tvVenue);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvOrganizer = itemView.findViewById(R.id.tvOrganizer);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}