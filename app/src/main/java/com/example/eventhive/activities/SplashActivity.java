package com.example.eventhive.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import com.example.eventhive.R;
import com.example.eventhive.utils.SessionManager;

public class SplashActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_splash);
        } catch (Exception e) {
            android.util.Log.e("SplashActivity", "Error loading splash screen", e);
            navigateToLogin();
            return;
        }

        sessionManager = new SessionManager(this);

        // Delay for splash screen effect
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            try {
                checkSessionAndNavigate();
            } catch (Exception e) {
                android.util.Log.e("SplashActivity", "Error checking session", e);
                navigateToLogin();
            }
        }, 2000);
    }

    /**
     * Checks if user is logged in and navigates to appropriate screen.
     */
    private void checkSessionAndNavigate() {
        if (sessionManager.isLoggedIn()) {
            // User is logged in, navigate to dashboard based on role
            String role = sessionManager.getUserRole();
            Intent intent;

            if ("Admin".equalsIgnoreCase(role)) {
                intent = new Intent(SplashActivity.this, AdminDashboardActivity.class);
            } else if ("Organizer".equalsIgnoreCase(role)) {
                intent = new Intent(SplashActivity.this, OrganizerDashboardActivity.class);
            } else {
                intent = new Intent(SplashActivity.this, UserDashboardActivity.class);
            }

            startActivity(intent);
            finish();
        } else {
            // User not logged in, go to login screen
            navigateToLogin();
        }
    }

    private void navigateToLogin() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
