package com.example.ticketcard.model;

import android.net.Uri;

public class Event {
    public String match;
    public String round;
    public String imageUrl;
    public String teamA;
    public String teamB;
    public String teamALogo;
    public String teamBLogo;
    public String matchTime;
    public String matchDate;
    public String matchMonth;
    public String matchVenue;
    public String matchRegular;
    public String matchVIP;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;
    public transient Uri imageUri; // Transient because we don't want to serialize this field in Firebase

    public Event() {
    }

    public Event(String imageUrl, String match, String round, String teamA, String teamB, String teamALogo, String teamBLogo, String matchTime, String matchDate, String matchMonth, String matchVenue, String matchRegular, String matchVIP){
        this.imageUrl = imageUrl;
        this.match = match;
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
        this.round = round;
    }

    public Event(String match, String round, Uri imageUri) {
        this.match = match;
        this.round = round;
        this.imageUri = imageUri;
    }

    public Event(String match, String round, String imageUrl) {
        this.match = match;
        this.round = round;
        this.imageUrl = imageUrl;
    }

    public String getMatch() {
        return match;
    }

    public void setMatch(String match) {
        this.match = match;
    }

    public String getRound() {
        return round;
    }

    public void setRound(String round) {
        this.round = round;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    public String getTeamALogo() {
        return teamALogo;
    }

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

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

}
