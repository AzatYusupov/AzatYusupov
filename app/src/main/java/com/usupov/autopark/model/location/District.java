package com.usupov.autopark.model.location;


import java.util.List;

public class District {

    long id, cityId;
    String name;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCityId() {
        return cityId;
    }

    public void setCityId(long cityId) {
        this.cityId = cityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static String[] districtNameAsArray(List<District> districts) {
        String[] districtNamesAray = new String[districts.size()];
        for (int i = 0; i < districtNamesAray.length; i++) {
            districtNamesAray[i] = districts.get(i).getName();
        }
        return districtNamesAray;
    }
}
