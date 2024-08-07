package com.example.ticketcard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AccountInfo extends AppCompatActivity {

    TextInputEditText email, username, number;
    Button updateButton, passwordChange;
    String currentUsername;
    DatabaseReference usersRef, reservedSeatsRef, ticketsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account_info);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Retrieve the user name from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        currentUsername = sharedPreferences.getString("userName", "user"); // "user" is the default value if "userName" is not found

        email = findViewById(R.id.email);
        username = findViewById(R.id.username);
        number = findViewById(R.id.number);
        updateButton = findViewById(R.id.updateButton);
        passwordChange = findViewById(R.id.passwordChange);

        usersRef = FirebaseDatabase.getInstance().getReference("Users_info");
        reservedSeatsRef = FirebaseDatabase.getInstance().getReference("reservedSeats");
        ticketsRef = FirebaseDatabase.getInstance().getReference("tickets");

        // Load the current user info from the database
        loadUserInfo();

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserInfo();
            }
        });

        passwordChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AccountInfo.this, Password.class));
            }
        });
    }

    private void loadUserInfo() {
        usersRef.child(currentUsername).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String currentEmail = dataSnapshot.child("email").getValue(String.class);
                    String currentName = dataSnapshot.child("name").getValue(String.class);
                    String currentNumber = dataSnapshot.child("number").getValue(String.class);

                    email.setText(currentEmail);
                    username.setText(currentName);
                    number.setText(currentNumber);
                } else {
                    Toast.makeText(AccountInfo.this, "User not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(AccountInfo.this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserInfo() {
        String newEmail = email.getText().toString().trim();
        String newName = username.getText().toString().trim();
        String newNumber = number.getText().toString().trim();

        if (TextUtils.isEmpty(newEmail) || TextUtils.isEmpty(newName) || TextUtils.isEmpty(newNumber)) {
            Toast.makeText(AccountInfo.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        usersRef.child(currentUsername).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String currentEmail = dataSnapshot.child("email").getValue(String.class);
                    String currentName = dataSnapshot.child("name").getValue(String.class);
                    String currentNumber = dataSnapshot.child("number").getValue(String.class);

                    if (newEmail.equals(currentEmail) && newName.equals(currentName) && newNumber.equals(currentNumber)) {
                        Toast.makeText(AccountInfo.this, "Values haven't changed", Toast.LENGTH_SHORT).show();
                    } else {
                        if (!newEmail.equals(currentEmail)) {
                            usersRef.child(currentUsername).child("email").setValue(newEmail);
                        }
                        if (!newName.equals(currentName)) {
                            usersRef.child(currentUsername).child("name").setValue(newName);
                        }
                        if (!newNumber.equals(currentNumber)) {
                            usersRef.child(currentUsername).child("number").setValue(newNumber);
                        }
                        usersRef.getParent().child(currentUsername).setValue(newName);
                        updateReservedSeats(newEmail, newName);
                        updateTickets(newName);
                        updateSharedPreferences(newEmail, newName, newNumber);
                        Toast.makeText(AccountInfo.this, "Information updated successfully", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(AccountInfo.this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateReservedSeats(String newEmail, String newName) {
        reservedSeatsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot stadiumSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot matchSnapshot : stadiumSnapshot.getChildren()) {
                        if (matchSnapshot.child(currentUsername).exists()) {
                            DatabaseReference userRef = matchSnapshot.child(currentUsername).getRef();
                            userRef.child("userEmail").setValue(newEmail);
                            userRef.child("userName").setValue(newName);
                            if (!currentUsername.equals(newName)) {
                                userRef.getParent().child(newName).setValue(matchSnapshot.child(currentUsername).getValue());
                                userRef.removeValue();
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(AccountInfo.this, "Failed to update reserved seats", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTickets(String newName) {
        ticketsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(currentUsername).exists()) {
                    DatabaseReference userRef = dataSnapshot.child(currentUsername).getRef();
                    if (!currentUsername.equals(newName)) {
                        ticketsRef.child(newName).setValue(dataSnapshot.child(currentUsername).getValue());
                        userRef.removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(AccountInfo.this, "Failed to update tickets", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateSharedPreferences(String newEmail, String newName, String newNumber) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userEmail", newEmail);
        editor.putString("userName", newName);
        editor.putString("userNumber", newNumber);
        editor.apply();
    }
}
