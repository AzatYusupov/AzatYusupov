package com.usupov.autopark.model;

public class PartModel {
    protected long id;
    protected long categoryId;
    protected String article;
    protected String title;
    protected long carId;
    protected String brand, status, store, comment;
    public long getId() {
        return this.id;
    }
    public String getArticle() {
        return this.article;
    }
    public long getCategoryId() {
        return this.categoryId;
    }
    public void setId(long id) {
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
    public void setCarId(long carId) {
        this.carId = carId;
    }
    public long getCarId() {
        return this.carId;
    }
    public PartModel(long id, long categoryId, String article) {
        this.id = id;
        this.categoryId = categoryId;
        this.article = article;
    }
    public String getBrand() {
        return brand;
    }
    public String getStatus() {
        return status;
    }
    public String getStore() {
        return store;
    }
    public String getComment() {
        return comment;
    }
    public void setBrand(String brand) {
        this.brand = brand;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public void setStore(String store) {
        this.store = store;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public PartModel(){}
}
