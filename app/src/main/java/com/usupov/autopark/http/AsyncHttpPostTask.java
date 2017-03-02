package com.usupov.autopark.http;

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Azat on 02.03.2017.
 */

public class AsyncHttpPostTask extends AsyncTask<File, Void, String> {

    private static final String TAG = AsyncHttpPostTask.class.getSimpleName();
    private String server;

    public AsyncHttpPostTask(final String server) {
        this.server = server;
    }

    @Override
    protected String doInBackground(File... params) {
        Log.d(TAG, "doInBackground");
        HttpClient http = AndroidHttpClient.newInstance("MyApp");
        HttpPost method = new HttpPost(this.server);
        method.setEntity(new FileEntity(params[0], "text/plain"));
        try {
            HttpResponse response = http.execute(method);
            BufferedReader rd = new BufferedReader(new InputStreamReader(
                    response.getEntity().getContent()));
            final StringBuilder out = new StringBuilder();
            String line;
            try {
                while ((line = rd.readLine()) != null) {
                    out.append(line);
                }
            } catch (Exception e) {}
            // wr.close();
            try {
                rd.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // final String serverResponse = slurp(is);
            Log.d(TAG, "serverResponse: " + out.toString());
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

