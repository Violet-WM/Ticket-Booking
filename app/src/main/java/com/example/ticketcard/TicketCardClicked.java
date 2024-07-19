package com.example.ticketcard;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ticketcard.model.Players;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TicketCardClicked extends AppCompatActivity {

    private RecyclerView playersRecyclerView;
    private PlayersAdapter playersAdapter;
    private List<Players> playersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_card_clicked);

        String imageUrl = getIntent().getStringExtra("imageURL");
        String imageDescription = getIntent().getStringExtra("imageDescription");

        TextView matchTextView = findViewById(R.id.matchTextView);
        matchTextView.setText(imageDescription);

        ImageView roundedImageview = findViewById(R.id.roundedImageView);
        Glide.with(this).load(imageUrl).into(roundedImageview);

        playersRecyclerView = findViewById(R.id.playersRecyclerView);
        playersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        playersList = new ArrayList<>();
        playersAdapter = new PlayersAdapter(this, playersList);
        playersRecyclerView.setAdapter(playersAdapter);

        fetchPlayers();
    }

    private void fetchPlayers() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("teams");
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                playersList.clear();
                for (DataSnapshot teamSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot playerSnapshot : teamSnapshot.child("players").getChildren()) {
                        Players player = playerSnapshot.getValue(Players.class);
                        if (player != null) {
                            playersList.add(player);
                        }
                    }
                }
                playersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase Database Error", "Error getting data: ", databaseError.toException());
            }
        });
    }
}
