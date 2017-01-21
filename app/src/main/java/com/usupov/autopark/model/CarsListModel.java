package com.usupov.autopark.model;

public class CarsListModel {

    private String fullName, imageUrl, description;

    public CarsListModel(String imageUrl, String fullName, String description) {
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
