package com.example.ticketcard.model;

public class Fixture {
    private String matchup;
    private String time;
    private String date;
    private String round;
    private String venue;

    public Fixture(String matchup, String time, String date, String round, String venue) {
        this.matchup = matchup;
        this.time = time;
        this.date = date;
        this.round = round;
        this.venue = venue;
    }

    public String getMatchup() {
        return matchup;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public String getRound() {
        return round;
    }

    public String getVenue() {
        return venue;
    }
}
