package com.usupov.autopark.model;

/**
 * Created by Azat on 07.04.2017.
 */

public class CarBrand {
    private int id;
    private String name;
    private String imageUrl;
    public int getId() {
        return this.id;
    }
    public String getName() {
        return this.name;
    }
    public String getImageUrl() {
        return this.imageUrl;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public CarBrand(int id, String name, String imageUrl) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
    }
}
