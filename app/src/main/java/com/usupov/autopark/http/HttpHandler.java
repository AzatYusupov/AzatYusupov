package com.usupov.autopark.http;

/**
 * Created by Azat on 26.02.2017.
 */

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HttpHandler {

    private static final String TAG = HttpHandler.class.getSimpleName();

    public HttpHandler() {
    }
    public String ReadHttpResponse(String url){
        StringBuilder sb= new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);
        try {
            HttpResponse response = client.execute(request);
            StatusLine sl = response.getStatusLine();
            int sc = sl.getStatusCode();
            if (sc==200)
            {
                HttpEntity ent = response.getEntity();
                InputStream inpst = ent.getContent();
                BufferedReader rd = new BufferedReader(new InputStreamReader(inpst));
                String line;
                while ((line=rd.readLine())!=null)
                {
                    sb.append(line);
                }
                return sb.toString();
            }
            else
            {
                Log.e( "log_tag","I didn't  get the response!");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public boolean deleteQuery(String url) {
        HttpClient client = new DefaultHttpClient();
        HttpDelete request = new HttpDelete(url);
        StringBuilder sb= new StringBuilder();
        try {
            HttpResponse response = client.execute(request);
            StatusLine sl = response.getStatusLine();
            int sc = sl.getStatusCode();
            if (sc==200)
                return true;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}