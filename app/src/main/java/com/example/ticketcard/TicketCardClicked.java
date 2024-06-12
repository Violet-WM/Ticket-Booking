package com.example.ticketcard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TicketCardClicked extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_card_clicked);

        Button bookYourSeat = findViewById(R.id.bookYourSeat);
        double VIPPrice = 90.00;
        double regularPrice = 60.00;

        bookYourSeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(TicketCardClicked.this);
                View view1 = LayoutInflater.from(TicketCardClicked.this).inflate(R.layout.bottom_sheet, null);
                bottomSheetDialog.setContentView(view1);
                bottomSheetDialog.show();

                Button buttonBuyTicket = view1.findViewById(R.id.buttonBuyTicket);
                ChipGroup chipGroup = view1.findViewById(R.id.chipGroup2);
                Chip chipAll = view1.findViewById(R.id.chipAllSeats);
                Chip chipRegular = view1.findViewById(R.id.chipRegularSeats);
                Chip chipVIP = view1.findViewById(R.id.chipVIPSeats);
                TextView textViewVIPPrice = view1.findViewById(R.id.textViewVIPPrice);
                TextView textViewRegularPrice = view1.findViewById(R.id.textViewRegularPrice);
                TextView textViewTotalPrice = view1.findViewById(R.id.textViewTotalPrice);

                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.add("AA1");
                arrayList.add("AA2");
                arrayList.add("BB1");
                arrayList.add("BA2");
                arrayList.add("CC1");
                arrayList.add("CD2");
                arrayList.add("DC1");
                arrayList.add("DD2");

                Random random = new Random();

                chipAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(chipAll.isChecked()){
                            //Code for filtering based on chip selected
                            chipGroup.removeAllViews();
                            for(String s: arrayList) {
                                Chip chip = (Chip) LayoutInflater.from(TicketCardClicked.this).inflate(R.layout.standalone_chip, null);
                                chip.setText(s);
                                chip.setId(random.nextInt());
                                chipGroup.addView(chip);

                            }
                        }
                    }
                });

                chipRegular.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(chipRegular.isChecked()){
                            //Code for filtering based on chip selected
                            chipGroup.removeAllViews();
                            for(String s: arrayList) {
                                if(s.indexOf('A') != -1) {
                                    Chip chip = (Chip) LayoutInflater.from(TicketCardClicked.this).inflate(R.layout.standalone_chip, null);
                                    chip.setText(s);
                                    chip.setId(random.nextInt());
                                    chipGroup.addView(chip);
                                }
                            }
                        }
                    }
                });

                chipVIP.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(chipVIP.isChecked()){
                            //Code for filtering based on chip selected
                            chipGroup.removeAllViews();
                            for(String s: arrayList) {
                                if(s.indexOf('C') != -1) {
                                    Chip chip = (Chip) LayoutInflater.from(TicketCardClicked.this).inflate(R.layout.standalone_chip, null);
                                    chip.setText(s);
                                    chip.setId(random.nextInt());
                                    chipGroup.addView(chip);
                                }
                            }
                        }
                    }
                });

                chipGroup.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
                    @Override
                    public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                        if(checkedIds.isEmpty()){
                            Toast.makeText(TicketCardClicked.this, "No Seat selected", Toast.LENGTH_SHORT).show();
                        } else {
                            double addedRegularPrice = 0.00;
                            double addedVIPPrice = 0.00;
                            double totalPrice = 0.00;
                            for(int i : checkedIds) {
                                Chip chip = view1.findViewById(i);
                                if(chip.getText().toString().indexOf('A') != -1){
                                    Toast.makeText(TicketCardClicked.this, "Regular Seat: " + chip.getText().toString() + " selected", Toast.LENGTH_SHORT).show();
                                    addedRegularPrice = regularPrice + addedRegularPrice;
                                    textViewRegularPrice.setText("Ksh. " + addedRegularPrice);
                                    if(!chip.isChecked()){
                                        addedRegularPrice = addedRegularPrice - regularPrice;
                                        textViewRegularPrice.setText("Ksh. " + addedRegularPrice);
                                    }
                                } else if (chip.getText().toString().indexOf('C') != -1) {
                                    Toast.makeText(TicketCardClicked.this, "VIP Seat: " + chip.getText().toString() + " selected", Toast.LENGTH_SHORT).show();
                                    addedVIPPrice = VIPPrice + addedVIPPrice;
                                    textViewVIPPrice.setText("Ksh. " + addedVIPPrice);
                                    if(!chip.isChecked()){
                                        addedVIPPrice = addedVIPPrice - VIPPrice;
                                        textViewVIPPrice.setText("Ksh. " + addedVIPPrice);
                                    }
                                } else {
                                    Toast.makeText(TicketCardClicked.this, chip.getText().toString() + " selected", Toast.LENGTH_SHORT).show();
                                }
                                totalPrice = addedRegularPrice + addedVIPPrice;
                                textViewTotalPrice.setText("Total Ksh. " + totalPrice);
                            }
                        }
                    }
                });

                buttonBuyTicket.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(TicketCardClicked.this, "Dismiss button clicked", Toast.LENGTH_SHORT).show();
                        bottomSheetDialog.dismiss();
                    }
                });

            }
        });
    }
}