package com.example.eventhive.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventhive.R;
import com.example.eventhive.adapters.EventAdapter;
import com.example.eventhive.utils.SessionManager;
import com.example.eventhive.viewmodel.EventViewModel;

public class UserDashboardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private TextView tvEmptyState;
    private ProgressBar progressBar;
    private ImageView navHome, navTicket, navProfile;

    private EventViewModel eventViewModel;
    private SessionManager session;

    // Filter states
    private String currentQuery = "";
    private String currentFilterType = "ALL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.util.Log.d("UserDashboard", "onCreate started");

        try {
            setContentView(R.layout.activity_user_dashboard);

            // 1. Session Safety Logic - Check before anything else
            session = new SessionManager(this);
            if (!session.isLoggedIn()) {
                android.util.Log.e("UserDashboard", "Invalid session, redirecting to login");
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return;
            }

            // 2. Initialize ViewModel
            eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);
            android.util.Log.d("UserDashboard", "ViewModel initialized");

            // 3. Initialize Views
            recyclerView = findViewById(R.id.recyclerViewEvents);
            tvEmptyState = findViewById(R.id.tvEmptyState);
            progressBar = findViewById(R.id.progressBar);
            navHome = findViewById(R.id.navHome);
            navTicket = findViewById(R.id.navTicket);
            navProfile = findViewById(R.id.navProfile);

            // 4. Defensive RecyclerView Setup
            // Initialize with empty list immediately so it's never null/unbound
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new EventAdapter(this, new java.util.ArrayList<>());
            recyclerView.setAdapter(adapter);
            android.util.Log.d("UserDashboard", "Adapter initialized with empty list");

            // 5. Setup Logic
            setupObservers();
            setupClickListeners();

        } catch (Exception e) {
            android.util.Log.e("UserDashboard", "Critical error in onCreate", e);
            // Fallback to login in case of catastrophic UI/Logic failure
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    /**
     * Sets up LiveData observers for ViewModel.
     */
    private void setupObservers() {
        // Observe event list changes
        eventViewModel.getAllEvents().observe(this, events -> {
            android.util.Log.d("UserDashboard", "Observing events: " + (events != null ? events.size() : "null"));

            if (events != null && !events.isEmpty()) {
                // Safe update using DiffUtil (assuming adapter is already set in onCreate)
                if (adapter != null) {
                    adapter.updateEvents(events);
                } else {
                    // Fallback re-init (shouldn't happen with new onCreate logic)
                    adapter = new EventAdapter(this, events);
                    recyclerView.setAdapter(adapter);
                }

                recyclerView.setVisibility(View.VISIBLE);
                if (tvEmptyState != null) {
                    tvEmptyState.setVisibility(View.GONE);
                }
            } else {
                // Handle empty list safely
                if (adapter != null) {
                    adapter.updateEvents(new java.util.ArrayList<>());
                }

                // Show empty state
                recyclerView.setVisibility(View.GONE);
                if (tvEmptyState != null) {
                    tvEmptyState.setVisibility(View.VISIBLE);
                }
            }
        });

        // Observe loading state
        eventViewModel.getIsLoading().observe(this, isLoading -> {
            if (progressBar != null) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });
    }

    /**
     * Sets up click listeners for navigation buttons.
     */
    private void setupClickListeners() {
        // Search View Logic
        androidx.appcompat.widget.SearchView searchView = findViewById(R.id.searchView);
        if (searchView != null) {
            searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    currentQuery = query;
                    eventViewModel.setFilters(currentQuery, currentQuery, currentFilterType); // Omni-search: query =
                                                                                              // location
                    searchView.clearFocus();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    currentQuery = newText;
                    eventViewModel.setFilters(currentQuery, currentQuery, currentFilterType);
                    return true;
                }
            });
        }

        // Spinner Logic
        android.widget.Spinner spinner = findViewById(R.id.spinnerFilter);
        if (spinner != null) {
            java.util.List<String> filters = new java.util.ArrayList<>();
            filters.add("All Events");
            filters.add("Upcoming");
            filters.add("Past");

            android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, filters);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);

            spinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                    String selected = filters.get(position);
                    if (selected.contains("Upcoming")) {
                        currentFilterType = "UPCOMING";
                    } else if (selected.contains("Past")) {
                        currentFilterType = "PAST";
                    } else {
                        currentFilterType = "ALL";
                    }
                    eventViewModel.setFilters(currentQuery, currentQuery, currentFilterType);
                }

                @Override
                public void onNothingSelected(android.widget.AdapterView<?> parent) {
                }
            });
        }

        if (navHome != null) {
            navHome.setOnClickListener(v -> {
                // Reset filters
                if (searchView != null)
                    searchView.setQuery("", false);
                if (spinner != null)
                    spinner.setSelection(0);
            });
        }

        if (navTicket != null) {
            navTicket.setOnClickListener(v -> {
                Intent intent = new Intent(UserDashboardActivity.this, MyTicketsActivity.class);
                startActivity(intent);
            });
        }

        if (navProfile != null) {
            navProfile.setOnClickListener(v -> {
                Intent intent = new Intent(UserDashboardActivity.this, ProfileActivity.class);
                startActivity(intent);
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // LiveData will automatically update the UI when data changes
    }
}
