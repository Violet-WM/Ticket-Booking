package com.example.ticketcard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketcard.model.TicketEvent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TicketsFragment extends Fragment {

    private RecyclerView ticketsRecyclerView;
    TicketEventAdapter ticketEventAdapter;
    List<TicketEvent> eventsList;

    public TicketsFragment(){
        // require a empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tickets, container, false);


        // Retrieve the user name from SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userName = sharedPreferences.getString("userName", "user"); // "User" is the default value if "userName" is not found

        // Initialize the RecyclerView
        ticketsRecyclerView = view.findViewById(R.id.ticketsRecyclerView);

        //Set vertical layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        ticketsRecyclerView.setLayoutManager(linearLayoutManager);

        // Fetch data for the events
        List<TicketEvent> events = fetchEvents();

        // Initialize the adapter with the fetched events
        ticketEventAdapter = new TicketEventAdapter(getActivity(), events);

        ticketEventAdapter.setOnItemClickListener(new TicketEventAdapter.OnItemClickListener() {
            @Override
            public void onClick(ImageView ticketImage, String imageUrl, String imgDescription) {
                Intent intent = new Intent(getActivity(), TicketCardClicked.class);
                intent.putExtra("imageURL", imageUrl);
                intent.putExtra("imageDescription", imgDescription);
                startActivity(intent);
            }
        });


        ticketsRecyclerView.setAdapter(ticketEventAdapter);

        return view;
    }

    private List<TicketEvent> fetchEvents() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("events");
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventsList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    TicketEvent event = snapshot.getValue(TicketEvent.class);
                    if (event != null) {
                        eventsList.add(event);
                    }
                }
                // Update the adapter with the fetched events
                ticketEventAdapter.setEvents(eventsList);
                ticketEventAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Firebase Database Error", "Error getting data: ", databaseError.toException());
            }
        });

        return eventsList;
    }
}