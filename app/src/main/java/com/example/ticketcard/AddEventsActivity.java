package com.example.ticketcard;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class AddEventsActivity extends AppCompatActivity {
    private Spinner leaguesSpinner;
    private RecyclerView fixturesListRecycler;
    private FloatingActionButton submitButton;
    private Button generateFixturesButton;
    private ProgressBar progressBar;
    private TextView timeNotis;

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
        progressBar = findViewById(R.id.progressBar);
        timeNotis = findViewById(R.id.timeNotis);

        leaguesSpinner = findViewById(R.id.leaguesSpinner);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, leagues);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        leaguesSpinner.setAdapter(spinnerAdapter);

        teamsList = new ArrayList<>();
        fixturesList = new ArrayList<>();
        fixtureAdapter = new FixtureAdapter(fixturesList);
        fixturesListRecycler.setAdapter(fixtureAdapter);
        Log.d("tag", "Recycler is set bana");

        generateFixturesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE); // Show progress bar
                timeNotis.setVisibility(View.VISIBLE);
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
                            Log.d("tag", "Fetching and adding teams to list.");
                        }
                        generateFixtures();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Firebase", "Failed to fetch teams", error.toException());
                    }
                });
    }

    private void generateFixtures() {
        fixturesList.clear();
        List<List<Match>> allFixtures = Utils.generateDoubleRoundRobinFixtures(teamsList);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
        calendar.set(Calendar.MONTH, Calendar.AUGUST);
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        int totalRounds = allFixtures.size();
        int roundsPerMonth = totalRounds / 10;
        int extraRounds = totalRounds % 10;

        int currentMonth = Calendar.AUGUST;
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        for (int round = 0; round < totalRounds; round++) {
            List<Match> roundMatches = allFixtures.get(round);

            for (Match match : roundMatches) {
                String matchDate = Utils.getNextValidDate(calendar);
                String time = new Random().nextBoolean() ? "17:00HRS" : "14:00HRS";
                fixturesList.add(new Fixture(match.getTeamA() + " vs " + match.getTeamB(),
                        time, matchDate, "Round " + (round + 1), match.getVenue()));
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }

            int roundsInCurrentMonth = (round < extraRounds) ? roundsPerMonth + 1 : roundsPerMonth;
            if ((round + 1) % roundsInCurrentMonth == 0) {
                currentMonth++;
                if (currentMonth > Calendar.MAY) {
                    currentMonth = Calendar.AUGUST;
                    currentYear++;
                }
                calendar.set(Calendar.MONTH, currentMonth);
                calendar.set(Calendar.YEAR, currentYear);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
            }
        }

        fixtureAdapter.notifyDataSetChanged();
        progressBar.setVisibility(View.INVISIBLE);
        timeNotis.setVisibility(View.INVISIBLE);
    }

    private void submitFixturesToFirebase() {
        FirebaseDatabase.getInstance().getReference("fixtures").removeValue(); // Clear existing data
        progressBar.setVisibility(View.VISIBLE);
        String selectedLeague = leaguesSpinner.getSelectedItem().toString();
        Map<String, Object> leagueMap = new HashMap<>();

        for (int i = 0; i < fixturesList.size(); i++) {
            Fixture fixture = fixturesList.get(i);

            String[] matchDetails = fixture.getMatchup().split(" vs ");
            String teamAName = matchDetails[0].trim();
            String teamBName = matchDetails[1].trim();

            Team teamA = teamsList.stream().filter(t -> t.getTeamName().equals(teamAName)).findFirst().orElse(null);
            Team teamB = teamsList.stream().filter(t -> t.getTeamName().equals(teamBName)).findFirst().orElse(null);

            if (teamA != null && teamB != null) {
                Calendar calendar = Calendar.getInstance();
                try {
                    calendar.setTime(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(fixture.getDate()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String day = new SimpleDateFormat("EEEE", Locale.getDefault()).format(calendar.getTime());
                String month = new SimpleDateFormat("MMMM", Locale.getDefault()).format(calendar.getTime());

                Map<String, Object> matchDetailsMap = new HashMap<>();
                matchDetailsMap.put("Team A", new HashMap<String, String>() {{
                    put("teamName", teamA.getTeamName());
                    put("teamLogoUrl", teamA.getTeamLogoUrl());
                }});
                matchDetailsMap.put("Team B", new HashMap<String, String>() {{
                    put("teamName", teamB.getTeamName());
                    put("teamLogoUrl", teamB.getTeamLogoUrl());
                }});
                matchDetailsMap.put("Date", String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
                matchDetailsMap.put("Day", day);
                matchDetailsMap.put("Month", month);
                matchDetailsMap.put("venue", fixture.getVenue());
                matchDetailsMap.put("time", fixture.getTime());
                matchDetailsMap.put("VIP", "2"); // Replace with actual VIP price
                matchDetailsMap.put("Regular", "1"); // Replace with actual Regular price

                String roundKey = Utils.sanitizeKey(fixture.getRound());
                String matchKey = Utils.sanitizeKey(teamAName + " vs " + teamBName);

                if (!leagueMap.containsKey(roundKey)) {
                    leagueMap.put(roundKey, new HashMap<String, Object>());
                }

                Map<String, Object> roundMap = (Map<String, Object>) leagueMap.get(roundKey);
                roundMap.put(matchKey, matchDetailsMap);
            }
        }

        FirebaseDatabase.getInstance().getReference("fixtures").child(selectedLeague).updateChildren(leagueMap).addOnSuccessListener(unused -> {
            Toast.makeText(AddEventsActivity.this, "Fixtures submitted to the database", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.INVISIBLE);
//            Intent intent = new Intent(AddEventsActivity.this, AdminFragment.class);
//            startActivity(intent);
        });
    }


}
