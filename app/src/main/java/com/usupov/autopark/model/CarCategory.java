package com.usupov.autopark.model;

import java.util.ArrayList;

/**
 * Created by Azat on 14.02.2017.
 */

public class CarCategory {
    private String categoryName;
    private ArrayList<CarCategory>chilren;
    private boolean isFirstClick;
    public CarCategory(String categoryName) {
        this.categoryName = categoryName;
        this.chilren = new ArrayList<>();
    }
    public String getCategoryName() {
        return categoryName;
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
    public boolean hasChildren() {
        return chilren.size() > 0;
    }
    public ArrayList<CarCategory> getChildren() {
        return chilren;
    }
}
