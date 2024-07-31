package com.example.ticketcard.model;

public class Fixture {
    private String matchup;
    private String matchDetails;

    public Fixture(String matchup, String matchDetails) {
        this.matchup = matchup;
        this.matchDetails = matchDetails;
    }

    public String getMatchup() {
        return matchup;
    }

    public String getMatchDetails() {
        return matchDetails;
    }
}
