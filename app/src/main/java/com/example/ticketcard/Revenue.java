package com.example.ticketcard;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Revenue extends AppCompatActivity {

    private TextView ticketDetailsTextView, seatsDetailsTextView, revenueTextView, vipRevenueTextView, regularRevenueTextView;
    String seatName, seatType, seatsVenue;
    int seatPrice, seatCounter, seatCapacity;
    ArrayList<String> matches, venues;
    Map<String, Object> venueMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revenue);

        ticketDetailsTextView = findViewById(R.id.ticketDetailsTextView);
        revenueTextView = findViewById(R.id.revenueTextView);
        vipRevenueTextView = findViewById(R.id.vipRevenueTextView);
        regularRevenueTextView = findViewById(R.id.regularRevenueTextView);
        seatsDetailsTextView = findViewById(R.id.seatDetailsTextView);

        loadVenueCapacity();
        loadSeatCount();
        loadTicketDetails();
        calculateRevenue();
    }

    private void loadVenueCapacity () {
        DatabaseReference venuesRef = FirebaseDatabase.getInstance().getReference("venues");
        venuesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snap : snapshot.getChildren()) {
                    seatCapacity = snap.child("seatCapacity").getValue(Integer.class);
                    seatsVenue = snap.child("venueName").getValue(String.class);
                    venueMap.put(seatsVenue, seatCapacity);
                    Log.d("taga", venueMap.get(seatsVenue).toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadSeatCount() {
        DatabaseReference reservedSeatsRef = FirebaseDatabase.getInstance().getReference("reservedSeats");
        reservedSeatsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String venueSeatsCapacity = "";
                StringBuilder seatDetails = new StringBuilder();
                for(DataSnapshot snapshot1 : snapshot.getChildren()) {
                    //venues.add(snapshot1.getKey());
                    String venue = snapshot1.getKey();
                    seatCounter = 0;
                    for(DataSnapshot matchSnapshot : snapshot1.getChildren()){
                        //matches.add(matchSnapshot.getKey());
                        String match = matchSnapshot.getKey().replace("_", ".");
                        for(DataSnapshot userSnap : matchSnapshot.getChildren()) {
                            for(DataSnapshot seatsSnap : userSnap.child("seats").getChildren()) {
                                seatCounter++;
                                //Log.d("Tagaaaaa", "The value of venueMap.get(venue) is " + venueMap.get(venue).toString());
                            }
                        }
                        if(venueMap.containsKey(venue)){
                            venueSeatsCapacity = String.valueOf(venueMap.get(venue));
                        }
                        //after counting seats update the view
                        seatDetails.append("Stadium: ").append(venue).append("\n").append("Match: ").append(match)
                                .append("\n").append("Booked seats: ").append(seatCounter).append(" out of ").append(venueSeatsCapacity)
                                .append("\n\n");
                    }
                }
                seatsDetailsTextView.setText(seatDetails.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadTicketDetails() {
        DatabaseReference ticketsRef = FirebaseDatabase.getInstance().getReference("tickets");
        ticketsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                StringBuilder ticketDetails = new StringBuilder();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String username = snapshot.getKey();
                    for(DataSnapshot userSnapshot : snapshot.getChildren()){
                        String ticketId = userSnapshot.child("ticketID").getValue(String.class);
                        for(DataSnapshot seatsSnapshot : userSnapshot.child("seatsMap").getChildren()){
                            seatName = seatsSnapshot.child("seatName").getValue(String.class);
                            seatType = seatsSnapshot.child("seatType").getValue(String.class);
                            seatPrice = seatsSnapshot.child("seatPrice").getValue(Integer.class);
                            seatCounter++;
                        }
                        ticketDetails.append("Username: ").append(username).append("\n").append("Seat: ").append(seatName)
                                .append("\n").append("Price: Ksh. ").append(seatPrice).append("\n").append("Type: ").append(seatType)
                                .append("\n").append("Ticket ID: ").append(ticketId).append("\n\n");
                    }
                }
                ticketDetailsTextView.setText(ticketDetails.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Revenue.this, "Failed to load ticket details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calculateRevenue() {
        DatabaseReference ticketsRef = FirebaseDatabase.getInstance().getReference("tickets");
        ticketsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                double totalRevenue = 0;
                double vipRevenue = 0;
                double regularRevenue = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot matchSnapshot : snapshot.getChildren()) {
                        DataSnapshot seatsMapSnapshot = matchSnapshot.child("seatsMap");
                        for (DataSnapshot seatSnapshot : seatsMapSnapshot.getChildren()) {
                            double seatPrice = seatSnapshot.child("seatPrice").getValue(Double.class);
                            String seatType = seatSnapshot.child("seatType").getValue(String.class);
                            totalRevenue += seatPrice;
                            if ("VIP".equals(seatType)) {
                                vipRevenue += seatPrice;
                            } else if ("Regular".equals(seatType)) {
                                regularRevenue += seatPrice;
                            }
                        }
                    }
                }

                revenueTextView.setText("Total Revenue: Ksh." + totalRevenue);
                vipRevenueTextView.setText("VIP Revenue: Ksh." + vipRevenue);
                regularRevenueTextView.setText("Regular Revenue: Ksh." + regularRevenue);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Revenue.this, "Failed to calculate revenue", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
