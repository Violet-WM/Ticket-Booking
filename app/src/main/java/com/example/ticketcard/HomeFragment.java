package com.example.ticketcard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketcard.model.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    // Define the RecyclerView and the adapter
    private RecyclerView eventsRecyclerView;
    EventsAdapter eventsAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize the RecyclerView
        eventsRecyclerView = view.findViewById(R.id.popular_events_recycler_view);
        // Set layout manager for horizontal scrolling
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        eventsRecyclerView.setLayoutManager(layoutManager);

        // Fetch data for the events
        List<Event> events = fetchEvents();
        // Initialize the adapter with the fetched events
        eventsAdapter = new EventsAdapter(events);
        eventsRecyclerView.setAdapter(eventsAdapter);

        CardView footballct = view.findViewById(R.id.ftcategiries);
        footballct.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), football_categories.class);
            startActivity(intent);
        });

        return view;
    }

    private List<Event> fetchEvents() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<Event> eventsList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Event event = document.toObject(Event.class);
                                eventsList.add(event);
                            }
                            // Update the adapter with the fetched events
                            eventsAdapter.setEvents(eventsList);
                            eventsAdapter.notifyDataSetChanged();
                        } else {
                            Log.d("Firestore Error", "Error getting documents: ", task.getException());
                        }
                    }
                });
        return null;
    }}
