package com.example.ticketcard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.ArrayList;

//FIX THIS CODE IT'S TAKING TOO LONG TO RUN//

public class AddVenuesFragment extends Fragment {

    TextInputEditText venueEditText, capacityEditText;
    Button generateSeatsButton;
    Set<String> seatNames; // Use Set to ensure uniqueness

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference venuesRef;

    public AddVenuesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_venues, container, false);

        venueEditText = view.findViewById(R.id.venueEditText);
        capacityEditText = view.findViewById(R.id.capacityEditText);
        generateSeatsButton = view.findViewById(R.id.generateSeatsButton);

        seatNames = new HashSet<>(); // Initialize Set to store unique seat names

        // Initialize Firebase Realtime Database
        firebaseDatabase = FirebaseDatabase.getInstance();
        venuesRef = firebaseDatabase.getReference("venues");

        generateSeatsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String venue = venueEditText.getText().toString().trim();
                String capacityStr = capacityEditText.getText().toString().trim();

                if (venue.isEmpty()) {
                    Toast.makeText(getActivity(), "You need to add a venue first.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (capacityStr.isEmpty()) {
                    Toast.makeText(getActivity(), "You need to add the seat capacity!", Toast.LENGTH_SHORT).show();
                    return;
                }

                int capacity = Integer.parseInt(capacityStr);

                if (capacity > 45918) {
                    Toast.makeText(getActivity(), "Capacity too large, exceeds maximum unique seat names.", Toast.LENGTH_SHORT).show();
                    return;
                }

                generateSeats(capacity);
                uploadVenueToDatabase(venue, capacity, new ArrayList<>(seatNames));
            }
        });

        return view;
    }

    private void generateSeats(int capacity) {
        seatNames.clear();

        int vipSeatsCount = (int) Math.ceil(capacity * 0.15);
        int regularSeatsCount = capacity - vipSeatsCount;

        char[] allowedChars = "ABCDEFGHJKLMNPQSTUWXYZ".toCharArray();
        Random random = new Random();

        generateSeatNames(vipSeatsCount, "V", allowedChars, random);
        generateSeatNames(regularSeatsCount, "R", allowedChars, random);
    }

    private void generateSeatNames(int count, String prefix, char[] allowedChars, Random random) {
        while (seatNames.size() < count) {
            String seatName = generateSeatName(prefix, allowedChars, random);
            seatNames.add(seatName); // Add seat name to the set, duplicates will be ignored
        }
    }

    private String generateSeatName(String prefix, char[] allowedChars, Random random) {
        String seatName;
        do {
            int charIndex = random.nextInt(allowedChars.length);
            char letter = allowedChars[charIndex];
            int number = random.nextInt(99) + 1; // Random number between 1 and 99
            seatName = String.format("%s%c%02d", prefix, letter, number); // Ensures number is at least two digits
        } while (seatName.length() > 4);

        return seatName;
    }

    private void uploadVenueToDatabase(String venueName, int seatCapacity, List<String> seatNames) {
        DatabaseReference newVenueRef = venuesRef.push(); // Create a new child under "venues"

        // Create a venue object to store in Firebase
        Venue venue = new Venue(venueName, seatCapacity, seatNames);

        newVenueRef.setValue(venue).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getActivity(), "Venue added successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Failed to add venue.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Define the Venue class to match the structure you want to store in Firebase
    public static class Venue {
        public String venueName;
        public int seatCapacity;
        public List<String> seatNames;

        public Venue() {
            // Default constructor required for calls to DataSnapshot.getValue(Venue.class)
        }

        public Venue(String venueName, int seatCapacity, List<String> seatNames) {
            this.venueName = venueName;
            this.seatCapacity = seatCapacity;
            this.seatNames = seatNames;
        }
    }
}
