package com.example.eventhive.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eventhive.R;
import com.example.eventhive.databases.DatabaseHelper;
import com.example.eventhive.models.User;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFirstName, etLastName, etEmail, etPhone, etPassword;
    private Spinner spinnerRole;
    private Button btnRegister;
    private TextView tvLogin;
    private ProgressBar progressBar;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_register);
        } catch (Exception e) {
            android.util.Log.e("RegisterActivity", "Error inflating layout", e);
            Toast.makeText(this, "Error loading registration screen", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Initialize DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Initialize views
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        spinnerRole = findViewById(R.id.spinnerRole);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.goLoginBtn);
        progressBar = findViewById(R.id.progressBar);

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        // Check if critical views are found
        if (etFirstName == null || etLastName == null || etEmail == null ||
                etPhone == null || etPassword == null || spinnerRole == null ||
                btnRegister == null || tvLogin == null) {
            Toast.makeText(this, "Error: Missing UI elements", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        setupSpinner();
        setupClickListeners();
    }

    private void setupSpinner() {
        String[] roles = new String[] { "User", "Organizer", "Admin" };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);
        spinnerRole.setSelection(0); // Default to "User"
    }

    private void setupClickListeners() {
        btnRegister.setOnClickListener(v -> handleRegister());

        tvLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void handleRegister() {
        String fName = etFirstName.getText().toString().trim();
        String lName = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();

        Object selectedItem = spinnerRole.getSelectedItem();
        if (selectedItem == null) {
            Toast.makeText(this, "Please select a role", Toast.LENGTH_SHORT).show();
            return;
        }
        String role = selectedItem.toString();

        // Validate inputs
        if (fName.isEmpty()) {
            Toast.makeText(this, "First name is required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (lName.isEmpty()) {
            Toast.makeText(this, "Last name is required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (email.isEmpty()) {
            Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
            return;
        }

        if (pass.isEmpty()) {
            Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (pass.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if (phone.isEmpty()) {
            Toast.makeText(this, "Phone number is required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        btnRegister.setEnabled(false);

        // Create user
        User newUser = new User(fName, lName, email, pass, role, phone);
        boolean success = dbHelper.createUser(newUser);

        // Hide loading
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
        btnRegister.setEnabled(true);

        if (success) {
            Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Registration failed. Email may already exist.", Toast.LENGTH_SHORT).show();
        }
    }
}
