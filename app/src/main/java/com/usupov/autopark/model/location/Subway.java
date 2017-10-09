package com.usupov.autopark.model.location;


import java.util.List;

public class Subway {
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

    public static String[]getSubwayNamesArray(List<Subway> subways) {
        String[]subwayNamesArray = new String[subways.size()];
        for (int i = 0; i < subwayNamesArray.length; i++) {
            subwayNamesArray[i] = subways.get(i).getName();
        }
        return subwayNamesArray;
    }
}
