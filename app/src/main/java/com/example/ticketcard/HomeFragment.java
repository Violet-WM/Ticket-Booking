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
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketcard.model.Event;
import com.google.android.material.carousel.CarouselLayoutManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    // Define the RecyclerView and the adapter
    private RecyclerView eventsRecyclerView;
    EventsAdapter eventsAdapter;
    List<Event> eventsList;

    public HomeFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Retrieve the user name from SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userName = sharedPreferences.getString("userName", "user"); // "User" is the default value if "userName" is not found

        // Initialize the RecyclerView
        eventsRecyclerView = view.findViewById(R.id.popular_events_recycler_view);

        // Set layout manager for horizontal scrolling
        CarouselLayoutManager carouselLayoutManager = new CarouselLayoutManager();
        eventsRecyclerView.setLayoutManager(carouselLayoutManager);

        //SnapHelper snapHelper = new CarouselSnapHelper();
        //snapHelper.attachToRecyclerView(eventsRecyclerView);

        // Fetch data for the events
        List<Event> events = fetchEvents();

        // Initialize the adapter with the fetched events
        eventsAdapter = new EventsAdapter(getActivity(), events);

        eventsAdapter.setOnItemClickListener(new EventsAdapter.OnItemClickListener() {
            @Override
            public void onClick(ImageView imageView, String imageUrl) {
                Intent intent = new Intent(getActivity(), TicketCardClicked.class);

                startActivity(intent);
            }
        });

        eventsRecyclerView.setAdapter(eventsAdapter);

        CardView footballct = view.findViewById(R.id.ftcategiries);
        footballct.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), football_categories.class);
            startActivity(intent);
        });

        return view;
    }

    private List<Event> fetchEvents() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("events");
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventsList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Event event = snapshot.getValue(Event.class);
                    if (event != null) {
                        eventsList.add(event);
                    }
                }
                // Update the adapter with the fetched events
                eventsAdapter.setEvents(eventsList);
                eventsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Firebase Database Error", "Error getting data: ", databaseError.toException());
            }
        });

        return eventsList;
    }

}
