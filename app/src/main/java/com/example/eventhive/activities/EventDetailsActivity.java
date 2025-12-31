package com.example.eventhive.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.example.eventhive.R;
import com.example.eventhive.models.Event;
import com.example.eventhive.utils.SessionManager;
import com.example.eventhive.databases.DatabaseHelper;
import com.example.eventhive.utils.ImageStorageHelper;

public class EventDetailsActivity extends AppCompatActivity {

    private Event event;
    private Button btnPurchase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_event_details);

            // Get views
            ImageView ivEvent = findViewById(R.id.detailImage);
            TextView tvTitle = findViewById(R.id.detailTitle);
            TextView tvLocation = findViewById(R.id.detailLocation);
            TextView tvSubLoc = findViewById(R.id.detailSubLoc);
            TextView tvDesc = findViewById(R.id.detailDescription);
            TextView tvTime = findViewById(R.id.detailTime);
            TextView tvEventStatus = findViewById(R.id.tvEventStatus);
            TextView tvEventType = findViewById(R.id.tvEventType);
            TextView tvTicketPrice = findViewById(R.id.tvTicketPrice);
            TextView tvTicketAvailability = findViewById(R.id.tvTicketAvailability);
            HorizontalScrollView galleryScrollView = findViewById(R.id.galleryScrollView);
            LinearLayout galleryImagesContainer = findViewById(R.id.galleryImagesContainer);

            ImageView btnBack = findViewById(R.id.btnBack);
            btnPurchase = findViewById(R.id.btnPurchase);

            // Retrieve event
            event = (Event) getIntent().getSerializableExtra("EVENT");
            if (event != null) {
                // Set title
                if (tvTitle != null)
                    tvTitle.setText(event.getTitle());

                // Set event type
                if (tvEventType != null) {
                    String eventType = event.getEventType();
                    if (eventType != null && !eventType.isEmpty()) {
                        tvEventType.setText("📋 " + eventType);
                        tvEventType.setVisibility(View.VISIBLE);
                    } else {
                        tvEventType.setVisibility(View.GONE);
                    }
                }

                // Set event status with color coding
                if (tvEventStatus != null) {
                    String status = event.getStatus();
                    tvEventStatus.setText(status);

                    // Set background color based on status
                    if (Event.STATUS_ACTIVE.equals(status)) {
                        tvEventStatus.setBackgroundColor(getResources().getColor(R.color.brand_primary, null));
                    } else if (Event.STATUS_HOLD.equals(status)) {
                        tvEventStatus.setBackgroundColor(Color.parseColor("#FF9800")); // Orange
                    } else if (Event.STATUS_CANCELLED.equals(status)) {
                        tvEventStatus.setBackgroundColor(getResources().getColor(R.color.destructive, null));
                    }
                }

                // Set location
                if (tvLocation != null)
                    tvLocation.setText(event.getLocation());
                if (tvSubLoc != null)
                    tvSubLoc.setText("Dhaka, Bangladesh");

                // Set description
                if (tvDesc != null)
                    tvDesc.setText(event.getDescription());

                // Set time
                if (tvTime != null) {
                    tvTime.setText("🕒 " + event.getDate());
                }

                // Set ticket price
                if (tvTicketPrice != null) {
                    double price = event.getTicketPrice();
                    if (price > 0) {
                        tvTicketPrice.setText("৳" + String.format("%.2f", price));
                    } else {
                        tvTicketPrice.setText("Free");
                    }
                }

                // Set ticket availability
                if (tvTicketAvailability != null) {
                    int quantity = event.getTicketQuantity();
                    if (quantity > 0) {
                        tvTicketAvailability.setText(quantity + " tickets");
                    } else {
                        tvTicketAvailability.setText("No info");
                    }
                }

                // Load cover image from internal storage
                String coverImagePath = event.getCoverImagePath();
                if (coverImagePath != null && !coverImagePath.isEmpty()) {
                    Bitmap coverBitmap = ImageStorageHelper.loadImageFromInternalStorage(this, coverImagePath);
                    if (coverBitmap != null && ivEvent != null) {
                        ivEvent.setImageBitmap(coverBitmap);
                    }
                }

                // Load and display gallery images
                String galleryPathsString = event.getGalleryImagePaths();
                if (galleryPathsString != null && !galleryPathsString.isEmpty()) {
                    String[] galleryPaths = galleryPathsString.split(",");
                    if (galleryPaths.length > 0 && galleryImagesContainer != null) {
                        galleryScrollView.setVisibility(View.VISIBLE);

                        for (String path : galleryPaths) {
                            if (path != null && !path.trim().isEmpty()) {
                                Bitmap bitmap = ImageStorageHelper.loadImageFromInternalStorage(this, path.trim());
                                if (bitmap != null) {
                                    addGalleryImageView(galleryImagesContainer, bitmap);
                                }
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Error: Event data missing", Toast.LENGTH_SHORT).show();
            }

            if (btnBack != null) {
                btnBack.setOnClickListener(v -> finish());
            }

            setupPurchaseButton();

        } catch (Exception e) {
            android.util.Log.e("EventDetailsActivity", "Error in onCreate", e);
            Toast.makeText(this, "Error loading event details: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void addGalleryImageView(LinearLayout container, Bitmap bitmap) {
        // Create card for gallery image
        CardView cardView = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                300, // width in pixels
                300 // height in pixels
        );
        cardParams.setMargins(8, 0, 8, 0);
        cardView.setLayoutParams(cardParams);
        cardView.setRadius(16f);
        cardView.setCardElevation(0f);

        // Create image view
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new CardView.LayoutParams(
                CardView.LayoutParams.MATCH_PARENT,
                CardView.LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageBitmap(bitmap);

        cardView.addView(imageView);
        container.addView(cardView);
    }

    private void setupPurchaseButton() {
        DatabaseHelper db = new DatabaseHelper(this);
        SessionManager session = new SessionManager(this);

        btnPurchase.setOnClickListener(v -> {
            if (!session.isLoggedIn()) {
                Toast.makeText(this, "Please Login First", Toast.LENGTH_SHORT).show();
                return;
            }

            String role = session.getUserRole();
            if ("Admin".equalsIgnoreCase(role) || "Organizer".equalsIgnoreCase(role)) {
                Toast.makeText(this, "Organizers/Admins cannot purchase tickets", Toast.LENGTH_SHORT).show();
                return;
            }

            if (event == null) {
                Toast.makeText(this, "Event details not loaded.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check event status
            String status = event.getStatus();
            if (Event.STATUS_HOLD.equals(status)) {
                Toast.makeText(this, "Ticket sales temporarily on hold", Toast.LENGTH_LONG).show();
                return;
            }

            if (Event.STATUS_CANCELLED.equals(status)) {
                Toast.makeText(this, "Event has been cancelled", Toast.LENGTH_LONG).show();
                return;
            }

            // Generate unique ticket code
            String uniqueCode = "TICKET-" + System.currentTimeMillis() + "-" + (int) (Math.random() * 1000);
            int userId = session.getUserId();
            int eventId = event.getId();
            long timestamp = System.currentTimeMillis();

            boolean success = db.registerTicket(userId, eventId, uniqueCode, timestamp);
            if (success) {
                Intent intent = new Intent(EventDetailsActivity.this, TicketConfirmationActivity.class);
                intent.putExtra("TICKET_CODE", uniqueCode);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Purchase Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
