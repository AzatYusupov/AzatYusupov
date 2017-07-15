package com.usupov.autopark.http;


public class Config {

    private final static String urlCarDelete = "car/delete/";
    private final static String pathCar = "car/";
    private final static String urlVin = "car/vin/";
    private final static String urlCars = "cars/";

    private final static String ipServer = "http://88.99.174.4:8080";
    public final static String api = ipServer + "/api/";
    public final static String apiOpen = ipServer + "/api/open/";

    private final static String apiCarCreat = "car/create";
    private final static String pathCategory = "category";
    private final static String pathModel = "model";
    private final static String pathBrands = "brand";
    private final static String pathYears = "year";
    private final static String pathUserPart = "user_part";
    private final static String pathSignup = "signup";
    private final static String pathSignIn = "sign_in";

    public static String getUrlCarDelete() {
        return api +urlCarDelete;
    }

    public static String getUrlVin() {
        return apiOpen +urlVin;
    }

    public static String getUrlCarCreat() {
        return api + apiCarCreat;
    }

    public static String getUrlCars() {
        return api + urlCars;
    }

    public static String getpathCategory() {
        return pathCategory;
    }

    public static String getUrlCar() {
        return api + pathCar;
    }

    public static String getUrlBrands() {
        return apiOpen + pathBrands;
    }

    public static String getPathModel() {
        return pathModel;
    }

    public static String getPathYears() {
        return pathYears;
    }
    public static String getUrlGetByCatalog(int brandId, int modelId, int yearId) {
        return getUrlCar()+"brand/"+brandId+"/model/"+modelId+"/year/"+yearId;
    }
    public static String getUrlUserPart() {
        return urlServer + pathUserPart;
    }

    public static String getUrlSignup() {
        return urlServer + pathSignup;
    }
    public static String getUrlSignIn() {
        return urlServer + pathSignIn;
    }
    public static String TOKEN = "restApiToken";
    public static String APP_NAME = "productCard";
}