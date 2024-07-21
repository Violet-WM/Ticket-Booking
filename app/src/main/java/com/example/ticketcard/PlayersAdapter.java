package com.example.ticketcard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketcard.model.Players;

import java.util.List;

public class PlayersAdapter extends RecyclerView.Adapter<PlayersAdapter.ViewHolder> {

    private List<Players> players;
    Context context;

    public PlayersAdapter(Context context, List<Players> players) {
        this.players = players;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.team_stats_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Players player = players.get(position);

        holder.nameTextView.setText(player.getName());
        holder.roleTextView.setText(player.getRole());
        holder.ageTextView.setText(player.getAge());
    }

    @Override
    public int getItemCount() {
        return players != null ? players.size() : 0;
    }

    public void setEvents(List<Players> playersList) {
        players.clear();
        players.addAll(playersList);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView roleTextView;
        TextView ageTextView;

        ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            roleTextView = itemView.findViewById(R.id.teamRoleTextView);
            ageTextView = itemView.findViewById(R.id.ageTextView);
        }
    }
}
