package com.example.eventhive.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.eventhive.R;

public class AdminDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        android.view.View btnManageUsers = findViewById(R.id.btnManageUsers);
        android.view.View btnViewEvents = findViewById(R.id.btnViewEvents);
        android.view.View btnSettings = findViewById(R.id.btnSettings);
        Button btnLogout = findViewById(R.id.btnLogout);

        btnManageUsers.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminUserListActivity.class);
            startActivity(intent);
        });

        btnViewEvents.setOnClickListener(v -> {
            // Reusing UserDashboard for now, or create AdminEventListActivity
            Intent intent = new Intent(this, UserDashboardActivity.class);
            startActivity(intent);
        });

        if (btnSettings != null) {
            btnSettings.setOnClickListener(v -> {
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
            });
        }

        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
