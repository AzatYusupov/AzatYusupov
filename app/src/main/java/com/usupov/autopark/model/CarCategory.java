package com.usupov.autopark.model;

import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class CarCategory {
    private boolean isRealised;
    private View view;
    private LinearLayout linearLayout;
    private String name;
    int id;
    int percent;
    private ArrayList<CarCategory>chilren;
    private boolean isFirstClick;
    int cntClicks;
    public CarCategory(String name, int id, int percent) {
        this.name = name;
        this.id = id;
        this.percent = percent;
        this.chilren = new ArrayList<>();
        this.isRealised = false;
        this.cntClicks = 0;
    }
    public boolean isRealised() {
        return isRealised;
    }
    public void setRealised() {
        this.isRealised = true;
    }
    public String getName() {
        return name;
    }
    public int getId() {
        return id;
    }
    public boolean isFirstClick() {
        return  this.isFirstClick;
    }
    public void click() {
        isFirstClick = !isFirstClick;
        cntClicks++;
    }
    public void setName(String name) {
        this.name = name;
        this.isFirstClick = false;
    }
    public int getCntClicks() {
        return cntClicks;
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
    public View getView() {
        return this.view;
    }
    public void setView(View view) {
        this.view = view;
    }
    public LinearLayout getLinearLayout() {
        return this.linearLayout;
    }
    public void setLinearLayout(LinearLayout linearLayout) {
        this.linearLayout = linearLayout;
    }
}
