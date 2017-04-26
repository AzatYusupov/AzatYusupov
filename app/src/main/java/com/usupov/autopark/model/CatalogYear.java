package com.usupov.autopark.model;

/**
 * Created by Azat on 22.04.2017.
 */

public class CatalogYear {
    private int id;
    private int modelId;
    private String name;
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
}
