package com.example.ticketcard;

import android.content.SharedPreferences;
import android.os.Bundle;
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

public class Password extends AppCompatActivity {

    TextInputEditText oldPassword, newPassword, confirmPassword;
    Button updatePasswordButton;
    String currentUsername;
    DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Retrieve the user name from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        currentUsername = sharedPreferences.getString("userName", "user"); // "user" is the default value if "userName" is not found

        oldPassword = findViewById(R.id.oldPassword);
        newPassword = findViewById(R.id.newPassword);
        confirmPassword = findViewById(R.id.confirmPassword);
        updatePasswordButton = findViewById(R.id.updatePasswordButton);

        usersRef = FirebaseDatabase.getInstance().getReference("Users_info");

        // Load the current password from the database
        usersRef.child(currentUsername).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String currentPassword = dataSnapshot.child("password").getValue(String.class);
                    oldPassword.setText(currentPassword);
                } else {
                    Toast.makeText(Password.this, "User not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Password.this, "Failed to retrieve data", Toast.LENGTH_SHORT).show();
            }
        });

        updatePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newPass = newPassword.getText().toString().trim();
                String confirmPass = confirmPassword.getText().toString().trim();

                if (!newPass.equals(confirmPass)) {
                    Toast.makeText(Password.this, "New password and confirm password don't match", Toast.LENGTH_SHORT).show();
                } else {
                    updatePassword(newPass);
                }
            }
        });
    }

    private void updatePassword(String newPass) {
        usersRef.child(currentUsername).child("password").setValue(newPass).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(Password.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Password.this, "Failed to update password", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
