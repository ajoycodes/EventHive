package com.example.eventhive.activities;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventhive.R;
import com.example.eventhive.adapters.EventAdapter;
import com.example.eventhive.models.Event;

import java.util.ArrayList;

public class DashboardActivity extends AppCompatActivity {

    RecyclerView recyclerEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);


        recyclerEvents = findViewById(R.id.recyclerEvents);
        recyclerEvents.setLayoutManager(new LinearLayoutManager(this));


        ArrayList<Event> eventList = new ArrayList<>();

        eventList.add(new Event(
                "17",
                "Dec",
                "WaveFest - Feel The Winter",
                "Madani Avenue, Bashundhara R/A, Dhaka.",
                "Prime Wave Communication",
                Color.parseColor("#7E8AFF")
        ));

        eventList.add(new Event(
                "17",
                "Dec",
                "WaveFest - Feel The Winter",
                "Madani Avenue, Bashundhara R/A, Dhaka.",
                "Prime Wave Communication",
                Color.parseColor("#F6C6DA")
        ));

        eventList.add(new Event(
                "17",
                "Dec",
                "WaveFest - Feel The Winter",
                "Madani Avenue, Bashundhara R/A, Dhaka.",
                "Prime Wave Communication",
                Color.parseColor("#C6F6DA")
        ));

        // Set adapter
        EventAdapter adapter = new EventAdapter(this, eventList);
        recyclerEvents.setAdapter(adapter);
    }
}