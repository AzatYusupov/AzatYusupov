package com.usupov.autopark.model.location;


import java.util.List;

public class Region{

    long id;
    String name;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static String[] getRegionNamesAsArray(List<Region> regions) {
        String[] regionNamesArray = new String[regions.size()];
        for (int i = 0; i < regionNamesArray.length; i++) {
            regionNamesArray[i] = regions.get(i).getName();
        }
        return regionNamesArray;
    }
}
