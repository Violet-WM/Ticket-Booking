package com.example.ticketcard.model;

import java.util.Map;

public class Fixtures {

    private String imageUrl;
    private String match;
    private String round;
    private String matchup;
    private String date;
    private String day;
    private String month;
    private String time;
    private String venue;
    private String vipPrice;
    private String regularPrice;
    private Map<String, String> teamA;
    private Map<String, String> teamB;

    public Fixtures(String imageUrl, String match, String round, String matchup, String date, String day, String month, String time, String venue, String vipPrice, String regularPrice, Map<String, String> teamA, Map<String, String> teamB) {
        this.imageUrl = imageUrl;
        this.match = match;
        this.round = round;
        this.matchup = matchup;
        this.date = date;
        this.day = day;
        this.month = month;
        this.time = time;
        this.venue = venue;
        this.vipPrice = vipPrice;
        this.regularPrice = regularPrice;
        this.teamA = teamA;
        this.teamB = teamB;
    }

    public Fixtures(String matchup, String date, String day, String month, String time, String venue, String vipPrice, String regularPrice, Map<String, String> teamA, Map<String, String> teamB) {
        this.matchup = matchup;
        this.date = date;
        this.day = day;
        this.month = month;
        this.time = time;
        this.venue = venue;
        this.vipPrice = vipPrice;
        this.regularPrice = regularPrice;
        this.teamA = teamA;
        this.teamB = teamB;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
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

    public String getMatchup() {
        return matchup;
    }

    public void setMatchup(String matchup) {
        this.matchup = matchup;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getVipPrice() {
        return vipPrice;
    }

    public void setVipPrice(String vipPrice) {
        this.vipPrice = vipPrice;
    }

    public String getRegularPrice() {
        return regularPrice;
    }

    public void setRegularPrice(String regularPrice) {
        this.regularPrice = regularPrice;
    }

    public Map<String, String> getTeamA() {
        return teamA;
    }

    public void setTeamA(Map<String, String> teamA) {
        this.teamA = teamA;
    }

    public Map<String, String> getTeamB() {
        return teamB;
    }

    public void setTeamB(Map<String, String> teamB) {
        this.teamB = teamB;
    }




}

