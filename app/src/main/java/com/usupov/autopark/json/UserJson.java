package com.usupov.autopark.json;


import android.content.Context;

import com.google.gson.Gson;
import com.usupov.autopark.config.UserURIConstants;
import com.usupov.autopark.http.HttpHandler;
import com.usupov.autopark.model.CustomHttpResponse;
import com.usupov.autopark.model.UserModel;

import javax.inject.Inject;


public class UserJson {


    public static UserModel getUser(Context context) {
        HttpHandler handler = new HttpHandler();
        String url = UserURIConstants.USER_INFO;
        CustomHttpResponse response = handler.doHttpGet(url, context);
        Gson gson = new Gson();
        if (response.getStatusCode()==200)
            return gson.fromJson(response.getBodyString(), UserModel.class);
        return null;
    }
}
