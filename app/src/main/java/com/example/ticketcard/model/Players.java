package com.example.ticketcard.model;

public class Players {
    private String name;
    private String role;
    private String no;

    // No argument constructor needed for Firebase
    public Players() {
    }

    public Players(String name, String role, String no) {
        this.name = name;
        this.role = role;
        this.no = no;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String age) {
        this.no = no;
    }
}
