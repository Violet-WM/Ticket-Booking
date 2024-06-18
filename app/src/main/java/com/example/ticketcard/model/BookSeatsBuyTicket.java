package com.example.ticketcard.model;

public class BookSeatsBuyTicket {

    private boolean reserved;
    private String userEmail;
    private String firstName;
    private String lastName;
    private int phoneNumber;
    private int remainingSeats;

    public BookSeatsBuyTicket(int remainingSeats) {
        this.remainingSeats = remainingSeats;
    }

    public BookSeatsBuyTicket(boolean reserved, String userEmail, String firstName, String lastName, int phoneNumber) {
        this.reserved = reserved;
        this.userEmail = userEmail;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }

    public boolean isReserved() {
        return reserved;
    }

    public void setReserved(boolean reserved) {
        this.reserved = reserved;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getRemainingSeats() {
        return remainingSeats;
    }

    public void setRemainingSeats(int remainingSeats) {
        this.remainingSeats = remainingSeats;
    }

    public BookSeatsBuyTicket() {
    }
}
