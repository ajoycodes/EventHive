package com.example.eventhive.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eventhive.R;
import com.example.eventhive.auth.AuthManager;
import com.example.eventhive.utils.SessionManager;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private ProgressBar progressBar;

    private AuthManager authManager;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authManager = new AuthManager();
        sessionManager = new SessionManager(this);

        // Check if already logged in
        if (FirebaseAuth.getInstance().getCurrentUser() != null && sessionManager.isLoggedIn()) {
            navigateToDashboard();
            return;
        }

        initViews();
        setupListeners();
    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> loginUser());
        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (!validateInputs(email, password)) {
            return;
        }

        setLoading(true);

        authManager.login(email, password, new com.example.eventhive.auth.AuthCallback() {
            @Override
            public void onSuccess(String uid) {
                fetchUserRole(uid);
            }

            @Override
            public void onFailure(String error) {
                setLoading(false);
                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchUserRole(String uid) {
        authManager.getUserRole(uid, new com.example.eventhive.auth.RoleCallback() {
            @Override
            public void onRoleFetched(String role, String firstName, String email, String phone) {
                // Pass email to session manager
                sessionManager.createLoginSession(uid, role, firstName, email, phone);
                setLoading(false);
                navigateToDashboard();
            }

            @Override
            public void onFailure(String error) {
                setLoading(false);
                FirebaseAuth.getInstance().signOut();
                sessionManager.clearSession();
                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInputs(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email required");
            etEmail.requestFocus();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Invalid email");
            etEmail.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password required");
            etPassword.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void navigateToDashboard() {
        String role = sessionManager.getUserRole();
        Intent intent;

        switch (role.toLowerCase()) {
            case "admin":
                intent = new Intent(this, AdminDashboardActivity.class);
                break;
            case "organizer":
                intent = new Intent(this, OrganizerDashboardActivity.class);
                break;
            default:
                intent = new Intent(this, UserDashboardActivity.class);
                break;
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!loading);
        etEmail.setEnabled(!loading);
        etPassword.setEnabled(!loading);
    }
}
