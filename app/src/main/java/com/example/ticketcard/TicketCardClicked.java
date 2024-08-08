package com.example.ticketcard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.bumptech.glide.Glide;
import com.example.ticketcard.model.Fixtures;
import com.example.ticketcard.model.Players;
import com.example.ticketcard.model.StadiumViews;
import com.example.ticketcard.model.TicketEvent;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.carousel.CarouselLayoutManager;
import com.google.android.material.carousel.CarouselSnapHelper;
import com.google.android.material.carousel.HeroCarouselStrategy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TicketCardClicked extends AppCompatActivity {

    private RecyclerView playersRecyclerView;
    private PlayersAdapter playersAdapter;
    private List<Players> playersList;
    private List<String> seatsList;
    private TextView winsTextView, lossTextView, drawsTextView, matchesTextView;
    private TextView timeAndDateText, venueText, regularText, VIPText;
    private TextView textViewTeam, textViewTeamB;
    private Button teamAButton, teamBButton, bookYourSeat;
    private StadiumViewsAdapter stadiumViewsAdapter;
    private List<StadiumViews> stadiumViewsList;
    private MaterialButtonToggleGroup toggleButtonTeams;
    private GridView gridView;
    private RecyclerView seatsGVRecycler;
    private SeatsAdapter seatsAdapter;
    private int totalPrice = 0;
    private String userEmail;
    private String userName;
    private List<String> bookedSeatsList;
    private String matchDetails;
    private String round;
    private List<TicketEvent> eventDetailsList;
    private String matchName, matchTime, matchDate, matchMonth, matchVenue, matchVIP, matchRegular, teamA, teamB, teamALogoUrl, teamBLogoUrl;

    DatabaseReference roundsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_card_clicked);

        String imageUrl = getIntent().getStringExtra("imageURL");
        matchDetails = getIntent().getStringExtra("matchDetails");
        teamA = getIntent().getStringExtra("teamA");
        teamB = getIntent().getStringExtra("teamB");
        teamALogoUrl = getIntent().getStringExtra("teamALogoUrl");
        teamBLogoUrl = getIntent().getStringExtra("teamBLogoUrl");
        round = getIntent().getStringExtra("round");
        matchVenue = getIntent().getStringExtra("matchVenue");
        matchDate = getIntent().getStringExtra("matchDate");
        matchMonth = getIntent().getStringExtra("matchMonth");
        matchTime = getIntent().getStringExtra("matchTime");
        matchVIP = getIntent().getStringExtra("matchVIP");
        matchRegular = getIntent().getStringExtra("matchRegular");

        // Initialize Firebase reference
        roundsRef = FirebaseDatabase.getInstance().getReference("fixtures").child("Kenya Premier League");

        fetchMatchesRoundSpecific(round);

        // get the price for VIp, reg, team A, Team B, Team A logo, Team B Logo, venue

        // Retrieve the user name and email from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userName = sharedPreferences.getString("userName", "user"); // "user" is the default value if "userName" is not found
        userEmail = sharedPreferences.getString("userEmail", "email"); // "email" is the default value if "userEmail" is not found

        Log.d("TicketCardClicked", "Username is  " + userName);
       Log.d("TicketCardClicked", "User email is " + userEmail);

        TextView matchTextView = findViewById(R.id.matchTextView);
        matchTextView.setText(matchDetails.replace("_","."));

        ImageView teamALogo = findViewById(R.id.roundedImageViewTeamOne);
        ImageView teamBLogo = findViewById(R.id.roundedImageViewTeamTwo);
        Glide.with(this).load(teamALogoUrl).into(teamALogo);
        Glide.with(this).load(teamBLogoUrl).into(teamBLogo);

        textViewTeam = findViewById(R.id.textViewTeamOne);
        venueText = findViewById(R.id.venueText);
        VIPText = findViewById(R.id.vipText);
        regularText = findViewById(R.id.regularText);
        timeAndDateText = findViewById(R.id.timeAndDateText);

        //set the text
        textViewTeam.setText(teamA);
        venueText.setText(matchVenue);
        VIPText.setText("VIP Price: Ksh. " + matchVIP);
        regularText.setText("Regular Price: Ksh. " + matchRegular);
        timeAndDateText.setText(matchTime + ", " + matchDate + " " + matchMonth);

        teamAButton = findViewById(R.id.teamAButton);
        teamBButton = findViewById(R.id.teamBButton);

        teamAButton.setText(teamA);
        teamBButton.setText(teamB);

        playersList = new ArrayList<>();

        playersRecyclerView = findViewById(R.id.playersRecyclerView);
        playersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        playersAdapter = new PlayersAdapter(this, playersList);
        playersRecyclerView.setAdapter(playersAdapter);

        winsTextView = findViewById(R.id.wonMatchesText);
        lossTextView = findViewById(R.id.lostMatchesText);
        drawsTextView = findViewById(R.id.drawMatchesText);
        matchesTextView = findViewById(R.id.matchesPlayedText);

        toggleButtonTeams = findViewById(R.id.toggleButtonTeams);

        toggleButtonTeams.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                    if (isChecked) {
                        if (checkedId == R.id.teamAButton) {
                            textViewTeam.setText(teamA);
                            fetchTeamData(teamA.replace("_", "."));
                        } else if (checkedId == R.id.teamBButton) {
                            textViewTeam.setText(teamB);
                            fetchTeamData(teamB.replace("_", "."));
                        }
                    }
            }
        });

        bookYourSeat = findViewById(R.id.bookYourSeat);

        bookYourSeat.setOnClickListener(view -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(TicketCardClicked.this);
            View bottomSheetView = LayoutInflater.from(TicketCardClicked.this).inflate(R.layout.bottom_sheet, null);
            bottomSheetDialog.setContentView(bottomSheetView);
            bottomSheetDialog.show();

            TextView bottomSheetTitle = bottomSheetView.findViewById(R.id.bottomSheetTitle);
            Button buttonBuyTicket = bottomSheetView.findViewById(R.id.buttonBuyTicket);
            buttonBuyTicket.setEnabled(false);

            bottomSheetTitle.setText(matchDetails.replace("_","."));

            RecyclerView bottomSheetCarousel = bottomSheetView.findViewById(R.id.bottomSheetCarousel);

            CarouselLayoutManager carouselLayoutManager = new CarouselLayoutManager(new HeroCarouselStrategy());
            carouselLayoutManager.setCarouselAlignment(CarouselLayoutManager.ALIGNMENT_CENTER);
            bottomSheetCarousel.setLayoutManager(carouselLayoutManager);

            SnapHelper snapHelper = new CarouselSnapHelper();
            snapHelper.attachToRecyclerView(bottomSheetCarousel);

            List<StadiumViews> stadiumViews = fetchStadiumViews();

            stadiumViewsAdapter = new StadiumViewsAdapter(TicketCardClicked.this, stadiumViews);
            bottomSheetCarousel.setAdapter(stadiumViewsAdapter);

            seatsGVRecycler = bottomSheetView.findViewById(R.id.idRVSeats);
            seatsGVRecycler.setLayoutManager(new GridLayoutManager(TicketCardClicked.this, 5));
            seatsList = new ArrayList<>();
            List<String> fetchedSeats = fetchSeats();
            List<String> bookedSeatsToAdapter = fetchBookedSeats();
            seatsAdapter = new SeatsAdapter(TicketCardClicked.this, fetchedSeats, bookedSeatsToAdapter, matchVIP, matchRegular, new SeatsAdapter.PriceChangeListener() {
                @Override
                public void onPriceChange(int amount, boolean isAdding) {
                    if (isAdding) {
                        totalPrice += amount;
                    } else {
                        totalPrice -= amount;
                    }

                    if(totalPrice > 0){
                        buttonBuyTicket.setEnabled(true);
                        buttonBuyTicket.setText("Pay " + totalPrice + " Shillings");
                    } else {
                        buttonBuyTicket.setText("Buy Ticket");
                        buttonBuyTicket.setEnabled(false);
                    }

                }
            });
            seatsGVRecycler.setAdapter(seatsAdapter);


            buttonBuyTicket.setOnClickListener(v -> {
                Boolean bookedSeatExists = false;
                Map<String, Object> selectedSeatDetails = seatsAdapter.getSelectedSeatsDetails();

                // Convert the seat details map to a string representation
                StringBuilder seatDetailsBuilder = new StringBuilder();
                for (Map.Entry<String, Object> entry : selectedSeatDetails.entrySet()) {
                    SeatsAdapter.SeatDetail seatDetail = (SeatsAdapter.SeatDetail) entry.getValue();
                    if(bookedSeatsToAdapter.contains(seatDetail.seatName)){
                        bookedSeatExists = true;
                    }
                    seatDetailsBuilder.append(seatDetail.seatName)
                            .append(":")
                            .append(seatDetail.seatType)
                            .append(":")
                            .append(seatDetail.seatPrice)
                            .append(";");
                }
                String seatDetailsString = seatDetailsBuilder.toString();

                if(bookedSeatExists){
                    //prevent user from moving to payment
                    Toast.makeText(this, "A seat that you have selected has just been booked. Close and open this tab to" +
                            " view unbooked seats.", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(getApplicationContext(), Payment.class);
                    intent.putExtra("Price", String.valueOf(totalPrice));
                    intent.putExtra("UserEmail", userEmail);
                    intent.putExtra("UserName", userName);
                    intent.putExtra("matchName", matchDetails);
                    intent.putExtra("stadiumName", matchVenue);
                    intent.putExtra("round", round);
                    intent.putExtra("matchDate", matchDate);
                    intent.putExtra("matchTime", matchTime);
                    intent.putExtra("matchMonth", matchMonth);
                    Log.d("TicketCardClicked Intent call", "Intent Username is  " + userName);
                    Log.d("TicketCardClicked Intent call", "Intent User email is " + userEmail);
                    intent.putExtra("SeatDetails", seatDetailsString);
                    startActivity(intent);
                    totalPrice = 0;
                    bottomSheetDialog.dismiss();
                }
            });
        });
    }

    private void fetchMatchesRoundSpecific(String roundSent) {
        roundsRef.child(roundSent).orderByChild(matchDetails).equalTo(matchDetails).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Fixtures> roundsList = new ArrayList<>();
                for (DataSnapshot matchSnapshot : dataSnapshot.getChildren()) {
                    String matchKey = matchSnapshot.getKey();
                    Map<String, Object> matchDetails = (Map<String, Object>) matchSnapshot.getValue();
                    String date = matchDetails.get("Date").toString();
                    String day = matchDetails.get("Day").toString();
                    String month = matchDetails.get("Month").toString();
                    String time = matchDetails.get("time").toString();
                    String venue = matchDetails.get("venue").toString();
                    String vipPrice = matchDetails.get("VIP").toString();
                    String regularPrice = matchDetails.get("Regular").toString();
                    Map<String, String> teamA = (Map<String, String>) matchDetails.get("Team A");
                    Map<String, String> teamB = (Map<String, String>) matchDetails.get("Team B");

                    roundsList.add(new Fixtures(matchKey, date, day, month, time, venue, vipPrice, regularPrice, teamA, teamB));
                }

                extractRoundsForEase(roundsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Failed to load matches for round", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void extractRoundsForEase(List<Fixtures> fixtures) {
        eventDetailsList = new ArrayList<>();
        for (Fixtures fixture : fixtures) {
            matchName = fixture.getMatchup();
            matchTime = fixture.getTime();
            matchDate = fixture.getDate();
            matchMonth = fixture.getMonth();
            matchVenue = fixture.getVenue();
            matchVIP = fixture.getVipPrice();
            matchRegular = fixture.getRegularPrice();
            teamA = fixture.getTeamA().get("teamName");
            teamB = fixture.getTeamB().get("teamName");
            //String description = fixture.getMatchup() + "\nDate: " + fixture.getDate() + "\nTime: " + fixture.getTime() + "\nVenue: " + fixture.getVenue();
            teamALogoUrl = fixture.getTeamA().get("teamLogoUrl"); // Example: using Team A's logo
            teamBLogoUrl = fixture.getTeamB().get("teamLogoUrl");
        }

    }

    private void fetchTeamData(String team) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("teams");
        db.orderByChild("teamName").equalTo(team).addListenerForSingleValueEvent(new ValueEventListener() {
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
                    DataSnapshot statsSnapshot = teamSnapshot.child("stats");
                    if (statsSnapshot.exists()) {
                        String wins = statsSnapshot.child("wins").getValue(String.class);
                        String loss = statsSnapshot.child("loss").getValue(String.class);
                        String draws = statsSnapshot.child("draws").getValue(String.class);
                        String matches = statsSnapshot.child("matches").getValue(String.class);

                        winsTextView.setText(wins != null ? "Won: " + wins : "Won: 0");
                        lossTextView.setText(loss != null ? "Lost: " + loss : "Lost: 0");
                        drawsTextView.setText(draws != null ? "Draw: " + draws : "Draw: 0");
                        matchesTextView.setText(matches != null ? "Matches played: " + matches : "Matches played: 0");
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

    private List<StadiumViews> fetchStadiumViews() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("venues");
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                stadiumViewsList = new ArrayList<>();
                for (DataSnapshot venueSnapshot : dataSnapshot.getChildren()) {
                    if(venueSnapshot.child("venueName").getValue(String.class).equals(matchVenue)){
                        DataSnapshot stadiumViewsSnapshot = venueSnapshot.child("stadiumViews");
                        for (DataSnapshot snapshot : stadiumViewsSnapshot.getChildren()) {
                            StadiumViews stadiumViews = snapshot.getValue(StadiumViews.class);
                            if (stadiumViews != null) {
                                stadiumViewsList.add(stadiumViews);
                            }
                        }
                    }
                }
                if (stadiumViewsList != null) {
                    stadiumViewsAdapter.setEvents(stadiumViewsList);
                    stadiumViewsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Firebase Database Error", "Error getting data: ", databaseError.toException());
            }
        });
        return stadiumViewsList;
    }

    private List<String> fetchSeats() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("venues");
        seatsList = new ArrayList<>();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                seatsList.clear();
                for (DataSnapshot venueSnapshot : dataSnapshot.getChildren()) {
                    if(venueSnapshot.child("venueName").getValue(String.class).equals(matchVenue)) {
                        DataSnapshot seatNamesSnapShot = venueSnapshot.child("seatNames");
                        for (DataSnapshot snapshot : seatNamesSnapShot.getChildren()) {
                            String seat = snapshot.getValue(String.class);
                            if (seat != null) {
                                seatsList.add(seat);
                            }
                        }
                    }
                }
                seatsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors.
            }
        });
        return seatsList;
    }

    private List<String> fetchBookedSeats() {
        DatabaseReference bookedReference = FirebaseDatabase.getInstance().getReference("reservedSeats").child(matchVenue).child(matchDetails);
        bookedSeatsList = new ArrayList<>();
        bookedReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookedSeatsList.clear();
                for(DataSnapshot userSnapshot : snapshot.getChildren()){
                    for(DataSnapshot seatsSnapshot : userSnapshot.getChildren()){
                        for(DataSnapshot seatNameSnapshot : seatsSnapshot.getChildren()) {
                            String bookedSeat = seatNameSnapshot.getKey();
                            bookedSeatsList.add(bookedSeat);
                            Log.d("TicketCardClicked", "Booked seats are: " + bookedSeat);
                        }
                    }
                }
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return bookedSeatsList;
    }
}