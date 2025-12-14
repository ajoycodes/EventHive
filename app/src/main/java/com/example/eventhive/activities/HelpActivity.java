package com.example.eventhive.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.eventhive.R;

public class HelpActivity extends AppCompatActivity {

    private Button btnEmailSupport;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        btnEmailSupport = findViewById(R.id.btnEmailSupport);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        btnEmailSupport.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:support@eventhive.com"));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "EventHive Support Request");

            try {
                startActivity(Intent.createChooser(emailIntent, "Send email via..."));
            } catch (android.content.ActivityNotFoundException ex) {
                android.widget.Toast.makeText(this, "No email app found", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }
}
