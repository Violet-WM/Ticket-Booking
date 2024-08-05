package com.example.ticketcard;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketcard.model.StadiumViews;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class AddVenuesActivity extends AppCompatActivity {

    private static final int IMAGE_PICKER_REQUEST_CODE = 1000;

    TextInputEditText venueEditText, capacityEditText;
    Button generateSeatsButton, selectImagesButton;
    RecyclerView imagesRecyclerView;
    ImageAdapter imageAdapter;
    List<Uri> imageUriList = new ArrayList<>();
    Set<String> seatNames; // Use Set to ensure uniqueness

    // HashMap to store stadium views
    private Map<String, StadiumViews> stadiumViewsMap = new HashMap<>();

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference venuesRef;
    private FirebaseStorage firebaseStorage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_venues);

        venueEditText = findViewById(R.id.venueEditText);
        capacityEditText = findViewById(R.id.capacityEditText);
        generateSeatsButton = findViewById(R.id.generateSeatsButton);
        selectImagesButton = findViewById(R.id.selectImagesButton);
        imagesRecyclerView = findViewById(R.id.imagesRecyclerView);

        seatNames = new HashSet<>(); // Initialize Set to store unique seat names

        // Initialize RecyclerView
        imageAdapter = new ImageAdapter(imageUriList);
        imagesRecyclerView.setLayoutManager(new LinearLayoutManager(AddVenuesActivity.this, LinearLayoutManager.HORIZONTAL, false));
        imagesRecyclerView.setAdapter(imageAdapter);

        String venueRetrieved = getIntent().getStringExtra("stadium");

        venueEditText.setText(venueRetrieved);
        venueEditText.setFocusable(false);
        venueEditText.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
        venueEditText.setClickable(false);

        // Initialize Firebase Realtime Database and Storage
        firebaseDatabase = FirebaseDatabase.getInstance();
        venuesRef = firebaseDatabase.getReference("venues");
        firebaseStorage = FirebaseStorage.getInstance();

        selectImagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.Companion.with(AddVenuesActivity.this)
                        .galleryOnly()
                        .galleryMimeTypes(new String[]{"image/*"})
                        .maxResultSize(1080, 1080)
                        .start(IMAGE_PICKER_REQUEST_CODE);
            }
        });

        generateSeatsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String capacityStr = capacityEditText.getText().toString().trim();

//                if (venue.isEmpty()) {
//                    Toast.makeText(getApplicationContext(), "You need to add a venue first.", Toast.LENGTH_SHORT).show();
//                    return;
//                }

                if (capacityStr.isEmpty()) {
                    Toast.makeText(AddVenuesActivity.this, "You need to add the seat capacity!", Toast.LENGTH_SHORT).show();
                    return;
                }

                int capacity = Integer.parseInt(capacityStr);

                if (capacity > 45918) {
                    Toast.makeText(AddVenuesActivity.this, "Capacity too large, exceeds maximum unique seat names.", Toast.LENGTH_SHORT).show();
                    return;
                }

                generateSeats(capacity);
                uploadVenueToDatabase(venueRetrieved, capacity, new ArrayList<>(seatNames));
            }
        });
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
        int i = 0;
        while (i < count) {
            String seatName = generateSeatName(prefix, allowedChars, random);
            seatNames.add(seatName); // Add seat name to the set, duplicates will be ignored
            i++;
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
                Toast.makeText(AddVenuesActivity.this, "Venue added successfully!", Toast.LENGTH_SHORT).show();
                uploadImagesToDatabase(newVenueRef.getKey());
            } else {
                Toast.makeText(AddVenuesActivity.this, "Failed to add venue.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadImagesToDatabase(String venueId) {
        if (imageUriList.isEmpty()) {
            Toast.makeText(AddVenuesActivity.this, "No images selected.", Toast.LENGTH_SHORT).show();
            return;
        }

        for (Uri imageUri : imageUriList) {
            String imageName = imageUri.getLastPathSegment();
            StorageReference imageRef = firebaseStorage.getReference("stadium_views/" + venueId + "/" + imageName);

            imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    String sideName = "Side " + (stadiumViewsMap.size() + 1); // Generate a unique side name
                    StadiumViews stadiumView = new StadiumViews(sideName, imageUrl);
                    stadiumViewsMap.put(sideName, stadiumView);

                    // Update the venue with stadium views
                    venuesRef.child(venueId).child("stadiumViews").setValue(stadiumViewsMap);
                });
                Intent intent = new Intent(AddVenuesActivity.this, AddTeamsActivity.class);
                startActivity(intent);
            }).addOnFailureListener(e -> {
                Toast.makeText(AddVenuesActivity.this, "Failed to upload image: " + imageName, Toast.LENGTH_SHORT).show();
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            if (data.getClipData() != null) {
                // Multiple images selected
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    imageUriList.add(imageUri);
                }
            } else if (data.getData() != null) {
                // Single image selected
                Uri imageUri = data.getData();
                imageUriList.add(imageUri);
            }
            imageAdapter.notifyDataSetChanged();
        }
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