package com.example.ticketcard.model;

public class Event {
    private String title;
    private String description; // Renamed from 'desc' to 'description' to match the variable name used in the fragment
    private String imageUrl;

    // No-argument constructor required for Firebase
    public Event() {
    }

    // Constructor
    public Event(String title, String description, String imageUrl) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    // Getter for title
    public String getTitle() {
        return title;
    }

    // Setter for title
    public void setTitle(String title) {
        this.title = title;
    }

    // Getter for description
    public String getDescription() {
        return description;
    }

    // Setter for description
    public void setDescription(String description) {
        this.description = description;
    }

    // Getter for imageUrl
    public String getImageUrl() {
        return imageUrl;
    }

    // Setter for imageUrl
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
