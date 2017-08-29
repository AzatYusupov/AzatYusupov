package com.usupov.autopark.config;

public class PartRestURIConstants {


    public static final String CREATE = ApiURIConstants.API + "/car/%d/category/%d/create";
    public static final String SEARCH = ApiURIConstants.API + "/car/%d/part/search?search=%s";
    public static final String ADDIMAGE = ApiURIConstants.API + "/user_part/add_image";
    public static final String GET_USER_PARTS = ApiURIConstants.API + "/user_part";
    public static final String GET_BY_CAR = ApiURIConstants.API + "/user_part/%d";
    public static final String GET_ALL = ApiURIConstants.API + "/car/%d/category/%d/list";
    public static final String GET_APPLICABILITY = ApiURIConstants.API  + "/user_part/app/%d";


}
