package com.example.ticketcard.model;

public class Match {
    private String teamA;
    private String teamALogoUrl;
    private String teamB;
    private String teamBLogoUrl;
    private String venue;
    private String dayOfWeek;
    private int day;
    private String month;
    private String time;
    private int regularPrice;
    private int vipPrice;

    public Match(){}

    public Match(String teamA, String teamALogoUrl, String teamB, String teamBLogoUrl, String venue, String dayOfWeek, int day, String month, String time, int regularPrice, int vipPrice) {
        this.teamA = teamA;
        this.teamALogoUrl = teamALogoUrl;
        this.teamB = teamB;
        this.teamBLogoUrl = teamBLogoUrl;
        this.venue = venue;
        this.dayOfWeek = dayOfWeek;
        this.day = day;
        this.month = month;
        this.time = time;
        this.regularPrice = regularPrice;
        this.vipPrice = vipPrice;
    }

    public String getTeamA() {
        return teamA;
    }

    public String getTeamALogoUrl() {
        return teamALogoUrl;
    }

    public String getTeamB() {
        return teamB;
    }

    public String getTeamBLogoUrl() {
        return teamBLogoUrl;
    }

    public String getVenue() {
        return venue;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public int getDay() {
        return day;
    }

    public String getMonth() {
        return month;
    }

    public String getTime() {
        return time;
    }

    public int getRegularPrice() {
        return regularPrice;
    }

    public int getVipPrice() {
        return vipPrice;
    }
}
