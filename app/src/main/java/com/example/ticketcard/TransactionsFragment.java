package com.example.ticketcard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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

public class TransactionsFragment extends Fragment {
    private RecyclerView bookedRecycler;
    private String userName;
    TransactionsFragAdapter adapter;
    private List<TransactionsFrag> transactionsFrags, transactionsFragList;

    public TransactionsFragment() {
        // require a empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_transactions, container, false);

        // Retrieve the user name from SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        userName = sharedPreferences.getString("userName", "user"); // "User" is the default value if "userName" is not found

        bookedRecycler = view.findViewById(R.id.bookedRecycler);
        bookedRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new TransactionsFragAdapter(getContext(), transactionsFragList);

        transactionsFrags =  fetchMatchDetails();

        adapter.setOnItemClickListener(new TransactionsFragAdapter.OnItemClickListener() {
            @Override
            public void onClick(String matchName) {
                Intent intent =  new Intent(getContext(), Tickets.class);
                intent.putExtra("matchName", matchName);
                startActivity(intent);
            }
        });

        bookedRecycler.setAdapter(adapter);

        return view;
    }

    public List<TransactionsFrag> fetchMatchDetails() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("tickets").child(userName);
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                transactionsFragList = new ArrayList<>();
                for (DataSnapshot matchSnapshot : snapshot.getChildren()) {
                    TransactionsFrag transactionsFrag = matchSnapshot.getValue(TransactionsFrag.class);
                    if (transactionsFrag != null) {
                        transactionsFragList.add(transactionsFrag);
                    }
                }

                // Set up the adapter with the fetched data
                adapter.setEvents(transactionsFragList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("TransactionsFragment", "Failed to read data", error.toException());
            }
        });
        return transactionsFragList;
    }
}
