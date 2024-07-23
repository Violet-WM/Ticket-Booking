package com.example.ticketcard;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
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
    private ImageView roundedImageViewTeamOne, roundedImageViewTeamTwo;
    private TextView winsTextView, lossTextView, drawsTextView, matchesTextView;
    private TextView textViewTeamOne, textViewTeamTwo;
    private Button teamOneButton, teamTwoButton;

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

        textViewTeamOne = findViewById(R.id.textViewTeamOne);
        textViewTeamTwo = findViewById(R.id.textViewTeamTwo);

        teamOneButton = findViewById(R.id.addTeamsButton);
        teamTwoButton = findViewById(R.id.addVenuesButton);

        roundedImageViewTeamOne = findViewById(R.id.roundedImageViewTeamOne);
        roundedImageViewTeamTwo = findViewById(R.id.roundedImageViewTeamTwo);

        playersRecyclerView = findViewById(R.id.playersRecyclerView);
        playersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        playersList = new ArrayList<>();
        playersAdapter = new PlayersAdapter(this, playersList);
        playersRecyclerView.setAdapter(playersAdapter);

        winsTextView = findViewById(R.id.winsText);
        lossTextView = findViewById(R.id.lostText);
        drawsTextView = findViewById(R.id.drawsText);
        matchesTextView = findViewById(R.id.matchesText);

        fetchTeamData(textViewTeamOne.getText().toString(), textViewTeamTwo.getText().toString());
    }

    private void fetchTeamData(String teamOne, String teamTwo) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("teams");
        db.orderByChild("teamName").equalTo(teamOne).addListenerForSingleValueEvent(new ValueEventListener() {
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

                    // If teamOne exists in db get details and load the image and team name
                    if(teamSnapshot.exists()){
                        teamOneButton.setText(teamSnapshot.child("teamName").getValue(String.class));
                        String imageUrl = teamSnapshot.child("teamLogoUrl").getValue(String.class);
                        Glide.with(getApplicationContext()).load(imageUrl).into(roundedImageViewTeamOne);
                    }

                    // Fetch statistics
                    DataSnapshot statsSnapshot = teamSnapshot.child("stats");
                    if (statsSnapshot.exists()) {
                        String wins = statsSnapshot.child("wins").getValue(String.class);
                        String loss = statsSnapshot.child("loss").getValue(String.class);
                        String draws = statsSnapshot.child("draws").getValue(String.class);
                        String matches = statsSnapshot.child("matches").getValue(String.class);

                        winsTextView.setText(wins != null ? wins : "0");
                        lossTextView.setText(loss != null ? loss : "0");
                        drawsTextView.setText(draws != null ? draws : "0");
                        matchesTextView.setText(matches != null ? matches : "0");
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
