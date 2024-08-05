package com.example.ticketcard;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class login extends AppCompatActivity {

    private Button loginBtn;
    private EditText emailEditText;
    private EditText passwordEditText;
    TextView signUpBtn, forgotPassBtn;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        signUpBtn = findViewById(R.id.signUpBtn);
        loginBtn = findViewById(R.id.lgnBtn);
        forgotPassBtn = findViewById(R.id.forgotPasswordBtn);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
                finish();
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateEmail() && validatePassword()) {
                    checkEmailExists();
                }
            }
        });

        forgotPassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ForgotPassword.class));
                finish();
            }
        });
    }

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

                            //Get the username from DB and store it in a string variable
                            String nameFromDB = userSnapshot.child("name").getValue(String.class);
                            String adminStatus = "";

                            if(userSnapshot.child("admin").exists()){
                                adminStatus = userSnapshot.child("admin").getValue(String.class);
                            }

                            // Save login status and user data in SharedPreferences
                            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("isLoggedIn", true);
                            editor.putString("userName", nameFromDB);
                            editor.putString("userEmail", userEmail);
                            editor.apply();

                            if(adminStatus.equals("true")){
                                // go to admin section
                                Intent intent = new Intent(getApplicationContext(), AdminDashboard.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // Pass user data to Home activity if needed
                                Intent intent = new Intent(getApplicationContext(), Home.class);
                                startActivity(intent);
                                finish();
                            }
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
                Toast.makeText(login.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}