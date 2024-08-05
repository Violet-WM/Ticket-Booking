package com.example.ticketcard;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Revenue extends AppCompatActivity {

    private TextView ticketDetailsTextView, seatsDetailsTextView, revenueTextView, vipRevenueTextView, regularRevenueTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revenue);

        ticketDetailsTextView = findViewById(R.id.ticketDetailsTextView);
        seatsDetailsTextView = findViewById(R.id.seatsDetailsTextView);
        revenueTextView = findViewById(R.id.revenueTextView);
        vipRevenueTextView = findViewById(R.id.vipRevenueTextView);
        regularRevenueTextView = findViewById(R.id.regularRevenueTextView);

        loadTicketDetails();
        loadSeatsDetails();
        calculateRevenue();
    }

    private void loadTicketDetails() {
        DatabaseReference ticketsRef = FirebaseDatabase.getInstance().getReference("tickets");
        ticketsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                StringBuilder ticketDetails = new StringBuilder();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String ticketId = snapshot.getKey();
                    String userId = snapshot.child("userId").getValue(String.class);
                    ticketDetails.append("Ticket ID: ").append(ticketId).append(", User ID: ").append(userId).append("\n");
                }
                ticketDetailsTextView.setText(ticketDetails.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Revenue.this, "Failed to load ticket details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadSeatsDetails() {
        DatabaseReference seatsRef = FirebaseDatabase.getInstance().getReference("seats");
        seatsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                StringBuilder seatsDetails = new StringBuilder();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String seatId = snapshot.getKey();
                    String userId = snapshot.child("userId").getValue(String.class);
                    String stadiumId = snapshot.child("stadiumId").getValue(String.class);
                    seatsDetails.append("Seat ID: ").append(seatId).append(", User ID: ").append(userId).append(", Stadium ID: ").append(stadiumId).append("\n");
                }
                seatsDetailsTextView.setText(seatsDetails.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Revenue.this, "Failed to load seats details", Toast.LENGTH_SHORT).show();
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

                revenueTextView.setText("Total Revenue: $" + totalRevenue);
                vipRevenueTextView.setText("VIP Revenue: $" + vipRevenue);
                regularRevenueTextView.setText("Regular Revenue: $" + regularRevenue);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Revenue.this, "Failed to calculate revenue", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
