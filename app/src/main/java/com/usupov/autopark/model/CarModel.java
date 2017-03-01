package com.usupov.autopark.model;

public class CarModel {

    private String fullName, imageUrl, description;
    private int id;

    public CarModel(int id, String imageUrl, String fullName, String description) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.fullName = fullName;
        this.description = description;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }
    public String getFullName() {
        return fullName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDescription() {
        return description;
    }


}
