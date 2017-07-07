package com.usupov.autopark.http;

/**
 * Created by Azat on 26.02.2017.
 */

import android.util.Log;


import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class HttpHandler {

    private static final String TAG = HttpHandler.class.getSimpleName();

    public HttpHandler() {
    }

    public String ReadHttpResponse(String url){

        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);
        try {
            System.out.println("UUUUUUUUUUUUUUUUUUU="+url);
            HttpResponse response = client.execute(request);
            StatusLine sl = response.getStatusLine();
            int sc = sl.getStatusCode();
            if (sc==200)
            {
                return getResponseString(response);
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

    public boolean deleteQuery(String url) {
        HttpClient client = new DefaultHttpClient();
        HttpDelete request = new HttpDelete(url);
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

    public boolean postWithMultipleFiles(String urlTo, HashMap<String, String> params, List<String> filePaths) {
        try {
            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

            ContentType contentType = ContentType.create(
                    HTTP.PLAIN_TEXT_TYPE, HTTP.UTF_8);
            for (String key : params.keySet()) {
                entityBuilder.addTextBody(key, params.get(key), contentType);
            }

            HttpPost post = new HttpPost(urlTo);
//            post.setHeader("Accept-Charset","utf-8");
//            post.addHeader("User-Agent", "Test");
//            post.addHeader("Content-type", "multipart/form-data");
//            post.addHeader("Accept", "image/jpg");

            HttpEntity entity = entityBuilder.build();
            post.setEntity(entity);
//            System.out.println("--------------------------");
//            for (String key : params.keySet()) {
//                System.out.println(key + " " + params.get(key));
//            }

            HttpClient client = new DefaultHttpClient();
            HttpResponse response = client.execute(post);
            if (response.getStatusLine().getStatusCode()==200) {
                String responseText = getResponseString(response);
                try {
                    long userPartId = Long.parseLong(responseText);
                    if (filePaths != null && filePaths.size() > 0) {
                        String url = Config.getUrlUserPart() +"/"+ userPartId + "/add_image";

                        for (int i = 0; i < filePaths.size(); i++) {
                            post = new HttpPost(url);
                            entityBuilder = MultipartEntityBuilder.create();
                            entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                            entityBuilder.addBinaryBody("file", new File(filePaths.get(i)));
                            entityBuilder.addTextBody("cnt", i+"");
                            entity = entityBuilder.build();
                            post.setEntity(entity);
                            response = client.execute(post);
//                            System.out.println(response.getStatusLine().getStatusCode()+" codeeeeeeeeeeeeeeeeeee");
                        }
                    }
                }
                catch (Exception e){}
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public int doSimplePost(String urlTo, List<NameValuePair> pairs) {
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(urlTo);

//        System.out.println("UUUUUUUUUUUUUUUUUUUUUUUUUUUU+ "+urlTo);
        try {
            post.setEntity(new UrlEncodedFormEntity(pairs));
            System.out.println("*-*-*-*-*-*-*-*");

//            post.setHeader("Accept-Charset","utf-8")
            HttpResponse response = client.execute(post);

            return response.getStatusLine().getStatusCode();

        } catch (Exception e) {

//            System.out.println("*********************************");
            e.printStackTrace();

        }
        return 0;
    }

    public int postWithOneFile(String urlTo, HashMap<String, String> params, String filePath) {

        try {

            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

            for (String key : params.keySet()) {
                entityBuilder.addTextBody(key, params.get(key));
            }


            if (filePath != null && !filePath.isEmpty())
                entityBuilder.addBinaryBody("file", new File(filePath));

            HttpPost post = new HttpPost(urlTo);
            HttpEntity entity = entityBuilder.build();
            post.setEntity(entity);
//            post.setHeader("Accept-Charset","utf-8");

            HttpClient client = new DefaultHttpClient();
            HttpResponse response = client.execute(post);

            return response.getStatusLine().getStatusCode();
        }
        catch (Exception e) {
            return 0;
        }
    }

    private static String convertInputStreamToString(InputStream inputStream)
            throws IOException {
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }
}

