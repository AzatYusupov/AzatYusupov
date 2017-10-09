package com.usupov.autopark.model.location;


import java.util.List;

public class City {

    long id, regionId;
    String name;

    boolean hasSubways, hasDistricts;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getRegionId() {
        return regionId;
    }

    public void setRegionId(long regionId) {
        this.regionId = regionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isHasSubways() {
        return hasSubways;
    }

    public void setHasSubways(boolean hasSubways) {
        this.hasSubways = hasSubways;
    }

    public boolean isHasDistricts() {
        return hasDistricts;
    }

    public void setHasDistricts(boolean hasDistricts) {
        this.hasDistricts = hasDistricts;
    }

    public static String[] getCityNameAsArray(List<City> cities) {
        String[] cityNamesArray = new String[cities.size()];
        for (int i = 0; i < cityNamesArray.length; i++) {
            cityNamesArray[i] = cities.get(i).getName();
        }
        return cityNamesArray;
    }
}
