package com.usupov.autopark.config;


public class LocationURIConstants {

    public static final String GET_REGIONS = ApiURIConstants.API + "/location/regions";
    public static final String GET_REGION_BY_ID = ApiURIConstants.API + "/location/region/%d";

    public static final String GET_CITIES = ApiURIConstants.API + "/location/region/%d/cities";
    public static final String GET_CITY_BY_ID = ApiURIConstants.API + "/location/region/%d/city/%d";

    public static final String GET_DISTRICTS = ApiURIConstants.API + "/location/city/%d/districts";
    public static final String GET_DISTRICT_BY_ID = ApiURIConstants.API + "/location/city/%d/district/%d";

    public static final String GET_SUBWAYS = ApiURIConstants.API + "/location/city/%d/subways";
    public static final String GET_SUBWAY_BY_ID = ApiURIConstants.API + "/location/city/%d/subway/%d";

    public static final String GET_ROADS = ApiURIConstants.API + "/location/city/%d/roads";
    public static final String GET_ROAD_BY_ID = ApiURIConstants.API + "/location/city/%d/road/%d";
}
