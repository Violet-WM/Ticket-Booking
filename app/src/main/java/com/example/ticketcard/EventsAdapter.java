package com.example.ticketcard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;

import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketcard.model.Event;

import java.util.ArrayList;
import java.util.List;

class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {

    private List<Event> events;

    EventsAdapter(List<Event> events) {
        this.events = events != null ? events : new ArrayList<>();

        /*  this.events = events;*/
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.eventscardview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Event event = events.get(position);

        // Use Picasso to load the image from the internet
        Picasso.get()
                .load(event.getImageUrl())
//              .placeholder(R.drawable.image2) // Optional: a placeholder image
                /*.error(R.drawable.error)*/ // Optional: an error image if the URL fails to load
                .into(holder.imageView); // Load the image into the ImageView

        holder.textView.setText(event.getDescription());
    }


    @Override
    public int getItemCount() {
        return events != null ? events.size() : 0;
        /*return events.size();*/
    }

    public void setEvents(List<Event> eventsList) {
        events.clear();
        events.addAll(eventsList);
        notifyDataSetChanged();  //
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image2);
            textView = itemView.findViewById(R.id.text2);
        }
    }
}
