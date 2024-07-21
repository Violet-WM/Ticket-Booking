package com.example.ticketcard.model;

public class Players {
    private String name;
    private String role;
    private String age;

    // No argument constructor needed for Firebase
    public Players() {
    }

    public Players(String name, String role, String age) {
        this.name = name;
        this.role = role;
        this.age = age;
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

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}
