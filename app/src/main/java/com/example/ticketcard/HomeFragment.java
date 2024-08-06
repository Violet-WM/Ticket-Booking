package com.example.ticketcard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketcard.model.Event;
import com.example.ticketcard.model.Fixtures;
import com.example.ticketcard.model.TicketEvent;
import com.google.android.material.carousel.CarouselLayoutManager;
import com.google.android.material.carousel.FullScreenCarouselStrategy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HomeFragment extends Fragment {

    // Define the RecyclerView and the adapter
    private RecyclerView eventsRecyclerView, round1Recycler;
    private ImageView imgShine;
    private RelativeLayout welcomeCard;
    RoundsAdapter roundAdapter;
    EventsAdapter eventsAdapter;
    List<Event> eventsList;
    List<TicketEvent> roundEventsList;
    private Button cardButton;
    private TextView welcomeText;
    private String matchDetails, imageUrl, match, round, matchTime, matchDate, matchMonth, matchVenue, matchVIP, matchRegular, teamA, teamB, teamALogo, teamBLogo;

    public HomeFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        cardButton = view.findViewById(R.id.cardButton);
        welcomeCard = view.findViewById(R.id.welcomeCard);
        imgShine = view.findViewById(R.id.shine);
        round1Recycler = view.findViewById(R.id.round1Recycler);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        round1Recycler.setLayoutManager(linearLayoutManager);

        //shine code
        ScheduledExecutorService scheduledExecutorService =
                Executors.newSingleThreadScheduledExecutor();

        scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        shineStart();
                    }
                });
            }
        }, 3, 3, TimeUnit.SECONDS);


        // Set an OnClickListener to the button to handle click events
        cardButton.setOnClickListener(v -> {
            Fragment frag = new TicketsFragment();

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.frame_layout, frag);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.addToBackStack(null);
            ft.commit();
        });

        // Retrieve the user name from SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userName = sharedPreferences.getString("userName", "user"); // "User" is the default value if "userName" is not found
        String userEmail = sharedPreferences.getString("userEmail", "email");

        Log.d("HomeFragment", "Username is  " + userName);
        Log.d("HomeFragment", "User email is " + userEmail);

        welcomeText = view.findViewById(R.id.welcomeText);
        welcomeText.setText("Welcome " + userName + "\uD83D\uDC4B");

        // Initialize the RecyclerView
        eventsRecyclerView = view.findViewById(R.id.popular_events_recycler_view);

        // Set layout manager for horizontal scrolling
        CarouselLayoutManager carouselLayoutManager = new CarouselLayoutManager(new FullScreenCarouselStrategy());
        eventsRecyclerView.setLayoutManager(carouselLayoutManager);

        //SnapHelper snapHelper = new CarouselSnapHelper();
        //snapHelper.attachToRecyclerView(eventsRecyclerView);

        // Fetch data for the events
        fetchEvents();
        fetchMatchesForRound("Round 1");

        roundAdapter = new RoundsAdapter(getActivity(), roundEventsList);

        roundAdapter.setOnItemClickListener(new RoundsAdapter.OnItemClickListener() {
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
                intent.putExtra("matchVIP", matchVIP);
                intent.putExtra("matchRegular", matchRegular);
                startActivity(intent);
            }
        });

        round1Recycler.setAdapter(roundAdapter);

        // Initialize the adapter with the fetched events
        eventsAdapter = new EventsAdapter(getActivity(), eventsList);

        eventsAdapter.setOnItemClickListener(new EventsAdapter.OnItemClickListener() {
            @Override
            public void onClick(String imageUrl, String matchDetails, String round, String teamA, String teamB, String teamALogo, String teamBLogo, String matchTime, String matchDate, String matchMonth, String matchVenue, String matchRegular, String matchVIP) {
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
                intent.putExtra("matchVIP", matchVIP);
                intent.putExtra("matchRegular", matchRegular);
                startActivity(intent);
            }
        });

        eventsRecyclerView.setAdapter(eventsAdapter);

        return view;
    }

    private void fetchEvents() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("events");
        //eventsList = new ArrayList<>();

        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Event> eventsToMethod = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Event event = snapshot.getValue(Event.class);
                    if (event != null) {
                        String eventImageUrl = event.getImageUrl();
                        String eventMatch = event.getMatch();
                        String eventRound = event.getRound();
                        eventsToMethod.add(event);
                    }
                }
                Log.d("taggggg", "The size of eventsToMethod is" + eventsToMethod.size());
                fetchRounds(eventsToMethod);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Firebase Database Error", "Error getting data: ", databaseError.toException());
            }
        });
    }

    private void fetchRounds(List<Event> eventMethod) {
        //Log.d("Round is", "Here's your round " + event.getRound());
        DatabaseReference roundsRef = FirebaseDatabase.getInstance().getReference("fixtures").child("Kenya Premier League");

        roundsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Fixtures> roundsList = new ArrayList<>();
                for (Event event : eventMethod) {
                    for (DataSnapshot matchSnapshot : snapshot.getChildren()) {
                        String roundsKey = matchSnapshot.getKey();
                        if (roundsKey.equals(event.getRound())) {
                            for (DataSnapshot detailsSnapshot : matchSnapshot.getChildren()) {
                                if (roundsList != null) {
                                    String matchKey = detailsSnapshot.getKey();
                                    if (matchKey.equals(event.getMatch())) {
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
                                        roundsList.add(new Fixtures(event.getImageUrl(), event.getMatch(), event.getRound(), matchKey, date, day, month, time, venue, vipPrice, regularPrice, teamA, teamB));
                                    }
                                }
                            }
                        }
                    }
                }

                eventsList = new ArrayList<>();
                for (Fixtures fixture : roundsList) {
                    match = fixture.getMatch();
                    round = fixture.getRound();
                    matchDetails = fixture.getMatchup();
                    matchTime = fixture.getTime();
                    matchDate = fixture.getDate();
                    matchMonth = fixture.getMonth();
                    matchVenue = fixture.getVenue();
                    matchVIP = fixture.getVipPrice();
                    matchRegular = fixture.getRegularPrice();
                    teamA = fixture.getTeamA().get("teamName");
                    teamB = fixture.getTeamB().get("teamName");
                    imageUrl = fixture.getImageUrl(); // Example: using Team A's logo
                    teamALogo = fixture.getTeamA().get("teamLogoUrl");
                    teamBLogo = fixture.getTeamB().get("teamLogoUrl");
                    eventsList.add(new Event(imageUrl, match, round, teamA, teamB, teamALogo, teamBLogo, matchTime, matchDate, matchMonth, matchVenue, matchRegular, matchVIP));
                }

                // Update the adapter with the fetched events and ticketEventList
                eventsAdapter.setEvents(eventsList);
                eventsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load matches for round", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void shineStart() {
        Animation animation = new TranslateAnimation(
                0, welcomeCard.getWidth()+imgShine.getWidth(),0,0
        );

        animation.setDuration(550);
        animation.setFillAfter(false);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        imgShine.startAnimation(animation);
    }

    private void fetchMatchesForRound(String round) {
        // Initialize Firebase reference
        DatabaseReference roundsRefs = FirebaseDatabase.getInstance().getReference("fixtures").child("Kenya Premier League");
        roundsRefs.orderByKey().equalTo(round).addListenerForSingleValueEvent(new ValueEventListener() {
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

                roundEventsList = new ArrayList<>();
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
                    roundEventsList.add(new TicketEvent(imageUrl, matchDetails, teamA, teamB, teamALogo, teamBLogo, matchTime, matchDate, matchMonth, matchVenue, matchRegular, matchVIP, round));
                }
                roundAdapter.setEvents(roundEventsList);
                roundAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load matches for round", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
