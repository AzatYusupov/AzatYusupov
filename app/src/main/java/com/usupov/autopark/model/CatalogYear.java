package com.usupov.autopark.model;


import java.util.List;

public class CatalogYear {
    private int id;
    private int modelId;
    private String name, modelName, brandName;
    public int getId() {
        return this.id;
    }
    public int getModelId() {
        return this.modelId;
    }
    public String getName() {
        return this.name;

    }
    public void setId(int id) {
        this.id = id;
    }
    public void setModelId(int modelId) {
        this.modelId = modelId;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public static String[] getNamesArray(List<CatalogYear> years) {
        String[] namesArray = new String[years.size()];
        for (int i = 0; i < namesArray.length; i++) {
            namesArray[i] = years.get(i).getName();
        }
        return namesArray;
    }
}
