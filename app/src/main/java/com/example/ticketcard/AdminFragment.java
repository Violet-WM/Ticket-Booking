package com.example.ticketcard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AdminFragment extends Fragment {

    // Called to create and return the view hierarchy associated with the fragment
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin, container, false);

        Button addEventsButton = view.findViewById(R.id.addEventsButton);
        Button addTeamsButton = view.findViewById(R.id.addTeamsButton);
        Button addStatisticsButton = view.findViewById(R.id.addStatisticsButton);

        addEventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddEventsActivity.class));
            }
        });

        addTeamsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddTeamsActivity.class));
            }
        });

        addStatisticsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddStatisticsActivity.class));
            }
        });
        return view;
    }
}
