package com.example.ticketcard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ticketcard.model.TicketEvent;

import java.util.ArrayList;
import java.util.List;

public class TicketEventAdapter extends RecyclerView.Adapter<TicketEventAdapter.ViewHolder> {
    private List<TicketEvent> ticketEvents;
    Context context;
    OnItemClickListener onItemClickListener;

    public TicketEventAdapter(Context context, List<TicketEvent> ticketEvents) {
        this.ticketEvents = ticketEvents != null ? ticketEvents : new ArrayList<>();
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ticketscardview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        TicketEvent ticketEvent = ticketEvents.get(position);
        Glide.with(context).load(ticketEvent.getImageUrl()).into(holder.ticketImage);
        holder.imgDescription.setText(ticketEvent.getImgDescription());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onClick(holder.ticketImage, ticketEvent.getImageUrl(), ticketEvent.getImgDescription());
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
    }


    public static class ViewHolder extends  RecyclerView.ViewHolder {

        ImageView ticketImage;
        TextView imgDescription;

        public ViewHolder(View itemView) {
            super(itemView);
            ticketImage = itemView.findViewById(R.id.ticketImage);
            imgDescription = itemView.findViewById(R.id.imgDescription);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onClick(ImageView ticketImage, String imageUrl, String imgDescription);
    }
}
