package com.usupov.autopark.model;


public class StatusModel {
    String number;
    int image;
    String title, description;

    public StatusModel(String number, int image, String title, String description) {
        this.number = number;
        this.image = image;
        this.title = title;
        this.description = description;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
