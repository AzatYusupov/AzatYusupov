package com.usupov.autopark.model;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Azat on 14.02.2017.
 */

public class CarCategory {
    private boolean isRealised;
    private View view;
    private LinearLayout linearLayout;
    private String categoryName;
    int categoryId;
    int percent;
    private ArrayList<CarCategory>chilren;
    private boolean isFirstClick;
    private ImageView openandcloseimg;
    public TextView textname;
    public CarCategory(String categoryName, int categoryId, int percent) {
        this.categoryName = categoryName;
        this.categoryId = categoryId;
        this.percent = percent;
        this.chilren = new ArrayList<>();
        this.isRealised = false;
    }
    public void setFirstClick(boolean firstClick) {
        this.isFirstClick = firstClick;
    }
    public void setTextView(TextView textname) {
        this.textname = textname;
    }
    public TextView getTextView() {
        return this.textname;
    }
    public ImageView getOpenandcloseimg() {
        return this.openandcloseimg;
    }
    public void setOpenandcloseimg (ImageView openandcloseimg) {
        this.openandcloseimg = openandcloseimg;
    }
    public boolean isRealised() {
        return isRealised;
    }
    public void setRealised() {
        this.isRealised = true;
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
