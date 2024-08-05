package com.example.ticketcard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketcard.model.Event;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> eventList;

    public EventAdapter(List<Event> eventList) {
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.matchTextView.setText(event.match);
        holder.roundTextView.setText(event.round);
        if (event.imageUri != null) {
            holder.imageViewEvent.setImageURI(event.imageUri);
        } else {
            holder.imageViewEvent.setImageResource(R.drawable.imageuploadicon);
        }
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

//    private void updateEventListWithImage() {
//        eventList.clear();
//        for (String match : selectedEvents.keySet()) {
//            String round = selectedEvents.get(match);
//            Uri imageUri = eventImages.get(match);
//            eventList.add(new Event(match, round, imageUri != null ? imageUri.toString() : null));
//        }
//        notifyDataSetChanged();
//    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        public TextView matchTextView;
        public TextView roundTextView;
        public ImageView imageViewEvent;

        public EventViewHolder(View view) {
            super(view);
            matchTextView = view.findViewById(R.id.text_view_match);
            roundTextView = view.findViewById(R.id.text_view_round);
            imageViewEvent = view.findViewById(R.id.image_view_event);
        }
    }
}
