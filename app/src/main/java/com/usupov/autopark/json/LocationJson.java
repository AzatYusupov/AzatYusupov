package com.usupov.autopark.json;

import android.content.Context;
import android.os.Handler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.usupov.autopark.config.LocationURIConstants;
import com.usupov.autopark.http.HttpHandler;
import com.usupov.autopark.model.location.City;
import com.usupov.autopark.model.location.District;
import com.usupov.autopark.model.location.Region;
import com.usupov.autopark.model.location.Subway;

import java.util.List;


public class LocationJson {

    public static Region getRegionById(long regionId, Context context) {
        HttpHandler handler = new HttpHandler();
        try {
            String url = String.format(LocationURIConstants.GET_REGION_BY_ID, regionId);
            System.out.println(url+" 777777777777888");
            Gson g = new Gson();
            return g.fromJson(handler.doHttpGet(url, context).getBodyString(), Region.class);
        }
        catch (Exception e) {
            return null;
        }
    }
    public static List<Region> getRegionList(Context context) {
        try {
            HttpHandler handler = new HttpHandler();
            String url = LocationURIConstants.GET_REGIONS;
            Gson g = new Gson();
            return  g.fromJson(handler.doHttpGet(url, context).getBodyString(), new TypeToken<Region>(){}.getType());
        }
        catch (Exception e) {
            return null;
        }
    }

    public static City getCityById(long regionId, long cityId, Context context) {
        HttpHandler handler = new HttpHandler();
        try {
            String url = String.format(LocationURIConstants.GET_CITY_BY_ID, regionId, cityId);
            Gson g = new Gson();
            return g.fromJson(handler.doHttpGet(url, context).getBodyString(), City.class);
        }
        catch (Exception e) {
            return null;
        }
    }

    public static District getDistrictById(long cityId, long districtId, Context context) {
        HttpHandler handler = new HttpHandler();
        try {
            String url = String.format(LocationURIConstants.GET_DISTRICT_BY_ID, cityId, districtId);
            Gson g = new Gson();
            return g.fromJson(handler.doHttpGet(url, context).getBodyString(), District.class);
        }
        catch (Exception e) {
            return null;
        }
    }

    public static Subway getSubwayById(long cityId, long subwayId, Context context) {
        HttpHandler handler = new HttpHandler();
        try {
            String url = String.format(LocationURIConstants.GET_SUBWAY_BY_ID, cityId, subwayId);
            System.out.println("uuuuu="+url);
            Gson g = new Gson();
            return g.fromJson(handler.doHttpGet(url, context).getBodyString(), Subway.class);
        }
        catch (Exception e) {
            return null;
        }
    }
}
