package com.usupov.autopark.http;


import android.content.Context;
import android.content.SharedPreferences;

import com.usupov.autopark.config.LocalConstants;
import com.usupov.autopark.config.PartRestURIConstants;
import com.usupov.autopark.model.CustomHttpResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;


public class HttpHandler {


    public HttpHandler() {
    }

    public static String getLocalServerToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(LocalConstants.APP_NAME, Context.MODE_PRIVATE);

        if (!sharedPreferences.contains(LocalConstants.TOKEN_KEY)) {
            return null;
        }
        String serverToken = sharedPreferences.getString(LocalConstants.TOKEN_KEY, "AAAAA");
        return serverToken;
    }

    private void saverAutToken(Context context, String token) {
        if (token==null || token.isEmpty())
            return;
        SharedPreferences sharedPreferences = context.getSharedPreferences(LocalConstants.APP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LocalConstants.TOKEN_KEY, token);
        editor.commit();
    }

    public static boolean removeAutToken(Context context) {

        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences(LocalConstants.APP_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(LocalConstants.TOKEN_KEY);
            editor.commit();
            return true;
        }
        catch (Exception e){}
        return false;
    }


    public CustomHttpResponse doHttpGet(String url, Context context){

        CustomHttpResponse customHttpResponse = new CustomHttpResponse(-1);

        String serverToken = getLocalServerToken(context);
        System.out.println("GGGGGGGGGGGGGGGG");
        System.out.println(url);
        System.out.println(serverToken);
        if (serverToken==null) {
            customHttpResponse.setStatusCode(HttpStatus.SC_UNAUTHORIZED);
            System.out.println("444444444444444444444444444");
            return customHttpResponse;
        }

        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);
        request.setHeader(LocalConstants.TOKEN_HEADER_KEY, serverToken);

        try {
            HttpResponse response = client.execute(request);
            StatusLine sl = response.getStatusLine();
            int sc = sl.getStatusCode();

            customHttpResponse.setStatusCode(sc);

            if (sc==HttpStatus.SC_OK)
                customHttpResponse.setBodyString(getResponseString(response));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return customHttpResponse;
    }

    private String getResponseString(HttpResponse response) {
        HttpEntity ent = response.getEntity();
        try {
            StringBuilder sb= new StringBuilder();
            InputStream inpst = ent.getContent();
            BufferedReader rd = new BufferedReader(new InputStreamReader(inpst));
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
        catch (Exception e) {
            return null;
        }
    }

    public CustomHttpResponse deleteQuery(String url, Context context) {

        CustomHttpResponse customHttpResponse = new CustomHttpResponse(-1);

        String serverToken = getLocalServerToken(context);
        if (serverToken==null) {
            customHttpResponse.setStatusCode(HttpStatus.SC_UNAUTHORIZED);
            return customHttpResponse;
        }

        HttpClient client = new DefaultHttpClient();
        HttpDelete request = new HttpDelete(url);
        request.setHeader(LocalConstants.TOKEN_HEADER_KEY, serverToken);

        try {
            HttpResponse response = client.execute(request);
            StatusLine sl = response.getStatusLine();

            int sc = sl.getStatusCode();
            customHttpResponse.setStatusCode(sc);
            if (sc==HttpStatus.SC_OK)
                customHttpResponse.setBodyString(getResponseString(response));

        } catch (IOException e) {
            e.printStackTrace();
        }

        return customHttpResponse;
    }

    public CustomHttpResponse postWithMultipleFiles(String urlTo, HashMap<String, String> params, List<String> filePaths, Context context, boolean isAuthorithing) {

        CustomHttpResponse customHttpResponse = new CustomHttpResponse(-1);

        String serverToken = getLocalServerToken(context);
        if (!isAuthorithing && serverToken==null) {
            customHttpResponse.setStatusCode(HttpStatus.SC_UNAUTHORIZED);
            return customHttpResponse;
        }

        try {

            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

            ContentType contentType = ContentType.create(
                    HTTP.PLAIN_TEXT_TYPE, HTTP.UTF_8);
            for (String key : params.keySet()) {
                entityBuilder.addTextBody(key, params.get(key), contentType);
            }

            HttpPost post = new HttpPost(urlTo);

            HttpEntity entity = entityBuilder.build();
            post.setEntity(entity);
            post.setHeader(LocalConstants.TOKEN_HEADER_KEY, serverToken);

            HttpClient client = new DefaultHttpClient();
            HttpResponse response = client.execute(post);
            int sc = response.getStatusLine().getStatusCode();
            customHttpResponse.setStatusCode(sc);
            if (sc==HttpStatus.SC_OK) {
                String responseText = getResponseString(response);
                try {
                    long userPartId = Long.parseLong(responseText);
                    if (filePaths != null && filePaths.size() > 0) {
                        String url = String.format(PartRestURIConstants.ADDIMAGE, userPartId);

                        for (int i = 0; i < filePaths.size(); i++) {
                            post = new HttpPost(url);
                            entityBuilder = MultipartEntityBuilder.create();
                            entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                            entityBuilder.addBinaryBody("file", new File(filePaths.get(i)));
                            entityBuilder.addTextBody("cnt", i+"");
                            entity = entityBuilder.build();
                            post.setEntity(entity);
                            post.setHeader(LocalConstants.TOKEN_HEADER_KEY, serverToken);
                            response = client.execute(post);

                            sc = response.getStatusLine().getStatusCode();
                            customHttpResponse.setStatusCode(sc);
                            if (sc!=HttpStatus.SC_OK) {
                                customHttpResponse.setBodyString(getResponseString(response));
                                break;
                            }
                        }
                    }
                }
                catch (Exception e){}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return customHttpResponse;
    }

    public CustomHttpResponse postWithOneFile(String urlTo, HashMap<String, String> params, String filePath, Context context, boolean isAuthorithing) {


        CustomHttpResponse customHttpResponse = new CustomHttpResponse(-1);

        String serverToken = getLocalServerToken(context);
        if (!isAuthorithing && serverToken==null) {
            customHttpResponse.setStatusCode(HttpStatus.SC_UNAUTHORIZED);
            return customHttpResponse;
        }

        try {

            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

            ContentType contentType = ContentType.create(
                    HTTP.PLAIN_TEXT_TYPE, HTTP.UTF_8);

            for (String key : params.keySet()) {
                entityBuilder.addTextBody(key, params.get(key), contentType);
            }


            if (filePath != null && !filePath.isEmpty())
                entityBuilder.addBinaryBody("file", new File(filePath));

            HttpPost post = new HttpPost(urlTo);
            HttpEntity entity = entityBuilder.build();
            post.setEntity(entity);

            if (!isAuthorithing)
                post.setHeader(LocalConstants.TOKEN_HEADER_KEY, serverToken);

            HttpClient client = new DefaultHttpClient();
            HttpResponse response = client.execute(post);

            int sc = response.getStatusLine().getStatusCode();
            customHttpResponse.setStatusCode(sc);

            if (sc==HttpStatus.SC_OK) {
                String responseText = getResponseString(response);
                customHttpResponse.setBodyString(responseText);

                if (isAuthorithing) {
                    saverAutToken(context, responseText);
                }
            }
        }

        catch (Exception e) {
            e.printStackTrace();
        }

        return customHttpResponse;
    }



}

