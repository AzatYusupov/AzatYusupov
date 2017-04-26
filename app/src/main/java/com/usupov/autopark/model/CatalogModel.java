package com.usupov.autopark.model;

/**
 * Created by Azat on 21.04.2017.
 */

public class CatalogModel {
    private int id;
    private String name;
    public int getId() {
        return this.id;
    }
    public String getName() {
        return this.name;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public CatalogModel(){}
}
