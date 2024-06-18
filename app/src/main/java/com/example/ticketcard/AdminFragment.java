package com.example.ticketcard;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ticketcard.model.Event;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AdminFragment extends Fragment {

    // UI elements
    private EditText uploadCaption;
    private ImageView imageView;
    private FloatingActionButton uploadButton;
    private ProgressBar progressBar;

    // Firebase instances
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage storage;

    // URI for selected image and URL for storing in Firebase Realtime Database
    private Uri selectedImageUri;
    private String imageUrlToSaveInDatabase;

    // Called to create and return the view hierarchy associated with the fragment
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin, container, false);

        // Initialize Firebase Realtime Database and Firebase Storage
        firebaseDatabase = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        // Initialize UI elements
        uploadCaption = view.findViewById(R.id.uploadcaption);
        imageView = view.findViewById(R.id.imageView);
        uploadButton = view.findViewById(R.id.uploadButton);
        progressBar = view.findViewById(R.id.progressBar);

        // Set onClick listener for choosing an image
        imageView.setOnClickListener(onClick -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            chooseEventImageLauncher.launch(intent);
        });

        // Set onClick listener for uploading event details
        uploadButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (selectedImageUri == null) {
                    Toast.makeText(getActivity(), "Please select an image first", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE); // Show progress bar
                saveImageToStorage();
            }
        });

        return view;
    }

    // Method to save the selected image to Firebase Storage
    private void saveImageToStorage() {
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
                                uploadEventDetails();
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
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Method to upload event details to Firebase Realtime Database
    private void uploadEventDetails() {
        String title = "Popular Events";
        String description = uploadCaption.getText().toString();

        // Create an Event object
        Event event = new Event(title, description, imageUrlToSaveInDatabase);

        // Get a reference to the "events" node in Firebase Realtime Database
        DatabaseReference eventsRef = firebaseDatabase.getReference("events");

        // Add the event to Firebase Realtime Database
        eventsRef.push().setValue(event)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressBar.setVisibility(View.INVISIBLE); // Hide progress bar
                        Toast.makeText(getActivity(), "Uploaded Successfully to the database", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.INVISIBLE); // Hide progress bar
                        Toast.makeText(getActivity(), "Failed to upload, Kindly repeat uploading", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Launcher for the activity result of choosing an image
    private final ActivityResultLauncher<Intent> chooseEventImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == getActivity().RESULT_OK) {
                    // Handle the result, e.g., get the selected image URI
                    Intent data = result.getData();
                    if (data != null && data.getData() != null) {
                        selectedImageUri = data.getData();
                        imageView.setImageURI(selectedImageUri);
                    }
                }
            }
    );
}
