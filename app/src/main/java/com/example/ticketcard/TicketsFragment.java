package com.example.ticketcard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketcard.model.Fixtures;
import com.example.ticketcard.model.TicketEvent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TicketsFragment extends Fragment {

    private RecyclerView ticketsRecyclerView;
    private TicketEventAdapter ticketEventAdapter;
    private List<TicketEvent> eventsList;
    private Spinner roundsSpinner;
    private DatabaseReference roundsRef;
    private List<String> roundNames;
    private Map<String, List<Fixtures>> roundsMap;
    private String roundAdapter;

    public TicketsFragment() {
        // require an empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tickets, container, false);
        Log.d("tag", "In fragment");

        // Retrieve the user name from SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userName = sharedPreferences.getString("userName", "user"); // "User" is the default value if "userName" is not found

        roundsMap = new HashMap<>();
        // Initialize the RecyclerView
        ticketsRecyclerView = view.findViewById(R.id.ticketsRecyclerView);
        // Set vertical layout manager
        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        ticketsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ticketEventAdapter = new TicketEventAdapter(getContext(), eventsList);

        // Initialize Firebase reference
        roundsRef = FirebaseDatabase.getInstance().getReference("fixtures").child("Kenya Premier League");

        // Initialize rounds spinner and fetch rounds
        roundsSpinner = view.findViewById(R.id.roundsSpinner);
        fetchRounds();

        // Set spinner item selected listener
        roundsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedRound = roundNames.get(position);
                roundAdapter = selectedRound;
                Log.d("round tagggggg", "The value of selected round is " + selectedRound);
                fetchMatchesForRound(selectedRound);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        ticketEventAdapter.setOnItemClickListener(new TicketEventAdapter.OnItemClickListener() {
            @Override
            public void onClick(String imageUrl, String matchDetails, String teamA, String teamB, String teamALogo, String teamBLogo, String matchTime, String matchDate, String matchMonth, String matchVenue, String matchRegular, String matchVIP, String round) {
                Intent intent = new Intent(getActivity(), TicketCardClicked.class);
                intent.putExtra("imageURL", imageUrl);
                intent.putExtra("matchDetails", matchDetails);
                intent.putExtra("teamALogoUrl", teamALogo);
                intent.putExtra("teamBLogoUrl", teamBLogo);
                intent.putExtra("teamA", teamA);
                intent.putExtra("teamB", teamB);
                intent.putExtra("round", round);
                intent.putExtra("matchVenue", matchVenue);
                intent.putExtra("matchTime", matchTime);
                intent.putExtra("matchDate", matchDate);
                intent.putExtra("matchMonth", matchMonth);
                startActivity(intent);
        }
    });

        ticketsRecyclerView.setAdapter(ticketEventAdapter);

        return view;
    }

    private void fetchRounds() {
        roundsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                roundNames = new ArrayList<>();
                for (DataSnapshot roundSnapshot : dataSnapshot.getChildren()) {
                    String roundName = roundSnapshot.getKey();
                    if (roundName != null) {
                        roundNames.add(roundName);
                    }
                }
                // Update spinner adapter
                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, roundNames);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                roundsSpinner.setAdapter(spinnerAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load rounds", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchMatchesForRound(String round) {

        roundsRef.orderByKey().equalTo(round).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Fixtures> roundsList = new ArrayList<>();
                for (DataSnapshot matchSnapshot : dataSnapshot.getChildren()) {
                    for(DataSnapshot detailsSnapshot: matchSnapshot.getChildren()) {
                        String matchKey = detailsSnapshot.getKey();
                        Map<String, Object> matchDetails = (Map<String, Object>) detailsSnapshot.getValue();
                        String day = matchDetails.get("Day").toString();
                        String date = matchDetails.get("Date").toString();
                        String month = matchDetails.get("Month").toString();
                        String time = matchDetails.get("time").toString();
                        String venue = matchDetails.get("venue").toString();
                        String vipPrice = matchDetails.get("VIP").toString();
                        String regularPrice = matchDetails.get("Regular").toString();
                        Map<String, String> teamA = (Map<String, String>) matchDetails.get("Team A");
                        Map<String, String> teamB = (Map<String, String>) matchDetails.get("Team B");
                        Log.d("venue tag", "The value of venue is " + venue);
                        roundsList.add(new Fixtures(matchKey, date, day, month, time, venue, vipPrice, regularPrice, teamA, teamB));
                    }
                }
                // Store rounds for later access
                roundsMap.put(round, roundsList);

                eventsList = new ArrayList<>();
                for (Fixtures fixture : roundsList) {
                    String matchDetails = fixture.getMatchup();
                    String matchTime = fixture.getTime();
                    String matchDate = fixture.getDate();
                    String matchMonth = fixture.getMonth();
                    String matchVenue = fixture.getVenue();
                    String matchVIP = fixture.getVipPrice();
                    String matchRegular = fixture.getRegularPrice();
                    String teamA = fixture.getTeamA().get("teamName");
                    String teamB = fixture.getTeamB().get("teamName");
                    Log.d("tag", "Recycler venue is " + matchVenue);
                    //String description = fixture.getMatchup() + "\nDate: " + fixture.getDate() + "\nTime: " + fixture.getTime() + "\nVenue: " + fixture.getVenue();
                    String imageUrl = fixture.getTeamA().get("teamLogoUrl"); // Example: using Team A's logo
                    String teamALogo = fixture.getTeamA().get("teamLogoUrl");
                    String teamBLogo = fixture.getTeamB().get("teamLogoUrl");
                    eventsList.add(new TicketEvent(imageUrl, matchDetails, teamA, teamB, teamALogo, teamBLogo, matchTime, matchDate, matchMonth, matchVenue, matchRegular, matchVIP, roundAdapter));
                }
                ticketEventAdapter.setEvents(eventsList);
                ticketEventAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load matches for round", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
