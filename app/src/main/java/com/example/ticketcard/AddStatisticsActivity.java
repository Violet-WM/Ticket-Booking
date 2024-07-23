package com.example.ticketcard;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ticketcard.model.Stats;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddStatisticsActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference teamsRef;
    Spinner teamsSpinner;
    List<String> teamsList;
    private EditText matchesEditText;
    private EditText winsEditText;
    private EditText drawsEditText;
    private EditText lossEditText;
    private FloatingActionButton uploadButton;
    String matches, wins, draws, loss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_statistics);

        // Pull data about the teams from DB
        // Initialize Firebase Realtime Database
        firebaseDatabase = FirebaseDatabase.getInstance();
        teamsRef = firebaseDatabase.getReference("teams");

        // Initialize Spinner
        teamsSpinner = findViewById(R.id.teamsSpinner);

        // Initialize list to store league names
        teamsList = new ArrayList<>();

        // Retrieve data from Firebase and populate the Spinner
        loadTeamData();

        matchesEditText = findViewById(R.id.matchesEditText);
        winsEditText = findViewById(R.id.winsEditText);
        drawsEditText = findViewById(R.id.drawsEditText);
        lossEditText = findViewById(R.id.lostEditText);
        uploadButton = findViewById(R.id.uploadButton);

        //Allow the user to select a team and add details
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                matches = matchesEditText.getText().toString().trim();
                wins = winsEditText.getText().toString().trim();
                draws = drawsEditText.getText().toString().trim();
                loss = lossEditText.getText().toString().trim();
                String team = teamsSpinner.getSelectedItem().toString();

                if(matches.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please enter matches", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(wins.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please enter wins", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(draws.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please enter draws", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(loss.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please enter losses", Toast.LENGTH_SHORT).show();
                    return;
                }

                saveToDB(team);

            }
        });
        //Upload the entered data to DB
    }

    private void saveToDB(String team){

        Stats stats = new Stats(matches, wins, draws, loss);

        // Find the selected team's node and update stats
        teamsRef.orderByChild("teamName").equalTo(team).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        snapshot.getRef().child("stats").setValue(stats).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                // Save stats for team
                                Toast.makeText(AddStatisticsActivity.this, "Uploaded successfully", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    Toast.makeText(AddStatisticsActivity.this, "Team not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AddStatisticsActivity.this, "Failed to upload data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadTeamData() {
        teamsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                teamsList.clear(); // Clear the list to avoid duplicates
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String team = snapshot.child("teamName").getValue(String.class);
                    teamsList.add(team);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(AddStatisticsActivity.this, android.R.layout.simple_spinner_item, teamsList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                teamsSpinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
