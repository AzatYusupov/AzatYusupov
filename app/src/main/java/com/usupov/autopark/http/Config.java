package com.usupov.autopark.http;


public class Config {

    private final static String urlCarDelete = "car/delete/";
    private final static String pathCar = "car/";
    private final static String urlVin = "car/vin/";
    private final static String urlCars = "cars/";
    public final static String urlServer = "http://88.99.174.4:8080/api/";
//    public final static String urlServer = "http://192.168.1.4:8080/api/";
//    public final static String urlServer = "http://192.168.1.102:8080/api/";
    private final static String apiCarCreat = "car/create";
    private final static String pathCategory = "category";
    private final static String pathModel = "model";
    private final static String pathBrands = "brand";
    private final static String pathYears = "year";
    private final static String pathUserPart = "user_part";
    private final static String pathSignup = "signup";

    public static String getUrlCarDelete() {
        return urlServer +urlCarDelete;
    }

    public static String getUrlVin() {
        return urlServer +urlVin;
    }

    public static String getUrlCarCreat() {
        return urlServer + apiCarCreat;
    }

    public static String getUrlCars() {
        return urlServer + urlCars;
    }

    public static String getpathCategory() {
        return pathCategory;
    }

    public static String getUrlCar() {
        return urlServer + pathCar;
    }

    public static String getUrlBrands() {
        return urlServer + pathBrands;
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
}