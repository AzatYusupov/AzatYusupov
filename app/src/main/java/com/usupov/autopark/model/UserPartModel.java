package com.usupov.autopark.model;

/**
 * Created by Azat on 05.07.2017.
 */

public class UserPartModel extends PartModel {
    private String partName, categoryName, brandName, modelName, yearName, imageUrl;
    public String getPartName() {
        return partName;
    }
    public String getCategoryName() {
        return categoryName;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public String getBrandName(){
        return brandName;
    }
    public String getModelName() {
        return modelName;
    }
    public String getYearName() {
        return yearName;
    }
    public void setPartName(String partName) {
        this.partName = partName;
    }
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }
    public void setModelName(String modelName) {
        this.modelName = modelName;
    }
    public void setYearName(String yearName) {
        this.yearName = yearName;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
