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
            session = new SessionManager(this);

            if (ticketRecyclerView != null) {
                ticketRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            }

            ImageView btnBack = findViewById(R.id.btnBack);
            if (btnBack != null) {
                btnBack.setOnClickListener(v -> finish());
            }

            loadTickets();
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Error loading tickets", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadTickets() {
        try {
            int userId = session.getUserId();
            List<Ticket> tickets = dbHelper.getTicketsForUser(userId);

            if (tickets == null || tickets.isEmpty()) {
                if (ticketRecyclerView != null) {
                    ticketRecyclerView.setVisibility(View.GONE);
                }
                if (tvEmptyState != null) {
                    tvEmptyState.setVisibility(View.VISIBLE);
                }
            } else {
                if (ticketRecyclerView != null) {
                    ticketRecyclerView.setVisibility(View.VISIBLE);
                }
                if (tvEmptyState != null) {
                    tvEmptyState.setVisibility(View.GONE);
                }
                adapter = new TicketAdapter(tickets);
                if (ticketRecyclerView != null) {
                    ticketRecyclerView.setAdapter(adapter);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading tickets: " + e.getMessage(), e);
            Toast.makeText(this, "Error loading tickets", Toast.LENGTH_SHORT).show();
        }
    }

    // RecyclerView Adapter for Tickets
    private class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {
        private List<Ticket> tickets;

        public TicketAdapter(List<Ticket> tickets) {
            this.tickets = tickets != null ? tickets : new ArrayList<>();
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
                Ticket ticket = tickets.get(position);

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
            return tickets.size();
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
