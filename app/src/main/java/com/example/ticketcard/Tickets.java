package com.example.ticketcard;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketcard.model.TransactionsFrag;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Tickets extends AppCompatActivity {

    private String userName,userEmail;
    private RecyclerView bookedRecycler;
    TicketsAdapter adapter;
    private List<TransactionsFrag> transactionsFrags;
    private String matchName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tickets);

        matchName = getIntent().getStringExtra("matchName");

        // Retrieve the user name and email from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userName = sharedPreferences.getString("userName", "user"); // "user" is the default value if "userName" is not found
        userEmail = sharedPreferences.getString("userEmail", "email"); // "email" is the default value if "userEmail" is not found

        bookedRecycler = findViewById(R.id.bookedRecycler);
        bookedRecycler.setLayoutManager(new LinearLayoutManager(this));

        fetchMatchDetails();


    }

    public void fetchMatchDetails() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("tickets").child(userName);
        //check this before running app****************************
        //*****************BELOW
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                transactionsFrags = new ArrayList<>();
                    for (DataSnapshot matchSnapshot : snapshot.getChildren()) {
                        if(matchSnapshot.getKey().equals(matchName)) {
                            TransactionsFrag transactionsFrag = matchSnapshot.getValue(TransactionsFrag.class);
                            if (transactionsFrag != null) {
                                transactionsFrags.add(transactionsFrag);
                            }
                        }
                    }
                // Set up the adapter with the fetched data
                adapter = new TicketsAdapter(Tickets.this, transactionsFrags);
                bookedRecycler.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TransactionsFragment", "Failed to read data", error.toException());
            }
        });
    }
}