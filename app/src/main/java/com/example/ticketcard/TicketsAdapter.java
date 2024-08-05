package com.example.ticketcard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketcard.model.TransactionsFrag;
import com.google.android.material.card.MaterialCardView;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TicketsAdapter extends RecyclerView.Adapter<TicketsAdapter.ViewHolder> {
    private List<TransactionsFrag> transactionsFrags;
    private Context context;

    public TicketsAdapter(Context context, List<TransactionsFrag> transactionsFrags) {
        this.context = context;
        this.transactionsFrags = transactionsFrags;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.gentickets, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TransactionsFrag transactionsFrag = transactionsFrags.get(position);

        // Separate VIP and regular seats
        Map<String, Map<String, Object>> vipSeatsMap = transactionsFrag.getSeatsMap().entrySet().stream()
                .filter(entry -> entry.getKey().startsWith("V"))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Map<String, Map<String, Object>> regularSeatsMap = transactionsFrag.getSeatsMap().entrySet().stream()
                .filter(entry -> entry.getKey().startsWith("R"))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // Populate the VIP seats card
        if (!vipSeatsMap.isEmpty()) {
            holder.vipCard.setVisibility(View.VISIBLE);
            holder.vipSeatsTextView.setText(formatSeatDetails(vipSeatsMap));
            holder.vipMatchName.setText(transactionsFrag.getMatchName().replace("_", "."));
            holder.vipTimeAndDate.setText(transactionsFrag.getMatchTime() + " " + transactionsFrag.getMatchDate() + " " + transactionsFrag.getMatchMonth());
            holder.vipVenueText.setText(transactionsFrag.getMatchVenue());
            holder.vipCard.setCardBackgroundColor(context.getResources().getColor(android.R.color.black));
        } else {
            holder.vipCard.setVisibility(View.GONE);
        }

        // Populate the regular seats card
        if (!regularSeatsMap.isEmpty()) {
            holder.regularCard.setVisibility(View.VISIBLE);
            holder.regularSeatsTextView.setText(formatSeatDetails(regularSeatsMap));
            holder.regMatchName.setText(transactionsFrag.getMatchName().replace("_", "."));
            holder.regTimeAndDate.setText(transactionsFrag.getMatchTime() + " " + transactionsFrag.getMatchDate() + " " + transactionsFrag.getMatchMonth());
            holder.regVenueText.setText(transactionsFrag.getMatchVenue());
            holder.regularCard.setCardBackgroundColor(context.getResources().getColor(android.R.color.holo_red_dark));
        } else {
            holder.regularCard.setVisibility(View.GONE);
        }

    }

    private String formatSeatDetails(Map<String, Map<String, Object>> seatsMap) {
        StringBuilder seatDetails = new StringBuilder();
        for (Map.Entry<String, Map<String, Object>> entry : seatsMap.entrySet()) {
            seatDetails.append("Seat: ").append(entry.getKey())
                    .append(", Price: ").append(entry.getValue().get("seatPrice"))
                    .append(", Type: ").append(entry.getValue().get("seatType")).append("\n");
        }
        return seatDetails.toString().trim();
    }

    @Override
    public int getItemCount() {
        return transactionsFrags != null ? transactionsFrags.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView regMatchName;
        TextView regTimeAndDate;
        TextView regVenueText;
        TextView vipMatchName;
        TextView vipTimeAndDate;
        TextView vipVenueText;
        TextView vipSeatsTextView;
        TextView regularSeatsTextView;
        MaterialCardView vipCard;
        MaterialCardView regularCard;

        public ViewHolder(View itemView) {
            super(itemView);

            regMatchName = itemView.findViewById(R.id.regMatchNameTextView);
            regTimeAndDate = itemView.findViewById(R.id.regTimeAndDate);
            regVenueText = itemView.findViewById(R.id.regVenueText);
            vipMatchName = itemView.findViewById(R.id.vipMatchNameTextView);
            vipTimeAndDate = itemView.findViewById(R.id.vipTimeAndDate);
            vipVenueText = itemView.findViewById(R.id.vipVenueText);
            vipSeatsTextView = itemView.findViewById(R.id.vipSeatsTextView);
            regularSeatsTextView = itemView.findViewById(R.id.regSeatsTextView);
            vipCard = itemView.findViewById(R.id.vipCard);
            regularCard = itemView.findViewById(R.id.regularCard);
            //  seatsTextView = itemView.findViewById(R.id.seatsTextView);
        }
    }

}
