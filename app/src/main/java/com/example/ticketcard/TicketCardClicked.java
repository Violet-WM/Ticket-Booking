package com.example.ticketcard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TicketCardClicked extends AppCompatActivity {

    ImageView arrow;
    Button bookYourSeat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_card_clicked);
        

        arrow = findViewById(R.id.ticketcardbackarrow);
        arrow.setOnClickListener(v -> {
            Intent intent = new Intent(this, FkfPremierLeague.class);
                });

        bookYourSeat = findViewById(R.id.bookYourSeat);
        bookYourSeat.setOnClickListener(v -> {
            Intent intent = new Intent(this, Buyticket.class);
            startActivity(intent);
        });

    }
}