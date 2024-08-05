package com.example.ticketcard.model;

import java.util.HashMap;
import java.util.Map;

public class Team {
    private String teamName;
    private String teamLogoUrl;
    private String homeStadium;

    public Team (){}

    // Constructor
    public Team(String teamName, String teamLogoUrl, String homeStadium) {
        this.teamName = teamName;
        this.teamLogoUrl = teamLogoUrl;
        this.homeStadium = homeStadium;
    }

    // Getters
    public String getTeamName() {
        return teamName;
    }

    public String getTeamLogoUrl() {
        return teamLogoUrl;
    }

    public String getHomeStadium() {
        return homeStadium;
    }

    // Converts the Team object into a Map
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("teamName", teamName);
        map.put("teamLogoUrl", teamLogoUrl);
        map.put("homeStadium", homeStadium);
        return map;
    }
}
