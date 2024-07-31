package com.example.ticketcard;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.bumptech.glide.Glide;
import com.example.ticketcard.model.Players;
import com.example.ticketcard.model.StadiumViews;
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
    private ImageView roundedImageViewTeamOne, roundedImageViewTeamTwo, backarrowtofkf;
    private TextView winsTextView, lossTextView, drawsTextView, matchesTextView;
    private TextView textViewTeamOne, textViewTeamTwo;
    private Button teamOneButton, teamTwoButton, bookYourSeat;
    private StadiumViewsAdapter stadiumViewsAdapter;
    private List<StadiumViews> stadiumViewsList;
    private GridView gridView;
    private RecyclerView seatsGVRecycler;
    private SeatsAdapter seatsAdapter;
    private int totalPrice = 0;
    private String userEmail;
    private String userName;
    private List<String> bookedSeatsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_card_clicked);
        

        String imageUrl = getIntent().getStringExtra("imageURL");
        String imageDescription = getIntent().getStringExtra("imageDescription");

        // Retrieve the user name from SharedPreferences
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(getBaseContext());
        userName = sharedPreferences.getString("userName", "user"); // "User" is the default value if "userName" is not found
        userEmail = sharedPreferences.getString("userEmail", "email");

       // Log.d("TicketCardClicked", "Username is  " + userName);
       // Log.d("TicketCardClicked", "User email is " + userEmail);

        TextView matchTextView = findViewById(R.id.matchTextView);
        matchTextView.setText(imageDescription);

        ImageView roundedImageview = findViewById(R.id.roundedImageView);
        Glide.with(this).load(imageUrl).into(roundedImageview);

        textViewTeamOne = findViewById(R.id.textViewTeamOne);
        textViewTeamTwo = findViewById(R.id.textViewTeamTwo);
        backarrowtofkf = findViewById(R.id.backarrowtcc);


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

        bookYourSeat = findViewById(R.id.bookYourSeat);

        backarrowtofkf.setOnClickListener(view ->{
            Intent intent = new Intent(TicketCardClicked.this, FkfPremierLeague.class);
            startActivity(intent);
        });

      /*  bookYourSeat.setOnClickListener(view -> {
            Intent intent = new Intent(TicketCardClicked.this, BottomSheetDialog.class);
            startActivity(intent);
        });*/

        bookYourSeat.setOnClickListener(view -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(TicketCardClicked.this);
            View bottomSheetView = LayoutInflater.from(TicketCardClicked.this).inflate(R.layout.bottom_sheet, null);
            bottomSheetDialog.setContentView(bottomSheetView);
            bottomSheetDialog.show();

            Button buttonBuyTicket = bottomSheetView.findViewById(R.id.buttonBuyTicket);
            TextView priceCounter = bottomSheetView.findViewById(R.id.priceCounter);

            RecyclerView bottomSheetCarousel = bottomSheetView.findViewById(R.id.bottomSheetCarousel);

            CarouselLayoutManager carouselLayoutManager = new CarouselLayoutManager(new HeroCarouselStrategy());
            carouselLayoutManager.setCarouselAlignment(CarouselLayoutManager.ALIGNMENT_CENTER);
            bottomSheetCarousel.setLayoutManager(carouselLayoutManager);

            SnapHelper snapHelper = new CarouselSnapHelper();
            snapHelper.attachToRecyclerView(bottomSheetCarousel);

            List<StadiumViews> stadiumViews = fetchStadiumViews();

            MaterialButtonToggleGroup toggleGroup = bottomSheetView.findViewById(R.id.toggleButton);
            toggleGroup.setSelectionRequired(true);

            stadiumViewsAdapter = new StadiumViewsAdapter(TicketCardClicked.this, stadiumViews);
            bottomSheetCarousel.setAdapter(stadiumViewsAdapter);

            seatsGVRecycler = bottomSheetView.findViewById(R.id.idRVSeats);
            seatsGVRecycler.setLayoutManager(new GridLayoutManager(TicketCardClicked.this, 5));
            seatsList = new ArrayList<>();
            List<String> fetchedSeats = fetchSeats();
            List<String> bookedSeatsToAdapter = fetchBookedSeats();
            seatsAdapter = new SeatsAdapter(TicketCardClicked.this, fetchedSeats, bookedSeatsToAdapter, new SeatsAdapter.PriceChangeListener() {
                @Override
                public void onPriceChange(int amount, boolean isAdding) {
                    if (isAdding) {
                        totalPrice += amount;
                    } else {
                        totalPrice -= amount;
                    }
                    priceCounter.setText("Total: " + totalPrice + " Shillings");
                }
            });
            seatsGVRecycler.setAdapter(seatsAdapter);

            buttonBuyTicket.setOnClickListener(v -> {
                Map<String, Object> selectedSeatDetails = seatsAdapter.getSelectedSeatsDetails();

                Intent intent = new Intent(getApplicationContext(), Payment.class);
                intent.putExtra("Price", String.valueOf(totalPrice));
                intent.putExtra("UserEmail", userEmail);
                intent.putExtra("UserName", userName);

                Log.d("TicketCardClicked Intent call", "Intent Username is  " + userName);
                Log.d("TicketCardClicked Intent call", "Intent User email is " + userEmail);

                // Convert the seat details map to a string representation
                StringBuilder seatDetailsBuilder = new StringBuilder();
                for (Map.Entry<String, Object> entry : selectedSeatDetails.entrySet()) {
                    SeatsAdapter.SeatDetail seatDetail = (SeatsAdapter.SeatDetail) entry.getValue();
                    seatDetailsBuilder.append(seatDetail.seatName)
                            .append(":")
                            .append(seatDetail.seatType)
                            .append(":")
                            .append(seatDetail.seatPrice)
                            .append(";");
                }
                String seatDetailsString = seatDetailsBuilder.toString();
                intent.putExtra("SeatDetails", seatDetailsString);

                startActivity(intent);
                bottomSheetDialog.dismiss();
            });});

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
                    if (teamSnapshot.exists()) {
                        teamOneButton.setText(teamSnapshot.child("teamName").getValue(String.class));
                        String imageUrl = teamSnapshot.child("teamLogoUrl").getValue(String.class);
                        Glide.with(getApplicationContext()).load(imageUrl).into(roundedImageViewTeamOne);
                    }
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

    private List<StadiumViews> fetchStadiumViews() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("venues");
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                stadiumViewsList = new ArrayList<>();
                for (DataSnapshot venueSnapshot : dataSnapshot.getChildren()) {
                    DataSnapshot stadiumViewsSnapshot = venueSnapshot.child("stadiumViews");
                    for (DataSnapshot snapshot : stadiumViewsSnapshot.getChildren()) {
                        StadiumViews stadiumViews = snapshot.getValue(StadiumViews.class);
                        if (stadiumViews != null) {
                            stadiumViewsList.add(stadiumViews);
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
                    DataSnapshot seatNamesSnapShot = venueSnapshot.child("seatNames");
                    for (DataSnapshot snapshot : seatNamesSnapShot.getChildren()) {
                        String seat = snapshot.getValue(String.class);
                        if (seat != null) {
                            seatsList.add(seat);
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
        DatabaseReference bookedReference = FirebaseDatabase.getInstance().getReference("reservedSeats");
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
                            Toast.makeText(TicketCardClicked.this, "Booked Seat: " + bookedSeat, Toast.LENGTH_SHORT).show();
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