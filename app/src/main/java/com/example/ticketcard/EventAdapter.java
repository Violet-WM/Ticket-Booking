package com.example.ticketcard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ticketcard.model.Event;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private List<Event> eventList;
    private Context context;
    private EventRemovedListener eventRemovedListener;

    public interface EventRemovedListener {
        void onEventRemoved(Event event);
    }

    public EventAdapter(List<Event> eventList, Context context, EventRemovedListener eventRemovedListener) {
        this.eventList = eventList;
        this.context = context;
        this.eventRemovedListener = eventRemovedListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.eventTitle.setText(event.getMatch());
        holder.eventRound.setText(event.getRound());
        String imageUrl = event.getImageUrl();
        Glide.with(context).load(imageUrl).into(holder.eventImage);

        holder.deleteButton.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            Event removedEvent = eventList.get(pos);
            deleteEvent(pos);
            eventRemovedListener.onEventRemoved(removedEvent);
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView eventTitle, eventRound;
        public ImageView eventImage;
        public Button deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            eventTitle = itemView.findViewById(R.id.text_view_match);
            eventRound = itemView.findViewById(R.id.text_view_round);
            eventImage = itemView.findViewById(R.id.image_view_event);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }

    public void deleteEvent(int position) {
        if (position >= 0 && position < eventList.size()) {
            eventList.remove(position);
            notifyItemRemoved(position);
        }
    }
}
