package com.usupov.autopark.model;

public class PartModel {
    private int id;
    private int categoryId;
    private String article;
    private String title;
    int carId;
    public int getId() {
        return this.id;
    }
    public String getArticle() {
        return this.article;
    }
    public int getCategoryId() {
        return this.categoryId;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setArticle(String article) {
        this.article = article;
    }
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setCarId(int carId) {
        this.carId = carId;
    }
    public int getCarId() {
        return this.carId;
    }
    public PartModel(int id, int categoryId, String article) {
        this.id = id;
        this.categoryId = categoryId;
        this.article = article;
    }
    public PartModel(){}
}
