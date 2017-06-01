package com.usupov.autopark.jsonHelper;

import com.google.gson.Gson;
import com.usupov.autopark.http.Config;
import com.usupov.autopark.http.HttpHandler;
import com.usupov.autopark.model.PartModel;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;


public class Part {
    public static List<PartModel> searchStartWith(int carId, String statsWith) {
        HttpHandler handler = new HttpHandler();
        String url = Config.getUrlCar() + carId+"/part/search?search="+statsWith;
        try {
            String response = handler.ReadHttpResponse(url);
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
}
