package com.usupov.autopark.model;

import java.util.ArrayList;

/**
 * Created by Azat on 14.02.2017.
 */

public class CarCategories {
    private String categoryName;
    private ArrayList<CarCategories>chilrens;
    public CarCategories(String categoryName) {
        this.categoryName = categoryName;
        this.chilrens = new ArrayList<>();
    }
    public String getCategoryName() {
        return categoryName;
    }
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    public boolean hasChildrens() {
        return chilrens.size() > 0;
    }
}
