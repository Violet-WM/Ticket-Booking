package com.example.ticketcard.model;

import java.util.List;

public class Teams {
    String teamName;
    String teamLogoUrl;
    String teamLeague;
    String home;
    private List<String> playerNames;

    public Teams() {}

    public Teams(String teamLogoUrl, String teamName, String teamLeague, String home, List<String> playerNames) {
        this.teamLogoUrl = teamLogoUrl;
        this.teamName = teamName;
        this.teamLeague = teamLeague;
        this.home = home;
        this.playerNames = playerNames;
    }

    public String getTeamLeague() {
        return teamLeague;
    }

    public String getTeamLogoUrl() {
        return teamLogoUrl;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getHome() { return home; }

    public void setTeamLeague(String teamLeague) {
        this.teamLeague = teamLeague;
    }

    public void setTeamLogoUrl(String teamLogoUrl) {
        this.teamLogoUrl = teamLogoUrl;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public void setHome(String home) { this.home = home; }

    public List<String> getPlayerNames() {
        return playerNames;
    }

    public void setPlayerNames(List<String> playerNames) {
        this.playerNames = playerNames;
    }
}
