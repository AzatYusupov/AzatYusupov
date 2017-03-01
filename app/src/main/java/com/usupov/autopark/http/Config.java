package com.usupov.autopark.http;

/**
 * Created by Azat on 26.02.2017.
 */

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

/**
 * Created by Azat on 26.02.2017.
 */

public class Config {

    public final static String apiUrlCars = "api_url_cars";
    public final static String urlCarDelete = "car/delete/";
    public final static String urlVin = "vin/";
    public final static String urlServer = "http://192.168.1.4:8080/api/";
    public static String getMetaData(Context contex, String name) {
        try {
            ApplicationInfo ai = contex.getPackageManager().getApplicationInfo(contex.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            return bundle.getString(name);
        }
        catch (PackageManager.NameNotFoundException e) {
            // to do
        }
        return null;
    }
    public static String getUrlCarDelete() {
        return urlServer +urlCarDelete;
    }
    public static String getUrlVin() {
        return urlServer +urlVin;
    }
}