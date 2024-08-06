package com.example.ticketcard;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SeatsAdapter extends RecyclerView.Adapter<SeatsAdapter.ViewHolder> {

    private static final String TAG = "SeatsAdapter"; // Tag for logging
    private Context context;
    private List<String> seats;
    private List<String> bookedSeats;
    private Set<String> selectedSeats = new HashSet<>();
    private PriceChangeListener priceChangeListener;
    private Map<String, Integer> seatPriceMap = new HashMap<>();
    private String matchVIP, matchRegular;

    public SeatsAdapter(Context context, List<String> seats, List<String> bookedSeats, String matchVIP, String matchRegular, PriceChangeListener priceChangeListener) {
        this.context = context;
        this.seats = seats;
        this.bookedSeats = bookedSeats;
        this.priceChangeListener = priceChangeListener;
        this.matchVIP = matchVIP;
        this.matchRegular = matchRegular;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.standalone_chip, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String seat = seats.get(position);
        String seatText = seat.length() > 0 ? seat.substring(1) : seat;
        holder.seatChip.setText(seatText);

        // Check if the seat is booked and set the chip color and checkable state accordingly
        if (bookedSeats.contains(seat)) {
            holder.seatChip.setChipBackgroundColorResource(R.color.GRAY);
            holder.seatChip.setCheckable(false);
            holder.seatChip.setOnClickListener(v -> {
                holder.seatChip.setChipStrokeColorResource(R.color.RED);
                holder.seatChip.setChipStrokeWidth(4);
                Toast.makeText(context, "Seat is booked", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(() -> {
                    holder.seatChip.setChipStrokeColorResource(R.color.GRAY);
                    holder.seatChip.setChipStrokeWidth(0);
                }, 3000); // Revert the border color after 3 seconds
            });
        } else {
            // Set the chip color based on the first letter of the original seat text
            if (seat.startsWith("R")) {
                holder.seatChip.setChipBackgroundColorResource(R.color.BLUE);
            } else if (seat.startsWith("V")) {
                holder.seatChip.setChipStrokeColorResource(R.color.GOLD);
                holder.seatChip.setChipStrokeWidth(4);
                holder.seatChip.setChipBackgroundColorResource(R.color.GOLD_dil);
            }

            // Set the checked state
            holder.seatChip.setChecked(selectedSeats.contains(seat));

            // Set up checked change listener for each seat
            holder.seatChip.setOnCheckedChangeListener((chip, isChecked) -> {
                int seatPrice = seat.startsWith("R") ? Integer.parseInt(matchRegular) : Integer.parseInt(matchVIP); // Set the price based on seat type
                Log.d(TAG, "Seat Checked Change: Seat = " + seat + ", Checked = " + isChecked + ", Price = " + seatPrice);

                if (isChecked) {
                    //holder.seatChip.setChipStrokeColorResource(R.color.GREEN);
                    selectedSeats.add(seat);
                    priceChangeListener.onPriceChange(seatPrice, true);
                } else {
//                    if (holder.seatChip.getText().toString().startsWith("R")) {
//                        holder.seatChip.setChipStrokeColorResource(R.color.BLUE);
//                    } else if (holder.seatChip.getText().toString().startsWith("V")) {
//                        holder.seatChip.setChipStrokeColorResource(R.color.GOLD);
//                    }
                    selectedSeats.remove(seat);
                    priceChangeListener.onPriceChange(seatPrice, false);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return seats.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        Chip seatChip;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            seatChip = itemView.findViewById(R.id.chip);
        }
    }

    // Add a method in SeatsAdapter to get selected seats details
    public Map<String, Object> getSelectedSeatsDetails() {
        Map<String, Object> selectedSeatDetails = new HashMap<>();
        for (String seat : selectedSeats) {
            int seatPrice = seat.startsWith("R") ? 0 : 1;
            String seatType = seat.startsWith("R") ? "Regular" : "VIP";
            selectedSeatDetails.put(seat, new SeatDetail(seat, seatType, seatPrice));
        }
        return selectedSeatDetails;
    }

    // Define a class to store seat details
    public static class SeatDetail {
        String seatName;
        String seatType;
        int seatPrice;

        public SeatDetail(String seatName, String seatType, int seatPrice) {
            this.seatName = seatName;
            this.seatType = seatType;
            this.seatPrice = seatPrice;
        }
    }

    public interface PriceChangeListener {
        void onPriceChange(int amount, boolean isAdding);
    }
}
