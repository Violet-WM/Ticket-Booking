package com.example.ticketcard.model;

public class StadiumViews {

    private String sideName;
    private String stadiumImage;

    public StadiumViews(){}

    public StadiumViews(String sideName, String stadiumImage) {
        this.sideName = sideName;
        this.stadiumImage = stadiumImage;
    }


    public String getSideName() {
        return sideName;
    }

    public void setSideName(String sideName) {
        this.sideName = sideName;
    }

    public String getStadiumImage() {
        return stadiumImage;
    }

    public void setStadiumImage(String stadiumImage) {
        this.stadiumImage = stadiumImage;
    }
}
