package com.usupov.autopark.model;

import java.util.List;

public class CatalogBrand {
    private int id;
    private String name;
    private String code;
    private String imageUrl;
    public int getId() {
        return this.id;
    }
    public String getName() {
        return this.name;
    }
    public String getImageUrl() {
        return this.imageUrl;
    }
    public String getCode() {
        return this.code;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public CatalogBrand(int id, String name, String imageUrl) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
    }
    public CatalogBrand(){}
    public static String[] getNamesArray(List<CatalogBrand> brands) {
        String[] namesArray = new String[brands.size()];
        for (int i = 0; i < namesArray.length; i++) {
            namesArray[i] = brands.get(i).getName();
        }
        return namesArray;
    }
}
