package com.example.ticketcard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ticketcard.model.Event;

import java.util.ArrayList;
import java.util.List;

class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {

    private List<Event> events;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public EventsAdapter(Context context, List<Event> events) {
        this.context = context;
        this.events = events != null ? events : new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.eventscardview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
            Event event = events.get(position);

            holder.textView.setText(event.getMatch().replace("_", "."));

            Glide.with(context).load(event.getImageUrl()).into(holder.imageView);

            holder.itemView.setOnClickListener(view -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onClick(event.getImageUrl(),
                            event.getMatch(),
                            event.getRound(),
                            event.getTeamA(),
                            event.getTeamB(),
                            event.getTeamALogo(),
                            event.getTeamBLogo(),
                            event.getMatchTime(),
                            event.getMatchDate(),
                            event.getMatchMonth(),
                            event.getMatchVenue(),
                            event.getMatchRegular(),
                            event.getMatchVIP());
                }
            });
    }

    @Override
    public int getItemCount() {
        return events != null ? events.size() : 0;
    }

    public void setEvents(List<Event> eventsList) {
        events.clear();
        events.addAll(eventsList);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image2);
            textView = itemView.findViewById(R.id.text2);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onClick(String imageUrl, String matchDetails, String round, String teamA, String teamB, String teamALogo, String teamBLogo, String matchTime, String matchDate, String matchMonth, String matchVenue, String matchRegular, String matchVIP);
    }
}
