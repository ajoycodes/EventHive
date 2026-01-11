package com.example.eventhive.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.eventhive.R;
import com.example.eventhive.utils.SessionManager;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUserName, tvUserEmail;
    private TextView tvFirstName, tvLastName, tvPhone, tvRole;
    private Button btnEditProfile;
    private ImageView btnBack;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sessionManager = SessionManager.getInstance(this);

        // Initialize views
        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvFirstName = findViewById(R.id.tvFirstName);
        tvLastName = findViewById(R.id.tvLastName);
        tvPhone = findViewById(R.id.tvPhone);
        tvRole = findViewById(R.id.tvRole);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnBack = findViewById(R.id.btnBack);

        // Load user data from session
        loadUserProfile();

        btnBack.setOnClickListener(v -> finish());

        btnEditProfile.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserProfile();
    }

    private void loadUserProfile() {
        // Get user data from SessionManager
        String userName = sessionManager.getUserName();
        String email = sessionManager.getUserEmail();
        String phone = sessionManager.getUserPhone();
        String role = sessionManager.getUserRole();

        // Parse first and last name from userName
        String firstName = "";
        String lastName = "";
        if (userName != null && !userName.isEmpty()) {
            String[] nameParts = userName.split(" ", 2);
            firstName = nameParts[0];
            if (nameParts.length > 1) {
                lastName = nameParts[1];
            }
        }

        // Set user info
        tvUserName.setText(userName != null && !userName.isEmpty() ? userName : "User");
        tvUserEmail.setText(email != null && !email.isEmpty() ? email : "No email");
        tvFirstName.setText(firstName.isEmpty() ? "N/A" : firstName);
        tvLastName.setText(lastName.isEmpty() ? "N/A" : lastName);
        tvPhone.setText(phone != null && !phone.isEmpty() ? phone : "No phone");
        tvRole.setText(role != null && !role.isEmpty() ? role : "User");
    }
}
