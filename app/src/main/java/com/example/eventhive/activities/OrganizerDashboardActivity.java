package com.example.eventhive.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.eventhive.R;
import com.example.eventhive.auth.AuthManager;
import com.example.eventhive.databases.DatabaseHelper;
import com.example.eventhive.models.Event;
import com.example.eventhive.utils.SessionManager;
import java.util.List;

public class OrganizerDashboardActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private AuthManager authManager;
    private SessionManager sessionManager;
    private TextView tvTotalEvents, tvActiveEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_dashboard);

        dbHelper = new DatabaseHelper(this);
        authManager = new AuthManager();
        sessionManager = new SessionManager(this);

        android.view.View btnCreateEvent = findViewById(R.id.btnCreateEvent);
        android.view.View btnMyEvents = findViewById(R.id.btnMyEvents);
        Button btnLogout = findViewById(R.id.btnLogout);

        tvTotalEvents = findViewById(R.id.tvTotalEvents);
        tvActiveEvents = findViewById(R.id.tvActiveEvents);

        // Bottom Navigation
        ImageView navHome = findViewById(R.id.navHome);
        ImageView navEvents = findViewById(R.id.navEvents);
        ImageView navSettings = findViewById(R.id.navSettings);

        // Load statistics
        loadStatistics();

        btnCreateEvent.setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateEventActivity.class);
            startActivity(intent);
        });

        btnMyEvents.setOnClickListener(v -> {
            Intent intent = new Intent(OrganizerDashboardActivity.this, OrganizerEventsActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            authManager.logout();
            sessionManager.clearSession();

            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Bottom Navigation Handlers
        if (navHome != null) {
            navHome.setOnClickListener(v -> {
                // Already on home
                loadStatistics(); // Refresh stats
            });
        }

        if (navEvents != null) {
            navEvents.setOnClickListener(v -> {
                Intent intent = new Intent(this, OrganizerEventsActivity.class);
                startActivity(intent);
            });
        }

        if (navSettings != null) {
            navSettings.setOnClickListener(v -> {
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
            });
        }
    }

    private void loadStatistics() {
        List<Event> allEvents = dbHelper.getAllEvents();
        int totalEvents = allEvents != null ? allEvents.size() : 0;

        // Count only active events
        int activeEvents = 0;
        if (allEvents != null) {
            for (Event event : allEvents) {
                if (Event.STATUS_ACTIVE.equals(event.getStatus())) {
                    activeEvents++;
                }
            }
        }

        if (tvTotalEvents != null) {
            tvTotalEvents.setText(String.valueOf(totalEvents));
        }
        if (tvActiveEvents != null) {
            tvActiveEvents.setText(String.valueOf(activeEvents));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStatistics(); // Refresh when returning to this screen
    }
}
