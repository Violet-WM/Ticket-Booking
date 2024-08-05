package com.example.ticketcard;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ticketcard.model.TransactionsFrag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TransactionsFragAdapter extends RecyclerView.Adapter<TransactionsFragAdapter.ViewHolder> {
    private List<TransactionsFrag> transactionsFrags;
    private Context context;
    OnItemClickListener onItemClickListener;

    public TransactionsFragAdapter(Context context, List<TransactionsFrag> transactionsFrags) {
        this.context = context;
        this.transactionsFrags = transactionsFrags != null ? transactionsFrags : new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.booked_tickets, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TransactionsFrag transactionsFrag = transactionsFrags.get(position);

        holder.matchName.setText(transactionsFrag.getMatchName().replace("_", "."));

        StringBuilder seatDetails = new StringBuilder();
        for (Map.Entry<String, Map<String, Object>> entry : transactionsFrag.getSeatsMap().entrySet()) {
            String seatName = entry.getKey();
            seatDetails.append(seatName)
                    .append(" - ")
                    .append(entry.getValue().get("seatType"))
                    .append(", $")
                    .append(entry.getValue().get("seatPrice"))
                    .append("\n");

            if (seatName.startsWith("V")) {
               // holder.card.setBackgroundColor(context.getResources().getColor(android.R.color.holo_red_light));
            } else if (seatName.startsWith("R")) {
               // holder.card.setBackgroundColor(context.getResources().getColor(android.R.color.holo_blue_light));
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onClick(transactionsFrag.getMatchName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return transactionsFrags != null ? transactionsFrags.size() : 0;
    }

    public void setEvents(List<TransactionsFrag> transactionsFragList) {
        transactionsFrags.clear();
        transactionsFrags.addAll(transactionsFragList);
        notifyDataSetChanged();
        Log.d("Adapter logs", "Setting events in adapter, new size: " + transactionsFrags.size());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView matchName;

        public ViewHolder(View itemView) {
            super(itemView);

            matchName = itemView.findViewById(R.id.matchName);
          //  seatsTextView = itemView.findViewById(R.id.seatsTextView);
        }
    }

    public void setOnItemClickListener(TransactionsFragAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onClick(String matchName);
    }
}
