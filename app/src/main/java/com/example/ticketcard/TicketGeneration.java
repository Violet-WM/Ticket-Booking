package com.example.ticketcard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketcard.model.TicketGen;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TicketGeneration extends AppCompatActivity {

    private RecyclerView genTicketsRecyclerView;
    private TicketGenAdapter ticketGenAdapter;
    private String userName, userEmail;
    private List<TicketGen> ticketsList;
    private String matchName, stadiumName;
    Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_generation);

        // Retrieve the user name and email from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userName = sharedPreferences.getString("userName", "user"); // "user" is the default value if "userName" is not found
        userEmail = sharedPreferences.getString("userEmail", "email"); // "email" is the default value if "userEmail" is not found

        matchName = getIntent().getStringExtra("matchName");
        stadiumName = getIntent().getStringExtra("stadiumName");
        genTicketsRecyclerView = findViewById(R.id.genTicketsRecyclerView);
        genTicketsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        fetchTickets();
    }

    public void fetchTickets() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("reservedSeats");
        db.child(stadiumName).child(matchName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ticketsList = new ArrayList<>();
                DataSnapshot userSnapshot = dataSnapshot.child(userName);
                if (userSnapshot.exists()) {
                    String matchDate = userSnapshot.child("matchDate").getValue(String.class);
                    String matchMonth = userSnapshot.child("matchMonth").getValue(String.class);
                    String matchTime = userSnapshot.child("matchTime").getValue(String.class);
                    String round = userSnapshot.child("round").getValue(String.class);
                    String ticketId = userSnapshot.child("ticketID").getValue(String.class);

                    Map<String, Map<String, Object>> seatsMap = new HashMap<>();
                    for (DataSnapshot seatSnapshot : userSnapshot.child("seats").getChildren()) {
                        String seatName = seatSnapshot.child("seatName").getValue(String.class);
                        int seatPrice = seatSnapshot.child("seatPrice").getValue(Integer.class);
                        String seatType = seatSnapshot.child("seatType").getValue(String.class);

                        Map<String, Object> seatDetails = new HashMap<>();
                        seatDetails.put("seatName", seatName);
                        seatDetails.put("seatPrice", seatPrice);
                        seatDetails.put("seatType", seatType);

                        seatsMap.put(seatName, seatDetails);
                    }

                    TicketGen ticketGen = new TicketGen(seatsMap, matchDate, matchMonth, matchTime, round, stadiumName, matchName, ticketId);
                    ticketsList.add(ticketGen);
                }

                ticketGenAdapter = new TicketGenAdapter(TicketGeneration.this, ticketsList);
                genTicketsRecyclerView.setAdapter(ticketGenAdapter);

                // Call sendTicketsToDB after fetching tickets
                sendTicketsToDB();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TicketGeneration", "Failed to read data", error.toException());
            }
        });
    }

    public void sendTicketsToDB() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("tickets");
        for (TicketGen ticketGen : ticketsList) {
            String userPath = userName + "/" + matchName; // Unique path for each user's match tickets
            db.child(userPath).setValue(ticketGen).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("TicketGeneration", "Tickets successfully stored in DB");
                    Intent intent = new Intent(TicketGeneration.this, Tickets.class);
                    intent.putExtra("matchName", matchName);
                    startActivity(intent);
                } else {
                    Log.e("TicketGeneration", "Failed to store tickets in DB", task.getException());
                }
            });
        }
    }
}
