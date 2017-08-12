package com.usupov.autopark.model;


public class UserPartModel extends PartModel implements Comparable<UserPartModel>{
    private String partName, brandName, modelName, yearName, imageUrl;
    public String getPartName() {
        return partName;
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

    @Override
    public int compareTo(UserPartModel other) {
        if (this.carId > other.carId)
            return -1;
        if (this.carId < other.carId)
            return +1;
        return 0;
    }
}
