package com.example.eventhive.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.example.eventhive.models.Event;

import java.util.List;

/**
 * DiffUtil callback for Event list to efficiently update RecyclerView.
 */
public class EventDiffCallback extends DiffUtil.Callback {

    private final List<Event> oldList;
    private final List<Event> newList;

    public EventDiffCallback(List<Event> oldList, List<Event> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        // Compare by event ID
        return oldList.get(oldItemPosition).getId() == newList.get(newItemPosition).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Event oldEvent = oldList.get(oldItemPosition);
        Event newEvent = newList.get(newItemPosition);

        // Compare all relevant fields
        return oldEvent.getTitle().equals(newEvent.getTitle()) &&
                oldEvent.getDate().equals(newEvent.getDate()) &&
                oldEvent.getLocation().equals(newEvent.getLocation()) &&
                oldEvent.getStatus().equals(newEvent.getStatus()) &&
                oldEvent.getDescription().equals(newEvent.getDescription());
    }
}
