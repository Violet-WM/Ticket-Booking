package com.example.ticketcard;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketcard.model.Fixture;
import com.example.ticketcard.model.Match;
import com.example.ticketcard.model.Team;
import com.example.ticketcard.model.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class AddEventsActivity extends AppCompatActivity {
    private Spinner leaguesSpinner;
    private RecyclerView fixturesListRecycler;
    private FloatingActionButton submitButton;
    private Button generateFixturesButton;

    private List<Team> teamsList;
    private List<Fixture> fixturesList;
    private FixtureAdapter fixtureAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_events);

        String[] leagues = {"Kenya Premier League", "National Super League", "Women's Premier League"};

        generateFixturesButton = findViewById(R.id.generateFixturesButton);
        fixturesListRecycler = findViewById(R.id.fixturesListRecycler);
        fixturesListRecycler.setLayoutManager(new LinearLayoutManager(this));
        submitButton = findViewById(R.id.submitButton);

        leaguesSpinner = findViewById(R.id.leaguesSpinner);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, leagues);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        leaguesSpinner.setAdapter(spinnerAdapter);

        teamsList = new ArrayList<>();
        fixturesList = new ArrayList<>();
        fixtureAdapter = new FixtureAdapter(fixturesList);
        fixturesListRecycler.setAdapter(fixtureAdapter);

        generateFixturesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedLeague = leaguesSpinner.getSelectedItem().toString();
                fetchTeams(selectedLeague);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitFixturesToFirebase();
            }
        });
    }

    private void fetchTeams(String selectedLeague) {
        FirebaseDatabase.getInstance().getReference("teams")
                .orderByChild("teamLeague")
                .equalTo(selectedLeague)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        teamsList.clear();
                        for (DataSnapshot teamSnapshot : snapshot.getChildren()) {
                            String teamName = teamSnapshot.child("teamName").getValue(String.class);
                            String teamLogoUrl = teamSnapshot.child("teamLogoUrl").getValue(String.class);
                            String homeStadium = teamSnapshot.child("home").getValue(String.class);
                            teamsList.add(new Team(teamName, teamLogoUrl, homeStadium));
                        }
                        generateFixtures();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("AddEventsActivity", "Error fetching teams", error.toException());
                    }
                });
    }

    private void generateFixtures() {
        fixturesList.clear();
        List<List<Match>> allFixtures = Utils.generateDoubleRoundRobinFixtures(teamsList);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, Calendar.AUGUST);
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        for (int round = 0; round < allFixtures.size(); round++) {
            List<Match> roundMatches = allFixtures.get(round);
            for (Match match : roundMatches) {
                String matchDate = Utils.getNextValidDate(calendar);
                String time = new Random().nextBoolean() ? "17:00HRS" : "14:00HRS";
                fixturesList.add(new Fixture(match.getTeamA() + " vs " + match.getTeamB(),
                        time + " . " + matchDate + " . Round " + (round + 1)));
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
        }

        fixtureAdapter.notifyDataSetChanged();
    }

    private void submitFixturesToFirebase() {
        String selectedLeague = leaguesSpinner.getSelectedItem().toString();
        Map<String, Object> fixturesMap = new HashMap<>();
        for (int i = 0; i < fixturesList.size(); i++) {
            Fixture fixture = fixturesList.get(i);
            Map<String, Object> fixtureDetails = new HashMap<>();
            fixtureDetails.put("matchup", fixture.getMatchup());
            fixtureDetails.put("matchDetails", fixture.getMatchDetails());
            fixturesMap.put("Fixture " + (i + 1), fixtureDetails);
        }
        FirebaseDatabase.getInstance().getReference("fixtures")
                .child(selectedLeague)
                .setValue(fixturesMap)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("AddEventsActivity", "Fixtures successfully uploaded to Firebase");
                    } else {
                        Log.e("AddEventsActivity", "Error uploading fixtures", task.getException());
                    }
                });
    }
}
