package com.example.eventhive.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventhive.R;
import com.example.eventhive.models.Event;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    Context context;
    List<Event> eventList;

    public EventAdapter(Context context, List<Event> list) {
        this.context = context;
        this.eventList = list;
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

        holder.tvDate.setText(e.getDate());
        holder.tvMonth.setText(e.getMonth());
        holder.tvVenue.setText(e.getVenue());
        holder.tvLocation.setText(e.getVenue());
        holder.tvOrganizer.setText(e.getOrganizer());
        holder.tvTitle.setText(e.getTitle());
        holder.cardBg.setBackgroundColor(e.getColor());
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {

        TextView tvDate, tvMonth, tvVenue, tvLocation, tvOrganizer, tvTitle;
        LinearLayout cardBg;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);

            cardBg = itemView.findViewById(R.id.cardBg);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvMonth = itemView.findViewById(R.id.tvMonth);
            tvVenue = itemView.findViewById(R.id.tvVenue);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvOrganizer = itemView.findViewById(R.id.tvOrganizer);
            tvTitle = itemView.findViewById(R.id.tvTitle);
        }
    }
}