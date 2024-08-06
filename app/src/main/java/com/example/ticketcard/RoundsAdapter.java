package com.example.ticketcard;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ticketcard.model.TicketEvent;

import java.util.ArrayList;
import java.util.List;

public class RoundsAdapter extends RecyclerView.Adapter<RoundsAdapter.ViewHolder>{
    private List<TicketEvent> ticketEvents;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public RoundsAdapter(Context context, List<TicketEvent> ticketEvents) {
        this.ticketEvents = ticketEvents != null ? ticketEvents : new ArrayList<>();
        this.context = context;
    }

    @Override
    public RoundsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("Adapter logs", "onCreateViewHolder is being called");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rounds, parent, false);
        return new RoundsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RoundsAdapter.ViewHolder holder, int position) {
        TicketEvent ticketEvent = ticketEvents.get(position);
        Log.d("Adapter logs", "Binding view holder at position: " + position);

        Glide.with(context).load(ticketEvent.getImageUrl()).into(holder.ticketImage);
        holder.matchDetails.setText(ticketEvent.getMatchDetails().replace("_","."));
        holder.timeAndDate.setText(ticketEvent.getMatchTime() + " . " + ticketEvent.getMatchDate() + " . " + ticketEvent.getMatchMonth());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Adapter logs", "Item clicked at position: " + holder.getAdapterPosition());
                onItemClickListener.onClick(ticketEvent.getImageUrl(), ticketEvent.getMatchDetails(), ticketEvent.getTeamA(), ticketEvent.getTeamB(),
                        ticketEvent.getTeamALogo(), ticketEvent.getTeamBLogo(),
                        ticketEvent.getMatchTime(), ticketEvent.getMatchDate(), ticketEvent.getMatchMonth(), ticketEvent.getMatchVenue(),
                        ticketEvent.getMatchRegular(), ticketEvent.getMatchVIP(), ticketEvent.getRoundAdapter());
            }
        });
    }

    @Override
    public int getItemCount() {
        return ticketEvents != null ? ticketEvents.size() : 0;
    }

    public void setEvents(List<TicketEvent> eventsList) {
        ticketEvents.clear();
        ticketEvents.addAll(eventsList);
        notifyDataSetChanged();
        Log.d("Adapter logs", "Setting events in adapter, new size: " + ticketEvents.size());
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ticketImage;
        TextView matchDetails;
        TextView timeAndDate;

        public ViewHolder(View itemView) {
            super(itemView);
            ticketImage = itemView.findViewById(R.id.cardImage);
            matchDetails = itemView.findViewById(R.id.matchTxt);
            timeAndDate = itemView.findViewById(R.id.timeTxt);
        }
    }

    public void setOnItemClickListener(RoundsAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onClick(String imageUrl, String matchDetails, String teamA, String teamB, String teamALogo, String teamBLogo, String matchTime, String matchDate, String matchMonth, String matchVenue, String matchRegular, String matchVIP, String round);
    }
}
