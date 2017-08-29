package com.usupov.autopark.json;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.usupov.autopark.config.PartRestURIConstants;
import com.usupov.autopark.http.HttpHandler;
import com.usupov.autopark.model.CatalogYear;
import com.usupov.autopark.model.CategoryPartModel;
import com.usupov.autopark.model.CustomHttpResponse;
import com.usupov.autopark.model.UserPartModel;

import org.apache.http.HttpStatus;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;


public class Part {


    public static List<UserPartModel> searchStartWith(long carId, String statsWith, Context context) {
        HttpHandler handler = new HttpHandler();
        String url = String.format(PartRestURIConstants.SEARCH, carId, statsWith);
        try {
            String response = handler.doHttpGet(url, context).getBodyString();
            JSONArray partArray = new JSONArray(response);
            List<UserPartModel> partList = new ArrayList<>();
            for (int i = 0; i < partArray.length(); i++) {
                Gson g = new Gson();
                UserPartModel part = g.fromJson(partArray.getJSONObject(i).toString(), UserPartModel.class);
                if (carId != 0)
                    part.setCarId(carId);
                partList.add(part);
            }
            return partList;
        }
        catch (Exception e) {
            return null;
        }
    }

    public static List<UserPartModel> getUserPartList(Context context) {
        HttpHandler handler = new HttpHandler();
        String url = PartRestURIConstants.GET_USER_PARTS;

        try {
            String response = handler.doHttpGet(url, context).getBodyString();
            JSONArray userPartArray = new JSONArray(response);
            List<UserPartModel> userPartList = new ArrayList<>();
            for (int i = 0; i < userPartArray.length(); i++) {
                Gson g = new Gson();
                UserPartModel userPart = g.fromJson(userPartArray.getJSONObject(i).toString(), UserPartModel.class);
                userPartList.add(userPart);
            }
            return userPartList;
        }
        catch (Exception e) {
            System.out.println("EEEEEEEEEE");
            e.printStackTrace();
            return null;
        }
    }

    public static List<CategoryPartModel> getCategoryPartsList(long carId, long categoryId, Context context) {
        String url = String.format(PartRestURIConstants.GET_ALL, carId, categoryId);
        HttpHandler handler = new HttpHandler();
        CustomHttpResponse result = handler.doHttpGet(url, context);
        if (result.getStatusCode() == HttpStatus.SC_OK) {
            Gson g = new Gson();
            return g.fromJson(result.getBodyString(), new TypeToken<List<CategoryPartModel>>(){}.getType());
        }
        return null;
    }

    public static List<CatalogYear> getApplicableList(long partId, Context context) {
        String url = String.format(PartRestURIConstants.GET_APPLICABILITY, partId);
        System.out.println("UUUUUUUU="+url);
        HttpHandler handler = new HttpHandler();
        CustomHttpResponse result = handler.doHttpGet(url, context);
        if (result.getStatusCode()==HttpStatus.SC_OK) {
            Gson g = new Gson();
            return g.fromJson(result.getBodyString(), new TypeToken<List<CatalogYear>>(){}.getType());
        }
        return null;
    }
}
