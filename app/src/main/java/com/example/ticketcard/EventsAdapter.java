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
    Context context;
    OnItemClickListener onItemClickListener;

    public EventsAdapter(Context context, List<Event> events) {
        this.events = events != null ? events : new ArrayList<>();
        this.context = context;

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

        Glide.with(context).load(event.getImageUrl()).into(holder.imageView);
       // holder.textView.setText(event.getDescription());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onClick(holder.imageView, event.getImageUrl(), event.getDescription());
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
            //textView = itemView.findViewById(R.id.text2);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onClick(ImageView imageView, String imageUrl, String imageDescription);
    }
}
