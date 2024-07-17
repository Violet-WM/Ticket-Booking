package com.example.ticketcard;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ticketcard.model.Teams;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class AddTeamsActivity extends AppCompatActivity {

    // UI elements
    private EditText uploadTeamName;
    private ImageView imageView;
    private FloatingActionButton uploadButton;
    private ProgressBar progressBar;
    private Spinner leaguesSpinner;
    private TextInputEditText dynamicChipEditText;
    private ChipGroup chipGroup;

    // Firebase instances
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage storage;

    // URI for selected image and URL for storing in Firebase Realtime Database
    private Uri selectedImageUri;
    private String imageUrlToSaveInDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_teams);

        String[] leagues = { "N/A", "League not shown!", "FKF Premier League", "FKF Women Premier League",
                "National Super League" };

        dynamicChipEditText = findViewById(R.id.dynamicChipEditText);
        chipGroup = findViewById(R.id.chipGroup);

        dynamicChipEditText.addTextChangedListener(new TextWatcher() {

            private boolean isProcessing;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Not used
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (isProcessing) return;
                if (charSequence.toString().endsWith(",")) {
                    String name = charSequence.toString().substring(0, charSequence.length() - 1).trim();
                    if (!name.isEmpty()) {
                        addChip(name);
                        dynamicChipEditText.setText("");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (isProcessing) return;
                isProcessing = true;
                String input = editable.toString();
                String capitalizedInput = capitalizeWords(input);
                if (!input.equals(capitalizedInput)) {
                    dynamicChipEditText.setText(capitalizedInput);
                    dynamicChipEditText.setSelection(capitalizedInput.length());
                }
                isProcessing = false;//not used
            }

            private String capitalizeWords(String str) {
                StringBuilder capitalized = new StringBuilder();
                boolean capitalizeNext = true;
                for (char c : str.toCharArray()) {
                    if (Character.isWhitespace(c)) {
                        capitalizeNext = true;
                        capitalized.append(c);
                    } else if (capitalizeNext) {
                        capitalized.append(Character.toUpperCase(c));
                        capitalizeNext = false;
                    } else {
                        capitalized.append(Character.toLowerCase(c));
                    }
                }
                return capitalized.toString();
            }
        });

        leaguesSpinner = findViewById(R.id.leaguesSpinner);
        ArrayAdapter spinnerAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, leagues);
        spinnerAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
        leaguesSpinner.setAdapter(spinnerAdapter);

        // Initialize Firebase Realtime Database and Firebase Storage
        firebaseDatabase = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        // Initialize UI elements
        uploadTeamName = findViewById(R.id.teamEditText);
        imageView = findViewById(R.id.imageView);

        uploadButton = findViewById(R.id.uploadButton);
        progressBar = findViewById(R.id.progressBar);

        // Events fragment tab

        //Team details tab

        // Set onClick listener for choosing an image
        imageView.setOnClickListener(onClick -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            chooseEventImageLauncher.launch(intent);
        });

        // Set onClick listener for uploading team details
        uploadButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Check if all required fields are filled
                String teamName = uploadTeamName.getText().toString().trim();
                String playerNames = dynamicChipEditText.getText().toString().trim();
                String selectedLeague = leaguesSpinner.getSelectedItem().toString();

                if (teamName.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter a team name", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Validate number of chips
                if (chipGroup.getChildCount() < 3) {
                    Toast.makeText(getApplicationContext(), "Please add at least 3 players", Toast.LENGTH_SHORT).show();
                    return;
                }
                // You may want to validate player names further here if needed
                if (selectedLeague.equals("N/A")) {
                    Toast.makeText(getApplicationContext(), "Please select a league", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (selectedImageUri == null) {
                    Toast.makeText(getApplicationContext(), "Please select an image first", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE); // Show progress bar
                saveImageToStorage();
            }
        });

    }

    // Method to save the selected image to Firebase Storage
    private void saveImageToStorage() {

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if(isLoggedIn) {
            // Create a reference to the storage location
            StorageReference storageRef = storage.getReference();
            StorageReference imageRef = storageRef.child("IMAGES_FOLDER/" + System.currentTimeMillis());

            // Upload the image
            imageRef.putFile(selectedImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Image uploaded successfully, get the download URL
                            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri downloadUrl) {
                                    // Store the download URL in Firebase Realtime Database
                                    imageUrlToSaveInDatabase = downloadUrl.toString();
                                    uploadTeamDetails();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle the error
                            String errorMessage = e.getMessage();
                            progressBar.setVisibility(View.INVISIBLE); // Hide progress bar
                            Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            //Handle the case when the user is not authenticated
            progressBar.setVisibility((View.INVISIBLE)); //hide progress bar
            Toast.makeText(getApplicationContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to upload event details to Firebase Realtime Database
    private void uploadTeamDetails() {
        String teamName = uploadTeamName.getText().toString();
        String teamLeague = leaguesSpinner.getSelectedItem().toString();
        List<String> playerNames = getChipNames();

        // Create a Teams object
        Teams teams = new Teams(imageUrlToSaveInDatabase, teamName, teamLeague, playerNames);

        // Get a reference to the "events" node in Firebase Realtime Database
        DatabaseReference teamsRef = firebaseDatabase.getReference("teams");

        // Add the event to Firebase Realtime Database
        teamsRef.push().setValue(teams)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBar.setVisibility(View.INVISIBLE); // Hide progress bar
                        Toast.makeText(getApplicationContext(), "Uploaded Successfully to the database", Toast.LENGTH_SHORT).show();
                        clearAllInputs();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.INVISIBLE); // Hide progress bar
                        Toast.makeText(getApplicationContext(), "Failed to upload, Kindly repeat uploading", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Method to clear all chips and reset input fields
    private void clearAllInputs() {
        chipGroup.removeAllViews(); // Clear all chips
        dynamicChipEditText.setText(""); // Clear the input text field
        uploadTeamName.setText(""); // Clear the team name field
        leaguesSpinner.setSelection(0);// Reset the spinner selection
        imageView.setImageResource(R.drawable.imageuploadicon);// Reset the imageView to default image
    }

    private List<String> getChipNames() {
        List<String> names = new ArrayList<>();
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) chipGroup.getChildAt(i);
            names.add(chip.getText().toString());
        }
        return names;
    }

    // Launcher for the activity result of choosing an image
    private final ActivityResultLauncher<Intent> chooseEventImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Handle the result, e.g., get the selected image URI
                    Intent data = result.getData();
                    if (data != null && data.getData() != null) {
                        selectedImageUri = data.getData();
                        imageView.setImageURI(selectedImageUri);
                    }
                }
            }
    );

    private void addChip(String name) {
        Chip chip = new Chip(this);
        chip.setText(name);
        chip.setCloseIconVisible(true);
        chip.setChipBackgroundColorResource(R.color.black);
        chip.setTextColor(Color.WHITE);
        chip.setCloseIconTint(ColorStateList.valueOf(Color.WHITE));
        chip.setOnCloseIconClickListener(v -> chipGroup.removeView(chip));
        chipGroup.addView(chip);
        Toast.makeText(this, "Added: " + name, Toast.LENGTH_SHORT).show();
    }
}