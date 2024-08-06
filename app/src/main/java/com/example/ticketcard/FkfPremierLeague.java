package com.example.ticketcard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class FkfPremierLeague extends AppCompatActivity {

    ImageView backarrow;
    LinearLayout match;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_fkf_premier_league);

        backarrow = findViewById(R.id.back_arrow);
        match = findViewById(R.id.gormahiaLinearLayout);

        match.setOnClickListener(v -> {
            Intent intent = new Intent(this, TicketCardClicked.class);
            startActivity(intent);

        });

        backarrow.setOnClickListener(v -> {
            Intent intent = new Intent(FkfPremierLeague.this, football_categories.class);
            startActivity(intent);

        });
    }
}