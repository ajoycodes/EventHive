package com.example.eventhive.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.eventhive.R;
import com.example.eventhive.models.Event;
import com.example.eventhive.utils.SessionManager;
import com.example.eventhive.databases.DatabaseHelper;

public class EventDetailsActivity extends AppCompatActivity {

    private Event event;
    private Button btnPurchase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_event_details);

            ImageView ivEvent = findViewById(R.id.detailImage);
            TextView tvTitle = findViewById(R.id.detailTitle);
            TextView tvLocation = findViewById(R.id.detailLocation);
            TextView tvSubLoc = findViewById(R.id.detailSubLoc); // Added this in XML
            TextView tvDesc = findViewById(R.id.detailDescription);
            TextView tvTime = findViewById(R.id.detailTime); // Added this

            ImageView btnBack = findViewById(R.id.btnBack);
            btnPurchase = findViewById(R.id.btnPurchase);

            // Retrieve event
            event = (Event) getIntent().getSerializableExtra("EVENT");
            if (event != null) {
                if (tvTitle != null)
                    tvTitle.setText(event.getTitle());

                // Basic location setting
                if (tvLocation != null)
                    tvLocation.setText(event.getLocation());
                if (tvSubLoc != null)
                    tvSubLoc.setText("Dhaka, Bangladesh"); // Placeholder or parsing

                if (tvDesc != null)
                    tvDesc.setText(event.getDescription());

                // Time logic
                if (tvTime != null) {
                    tvTime.setText("🕒 " + event.getDate());
                    // Using event date string as time/date for now
                }

                // ivEvent.setImageResource(event.getImageResId()); Placeholder
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

            String uniqueCode = "TICKET-" + System.currentTimeMillis() + "-" + (int) (Math.random() * 1000);
            int userId = session.getUserId();
            int eventId = event.getId();

            boolean success = db.registerTicket(userId, eventId, uniqueCode);
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
