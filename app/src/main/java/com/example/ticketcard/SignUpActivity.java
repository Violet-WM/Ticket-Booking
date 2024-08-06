package com.example.ticketcard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUpActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText nameEditText;
    private EditText confirmPasswordEditText;
    private EditText numberEditText;
    private Button signUpButton;
    private Button logInButton;
    private Button forgotPasswordButton;
    private Boolean emailExists;
    private Boolean numberExists;
    private String key;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        nameEditText = findViewById(R.id.nameEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        numberEditText = findViewById(R.id.numberEditText);
        signUpButton = findViewById(R.id.signUpButton);
        logInButton = findViewById(R.id.logInButton);
        forgotPasswordButton = findViewById(R.id.forgotPasswordButton);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Connect to DB
                database = FirebaseDatabase.getInstance();
                reference = database.getReference("Users_info");
                key = reference.push().getKey();

                //check that edittexts aren't empty
                if(validateEmail() && validateName() && validateNumber() && validatePassword()) {
                    //check that the email isn't in the DB
                    checkForExistingEmail();

                }
            }
        });

    }
    public Boolean validateName() {
        String val = nameEditText.getText().toString();
        if(val.isEmpty()) {
            nameEditText.setError("Cannot be empty");
            return false;
        } else {
            nameEditText.setError(null);
            return true;
        }
    }
    public Boolean validatePassword() {
        String val = passwordEditText.getText().toString();
        if(val.isEmpty()) {
            passwordEditText.setError("Cannot be empty");
            return false;
        } else {
            passwordEditText.setError(null);
            return true;
        }
    }

    public Boolean validateNumber() {
        String val = numberEditText.getText().toString();
        if(val.isEmpty()) {
            numberEditText.setError("Cannot be empty");
            return false;
        } else {
            numberEditText.setError(null);
            return true;
        }
    }

    public Boolean validateEmail() {
        String val = emailEditText.getText().toString();
        if(val.isEmpty()) {
            emailEditText.setError("Cannot be empty");
            return false;
        } else {
            emailEditText.setError(null);
            return true;
        }
    }


    //check that a user with the same email doesn't exist in the DB
    public void checkForExistingEmail() {
        String userEmail = emailEditText.getText().toString().trim();
        reference.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    emailExists = true;
                    emailEditText.setError("User already exists");
                    emailEditText.requestFocus();

                } else {

                    emailExists = false;

                    checkForExistingNumber();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void checkForExistingNumber() {
        String userPhone = numberEditText.getText().toString().trim();
        reference.orderByChild("number").equalTo(userPhone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    numberEditText.setError("Phone number already exists");
                    numberEditText.requestFocus();
                    numberExists = true;
                } else {
                    numberExists = false;

                    String name = nameEditText.getText().toString();
                    String email = emailEditText.getText().toString();
                    String password = passwordEditText.getText().toString();
                    String number = numberEditText.getText().toString();

                    //Create an object to hold all the values entered
                    HelperClass helperClass = new HelperClass(name, email, password, number);

                    //use email because it's unique and no two people can have the same email
                    //add details to DB
                    reference.child(name).setValue(helperClass);

                    Toast.makeText(SignUpActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();

                    //navigate to login class
                    Intent intent = new Intent(getApplicationContext(), login.class);
                    startActivity(intent);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}