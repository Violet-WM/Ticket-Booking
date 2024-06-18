package com.example.ticketcard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class football_categories extends AppCompatActivity {
    TextView Text1;
    ImageView arrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_football_categories);

        Text1 = findViewById(R.id.text1);
        arrow = findViewById(R.id.footballcategoriesbackarrow);


        Text1.setOnClickListener(v -> {
            Intent intent = new Intent(football_categories.this,FkfPremierLeague.class);
            startActivity(intent);
        });

        arrow.setOnClickListener(v -> {
            Intent intent = new Intent(this, Home.class);
            startActivity(intent);
        });


    }

}