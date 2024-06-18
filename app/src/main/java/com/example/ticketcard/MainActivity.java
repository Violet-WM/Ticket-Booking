package com.example.ticketcard;

// Import statements
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;


// MainActivity class definition
public class MainActivity extends AppCompatActivity {
    // Declare a Button variable
    private Button button;

    // onCreate method is the entry point for the activity lifecycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the button by finding it from the layout
        button = findViewById(R.id.btn);

        // Set an OnClickListener to the button to handle click events
        button.setOnClickListener(v -> {

            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

            if (isLoggedIn) {
                // User is logged in, redirect to Home activity
                Intent intent = new Intent(getApplicationContext(), Home.class);
                startActivity(intent);
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
    }}

