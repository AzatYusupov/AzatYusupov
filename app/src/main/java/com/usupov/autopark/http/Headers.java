package com.usupov.autopark.http;


import android.content.Context;

import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.usupov.autopark.config.LocalConstants;

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
}