package com.usupov.autopark.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CarModel {

    private String fullName, imageUrl, description;
    //@JsonProperty("id")
    private int id;
    private int brandId, modelId, yearId;
    private String brandName, modelName, yearName;
    private String vin;
    private int percent;

    public CarModel(int id, String imageUrl, String fullName, String description, int percent) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.fullName = fullName;
        this.description = description;
        this.percent = percent;
    }
    public CarModel() {}

    public void setId(int id) {
        this.id = id;
    }
    public void setFullName() {
        String fullName = this.brandName+" "+this.modelName+", "+this.yearName;
        if (this.modelName != null && this.modelName.startsWith(this.brandName)) {
            fullName = this.brandName+" "+this.modelName.substring(this.brandName.length()+1)+", "+this.yearName;
        }
        this.fullName = fullName;
        this.description = fullName;
    }
    public static String getFullName(String brandName, String modelName, String yearName) {
        if (brandName==null)
            return "";
        String fullName = brandName+" "+modelName+", "+yearName;
        if (modelName != null && modelName.startsWith(brandName)) {
            fullName = brandName+" "+modelName.substring(brandName.length()+1)+", "+yearName;
        }
        return fullName;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
    }

    public void setModelId(int modelId) {
        this.modelId = modelId;
    }

    public void setYearId(int yearId) {
        this.yearId = yearId;
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

    public void setVin(String vin) {
        this.vin = vin;
    }

    public void setPercent(int percent) {
        this.percent = percent;
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

    public int getBrandId() {
        return this.brandId;
    }

    public int getModelId() {
        return this.modelId;
    }

    public int getYearId() {
        return this.yearId;
    }

    public String getBrandName() {
        return this.brandName;
    }

    public String getModelName() {
        return this.modelName;
    }

    public String getYearName() {
        return this.yearName;
    }

    public String getVin() {
        return this.vin;
    }

    public int getPercent() {
        return this.percent;
    }
}
