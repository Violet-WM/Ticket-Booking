package com.example.ticketcard.model;

public class TicketEvent {
    String imageUrl;
    String matchDetails;
    String teamA;
    String teamB;
    String teamALogo;
    String teamBLogo;
    String matchTime;
    String matchDate;
    String matchMonth;
    String matchVenue;
    String matchRegular;
    String matchVIP;
    String roundAdapter;

    public TicketEvent(String imageUrl, String matchDetails, String teamA, String teamB, String teamALogo, String teamBLogo, String matchTime, String matchDate, String matchMonth, String matchVenue, String matchRegular, String matchVIP, String roundAdapter){
        this.imageUrl = imageUrl;
        this.matchDetails = matchDetails;
        this.teamA = teamA;
        this.teamB = teamB;
        this.teamALogo = teamALogo;
        this.teamBLogo = teamBLogo;
        this.matchTime = matchTime;
        this.matchDate = matchDate;
        this.matchMonth = matchMonth;
        this.matchVenue = matchVenue;
        this.matchRegular = matchRegular;
        this.matchVIP = matchVIP;
        this.roundAdapter = roundAdapter;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getMatchDetails() {
        return matchDetails;
    }

    public void setMatchDetails(String matchDetails) {
        this.matchDetails = matchDetails;
    }

    public String getTeamA() {
        return teamA;
    }

    public void setTeamA(String teamA) {
        this.teamA = teamA;
    }

    public String getTeamB() {
        return teamB;
    }

    public void setTeamB(String teamB) {
        this.teamB = teamB;
    }

    public String getTeamALogo() { return teamALogo; }

    public void setTeamALogo(String teamALogo) {
        this.teamALogo = teamALogo;
    }

    public String getTeamBLogo() {
        return teamBLogo;
    }

    public void setTeamBLogo(String teamBLogo) {
        this.teamBLogo = teamBLogo;
    }

    public String getMatchTime() {
        return matchTime;
    }

    public void setMatchTime(String matchTime) {
        this.matchTime = matchTime;
    }

    public String getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(String matchDate) {
        this.matchDate = matchDate;
    }

    public String getMatchMonth() {
        return matchMonth;
    }

    public void setMatchMonth(String matchMonth) {
        this.matchMonth = matchMonth;
    }

    public String getMatchVenue() {
        return matchVenue;
    }

    public void setMatchVenue(String matchVenue) {
        this.matchVenue = matchVenue;
    }

    public String getMatchRegular() {
        return matchRegular;
    }

    public void setMatchRegular(String matchRegular) {
        this.matchRegular = matchRegular;
    }

    public String getMatchVIP() {
        return matchVIP;
    }

    public void setMatchVIP(String matchVIP) {
        this.matchVIP = matchVIP;
    }

    public String getRoundAdapter() {
        return roundAdapter;
    }

    public void setRoundAdapter(String roundAdapter) {
        this.roundAdapter = roundAdapter;
    }

    @Override
    public String toString() {
        return "TicketEvent{" +
                "imageUrl='" + imageUrl + '\'' +
                ", matchDetails='" + matchDetails + '\'' +
                ", teamA='" + teamA + '\'' +
                ", teamB='" + teamB + '\'' +
                ", teamALogo='" + teamALogo + '\'' +
                ", teamBLogo='" + teamBLogo + '\'' +
                ", matchTime='" + matchTime + '\'' +
                ", matchDate='" + matchDate + '\'' +
                ", matchMonth='" + matchMonth + '\'' +
                ", matchVenue='" + matchVenue + '\'' +
                ", matchRegular='" + matchRegular + '\'' +
                ", matchVIP='" + matchVIP + '\'' +
                ", roundAdapter='" + roundAdapter + '\'' +
                '}';
    }
}
