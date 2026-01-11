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

    private com.google.firebase.firestore.FirebaseFirestore db;
    private com.google.firebase.auth.FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_events);

        // Initialize Firestore & Auth
        db = com.google.firebase.firestore.FirebaseFirestore.getInstance();
        auth = com.google.firebase.auth.FirebaseAuth.getInstance();

        dbHelper = new DatabaseHelper(this); // Keep for safety if needed
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
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = auth.getCurrentUser().getUid();

        // Query Firestore for events created by this organizer
        db.collection("events")
                .whereEqualTo("organizerId", uid)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    java.util.List<Event> eventList = new java.util.ArrayList<>();
                    for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots) {
                        Event e = doc.toObject(Event.class);
                        if (e != null) {
                            e.setFirestoreId(doc.getId());
                            eventList.add(e);
                        }
                    }
                    adapter = new OrganizerEventsAdapter(eventList);
                    recyclerView.setAdapter(adapter);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading events: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
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
            if (currentStatus != null) {
                for (int i = 0; i < statusOptions.length; i++) {
                    if (statusOptions[i].equals(currentStatus)) {
                        statusPosition = i;
                        break;
                    }
                }
            }
            // Remove listener before setting selection to prevent crash/loop
            holder.spinnerStatus.setOnItemSelectedListener(null);
            holder.spinnerStatus.setSelection(statusPosition, false);

            // Handle status change
            holder.spinnerStatus.post(() -> {
                holder.spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    boolean isInitializing = true;

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                        if (isInitializing) {
                            isInitializing = false;
                            return;
                        }
                        String newStatus = statusOptions[pos];
                        if (!newStatus.equals(event.getStatus())) {
                            if (event.getFirestoreId() != null) {
                                db.collection("events").document(event.getFirestoreId())
                                        .update("status", newStatus)
                                        .addOnSuccessListener(aVoid -> {
                                            event.setStatus(newStatus);
                                            Toast.makeText(OrganizerEventsActivity.this, "Status updated",
                                                    Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> Toast.makeText(OrganizerEventsActivity.this,
                                                "Update failed", Toast.LENGTH_SHORT).show());
                            }
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            });

            holder.btnEdit.setOnClickListener(v -> {
                // EditEventActivity needs refactor to work with Firestore
                // For now, disabling or showing toast might be safer, but user kept files
                // minimal.
                // Assuming EditEventActivity uses serializable Event logic, passing the object
                // *might* work for UI prepopulation
                // but saving will fail if it uses SQLite.
                // Given constraints, I'll pass the intent but add a warning toast or just let
                // it try.
                // Or better, just Toast "Edit feature coming soon" to prevent crash/data
                // corruption.
                Toast.makeText(OrganizerEventsActivity.this, "Edit feature coming soon (Firestore migration)",
                        Toast.LENGTH_SHORT).show();

                // Intent intent = new Intent(OrganizerEventsActivity.this,
                // EditEventActivity.class);
                // intent.putExtra("EVENT", event);
                // startActivity(intent);
            });

            holder.btnDelete.setOnClickListener(v -> {
                if (event.getFirestoreId() != null) {
                    db.collection("events").document(event.getFirestoreId())
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(OrganizerEventsActivity.this, "Event deleted", Toast.LENGTH_SHORT)
                                        .show();
                                eventList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, eventList.size());
                            })
                            .addOnFailureListener(e -> Toast
                                    .makeText(OrganizerEventsActivity.this, "Delete failed", Toast.LENGTH_SHORT)
                                    .show());
                }
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
