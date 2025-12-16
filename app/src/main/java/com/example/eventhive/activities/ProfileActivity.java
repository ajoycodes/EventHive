package com.example.eventhive.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.eventhive.R;
import com.example.eventhive.databases.DatabaseHelper;
import com.example.eventhive.models.User;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvInitials, tvUserName, tvUserEmail;
    private TextView tvFirstName, tvLastName, tvPhone, tvRole;
    private Button btnEditProfile;
    private ImageView btnBack;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        dbHelper = new DatabaseHelper(this);

        // Initialize views
        tvInitials = findViewById(R.id.tvInitials);
        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvFirstName = findViewById(R.id.tvFirstName);
        tvLastName = findViewById(R.id.tvLastName);
        tvPhone = findViewById(R.id.tvPhone);
        tvRole = findViewById(R.id.tvRole);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnBack = findViewById(R.id.btnBack);

        // Load user data (placeholder - you'd get actual user from session/preferences)
        loadUserProfile();

        btnBack.setOnClickListener(v -> finish());

        btnEditProfile.setOnClickListener(v -> {
            // TODO: Navigate to edit profile screen
            android.widget.Toast.makeText(this, "Edit profile feature coming soon", android.widget.Toast.LENGTH_SHORT)
                    .show();
        });
    }

    private void loadUserProfile() {
        // Placeholder data - in real app, get from SharedPreferences or database
        String firstName = "John";
        String lastName = "Doe";
        String email = "john.doe@example.com";
        String phone = "+1 234 567 8900";
        String role = "User";

        // Set initials
        String initials = "";
        if (!firstName.isEmpty())
            initials += firstName.charAt(0);
        if (!lastName.isEmpty())
            initials += lastName.charAt(0);
        tvInitials.setText(initials.toUpperCase());

        // Set user info
        tvUserName.setText(firstName + " " + lastName);
        tvUserEmail.setText(email);
        tvFirstName.setText(firstName);
        tvLastName.setText(lastName);
        tvPhone.setText(phone);
        tvRole.setText(role);
    }
}
