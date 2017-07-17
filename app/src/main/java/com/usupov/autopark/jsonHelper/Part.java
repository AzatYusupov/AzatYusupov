package com.usupov.autopark.jsonHelper;

import android.content.Context;

import com.google.gson.Gson;
import com.usupov.autopark.config.PartRestURIConstants;
import com.usupov.autopark.http.HttpHandler;
import com.usupov.autopark.model.PartModel;
import com.usupov.autopark.model.UserPartModel;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;


public class Part {

    public static List<PartModel> searchStartWith(long carId, String statsWith, Context context) {
        HttpHandler handler = new HttpHandler();
        String url = String.format(PartRestURIConstants.SEARCH, carId, statsWith);
        try {
            String response = handler.doHttpGet(url, context).getBodyString();
            JSONArray partArray = new JSONArray(response);
            List<PartModel> partList = new ArrayList<>();
            for (int i = 0; i < partArray.length(); i++) {
                Gson g = new Gson();
                PartModel part = g.fromJson(partArray.getJSONObject(i).toString(), PartModel.class);
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
        String url = PartRestURIConstants.GET_ALL;

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
            return null;
        }
    }
}
