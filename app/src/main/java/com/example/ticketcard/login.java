package com.example.ticketcard;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
    private EditText emailEditText;
    private EditText passwordEditText;
    TextView signUpBtn, forgotPassBtn;

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
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);


// Set OnClickListener for sign-up button
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                progressBar.setVisibility(View.VISIBLE); // Show progress bar
                // Start SignUp activity
                startActivity(new Intent(getApplicationContext(), SignUp.class));
 startActivity(new Intent(getApplicationContext(), SignUpActivity.class));

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

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateEmail() && validatePassword()) {
                    checkEmailExists();
                }

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

    public Boolean validateEmail() {
        String val = emailEditText.getText().toString();
        if (val.isEmpty()) {
            emailEditText.setError("Email cannot be empty");
            return false;
        } else {
            return true;
        }
    }

    public Boolean validatePassword() {
        String val = passwordEditText.getText().toString();
        if (val.isEmpty()) {
            passwordEditText.setError("Password cannot be empty");
            return false;
        } else {
            return true;
        }
    }

    public void checkEmailExists() {
        String userEmail = emailEditText.getText().toString();
        String userPassword = passwordEditText.getText().toString();

        DatabaseReference loginReference = FirebaseDatabase.getInstance().getReference("Users_info");
        loginReference.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String passwordFromDB = userSnapshot.child("password").getValue(String.class);

                        if (passwordFromDB != null && passwordFromDB.equals(userPassword)) {
                            passwordEditText.setError(null);

                            // Pass user data to Home activity if needed
                            Intent intent = new Intent(getApplicationContext(), Home.class);
                            startActivity(intent);
                            finish();
                            return;
                        } else {
                            passwordEditText.setError("Invalid password");
                            passwordEditText.requestFocus();
                        }
                    }
                } else {
                    emailEditText.setError("Email does not exist");
                    emailEditText.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors.
            }
        });

    }
}
