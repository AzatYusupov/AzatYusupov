package com.usupov.autopark.config;


public class CarRestURIConstants {

    public static final String GET_BY_ID      = "/car/%d";
    public static final String GET_BY_VIN     = "/car/vin/%s";
    public static final String GET_BY_CATALOG = "/car/brand/%d/model/%d/year/%d";
    public static final String CREATE         = "/car/create";
    public static final String DELETE         = "/car/delete/%d";

}
