package com.usupov.autopark.model;

public class PartModel extends CategoryPartModel {

    protected long categoryId, partId;
    protected String article, note;
    String categoryName, parentCatName;

    protected long carId;
    protected String brand, status, store, comment;
    long price;
    long ycpId;
    int cntImages;
    long lastUpdateTime;

    public String getArticle() {
        return this.article;
    }
    public long getCategoryId() {
        return this.categoryId;
    }

    public void setArticle(String article) {
        this.article = article;
    }
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
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

    public String getNote() {
        return this.note;
    }
    public String getCategoryName() {
        return this.categoryName;
    }
    public String getParentCatName() {
        return this.parentCatName;
    }
    public void setNote(String note) {
        this.note = note;
    }
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    public void setParentCatName(String parentCatName) {
        this.parentCatName = parentCatName;
    }

    public long getPartId() {
        return partId;
    }

    public void setPartId(long partId) {
        this.partId = partId;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public long getYcpId() {
        return ycpId;
    }

    public void setYcpId(long ycpId) {
        this.ycpId = ycpId;
    }

    public int getCntImages() {
        return cntImages;
    }

    public void setCntImages(int cntImages) {
        this.cntImages = cntImages;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}
