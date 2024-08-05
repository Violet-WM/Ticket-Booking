package com.example.ticketcard;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketcard.model.Fixture;

import java.util.List;

public class FixtureAdapter extends RecyclerView.Adapter<FixtureAdapter.FixtureViewHolder> {
    private List<Fixture> fixtureList;

    public FixtureAdapter(List<Fixture> fixtureList) {
        this.fixtureList = fixtureList;
    }

    @NonNull
    @Override
    public FixtureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fixture, parent, false);
        return new FixtureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FixtureViewHolder holder, int position) {
        Fixture fixture = fixtureList.get(position);
        holder.matchup.setText(fixture.getMatchup());
        holder.matchDetails.setText(fixture.getTime() + " . " + fixture.getDate() + " . " + fixture.getRound() + " . " + fixture.getVenue());
        Log.d("tag", "In FixtureAdapter: adding views");
    }

    @Override
    public int getItemCount() {
        return fixtureList.size();
    }

    public static class FixtureViewHolder extends RecyclerView.ViewHolder {
        TextView matchup, matchDetails;

        public FixtureViewHolder(@NonNull View itemView) {
            super(itemView);
            matchup = itemView.findViewById(R.id.matchup);
            matchDetails = itemView.findViewById(R.id.matchDetails);
        }
    }
}
