package com.example.ticketcard.model;

public class Stats {
    private String matches;
    private String wins;
    private String draws;
    private String loss;

    public Stats(){
    }

    public Stats(String matches, String wins, String draws, String loss) {
        this.matches = matches;
        this.wins = wins;
        this.draws = draws;
        this.loss = loss;
    }


    public String getMatches() {
        return matches;
    }

    public void setMatches(String matches) {
        this.matches = matches;
    }

    public String getWins() {
        return wins;
    }

    public void setWins(String wins) {
        this.wins = wins;
    }

    public String getDraws() {
        return draws;
    }

    public void setDraws(String draws) {
        this.draws = draws;
    }

    public String getLoss() {
        return loss;
    }

    public void setLoss(String loss) {
        this.loss = loss;
    }
}
