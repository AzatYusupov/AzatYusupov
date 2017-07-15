package com.usupov.autopark.jsonHelper;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.usupov.autopark.http.Config;
import com.usupov.autopark.http.HttpHandler;
import com.usupov.autopark.model.CarModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Azat on 08.05.2017.
 */

public class Car {

    public static List<CarModel> getCarList(Context context) {

        HttpHandler handler = new HttpHandler();
        String url = Config.getUrlCars();
        String jsonStr = handler.ReadHttpResponse(url, context);
        if (jsonStr == null)
            return null;
        List<CarModel> carList = new ArrayList<>();
        JSONArray carsArray = null;
        try {
            carsArray = new JSONArray(jsonStr);
            for (int i = 0; i < carsArray.length(); i++) {
                JSONObject carObject = carsArray.getJSONObject(i);

                Gson gson = new Gson();
                CarModel carModel = gson.fromJson(carObject.toString(), CarModel.class);

//                carModel.setId(carObject.getInt("id"));
//
//                carModel.setBrandId(carObject.getInt("brandId"));
//                carModel.setModelId(carObject.getInt("modelId"));
//                carModel.setYearId(carObject.getInt("yearId"));
//
//                carModel.setBrandName(carObject.getString("brandName"));
//                carModel.setModelName(carObject.getString("modelName"));
//                carModel.setYearName(carObject.getString("yearName"));
//
//                carModel.setVin(carObject.getString("vin"));
                carModel.setFullName();

                carList.add(carModel);
            }
            return carList;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static CarModel getCarWithVin(String vin, Context context) {

        HttpHandler handler = new HttpHandler();
        final String urlVin = Config.getUrlVin();
        String url = urlVin+"/"+vin;
        String jSonString = handler.ReadHttpResponse(url, context);

        return fromJsonToCarModel(jSonString);
    }
    public static CarModel getCarByCatalog(int brandId, int modelId, int yearId, Context context) {
        HttpHandler handler = new HttpHandler();
        final String url = Config.getUrlGetByCatalog(brandId, modelId, yearId);
        String jsonString = handler.ReadHttpResponse(url, context);

        return fromJsonToCarModel(jsonString);
    }
    private static CarModel fromJsonToCarModel(String jsonString) {
        if (jsonString==null)
            return null;
        try {
            Gson g = new Gson();
            return g.fromJson(jsonString, CarModel.class);
        }
        catch (Exception e){}

        return null;
    }
}
