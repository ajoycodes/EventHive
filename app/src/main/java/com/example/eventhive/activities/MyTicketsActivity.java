package com.example.eventhive.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventhive.R;
import com.example.eventhive.databases.DatabaseHelper;
import com.example.eventhive.models.Ticket;
import com.example.eventhive.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;

public class MyTicketsActivity extends AppCompatActivity {

    private static final String TAG = "MyTicketsActivity";
    private RecyclerView ticketRecyclerView;
    private DatabaseHelper dbHelper;
    private SessionManager session;
    private TicketAdapter adapter;
    private View tvEmptyState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_my_tickets);

            ticketRecyclerView = findViewById(R.id.ticketRecyclerView);
            tvEmptyState = findViewById(R.id.tvEmptyState);
            dbHelper = new DatabaseHelper(this);
            session = SessionManager.getInstance(this);

            if (ticketRecyclerView != null) {
                ticketRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            }

            ImageView btnBack = findViewById(R.id.btnBack);
            if (btnBack != null) {
                btnBack.setOnClickListener(v -> finish());
            }

            loadTickets();

            // Search setup
            android.widget.EditText etSearch = findViewById(R.id.etSearch);
            etSearch.addTextChangedListener(new android.text.TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (adapter != null) {
                        adapter.filter(s.toString());
                    }
                }

                @Override
                public void afterTextChanged(android.text.Editable s) {
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Error loading tickets", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadTickets() {
        try {
            String userUid = session.getUserUid();

            // Show loading if possible
            if (tvEmptyState != null)
                tvEmptyState.setVisibility(View.GONE);
            if (ticketRecyclerView != null)
                ticketRecyclerView.setVisibility(View.GONE);

            // Fetch from Firestore
            com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore
                    .getInstance();
            db.collection("tickets")
                    .whereEqualTo("userId", userUid)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<Ticket> tickets = new ArrayList<>();
                            for (com.google.firebase.firestore.QueryDocumentSnapshot document : task.getResult()) {
                                try {
                                    Ticket ticket = document.toObject(Ticket.class);
                                    ticket.setFirestoreId(document.getId());
                                    tickets.add(ticket);
                                } catch (Exception e) {
                                    Log.e(TAG, "Error parsing ticket: " + e.getMessage());
                                }
                            }

                            // Update UI
                            if (tickets.isEmpty()) {
                                if (ticketRecyclerView != null)
                                    ticketRecyclerView.setVisibility(View.GONE);
                                if (tvEmptyState != null)
                                    tvEmptyState.setVisibility(View.VISIBLE);
                            } else {
                                if (ticketRecyclerView != null)
                                    ticketRecyclerView.setVisibility(View.VISIBLE);
                                if (tvEmptyState != null)
                                    tvEmptyState.setVisibility(View.GONE);

                                adapter = new TicketAdapter(tickets);
                                if (ticketRecyclerView != null) {
                                    ticketRecyclerView.setAdapter(adapter);
                                }
                            }

                        } else {
                            Log.e(TAG, "Error getting tickets: ", task.getException());
                            Toast.makeText(MyTicketsActivity.this, "Failed to load tickets", Toast.LENGTH_SHORT).show();
                            if (tvEmptyState != null)
                                tvEmptyState.setVisibility(View.VISIBLE);
                        }
                    });

        } catch (Exception e) {
            Log.e(TAG, "Error loading tickets: " + e.getMessage(), e);
            Toast.makeText(this, "Error loading tickets", Toast.LENGTH_SHORT).show();
        }
    }

    // RecyclerView Adapter for Tickets
    private class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {
        private List<Ticket> originalList; // Source of truth
        private List<Ticket> filteredList; // Displayed list

        public TicketAdapter(List<Ticket> tickets) {
            this.originalList = tickets != null ? tickets : new ArrayList<>();
            this.filteredList = new ArrayList<>(this.originalList);
        }

        // Filter Logic
        public void filter(String query) {
            filteredList.clear();
            if (query == null || query.trim().isEmpty()) {
                filteredList.addAll(originalList);
            } else {
                String lowerCaseQuery = query.toLowerCase().trim();
                for (Ticket ticket : originalList) {
                    // Search by: Title, Location, Date
                    boolean matchesTitle = ticket.getEventTitle() != null &&
                            ticket.getEventTitle().toLowerCase().contains(lowerCaseQuery);
                    boolean matchesLocation = ticket.getEventLocation() != null &&
                            ticket.getEventLocation().toLowerCase().contains(lowerCaseQuery);
                    boolean matchesDate = ticket.getEventDate() != null &&
                            ticket.getEventDate().toLowerCase().contains(lowerCaseQuery);

                    if (matchesTitle || matchesLocation || matchesDate) {
                        filteredList.add(ticket);
                    }
                }
            }
            notifyDataSetChanged();

            // Handle "No Search Results" visibility
            TextView tvNoSearchResults = findViewById(R.id.tvNoSearchResults);
            if (filteredList.isEmpty() && !originalList.isEmpty()) {
                if (tvNoSearchResults != null)
                    tvNoSearchResults.setVisibility(View.VISIBLE);
                if (ticketRecyclerView != null)
                    ticketRecyclerView.setVisibility(View.GONE);
            } else {
                if (tvNoSearchResults != null)
                    tvNoSearchResults.setVisibility(View.GONE);
                if (ticketRecyclerView != null)
                    ticketRecyclerView.setVisibility(View.VISIBLE);
            }
        }

        @NonNull
        @Override
        public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_ticket, parent, false);
            return new TicketViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
            try {
                Ticket ticket = filteredList.get(position);

                if (holder.tvEventTitle != null) {
                    holder.tvEventTitle.setText(ticket.getEventTitle() != null ? ticket.getEventTitle() : "Event");
                }
                if (holder.tvEventDate != null) {
                    holder.tvEventDate.setText(ticket.getEventDate() != null ? ticket.getEventDate() : "Date TBA");
                }
                if (holder.tvEventLocation != null) {
                    holder.tvEventLocation
                            .setText(ticket.getEventLocation() != null ? ticket.getEventLocation() : "Location TBA");
                }
                if (holder.tvTicketCode != null) {
                    holder.tvTicketCode.setText(ticket.getUniqueCode() != null ? ticket.getUniqueCode() : "N/A");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error binding ticket at position " + position + ": " + e.getMessage(), e);
            }
        }

        @Override
        public int getItemCount() {
            return filteredList.size();
        }

        class TicketViewHolder extends RecyclerView.ViewHolder {
            TextView tvEventTitle, tvEventDate, tvEventLocation, tvTicketCode;

            public TicketViewHolder(@NonNull View itemView) {
                super(itemView);
                tvEventTitle = itemView.findViewById(R.id.tvEventTitle);
                tvEventDate = itemView.findViewById(R.id.tvEventDate);
                tvEventLocation = itemView.findViewById(R.id.tvEventLocation);
                tvTicketCode = itemView.findViewById(R.id.tvTicketCode);
            }
        }
    }
}
