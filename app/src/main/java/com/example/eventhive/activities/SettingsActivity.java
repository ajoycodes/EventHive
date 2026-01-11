package com.example.eventhive.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.eventhive.R;
import com.example.eventhive.auth.AuthManager;
import com.example.eventhive.utils.SessionManager;

public class SettingsActivity extends AppCompatActivity {

    private View btnProfile, btnHelp, btnAbout;
    private Button btnLogout;
    private ImageView btnBack;
    private AuthManager authManager;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        authManager = new AuthManager();
        sessionManager = new SessionManager(this);

        btnProfile = findViewById(R.id.btnProfile);

        btnHelp = findViewById(R.id.btnHelp);
        btnAbout = findViewById(R.id.btnAbout);
        btnLogout = findViewById(R.id.btnLogout);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        });

        btnHelp.setOnClickListener(v -> {
            Intent intent = new Intent(this, HelpActivity.class);
            startActivity(intent);
        });

        btnAbout.setOnClickListener(v -> {
            Intent intent = new Intent(this, AboutActivity.class);
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
    }
}
