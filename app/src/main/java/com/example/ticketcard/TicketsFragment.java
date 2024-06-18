package com.example.ticketcard;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class TicketsFragment extends Fragment {

    public TicketsFragment(){
        // require a empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tickets, container, false);

        LinearLayout team = view.findViewById(R.id.GorAFC);
        team.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), TicketCardClicked.class);
            startActivity(intent);
        });
        return view;
    }
}