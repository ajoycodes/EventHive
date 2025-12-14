package com.example.eventhive.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.example.eventhive.R;
import com.example.eventhive.databases.DatabaseHelper;
import com.example.eventhive.models.Event;

public class CreateEventActivity extends AppCompatActivity {

    private EditText etTitle, etDate, etLocation, etDescription;
    private Button btnCreate;
    private ImageView ivEventImage;
    private LinearLayout uploadPlaceholder;
    private DatabaseHelper dbHelper;
    private Uri selectedImageUri;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        dbHelper = new DatabaseHelper(this);

        etTitle = findViewById(R.id.etTitle);
        etDate = findViewById(R.id.etDate);
        etLocation = findViewById(R.id.etLocation);
        etDescription = findViewById(R.id.etDescription);
        btnCreate = findViewById(R.id.btnCreate);
        ivEventImage = findViewById(R.id.ivEventImage);
        uploadPlaceholder = findViewById(R.id.uploadPlaceholder);

        android.widget.ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Initialize image picker
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            ivEventImage.setImageURI(selectedImageUri);
                            ivEventImage.setVisibility(View.VISIBLE);
                            uploadPlaceholder.setVisibility(View.GONE);
                        }
                    }
                });

        uploadPlaceholder.setOnClickListener(v -> openImagePicker());
        ivEventImage.setOnClickListener(v -> openImagePicker());

        btnCreate.setOnClickListener(v -> saveEvent());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void saveEvent() {
        String title = etTitle.getText().toString();
        String date = etDate.getText().toString();
        String loc = etLocation.getText().toString();
        String desc = etDescription.getText().toString();

        if (title.isEmpty() || date.isEmpty() || loc.isEmpty()) {
            Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Store image URI as string (in real app, you'd upload to server or save
        // locally)
        String imageUriString = selectedImageUri != null ? selectedImageUri.toString() : "";

        // Using 0 as placeholder image ID (you could store URI in Event model)
        Event newEvent = new Event(title, date, loc, desc, 0);
        boolean success = dbHelper.createEvent(newEvent);

        if (success) {
            Toast.makeText(this, "Event Created!", Toast.LENGTH_SHORT).show();
            finish(); // Go back to Dashboard
        } else {
            Toast.makeText(this, "Failed to create event", Toast.LENGTH_SHORT).show();
        }
    }
}
