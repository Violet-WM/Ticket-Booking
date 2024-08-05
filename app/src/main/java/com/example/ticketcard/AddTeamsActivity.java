package com.example.ticketcard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ticketcard.model.Players;
import com.example.ticketcard.model.Teams;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddTeamsActivity extends AppCompatActivity {
    // UI elements
    private EditText uploadTeamName;
    private ImageView imageView;
    private FloatingActionButton uploadButton;
    private ProgressBar progressBar;
    private Spinner leaguesSpinner;
    private Spinner teamRolesSpinner;
    private TextInputEditText dynamicChipEditText;
    private ChipGroup chipGroup;

    // Firebase instances
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage storage;

    // URI for selected image and URL for storing in Firebase Realtime Database
    private Uri selectedImageUri;
    private String imageUrlToSaveInDatabase;
    private String teamKey;
    private TextInputEditText homeStadium;

    // HashMap to store player details
    private Map<String, Players> playerDetailsMap = new HashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_teams);

        String[] leagues = {"Kenya Premier League", "National Super League", "Women's Premier League"};

        dynamicChipEditText = findViewById(R.id.dynamicChipEditText);
        chipGroup = findViewById(R.id.chipGroup);

        dynamicChipEditText.addTextChangedListener(new TextWatcher() {

            private boolean isProcessing;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Not used
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
                isProcessing = false;
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
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(AddTeamsActivity.this, android.R.layout.simple_spinner_item, leagues);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        leaguesSpinner.setAdapter(spinnerAdapter);

        // Initialize Firebase Realtime Database and Firebase Storage
        firebaseDatabase = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        // Initialize UI elements
        uploadTeamName = findViewById(R.id.teamEditText);
        imageView = findViewById(R.id.imageView);
        uploadButton = findViewById(R.id.uploadButton);
        progressBar = findViewById(R.id.progressBar);
        homeStadium = findViewById(R.id.homeStadium);

//        uploadTeamName.addTextChangedListener(new TextWatcher() {
//            private Boolean isProcessing;
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                if (isProcessing) return;
//                isProcessing = true;
//                String input = editable.toString();
//                String capitalizedInput = capitalizeWords(input);
//                if (!input.equals(capitalizedInput)) {
//                    uploadTeamName.setText(capitalizedInput);
//                    uploadTeamName.setSelection(capitalizedInput.length());
//                }
//                isProcessing = false;
//            }
//
//            private String capitalizeWords(String str) {
//                StringBuilder capitalized = new StringBuilder();
//                boolean capitalizeNext = true;
//                for (char c : str.toCharArray()) {
//                    if (Character.isWhitespace(c)) {
//                        capitalizeNext = true;
//                        capitalized.append(c);
//                    } else if (capitalizeNext) {
//                        capitalized.append(Character.toUpperCase(c));
//                        capitalizeNext = false;
//                    } else {
//                        capitalized.append(Character.toLowerCase(c));
//                    }
//                }
//                return capitalized.toString();
//            }
//        });

        homeStadium.addTextChangedListener(new TextWatcher() {
            private boolean isProcessing;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (isProcessing) return;
                isProcessing = true;
                String input = editable.toString();
                String capitalizedInput = capitalizeWords(input);
                if (!input.equals(capitalizedInput)) {
                    homeStadium.setText(capitalizedInput);
                    homeStadium.setSelection(capitalizedInput.length());
                }
                isProcessing = false;
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
                String selectedLeague = leaguesSpinner.getSelectedItem().toString();
                String homeName = homeStadium.getText().toString().trim();

                if (teamName.isEmpty()) {
                    Toast.makeText(AddTeamsActivity.this, "Please enter a team name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(homeName.isEmpty()) {
                    Toast.makeText(AddTeamsActivity.this, "Please enter home stadium", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Validate number of chips
                if (chipGroup.getChildCount() < 1) {
                    Toast.makeText(AddTeamsActivity.this, "Please add at least 1 player", Toast.LENGTH_SHORT).show();
                    return;
                }
                // You may want to validate player names further here if needed
                if (selectedLeague.equals("N/A")) {
                    Toast.makeText(AddTeamsActivity.this, "Please select a league", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (selectedImageUri == null) {
                    Toast.makeText(AddTeamsActivity.this, "Please select an image first", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE); // Show progress bar
                saveImageToStorage();
            }
        });

    }


    // Method to save the selected image to Firebase Storage
    private void saveImageToStorage() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
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
                                    // Save the details to Firebase structure
                                    uploadTeamDetails(); // After uploading image, proceed to upload team details
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
                            Toast.makeText(AddTeamsActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Handle the case when the user is not authenticated
            progressBar.setVisibility(View.INVISIBLE); // Hide progress bar
            Toast.makeText(AddTeamsActivity.this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to upload team details to Firebase Realtime Database
    private void uploadTeamDetails() {
        String teamName = uploadTeamName.getText().toString();
        String teamLeague = leaguesSpinner.getSelectedItem().toString();
        String home = homeStadium.getText().toString();
        List<String> playerNames = getChipNames();

        // Create a Teams object
        Teams teams = new Teams(imageUrlToSaveInDatabase, teamName, teamLeague, home, playerNames);

        // Get a reference to the "teams" node in Firebase Realtime Database
        DatabaseReference teamsRef = firebaseDatabase.getReference("teams");

        // Add the team to Firebase Realtime Database
        DatabaseReference newTeamRef = teamsRef.push();
        teamKey = newTeamRef.getKey();
        newTeamRef.setValue(teams)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Save player details for each player
                        for (Map.Entry<String, Players> entry : playerDetailsMap.entrySet()) {
                            String playerName = entry.getKey();
                            Players playerDetails = entry.getValue();
                            newTeamRef.child("players").child(playerName).setValue(playerDetails)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Successfully uploaded player details
                                            progressBar.setVisibility(View.INVISIBLE); // Hide progress bar
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Handle failure to upload player details
                                            progressBar.setVisibility(View.INVISIBLE); // Hide progress bar
                                            Toast.makeText(AddTeamsActivity.this, "Failed to upload player details", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                        Toast.makeText(AddTeamsActivity.this, "Team uploaded successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddTeamsActivity.this, AddVenuesActivity.class);
                        intent.putExtra("stadium", home);
                        startActivity(intent);
                        // Clear the fields after successful upload
                        clearFields();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle the error
                        String errorMessage = e.getMessage();
                        progressBar.setVisibility(View.INVISIBLE); // Hide progress bar
                        Toast.makeText(AddTeamsActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Method to add a chip for a player
    private void addChip(String playerName) {
        final Chip chip = new Chip(AddTeamsActivity.this);
        chip.setText(playerName);
        chip.setCloseIconVisible(true);
        chip.setChipBackgroundColor(ColorStateList.valueOf(Color.GRAY));
        chip.setTextColor(Color.BLACK);
        chip.setCloseIconTint(ColorStateList.valueOf(Color.RED));
        chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chipGroup.removeView(chip);
                playerDetailsMap.remove(playerName); // Remove player from HashMap
            }
        });

        chip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditPlayerDialog(playerName);
            }
        });

        chipGroup.addView(chip);
        // Prompt user to enter player details after adding a chip
        showEditPlayerDialog(playerName);
    }

    // Method to get names of players from chips
    private List<String> getChipNames() {
        List<String> names = new ArrayList<>();
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) chipGroup.getChildAt(i);
            names.add(chip.getText().toString());
        }
        return names;
    }

    // Method to show dialog for editing player details
    private void showEditPlayerDialog(String playerName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddTeamsActivity.this);
        builder.setTitle("Edit Player Details");

        View viewInflated = LayoutInflater.from(AddTeamsActivity.this).inflate(R.layout.chip_card_layout, null);
        builder.setView(viewInflated);

        final TextView inputName = viewInflated.findViewById(R.id.playerNameTextView);
        final EditText inputNo = viewInflated.findViewById(R.id.playerNoChipEditText);
        final Spinner roleSpinner = viewInflated.findViewById(R.id.teamRolesSpinner);

        // Initialize spinner with roles
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(AddTeamsActivity.this, android.R.layout.simple_spinner_item, new String[]{"Forward", "Midfielder", "Defender", "Goalkeeper"});
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(spinnerAdapter);

        // If player details exist, populate the fields
        Players existingPlayer = playerDetailsMap.get(playerName);
        if (existingPlayer != null) {
            inputName.setText(existingPlayer.getName());
            inputNo.setText(existingPlayer.getNo());
            int spinnerPosition = spinnerAdapter.getPosition(existingPlayer.getRole());
            roleSpinner.setSelection(spinnerPosition);
        } else {
            inputName.setText(playerName);
        }

        builder.setPositiveButton("Save", (dialog, which) -> {
            dialog.dismiss();
            String name = inputName.getText().toString().trim();
            String no = inputNo.getText().toString().trim();
            String role = roleSpinner.getSelectedItem().toString();

            if (!name.isEmpty() && !no.isEmpty() && !role.isEmpty()) {
                Players player = new Players(name, role, no);
                playerDetailsMap.put(name, player);
                Toast.makeText(AddTeamsActivity.this, "Player details saved in chip!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(AddTeamsActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    // Method to clear fields after successful upload
    private void clearFields() {
        uploadTeamName.setText("");
        homeStadium.setText("");
        imageView.setImageURI(null);
        selectedImageUri = null;
        chipGroup.removeAllViews();
        playerDetailsMap.clear();
    }

    // Method to handle image selection
    private final ActivityResultLauncher<Intent> chooseEventImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        selectedImageUri = data.getData();
                        imageView.setImageURI(selectedImageUri);
                    }
                }
            });
}
