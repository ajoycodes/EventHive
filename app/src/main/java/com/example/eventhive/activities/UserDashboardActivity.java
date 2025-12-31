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
    private ImageView navHome, navTicket, navNotifications, navProfile;

    private DatabaseHelper dbHelper;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.util.Log.d("UserDashboard", "onCreate started");

        try {
            setContentView(R.layout.activity_user_dashboard);

            // Session check
            session = new SessionManager(this);
            if (!session.isLoggedIn()) {
                android.util.Log.e("UserDashboard", "Invalid session, redirecting to login");
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return;
            }

            // Initialize DatabaseHelper
            dbHelper = new DatabaseHelper(this);

            // Initialize Views
            recyclerView = findViewById(R.id.recyclerViewEvents);
            tvEmptyState = findViewById(R.id.tvEmptyState);
            progressBar = findViewById(R.id.progressBar);
            navHome = findViewById(R.id.navHome);
            navTicket = findViewById(R.id.navTicket);
            navNotifications = findViewById(R.id.navNotifications);
            navProfile = findViewById(R.id.navProfile);

            // Setup RecyclerView
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new EventAdapter(this, new ArrayList<>());
            recyclerView.setAdapter(adapter);

            // Load events
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

        List<Event> events = dbHelper.getAllEvents();

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        if (events != null && !events.isEmpty()) {
            adapter.updateEvents(events);
            tvEmptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            tvEmptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    private void setupClickListeners() {
        navHome.setOnClickListener(v -> {
            // Already on home
        });

        navTicket.setOnClickListener(v -> {
            Intent intent = new Intent(UserDashboardActivity.this, MyTicketsActivity.class);
            startActivity(intent);
        });

        navNotifications.setOnClickListener(v -> {
            Intent intent = new Intent(UserDashboardActivity.this, NotificationsActivity.class);
            startActivity(intent);
        });

        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(UserDashboardActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload events when returning to dashboard
        loadEvents();
    }
}
