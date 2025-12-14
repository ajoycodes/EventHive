package com.example.eventhive.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventhive.R;
import com.example.eventhive.adapters.EventAdapter;
import com.example.eventhive.databases.DatabaseHelper;
import com.example.eventhive.models.Event;
import java.util.List;

public class EventListActivity extends AppCompatActivity {

    private RecyclerView recyclerEvents;
    private EventAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        dbHelper = new DatabaseHelper(this);
        recyclerEvents = findViewById(R.id.recyclerEvents);

        recyclerEvents.setLayoutManager(new LinearLayoutManager(this));
        loadEvents();

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void loadEvents() {
        List<Event> events = dbHelper.getAllEvents();
        adapter = new EventAdapter(this, events);
        recyclerEvents.setAdapter(adapter);
    }
}
