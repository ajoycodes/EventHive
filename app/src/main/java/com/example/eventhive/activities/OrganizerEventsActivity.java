package com.example.eventhive.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventhive.R;
import com.example.eventhive.databases.DatabaseHelper;
import com.example.eventhive.models.Event;
import java.util.List;

public class OrganizerEventsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseHelper dbHelper;
    private OrganizerEventsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_events);

        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.rvOrganizerEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Button btnCreateEvent = findViewById(R.id.btnCreateEvent);
        ImageView btnBack = findViewById(R.id.btnBack);

        if (btnCreateEvent != null) {
            btnCreateEvent.setOnClickListener(v -> {
                Intent intent = new Intent(this, CreateEventActivity.class);
                startActivity(intent);
            });
        }

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        loadEvents();
    }

    private void loadEvents() {
        List<Event> eventList = dbHelper.getAllEvents();
        // Filter logic could go here to match only events created by this user
        // For now displaying all events as per previous logic

        adapter = new OrganizerEventsAdapter(eventList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEvents(); // Refresh when returning
    }

    // RecyclerView Adapter
    private class OrganizerEventsAdapter extends RecyclerView.Adapter<OrganizerEventsAdapter.EventViewHolder> {

        private List<Event> eventList;
        private String[] statusOptions = { Event.STATUS_ACTIVE, Event.STATUS_HOLD, Event.STATUS_CANCELLED };

        public OrganizerEventsAdapter(List<Event> eventList) {
            this.eventList = eventList;
        }

        @NonNull
        @Override
        public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_organizer_event, parent, false);
            return new EventViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
            Event event = eventList.get(position);
            holder.tvTitle.setText(event.getTitle());
            holder.tvDate.setText(event.getDate());

            // Setup status spinner
            ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(
                    OrganizerEventsActivity.this,
                    android.R.layout.simple_spinner_item,
                    statusOptions);
            statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.spinnerStatus.setAdapter(statusAdapter);

            // Set current status
            int statusPosition = 0;
            String currentStatus = event.getStatus();
            for (int i = 0; i < statusOptions.length; i++) {
                if (statusOptions[i].equals(currentStatus)) {
                    statusPosition = i;
                    break;
                }
            }
            // Remove listener before setting selection to prevent crash
            holder.spinnerStatus.setOnItemSelectedListener(null);
            holder.spinnerStatus.setSelection(statusPosition, false);

            // Handle status change - use post to set listener after initial setup
            holder.spinnerStatus.post(() -> {
                holder.spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    private boolean isInitializing = true;

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                        if (isInitializing) {
                            isInitializing = false;
                            return;
                        }

                        String newStatus = statusOptions[pos];
                        if (!newStatus.equals(event.getStatus())) {
                            event.setStatus(newStatus);
                            boolean success = dbHelper.updateEvent(event);
                            if (success) {
                                Toast.makeText(OrganizerEventsActivity.this,
                                        "Status updated to " + newStatus, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            });

            holder.btnEdit.setOnClickListener(v -> {
                Intent intent = new Intent(OrganizerEventsActivity.this, EditEventActivity.class);
                intent.putExtra("EVENT", event);
                startActivity(intent);
            });

            holder.btnDelete.setOnClickListener(v -> {
                dbHelper.deleteEvent(event.getId());
                Toast.makeText(OrganizerEventsActivity.this, "Event deleted", Toast.LENGTH_SHORT).show();
                // Refresh list
                eventList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, eventList.size());
            });
        }

        @Override
        public int getItemCount() {
            return eventList.size();
        }

        class EventViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvDate;
            ImageView btnEdit, btnDelete;
            Spinner spinnerStatus;

            public EventViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.tvEventTitle);
                tvDate = itemView.findViewById(R.id.tvEventDate);
                btnEdit = itemView.findViewById(R.id.btnEdit);
                btnDelete = itemView.findViewById(R.id.btnDelete);
                spinnerStatus = itemView.findViewById(R.id.spinnerStatus);
            }
        }
    }
}
