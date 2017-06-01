package com.usupov.autopark.model;

public class CatalogBrand {
    private int id;
    private String name;
    private String code;
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
    public String getCode() {
        return this.code;
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
    public void setCode(String code) {
        this.code = code;
    }
    public CatalogBrand(int id, String name, String imageUrl) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
    }
    public CatalogBrand(){}
}
