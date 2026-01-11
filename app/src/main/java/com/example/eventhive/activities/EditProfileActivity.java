package com.example.eventhive.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eventhive.R;
import com.example.eventhive.utils.SessionManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etFirstName, etLastName, etPhone;
    private Button btnSave;
    private ImageView btnBack;
    private ProgressBar progressBar;
    private SessionManager sessionManager;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        sessionManager = SessionManager.getInstance(this);
        db = FirebaseFirestore.getInstance();

        initViews();
        loadCurrentData();
        setupListeners();
    }

    private void initViews() {
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etPhone = findViewById(R.id.etPhone);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);
        progressBar = findViewById(R.id.progressBar);
    }

    private void loadCurrentData() {
        String userName = sessionManager.getUserName();
        String phone = sessionManager.getUserPhone();

        String firstName = "";
        String lastName = "";
        if (userName != null && !userName.isEmpty()) {
            String[] nameParts = userName.split(" ", 2);
            firstName = nameParts[0];
            if (nameParts.length > 1) {
                lastName = nameParts[1];
            }
        }

        etFirstName.setText(firstName);
        etLastName.setText(lastName);
        etPhone.setText(phone);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void saveProfile() {
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (TextUtils.isEmpty(firstName)) {
            etFirstName.setError("First name required");
            return;
        }

        if (TextUtils.isEmpty(lastName)) {
            etLastName.setError("Last name required");
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("Phone required");
            return;
        }

        setLoading(true);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();

            // Update Firestore
            Map<String, Object> updates = new HashMap<>();
            updates.put("firstName", firstName);
            updates.put("lastName", lastName);
            updates.put("phone", phone);

            db.collection("users").document(uid)
                    .update(updates)
                    .addOnSuccessListener(aVoid -> {
                        // Update local session
                        String fullName = firstName + " " + lastName;
                        String email = sessionManager.getUserEmail(); // Keep existing email
                        sessionManager.updateSession(fullName, email, phone);

                        setLoading(false);
                        Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT)
                                .show();

                        // Return to previous screen (ProfileActivity) which should reload data
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        setLoading(false);
                        Toast.makeText(EditProfileActivity.this, "Failed to update profile: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    });
        } else {
            setLoading(false);
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnSave.setEnabled(!loading);
        etFirstName.setEnabled(!loading);
        etLastName.setEnabled(!loading);
        etPhone.setEnabled(!loading);
    }
}
