package com.example.ticketcard.model;

public class TicketEvent {
    private String imageUrl;
    private String imgDescription;
    private String imageTitle;

    public TicketEvent(){
    }

    public TicketEvent(String imageUrl, String imgDescription, String imageTitle) {
        this.imageUrl = imageUrl;
        this.imgDescription = imgDescription;
        this.imageTitle = imageTitle;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImgDescription() {
        return imgDescription;
    }

    public void setImgDescription(String imgDescription) {
        this.imgDescription = imgDescription;
    }

    public String getImageTitle() {
        return imageTitle;
    }

    public void setImageTitle(String imageTitle) {
        this.imageTitle = imageTitle;
    }
}
