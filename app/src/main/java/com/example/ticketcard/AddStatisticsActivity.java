package com.example.ticketcard;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

        //Allow the user to select a team and add details

        //Upload the entered data to DB
}