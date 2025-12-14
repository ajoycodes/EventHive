package com.example.eventhive.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.eventhive.R;
import com.example.eventhive.databases.DatabaseHelper;
import com.example.eventhive.models.Event;

public class EditEventActivity extends AppCompatActivity {

    private EditText etTitle, etDate, etLocation, etDescription;
    private Button btnUpdate;
    private DatabaseHelper dbHelper;
    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event); // Reuse layout

        dbHelper = new DatabaseHelper(this);

        etTitle = findViewById(R.id.etTitle);
        etDate = findViewById(R.id.etDate);
        etLocation = findViewById(R.id.etLocation);
        etDescription = findViewById(R.id.etDescription);
        btnUpdate = findViewById(R.id.btnCreate);

        // Change button text
        btnUpdate.setText("Update Event");

        event = (Event) getIntent().getSerializableExtra("EVENT");
        if (event != null) {
            etTitle.setText(event.getTitle());
            etDate.setText(event.getDate());
            etLocation.setText(event.getLocation());
            etDescription.setText(event.getDescription());
        }

        btnUpdate.setOnClickListener(v -> updateEvent());
    }

    private void updateEvent() {
        String title = etTitle.getText().toString();
        String date = etDate.getText().toString();
        String loc = etLocation.getText().toString();
        String desc = etDescription.getText().toString();

        if (title.isEmpty() || date.isEmpty() || loc.isEmpty()) {
            Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (event != null) {
            Event updatedEvent = new Event(event.getId(), title, date, loc, desc, event.getImageResId(),
                    event.getStatus());
            boolean success = dbHelper.updateEvent(updatedEvent);
            if (success) {
                Toast.makeText(this, "Event Updated!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Update Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
