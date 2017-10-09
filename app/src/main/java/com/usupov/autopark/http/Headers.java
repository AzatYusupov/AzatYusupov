package com.usupov.autopark.http;


import android.content.Context;

import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.usupov.autopark.config.LocalConstants;

import java.util.HashMap;
import java.util.Map;

public class Headers {

    public static GlideUrl getUrlWithHeaders(String url, Context context){
        try {
            return new GlideUrl(url, new LazyHeaders.Builder()
                    .addHeader(LocalConstants.TOKEN_HEADER_KEY, HttpHandler.getLocalServerToken(context))
                    .build());
        }
        catch (Exception e) {
            return null;
        }
    }

    public static Map<String, String> headerMap(Context context) {
        Map<String, String> params = new HashMap<>();
        String serverToken = HttpHandler.getLocalServerToken(context);
        params.put(LocalConstants.TOKEN_HEADER_KEY, serverToken);
        params.put("Content-Type","application/x-www-form-urlencoded");
        return params;
    }

    public static Map<String, String> headerMultipartMap(Context context) {
        Map<String, String> params = new HashMap<>();
        String serverToken = HttpHandler.getLocalServerToken(context);
        params.put(LocalConstants.TOKEN_HEADER_KEY, serverToken);
        params.put("Content-Type", "multipart/*");
        return params;
    }
}