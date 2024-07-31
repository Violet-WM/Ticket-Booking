package com.example.ticketcard.model;

public class Team {
    private String teamName;
    private String teamLogoUrl;
    private String homeStadium;

    public Team(String teamName, String teamLogoUrl, String homeStadium) {
        this.teamName = teamName;
        this.teamLogoUrl = teamLogoUrl;
        this.homeStadium = homeStadium;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getTeamLogoUrl() {
        return teamLogoUrl;
    }

    public String getHomeStadium() {
        return homeStadium;
    }
}
