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
import com.example.ticketcard.model.StadiumViews;

import java.util.ArrayList;
import java.util.List;

public class StadiumViewsAdapter extends RecyclerView.Adapter<StadiumViewsAdapter.ViewHolder> {

    private Context context;
    private List<StadiumViews> stadiumViews;

    public StadiumViewsAdapter(Context context, List<StadiumViews> stadiumViews) {
        this.context = context;
        this.stadiumViews = stadiumViews != null ? stadiumViews : new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.stadium_pictures_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StadiumViews stadiumView = stadiumViews.get(position);
        holder.sideNameTextView.setText(stadiumView.getSideName());
        Glide.with(context)
                .load(stadiumView.getStadiumImage())
                .into(holder.stadiumImageView);
    }

    @Override
    public int getItemCount() {
        return stadiumViews != null ? stadiumViews.size() : 0;
    }

    public void setEvents(List<StadiumViews> stadiumViewsList) {
        stadiumViews.clear();
        stadiumViews.addAll(stadiumViewsList);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView sideNameTextView;
        private ImageView stadiumImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            sideNameTextView = itemView.findViewById(R.id.sideNameTextView);
            stadiumImageView = itemView.findViewById(R.id.stadiumImageView);
        }
    }
}
