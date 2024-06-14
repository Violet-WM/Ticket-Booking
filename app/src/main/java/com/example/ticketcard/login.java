package com.example.ticketcard;

// Import necessary Android classes
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

// Import AppCompatActivity class from androidx.appcompat library
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

// Define a class named login, which extends AppCompatActivity
public class login extends AppCompatActivity {

// Declaration

    FirebaseAuth firebaseAuth;

    EditText email;
    private ProgressBar progressBar;
    EditText password;
    private Button loginBtn;
    TextView signUpBtn, forgotPassBtn;

    // Override the onCreate method of AppCompatActivity
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

// Call the superclass onCreate method passing the savedInstanceState
        super.onCreate(savedInstanceState);

// Set the layout of this activity to activity_login.xml
        setContentView(R.layout.activity_login);

// Initialize Firebase authentication
        firebaseAuth = FirebaseAuth.getInstance();

// Initialize UI elements
        signUpBtn = findViewById(R.id.signUpBtn);
        loginBtn = findViewById(R.id.login_Btn);
        forgotPassBtn = findViewById(R.id.forgotPasswordBtn);
        email = findViewById(R.id.txtemail_login);
        password = findViewById(R.id.txtPass_login);
        progressBar = findViewById(R.id.progressBar);

// Set OnClickListener for sign-up button
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressBar.setVisibility(View.VISIBLE); // Show progress bar
                // Start SignUp activity
                startActivity(new Intent(getApplicationContext(), SignUp.class));
                finish();
            }
        });

// Set OnClickListener for login button
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

// Get email and password input
                String myEmail = email.getText().toString().trim();
                String myPassword = password.getText().toString().trim();
                progressBar.setVisibility(View.VISIBLE); // Show progress bar

// Validate email and password
                if (TextUtils.isEmpty(myEmail)) {
                    email.setError("Email is required");
                    return;
                }
                if (TextUtils.isEmpty(myPassword)) {
                    password.setError("Password is required");
                    return;
                }

// Call method to authenticate user
                loginUser(myEmail, myPassword);
            }
        });

// Set OnClickListener for forgot password button
        forgotPassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressBar.setVisibility(View.VISIBLE); // Show progress bar

// Start ForgotPassword activity
                startActivity(new Intent(getApplicationContext(), ForgotPassword.class));
                finish();

            }
        });
    }

    // Method to authenticate user using Firebase
    private void loginUser(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(login.this, "Login Successful", Toast.LENGTH_LONG).show();
                            goToMainPage();
                        } else {
                            Toast.makeText(login.this, "Authentication Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    // Method to navigate to the home page
    private void goToMainPage() {
        Intent intent = new Intent(login.this, Home.class);
        startActivity(intent);
        finish();
    }
}
