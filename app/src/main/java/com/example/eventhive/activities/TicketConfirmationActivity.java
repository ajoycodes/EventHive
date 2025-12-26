package com.example.eventhive.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.eventhive.R;

public class TicketConfirmationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_confirmation);

        TextView tvCode = findViewById(R.id.tvTicketCode);
        Button btnDone = findViewById(R.id.btnDone);

        String code = getIntent().getStringExtra("TICKET_CODE");
        tvCode.setText(code != null ? code : "ERROR");

        btnDone.setOnClickListener(v -> {
            Intent intent = new Intent(this, UserDashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }
}
