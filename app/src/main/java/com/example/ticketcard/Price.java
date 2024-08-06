// Price.java
package com.example.ticketcard;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Price extends AppCompatActivity {

    private Spinner roundSpinner, matchSpinner;
    private EditText regularPriceEditText, vipPriceEditText;
    private Button updatePriceButton;

    private DatabaseReference databaseReference;
    private List<String> roundList = new ArrayList<>();
    private List<String> matchList = new ArrayList<>();
    private Map<String, String> matchMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price);

        roundSpinner = findViewById(R.id.roundSpinner);
        matchSpinner = findViewById(R.id.matchSpinner);
        regularPriceEditText = findViewById(R.id.regularPriceEditText);
        vipPriceEditText = findViewById(R.id.vipPriceEditText);
        updatePriceButton = findViewById(R.id.updatePriceButton);

        databaseReference = FirebaseDatabase.getInstance().getReference("fixtures").child("Kenya Premier League");

        loadRounds();

        updatePriceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTicketPrices();
            }
        });
    }

    private void loadRounds() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                roundList.clear();
                for (DataSnapshot roundSnapshot : snapshot.getChildren()) {
                    roundList.add(roundSnapshot.getKey());
                }
                ArrayAdapter<String> roundAdapter = new ArrayAdapter<>(Price.this, android.R.layout.simple_spinner_item, roundList);
                roundAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                roundSpinner.setAdapter(roundAdapter);
                roundSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        loadMatches(roundList.get(position));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Price.this, "Failed to load rounds", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMatches(String round) {
        databaseReference.child(round).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                matchList.clear();
                matchMap.clear();
                for (DataSnapshot matchSnapshot : snapshot.getChildren()) {
                    String matchKey = matchSnapshot.getKey();
                    matchList.add(matchKey);
                    matchMap.put(matchKey, round);
                }
                ArrayAdapter<String> matchAdapter = new ArrayAdapter<>(Price.this, android.R.layout.simple_spinner_item, matchList);
                matchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                matchSpinner.setAdapter(matchAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Price.this, "Failed to load matches", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTicketPrices() {
        String selectedMatch = matchSpinner.getSelectedItem().toString();
        String selectedRound = matchMap.get(selectedMatch);
        String regularPrice = regularPriceEditText.getText().toString().trim();
        String vipPrice = vipPriceEditText.getText().toString().trim();

        if (regularPrice.isEmpty() || vipPrice.isEmpty()) {
            Toast.makeText(this, "Please enter both prices", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> priceUpdate = new HashMap<>();
        priceUpdate.put("Regular", regularPrice);
        priceUpdate.put("VIP", vipPrice);

        databaseReference.child(selectedRound).child(selectedMatch).updateChildren(priceUpdate).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(Price.this, "Prices updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Price.this, "Failed to update prices", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
