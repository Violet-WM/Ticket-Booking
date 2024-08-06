package com.example.ticketcard;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketcard.model.Event;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PickEvents extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private Spinner roundsSpinner, matchesSpinner;
    private Button uploadImageButton, saveEventButton;
    private RecyclerView selectedEventsRecyclerView;
    private ImageView selectedImageView;

    private String selectedRound;

    private Uri imageUri;

    private DatabaseReference fixturesRef, eventsRef;
    private StorageReference storageRef;

    private ArrayList<String> roundsList, matchesList;
    private ArrayAdapter<String> roundsAdapter, matchesAdapter;

    private HashMap<String, String> selectedEvents;
    private HashMap<String, Uri> eventImages;
    private List<Event> eventList;
    private EventAdapter eventAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_events);

        roundsSpinner = findViewById(R.id.spinner_rounds);
        matchesSpinner = findViewById(R.id.spinner_events);
        uploadImageButton = findViewById(R.id.btn_upload_image);
        saveEventButton = findViewById(R.id.btn_save_events);
        selectedEventsRecyclerView = findViewById(R.id.recycler_view_selected_events);
        selectedImageView = findViewById(R.id.image_view_selected);

        fixturesRef = FirebaseDatabase.getInstance().getReference("fixtures");
        eventsRef = FirebaseDatabase.getInstance().getReference("events");
        storageRef = FirebaseStorage.getInstance().getReference("event_images");

        roundsList = new ArrayList<>();
        matchesList = new ArrayList<>();
        selectedEvents = new HashMap<>();
        eventImages = new HashMap<>();
        eventList = new ArrayList<>();

        roundsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roundsList);
        matchesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, matchesList);

        roundsSpinner.setAdapter(roundsAdapter);
        matchesSpinner.setAdapter(matchesAdapter);

        loadRounds();

        roundsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRound = roundsList.get(position);
                loadMatches(selectedRound);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        matchesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (selectedEvents.size() < 2) {
                    String selectedMatch = matchesList.get(position);
                    selectedEvents.put(selectedMatch, roundsList.get(roundsSpinner.getSelectedItemPosition()));
                } else {
                    Toast.makeText(PickEvents.this, "You can only select up to 2 events", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        saveEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedEvents.size() == 2) {
                    uploadImages();
                } else {
                    Toast.makeText(PickEvents.this, "Please select exactly 2 events", Toast.LENGTH_SHORT).show();
                }
            }
        });

        selectedEventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventAdapter = new EventAdapter(eventList);
        selectedEventsRecyclerView.setAdapter(eventAdapter);
    }

    private void loadRounds() {
        fixturesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                roundsList.clear();
                for (DataSnapshot leagueSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot roundSnapshot : leagueSnapshot.getChildren()) {
                        roundsList.add(roundSnapshot.getKey());
                    }
                }
                roundsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PickEvents.this, "Failed to load rounds", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMatches(String round) {
        fixturesRef.child("Kenya Premier League").child(round).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                matchesList.clear();
                for (DataSnapshot matchSnapshot : snapshot.getChildren()) {
                    matchesList.add(matchSnapshot.getKey());
                }
                matchesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PickEvents.this, "Failed to load matches", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            selectedImageView.setImageURI(imageUri);
            String selectedMatch = matchesList.get(matchesSpinner.getSelectedItemPosition());
            eventImages.put(selectedMatch, imageUri);
            Event eventHere = new Event(selectedMatch, selectedRound, imageUri);
            eventList.add(eventHere);
            eventAdapter.notifyDataSetChanged();
        }
    }

    private void uploadImages() {
        for (final String match : eventImages.keySet()) {
            Uri uri = eventImages.get(match);
            if (uri != null) {
                final StorageReference fileReference = storageRef.child(System.currentTimeMillis() + "_" + match + ".jpg");
                fileReference.putFile(uri)
                        .addOnSuccessListener(taskSnapshot ->
                                fileReference.getDownloadUrl().addOnSuccessListener(uri1 -> {
                                    String imageUrl = uri1.toString();
                                    Event event = new Event(match, selectedEvents.get(match), imageUrl);
                                    saveEventToDatabase(event);
                                })
                        )
                        .addOnFailureListener(e ->
                                Toast.makeText(PickEvents.this, "Failed to upload image for event: " + match, Toast.LENGTH_SHORT).show()
                        );
            } else {
                Toast.makeText(this, "No image selected for event: " + match, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveEventToDatabase(Event event) {
        eventsRef.push().setValue(event)
                .addOnSuccessListener(aVoid -> Toast.makeText(PickEvents.this, "Events saved successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(PickEvents.this, "Failed to save event", Toast.LENGTH_SHORT).show());
    }

}
