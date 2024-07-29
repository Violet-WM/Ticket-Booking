package com.example.ticketcard.model;

public class Seats {
    private String seatNames;

    public Seats(){
    }

    public Seats(String seatNames) {
        this.seatNames = seatNames;
    }

    public String getSeatNames() {
        return seatNames;
    }

    public void setSeatNames(String seatNames) {
        this.seatNames = seatNames;
    }
}
