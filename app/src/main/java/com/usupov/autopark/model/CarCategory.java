package com.usupov.autopark.model;

import java.util.ArrayList;

/**
 * Created by Azat on 14.02.2017.
 */

public class CarCategory {
    private String categoryName;
    int categoryId;
    int percent;
    private ArrayList<CarCategory>chilren;
    private boolean isFirstClick;
    public CarCategory(String categoryName, int categoryId, int percent) {
        this.categoryName = categoryName;
        this.categoryId = categoryId;
        this.percent = percent;
        this.chilren = new ArrayList<>();
    }
    public String getCategoryName() {
        return categoryName;
    }
    public int getCategoryId() {
        return categoryId;
    }
    public boolean isFirstClick() {
        return  this.isFirstClick;
    }
    public void click() {
        isFirstClick = !isFirstClick;
    }
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
        this.isFirstClick = false;
    }
    public int getPercent() {
        return this.percent;
    }
    public boolean hasChildren() {
        return chilren.size() > 0;
    }
    public ArrayList<CarCategory> getChildren() {
        return chilren;
    }
}
