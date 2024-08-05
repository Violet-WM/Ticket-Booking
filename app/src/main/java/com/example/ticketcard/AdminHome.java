package com.example.ticketcard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;


public class AdminHome extends Fragment {

    private Button revButton, eventsButton, priceButton;

    public AdminHome() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Retrieve the user name from SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userName = sharedPreferences.getString("userName", "user"); // "User" is the default value if "userName" is not found
        String userEmail = sharedPreferences.getString("userEmail", "email");

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_admin_home, container, false);

        eventsButton = view.findViewById(R.id.eventsButton);
        priceButton = view.findViewById(R.id.priceButton);
        revButton = view.findViewById(R.id.revButton);

        eventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), PickEvents.class);
                startActivity(intent);
            }
        });

        priceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), Price.class);
                startActivity(intent);
            }
        });

        revButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), Revenue.class);
                startActivity(intent);
            }
        });


        //the admin can track data such as
        //1. Tickets bought and by whom
        //2. Seats booked per stadium and by whom
        //3. Revenue earned from ticket sales
        //4. User Accounts for the app and their details (maybe)
        //
        //The admin can perform actions such as
        //1. Pick popular events to be displayed on the home page (max of 7)
        ///5. Delete details such as teams, venues and accounts
        //7. Set ticket price for events (make sure you transmit this data to the appropriate place)
        //8. Cancel events and start process to send back money to people (most likely not)

        return view;
    }
}