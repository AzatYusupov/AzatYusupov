package com.usupov.autopark.config;

public class PartRestURIConstants {


    public static final String CREATE = ApiURIConstants.API + "/car/%d/category/%d/create";
    public static final String SEARCH = ApiURIConstants.API + "/car/%d/part/search?search=%s";
    public static final String ADDIMAGE = ApiURIConstants.API + "/user_part/%d/add_image";
    public static final String GET_ALL = ApiURIConstants.API + "/user_part";
    public static final String GET_BY_CAR = ApiURIConstants.API + "/user_part/%d";

}
