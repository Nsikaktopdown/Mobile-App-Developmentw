package com.model;

import com.google.gson.annotations.SerializedName;

public class Restaurant {

    @SerializedName("icon")
    String icon;
    @SerializedName("place_id")
    String id;
    @SerializedName("name")
    String name;
    @SerializedName("vicinity")
    String vicinity;
    @SerializedName("rating")
    float rating;

    public Restaurant(String icon, String id, String name, String vicinity, float rating) {
        this.icon = icon;
        this.id = id;
        this.name = name;
        this.vicinity = vicinity;
        this.rating = rating;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
