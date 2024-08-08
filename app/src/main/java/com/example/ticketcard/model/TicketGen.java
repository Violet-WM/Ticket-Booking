package com.example.ticketcard.model;

import java.util.Map;

public class TicketGen {
    public TicketGen(Map<String, Map<String, Object>> seatsMap, String matchDate, String matchMonth, String matchTime, String round, String matchVenue, String matchName, String ticketID) {
        this.seatsMap = seatsMap;
        this.matchDate = matchDate;
        this.matchMonth = matchMonth;
        this.matchTime = matchTime;
        this.round = round;
        this.matchVenue = matchVenue;
        this.matchName = matchName;
        this.ticketID = ticketID;
    }

    private Map<String, Map<String, Object>> seatsMap;
    private String matchDate;
    private String matchMonth;
    private String matchTime;
    private String round;
    private String matchVenue;
    private String matchName;
    private String ticketID;


    public TicketGen(Map<String, Map<String, Object>> seatsMap, String matchDate, String matchMonth, String matchTime, String round, String matchVenue, String matchName) {
        this.seatsMap = seatsMap;
        this.matchDate = matchDate;
        this.matchMonth = matchMonth;
        this.matchTime = matchTime;
        this.round = round;
        this.matchVenue = matchVenue;
        this.matchName = matchName;
    }

    public String getTicketID() {
        return ticketID;
    }

    public void setTicketID(String ticketID) {
        this.ticketID = ticketID;
    }



    public Map<String, Map<String, Object>> getSeatsMap() {
        return seatsMap;
    }

    public void setSeatsMap(Map<String, Map<String, Object>> seatsMap) {
        this.seatsMap = seatsMap;
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

    public String getMatchTime() {
        return matchTime;
    }

    public void setMatchTime(String matchTime) {
        this.matchTime = matchTime;
    }

    public String getRound() {
        return round;
    }

    public void setRound(String round) {
        this.round = round;
    }

    public String getMatchVenue() {
        return matchVenue;
    }

    public void setMatchVenue(String matchVenue) {
        this.matchVenue = matchVenue;
    }

    public String getMatchName() {
        return matchName;
    }

    public void setMatchName(String matchName) {
        this.matchName = matchName;
    }



    public TicketGen() {
        // Default constructor required for calls to DataSnapshot.getValue(TicketGen.class)
    }


}
