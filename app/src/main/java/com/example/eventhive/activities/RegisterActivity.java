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
import androidx.lifecycle.ViewModelProvider;

import com.example.eventhive.R;
import com.example.eventhive.viewmodel.AuthViewModel;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFirstName, etLastName, etEmail, etPhone, etPassword;
    private Spinner spinnerRole;
    private Button btnRegister;
    private TextView tvLogin;
    private ProgressBar progressBar;

    private AuthViewModel authViewModel;

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

        // Initialize ViewModel
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

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

        if (progressBar == null) {
            android.util.Log.d("RegisterActivity", "ProgressBar not found in layout");
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
        setupObservers();
        setupClickListeners();
    }

    /**
     * Sets up the role spinner with options.
     */
    private void setupSpinner() {
        String[] roles = new String[] { "User", "Organizer", "Admin" };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);
        spinnerRole.setSelection(0); // Default to "User"
    }

    /**
     * Sets up LiveData observers for ViewModel.
     */
    private void setupObservers() {
        // Observe registration result
        authViewModel.getRegisterResult().observe(this, result -> {
            if (result != null) {
                if (result.success) {
                    Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Observe loading state
        authViewModel.getIsLoading().observe(this, isLoading -> {
            if (progressBar != null) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
            btnRegister.setEnabled(!isLoading);
        });
    }

    /**
     * Sets up click listeners for buttons.
     */
    private void setupClickListeners() {
        btnRegister.setOnClickListener(v -> handleRegister());

        tvLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    /**
     * Handles registration button click.
     */
    private void handleRegister() {
        String fName = etFirstName.getText().toString().trim();
        String lName = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();
        String confirmPass = pass; // No separate confirm field
                                   // confirm
                                   // field,
                                   // use
                                   // same
                                   // password

        Object selectedItem = spinnerRole.getSelectedItem();
        if (selectedItem == null) {
            Toast.makeText(this, "Please select a role", Toast.LENGTH_SHORT).show();
            return;
        }
        String role = selectedItem.toString();

        // ViewModel will handle comprehensive validation
        authViewModel.register(fName, lName, email, pass, confirmPass, role, phone);
    }
}
