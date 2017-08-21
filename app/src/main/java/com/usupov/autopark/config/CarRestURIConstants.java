package com.usupov.autopark.config;


public class CarRestURIConstants {

    public static final String GET_ALL        = ApiURIConstants.API + "/cars";
    public static final String GET_BY_ID      = ApiURIConstants.API + "/car/%d";
    public static final String GET_BY_VIN     = ApiURIConstants.API + "/car/vin/%s";
    public static final String GET_BY_CATALOG = ApiURIConstants.API + "/car/brand/%d/model/%d/year/%d";
    public static final String CREATE         = ApiURIConstants.API + "/car/create";
    public static final String DELETE         = ApiURIConstants.API + "/car/delete/%d";
    public static final String UPDATE         = ApiURIConstants.API + "/car/update/%d";

}
