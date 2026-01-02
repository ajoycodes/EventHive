package com.example.eventhive.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.eventhive.R;
import com.example.eventhive.auth.AuthManager;
import com.example.eventhive.auth.AuthCallback;
import com.example.eventhive.auth.RoleCallback;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Spinner spinnerRole;
    private Button loginBtn;
    private TextView goRegisterBtn;
    private ProgressBar progressBar;
    private AuthManager authManager;

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

        // Initialize Firebase AuthManager
        authManager = new AuthManager();

        emailInput = findViewById(R.id.etEmail);
        passwordInput = findViewById(R.id.etPassword);
        spinnerRole = findViewById(R.id.spinnerRole);
        loginBtn = findViewById(R.id.btnLogin);
        goRegisterBtn = findViewById(R.id.goRegisterBtn);
        progressBar = findViewById(R.id.progressBar);
        android.view.View registerContainer = findViewById(R.id.registerContainer);

        // Setup role spinner
        setupRoleSpinner();

        // Hide progress bar initially
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

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

    private void setupRoleSpinner() {
        String[] roles = new String[] { "User", "Organizer", "Admin" };
        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);
        spinnerRole.setSelection(0); // Default to "User"
    }

    private void handleLogin() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        Object selectedItem = spinnerRole.getSelectedItem();
        if (selectedItem == null) {
            Toast.makeText(this, "Please select a role", Toast.LENGTH_SHORT).show();
            return;
        }
        String selectedRole = selectedItem.toString();

        // Validate inputs
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        loginBtn.setEnabled(false);

        // Login with Firebase
        authManager.login(email, password, new AuthCallback() {
            @Override
            public void onSuccess(String uid) {
                // Fetch user role from Firestore
                authManager.getUserRole(uid, new RoleCallback() {
                    @Override
                    public void onRoleFetched(String role, String firstName, String phone) {
                        // Hide loading
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }
                        loginBtn.setEnabled(true);

                        // Verify role matches selected role
                        if (!role.equalsIgnoreCase(selectedRole)) {
                            Toast.makeText(LoginActivity.this,
                                    "Invalid role. Please select " + role + " and try again.",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }

                        android.util.Log.d("LOGIN", "Login successful: " + email + ", Role: " + role);

                        // Save Session with Firebase UID
                        com.example.eventhive.utils.SessionManager session = new com.example.eventhive.utils.SessionManager(
                                LoginActivity.this);
                        session.createLoginSession(uid, firstName, email, role, phone);

                        Toast.makeText(LoginActivity.this, "Welcome " + firstName, Toast.LENGTH_SHORT).show();
                        navigateToDashboard(role);
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        // Hide loading
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }
                        loginBtn.setEnabled(true);

                        android.util.Log.e("LOGIN", "Failed to fetch user role: " + errorMessage);
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                // Hide loading
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                loginBtn.setEnabled(true);

                android.util.Log.e("LOGIN", "Login failed: " + errorMessage);
                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToDashboard(String role) {
        Intent intent;
        // Role is stored in lowercase in Firestore, so compare case-insensitively
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
