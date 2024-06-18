package com.example.ticketcard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class FkfPremierLeague extends AppCompatActivity {

    ImageView backarrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_fkf_premier_league);

        backarrow = findViewById(R.id.back_arrow);

        backarrow.setOnClickListener(v -> {
            Intent intent = new Intent(FkfPremierLeague.this, football_categories.class);
            startActivity(intent);

        });
    }
}