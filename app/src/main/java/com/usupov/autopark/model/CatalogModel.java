package com.usupov.autopark.model;

import java.util.List;

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
    public static String[] getNamesArray(List<CatalogModel> models) {
        String[] namesArray = new String[models.size()];
        for (int i = 0; i < namesArray.length; i++) {
            namesArray[i] = models.get(i).getName();
        }
        return namesArray;
    }
}
