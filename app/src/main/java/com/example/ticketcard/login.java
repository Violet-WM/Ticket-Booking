package com.example.ticketcard;

// Import necessary Android classes
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

// Import AppCompatActivity class from androidx.appcompat library
import androidx.appcompat.app.AppCompatActivity;

// Define a class named login, which extends AppCompatActivity
public class login extends AppCompatActivity {

    // Declare a private member variable
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

        signUpBtn = findViewById(R.id.signUpBtn);
        loginBtn = findViewById(R.id.lgnBtn);
        forgotPassBtn = findViewById(R.id.forgotPasswordBtn);

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //the below code sends them to
                startActivity(new Intent(getApplicationContext(), SignUp.class));
                finish();
            }
        });


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //the below code sends them to admin tab
                startActivity(new Intent(getApplicationContext(), Home.class));
                finish();
            }
        });

        forgotPassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //this code sends them to forgot password activity
                startActivity(new Intent(getApplicationContext(), ForgotPassword.class));
                finish();
            }
        });
    }
}
//class to login
