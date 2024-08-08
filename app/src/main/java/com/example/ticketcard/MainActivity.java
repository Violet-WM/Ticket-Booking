package com.example.ticketcard;

// Import statements

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;


// MainActivity class definition
public class MainActivity extends AppCompatActivity {
    // Declare a Button variable
    private Button button;
    private String userName, userEmail;

    // onCreate method is the entry point for the activity lifecycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userName = sharedPreferences.getString("userName", "user"); // "user" is the default value if "userName" is not found
        userEmail = sharedPreferences.getString("userEmail", "email"); // "email" is the default value if "userEmail" is not found
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        // Initialize the button by finding it from the layout
        button = findViewById(R.id.btn);

        // Set an OnClickListener to the button to handle click events
        button.setOnClickListener(v -> {
            if (isLoggedIn) {
                if(userName.equals("admin")){
                    //redirect to adminDashboard
                    startActivity(new Intent(getApplicationContext(), AdminDashboard.class));
                } else {
                    // User is logged in, redirect to Home activity
                    Intent intent = new Intent(getApplicationContext(), Home.class);
                    startActivity(intent);
                }
            }
            else
            {
                // call Login Activity
                // Create an Intent to navigate from MainActivity to login activity
                Intent intent = new Intent(MainActivity.this, login.class);

                // Start the login activity
                startActivity(intent);

                // Show a Toast message to the user
                Toast.makeText(MainActivity.this, "Welcome, please Sign in.", Toast.LENGTH_SHORT).show();

                // Finish the current activity to prevent the user from returning to it
                finish();
            }
        });
    }
}