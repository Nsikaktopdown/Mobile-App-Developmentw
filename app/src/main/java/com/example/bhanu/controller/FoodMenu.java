package com.example.bhanu.controller;

public class FoodMenu {
    String name;
    String image;
    double amount;

    public FoodMenu(String name, String image, double amount) {
        this.name = name;
        this.image = image;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
