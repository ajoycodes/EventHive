package com.example.eventhive.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventhive.R;
import com.example.eventhive.adapters.EventAdapter;
import com.example.eventhive.auth.AuthManager;
import com.example.eventhive.databases.DatabaseHelper;
import com.example.eventhive.models.Event;
import com.example.eventhive.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class UserDashboardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private TextView tvEmptyState;
    private ProgressBar progressBar;
    private ImageView navHome, navTicket, navNotifications, navLogout;

    private DatabaseHelper dbHelper;
    private SessionManager session;
    private AuthManager authManager;
    private com.google.firebase.firestore.FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.util.Log.d("UserDashboard", "onCreate started");

        try {
            setContentView(R.layout.activity_user_dashboard);

            // Session check
            session = SessionManager.getInstance(this);
            if (!session.isLoggedIn()) {
                android.util.Log.e("UserDashboard", "Invalid session, redirecting to login");
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return;
            }

            // Initialize DatabaseHelper (legacy cleanup if needed), AuthManager, and
            // Firestore
            dbHelper = new DatabaseHelper(this); // Kept to avoid breaking potential lingering deps for now
            authManager = new AuthManager();
            db = com.google.firebase.firestore.FirebaseFirestore.getInstance();

            // Initialize Views
            recyclerView = findViewById(R.id.recyclerViewEvents);
            tvEmptyState = findViewById(R.id.tvEmptyState);
            progressBar = findViewById(R.id.progressBar);
            navHome = findViewById(R.id.navHome);
            navTicket = findViewById(R.id.navTicket);
            navNotifications = findViewById(R.id.navNotifications); // Re-added
            navLogout = findViewById(R.id.navLogout);

            // Setup RecyclerView
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new EventAdapter(this, new ArrayList<>());
            recyclerView.setAdapter(adapter);

            // Load events from Firestore
            loadEvents();

            // Setup navigation
            setupClickListeners();

        } catch (Exception e) {
            android.util.Log.e("UserDashboard", "Critical error in onCreate", e);
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private void loadEvents() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        // Fetch from Firestore "events" collection
        db.collection("events")
                .get()
                .addOnCompleteListener(task -> {
                    if (progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }

                    if (task.isSuccessful()) {
                        List<Event> events = new ArrayList<>();
                        for (com.google.firebase.firestore.QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                // Manual mapping to ensure safety
                                String title = document.getString("title");
                                String date = document.getString("date");
                                String location = document.getString("location");
                                String description = document.getString("description");
                                String status = document.getString("status");
                                Double price = document.getDouble("ticketPrice");
                                Long quantity = document.getLong("ticketQuantity");
                                String coverImg = document.getString("coverImagePath");
                                String galleryImgs = document.getString("galleryImagePaths");
                                String type = document.getString("eventType");

                                // Handle potential nulls for primitive wrappers
                                double p = price != null ? price : 0.0;
                                int q = quantity != null ? quantity.intValue() : 0;
                                // status default
                                if (status == null)
                                    status = Event.STATUS_ACTIVE;

                                // Note: Event model might need a no-arg constructor for automatic toObject()
                                // using manual construction for now based on what I saw in CreateEventActivity
                                Event e = new Event(title, date, location, description, status, p, q, coverImg,
                                        galleryImgs, type);
                                // Set document ID as the ID? Event model uses int ID likely.
                                // We might need to store Firestore ID inside Event if we want to update it.
                                // For now, just for display.
                                // If Event model has setFirestoreId method, use it.
                                e.setFirestoreId(document.getId());

                                events.add(e);
                            } catch (Exception ex) {
                                android.util.Log.e("UserDashboard", "Error parsing event: " + ex.getMessage());
                            }
                        }

                        if (!events.isEmpty()) {
                            adapter.updateEvents(events);
                            if (tvEmptyState != null)
                                tvEmptyState.setVisibility(View.GONE);
                            if (recyclerView != null)
                                recyclerView.setVisibility(View.VISIBLE);
                        } else {
                            if (tvEmptyState != null)
                                tvEmptyState.setVisibility(View.VISIBLE);
                            if (recyclerView != null)
                                recyclerView.setVisibility(View.GONE);
                        }

                    } else {
                        android.util.Log.e("UserDashboard", "Error getting documents: ", task.getException());
                        if (tvEmptyState != null)
                            tvEmptyState.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void setupClickListeners() {
        // Changed from btnProfile to btnSettings (header icon)
        ImageView btnSettings = findViewById(R.id.btnSettings);
        if (btnSettings != null) {
            btnSettings.setOnClickListener(v -> {
                startActivity(new Intent(UserDashboardActivity.this, SettingsActivity.class));
            });
        }

        // Handle "btnProfile" legacy Case if layout ID wasn't updated in logic
        // But I updated layout ID to btnSettings.

        navHome.setOnClickListener(v -> {
            // Already on home, maybe refresh?
            loadEvents();
        });

        navTicket.setOnClickListener(v -> {
            Intent intent = new Intent(UserDashboardActivity.this, MyTicketsActivity.class);
            startActivity(intent);
        });

        navNotifications.setOnClickListener(v -> {
            // Request #4: "If notifications screen not implemented, clicking it should
            // show: 'Coming soon' toast"
            android.widget.Toast.makeText(UserDashboardActivity.this, "Coming soon", android.widget.Toast.LENGTH_SHORT)
                    .show();
        });

        navLogout.setOnClickListener(v -> {
            // Logout: clear Firebase and session
            authManager.logout();
            session.clearSession();

            // Navigate to login
            Intent intent = new Intent(UserDashboardActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload events when returning to dashboard
        loadEvents();
    }
}
