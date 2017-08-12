package com.usupov.autopark.model;



public class AlbumModel {
    private String imageUri;
    boolean selected;
    int number;

    public AlbumModel(String imageUri) {
        selected = false;
        this.imageUri = imageUri;
    }
    public boolean isSelected() {
        return selected;
    }
    public void click() {
        selected = !selected;
    }
    public String getImageUri() {
        return this.imageUri;
    }
    public void setNumber(int number) {
        this.number = number;
    }
    public int getNumber() {
        return this.number;
    }
    public void decNumber() {
        number--;
    }
}
