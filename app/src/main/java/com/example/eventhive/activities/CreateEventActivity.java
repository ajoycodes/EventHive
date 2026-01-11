package com.example.eventhive.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.example.eventhive.R;
import com.example.eventhive.databases.DatabaseHelper;
import com.example.eventhive.models.Event;
import com.example.eventhive.utils.ImageStorageHelper;
import com.example.eventhive.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;

public class CreateEventActivity extends AppCompatActivity {

    private EditText etTitle, etDate, etLocation, etDescription, etTicketPrice, etTicketQuantity;
    private Button btnCreate, btnAddGalleryImages;
    private ImageView ivEventImage;
    private LinearLayout uploadPlaceholder, galleryImagesContainer;
    private HorizontalScrollView galleryScrollView;
    private Spinner spinnerEventType;
    private DatabaseHelper dbHelper;

    // Image URIs
    private Uri selectedCoverImageUri;
    private List<Uri> galleryImageUris = new ArrayList<>();

    // Image paths after saving to internal storage
    private String coverImagePath = "";
    private List<String> galleryImagePaths = new ArrayList<>();

    private ActivityResultLauncher<Intent> coverImagePickerLauncher;
    private ActivityResultLauncher<Intent> galleryImagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        dbHelper = new DatabaseHelper(this);

        // Initialize views
        etTitle = findViewById(R.id.etTitle);
        etDate = findViewById(R.id.etDate);
        etLocation = findViewById(R.id.etLocation);
        etDescription = findViewById(R.id.etDescription);
        etTicketPrice = findViewById(R.id.etTicketPrice);
        etTicketQuantity = findViewById(R.id.etTicketQuantity);
        btnCreate = findViewById(R.id.btnCreate);
        btnAddGalleryImages = findViewById(R.id.btnAddGalleryImages);
        ivEventImage = findViewById(R.id.ivEventImage);
        uploadPlaceholder = findViewById(R.id.uploadPlaceholder);
        galleryImagesContainer = findViewById(R.id.galleryImagesContainer);
        galleryScrollView = findViewById(R.id.galleryScrollView);
        spinnerEventType = findViewById(R.id.spinnerEventType);

        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Setup event type spinner
        setupEventTypeSpinner();

        // Initialize cover image picker
        coverImagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedCoverImageUri = result.getData().getData();
                        if (selectedCoverImageUri != null) {
                            ivEventImage.setImageURI(selectedCoverImageUri);
                            ivEventImage.setVisibility(View.VISIBLE);
                            uploadPlaceholder.setVisibility(View.GONE);
                        }
                    }
                });

        // Initialize gallery image picker (multiple selection)
        galleryImagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        if (result.getData().getClipData() != null) {
                            // Multiple images selected
                            int count = result.getData().getClipData().getItemCount();
                            for (int i = 0; i < count && i < 10; i++) { // Limit to 10 images
                                Uri imageUri = result.getData().getClipData().getItemAt(i).getUri();
                                galleryImageUris.add(imageUri);
                            }
                        } else if (result.getData().getData() != null) {
                            // Single image selected
                            galleryImageUris.add(result.getData().getData());
                        }
                        displayGalleryImages();
                    }
                });

        uploadPlaceholder.setOnClickListener(v -> openCoverImagePicker());
        ivEventImage.setOnClickListener(v -> openCoverImagePicker());
        btnAddGalleryImages.setOnClickListener(v -> openGalleryImagePicker());
        btnCreate.setOnClickListener(v -> saveEvent());
    }

    private void setupEventTypeSpinner() {
        // Event type options
        String[] eventTypes = { "Concert", "Seminar", "Festival", "Workshop", "Sports", "Conference", "Other" };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, eventTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEventType.setAdapter(adapter);
    }

    private void openCoverImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        coverImagePickerLauncher.launch(intent);
    }

    private void openGalleryImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // Allow multiple selection
        galleryImagePickerLauncher.launch(intent);
    }

    private void displayGalleryImages() {
        galleryImagesContainer.removeAllViews(); // Clear existing views

        if (galleryImageUris.isEmpty()) {
            galleryScrollView.setVisibility(View.GONE);
            return;
        }

        galleryScrollView.setVisibility(View.VISIBLE);

        for (int i = 0; i < galleryImageUris.size(); i++) {
            final int index = i;
            Uri uri = galleryImageUris.get(i);

            // Create a card for each gallery image
            CardView cardView = new CardView(this);
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    200, // width in dp (will be converted to px)
                    200 // height in dp
            );
            cardParams.setMargins(8, 0, 8, 0);
            cardView.setLayoutParams(cardParams);
            cardView.setRadius(12f);

            // Create image view
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new CardView.LayoutParams(
                    CardView.LayoutParams.MATCH_PARENT,
                    CardView.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageURI(uri);

            // Add click listener to remove image
            imageView.setOnClickListener(v -> {
                galleryImageUris.remove(index);
                displayGalleryImages(); // Refresh display
                Toast.makeText(this, "Image removed", Toast.LENGTH_SHORT).show();
            });

            cardView.addView(imageView);
            galleryImagesContainer.addView(cardView);
        }
    }

    private void saveEvent() {
        // Get input values
        String title = etTitle.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String priceString = etTicketPrice.getText().toString().trim();
        String quantityString = etTicketQuantity.getText().toString().trim();
        String eventType = spinnerEventType.getSelectedItem().toString();

        // Validation
        if (title.isEmpty() || date.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields (Title, Date, Location)",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (priceString.isEmpty()) {
            Toast.makeText(this, "Please enter ticket price", Toast.LENGTH_SHORT).show();
            return;
        }

        if (quantityString.isEmpty()) {
            Toast.makeText(this, "Please enter ticket quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        double ticketPrice;
        int ticketQuantity;
        try {
            ticketPrice = Double.parseDouble(priceString);
            if (ticketPrice < 0) {
                Toast.makeText(this, "Ticket price must be positive", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid ticket price format", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            ticketQuantity = Integer.parseInt(quantityString);
            if (ticketQuantity <= 0) {
                Toast.makeText(this, "Ticket quantity must be greater than 0", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid ticket quantity format", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading state (simple approach: disable button)
        btnCreate.setEnabled(false);
        btnCreate.setText("Creating...");

        // Save cover image to internal storage (Note: In a real production app, this
        // should go to Firebase Storage)
        // For this simple student version, we keep using local paths but store the PATH
        // string in Firestore
        // This means images only work on the specific device that created them.
        if (selectedCoverImageUri != null) {
            coverImagePath = ImageStorageHelper.saveImageToInternalStorage(this, selectedCoverImageUri);
        }

        // Save gallery images
        galleryImagePaths.clear();
        for (Uri uri : galleryImageUris) {
            String path = ImageStorageHelper.saveImageToInternalStorage(this, uri);
            if (path != null) {
                galleryImagePaths.add(path);
            }
        }
        String galleryPathsString = String.join(",", galleryImagePaths);

        // Prepare Firestore Map
        com.google.firebase.auth.FirebaseAuth auth = com.google.firebase.auth.FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "You must be logged in!", Toast.LENGTH_SHORT).show();
            btnCreate.setEnabled(true);
            btnCreate.setText("Create Event");
            return;
        }

        String organizerId = auth.getCurrentUser().getUid();
        long timestamp = System.currentTimeMillis();

        java.util.Map<String, Object> eventMap = new java.util.HashMap<>();
        eventMap.put("title", title);
        eventMap.put("date", date);
        eventMap.put("location", location);
        eventMap.put("description", description);
        eventMap.put("status", Event.STATUS_ACTIVE);
        eventMap.put("ticketPrice", ticketPrice);
        eventMap.put("ticketQuantity", ticketQuantity);
        eventMap.put("coverImagePath", coverImagePath);
        eventMap.put("galleryImagePaths", galleryPathsString);
        eventMap.put("eventType", eventType);
        eventMap.put("organizerId", organizerId);
        eventMap.put("createdAt", timestamp);

        // Write to Firestore
        com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore
                .getInstance();
        db.collection("events")
                .add(eventMap)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(CreateEventActivity.this, "Event Created Successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    android.util.Log.e("CreateEvent", "Error adding event", e);
                    Toast.makeText(CreateEventActivity.this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    btnCreate.setEnabled(true);
                    btnCreate.setText("Create Event");
                });
    }
}
