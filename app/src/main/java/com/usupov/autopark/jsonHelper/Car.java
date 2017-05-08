package com.usupov.autopark.jsonHelper;

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

    public static List<CarModel> getCatList() {

        List<CarModel> carList = new ArrayList<>();
        HttpHandler handler = new HttpHandler();
        String url = Config.getUrlCars();
        String jsonStr = handler.ReadHttpResponse(url);
        if (jsonStr == null)
            return null;
        JSONArray carsArray = null;
        try {
            carsArray = new JSONArray(jsonStr);
            for (int i = 0; i < carsArray.length(); i++) {
                CarModel carModel = new CarModel();
                JSONObject carObject = carsArray.getJSONObject(i);

                carModel.setId(carObject.getInt("id"));

                carModel.setBrandId(carObject.getInt("brandId"));
                carModel.setModelId(carObject.getInt("modelId"));
                carModel.setYearId(carObject.getInt("yearId"));

                carModel.setBrandName(carObject.getString("brandName"));
                carModel.setModelName(carObject.getString("modelName"));
                carModel.setYearName(carObject.getString("yearName"));

                carModel.setVin(carObject.getString("vin"));

                String fullName = carModel.getBrandName()+" "+carModel.getModelName()+", "+carModel.getYearName();
                if (carModel.getModelName().startsWith(carModel.getBrandName())) {
                    fullName = carModel.getBrandName()+" "+carModel.getModelName().substring(carModel.getBrandName().length()+1)+", "+carModel.getYearName();
                }
                
                carModel.setFullName(fullName);
                carList.add(carModel);
            }
            return carList;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;

    }

}
