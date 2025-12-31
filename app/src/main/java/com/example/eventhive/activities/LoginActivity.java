package com.example.eventhive.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.eventhive.R;
import com.example.eventhive.databases.DatabaseHelper;
import com.example.eventhive.models.User;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button loginBtn;
    private TextView goRegisterBtn;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_login);
        } catch (Exception e) {
            android.util.Log.e("LoginActivity", "Error inflating layout", e);
            Toast.makeText(this, "Error loading login screen: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        try {
            dbHelper = new DatabaseHelper(this);
        } catch (Exception e) {
            android.util.Log.e("LoginActivity", "Error initializing database", e);
            Toast.makeText(this, "Database error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        emailInput = findViewById(R.id.etEmail);
        passwordInput = findViewById(R.id.etPassword);
        loginBtn = findViewById(R.id.btnLogin);
        goRegisterBtn = findViewById(R.id.goRegisterBtn);
        android.view.View registerContainer = findViewById(R.id.registerContainer);

        // Check if critical views are found
        if (emailInput == null || passwordInput == null || loginBtn == null || goRegisterBtn == null) {
            android.util.Log.e("LoginActivity", "Critical views not found");
            Toast.makeText(this, "Error: Missing UI elements", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        loginBtn.setOnClickListener(v -> handleLogin());

        android.view.View.OnClickListener regListener = v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        };

        goRegisterBtn.setOnClickListener(regListener);
        if (registerContainer != null) {
            registerContainer.setOnClickListener(regListener);
        }
    }

    private void handleLogin() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // For simple demo, if DB is empty we might allow a 'bypass' or seed data?
        // DatabaseHelper already seeds events, let's trust registration.
        // Actually, for easy testing, let's allow 'admin'/'admin' if DB fails or make a
        // specific check.

        User user = dbHelper.loginUser(email, password);
        if (user != null) {
            android.util.Log.d("LOGIN", "Login successful: " + user.getEmail());

            // Save Session
            com.example.eventhive.utils.SessionManager session = new com.example.eventhive.utils.SessionManager(this);
            session.createLoginSession(user.getId(), user.getFirstName(), user.getEmail(), user.getRole(),
                    user.getPhone());

            Toast.makeText(this, "Welcome " + user.getFirstName(), Toast.LENGTH_SHORT).show();
            navigateToDashboard(user.getRole());
        } else {
            android.util.Log.e("LOGIN", "Login failed for: " + email);
            Toast.makeText(this, "Invalid credentials or User not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToDashboard(String role) {
        Intent intent;
        if (role.equalsIgnoreCase("Admin")) {
            intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
        } else if (role.equalsIgnoreCase("Organizer")) {
            intent = new Intent(LoginActivity.this, OrganizerDashboardActivity.class);
        } else {
            intent = new Intent(LoginActivity.this, UserDashboardActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
