package com.example.ticketcard;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Settings extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}

/*package com.example.ticketbooking;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    private HomeFragment homeFragment = new HomeFragment();
    private TransactionsFragment transactionsFragment = new TransactionsFragment();
    private TicketsFragment ticketsFragment = new TicketsFragment();
    private SettingsFragment settingsFragment = new SettingsFragment();
    private static final String TAG_FRAGMENT_HOME = "tag_frag_home";
    private static final String TAG_FRAGMENT_REGISTER = "tag_frag_register";
    private static final String TAG_FRAGMENT_REPORT = "tag_frag_report";
    private static final String TAG_FRAGMENT_SETTINGS = "tag_frag_settings";
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.home) {
                    //if home icon is clicked show home fragment
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, homeFragment, TAG_FRAGMENT_HOME).commit();
                    return true;
                } else if (item.getItemId() == R.id.trans) {
                    //if register icon is clicked show register fragment
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, transactionsFragment, TAG_FRAGMENT_REGISTER).commit();
                    return true;
                } else if (item.getItemId() == R.id.ticko) {
                    //if report icon is clicked show report fragment
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, ticketsFragment, TAG_FRAGMENT_REPORT).commit();
                    return true;
                } else if (item.getItemId() == R.id.settings) {
                    //if settings fragment is clicked show settings fragment
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, settingsFragment, TAG_FRAGMENT_SETTINGS).commit();
                    return true;
                } else {
                    return false;
                }
            }
        });

        //set the Home Fragment to be displayed by default.
        this.getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, homeFragment, TAG_FRAGMENT_HOME).commit();

        // Find the Button with id "button" from the layout
        button = findViewById(R.id.btn);

        // Set a click listener on the button
        button.setOnClickListener(v -> {
            // Create an Intent to navigate to the login activity
            Intent intent = new Intent(MainActivity.this, login.class);

            // Start the login activity with the created intent
            startActivity(intent);
            Toast.makeText(MainActivity.this, "Welcome, please Sign in.", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
*/