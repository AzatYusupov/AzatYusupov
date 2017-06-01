package com.usupov.autopark.http;

/**
 * Created by Azat on 26.02.2017.
 */

import android.util.Log;


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
import java.net.HttpURLConnection;
import java.net.URL;
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

    public boolean postQuery3(String urlTo, HashMap<String, String> parmas, String filepath) {
        HttpPost post = new HttpPost(urlTo);
        HttpClient hc = new DefaultHttpClient();

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        Iterator<String> keys = parmas.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            String value = parmas.get(key);
            nameValuePairs.add(new BasicNameValuePair(key, value));
        }

        try {
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
            post.getParams().setBooleanParameter("http.protocol.expect-continue", false);
            HttpResponse rp = hc.execute(post);
            return true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }
    public boolean postWithMultipleFiles(String urlTo, HashMap<String, String> params, List<String> filePaths) {
        try {
            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

            for (String key : params.keySet()) {
                entityBuilder.addTextBody(key, params.get(key));
            }


            HttpPost post = new HttpPost(urlTo);
//            post.addHeader("User-Agent", "Test");
//            post.addHeader("Content-type", "multipart/form-data");
//            post.addHeader("Accept", "image/jpg");
            HttpEntity entity = entityBuilder.build();
            post.setEntity(entity);

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
                            System.out.println(response.getStatusLine().getStatusCode()+" codeeeeeeeeeeeeeeeeeee");
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
    public boolean postWithOneFile(String urlTo, HashMap<String, String> params, String filePath) {
        try {
            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

            for (String key : params.keySet()) {
                entityBuilder.addTextBody(key, params.get(key));
            }
            if (filePath != null && !filePath.equals("")) {
                entityBuilder.addBinaryBody("file", new File(filePath));
            }
            HttpPost post = new HttpPost(urlTo);
//            post.addHeader("User-Agent", "Test");
//            post.addHeader("Content-type", "multipart/form-data");
//            post.addHeader("Accept", "image/jpg");

            HttpEntity entity = entityBuilder.build();
            post.setEntity(entity);

            HttpClient client = new DefaultHttpClient();
            HttpResponse response = client.execute(post);
            if (response.getStatusLine().getStatusCode()==200)
                return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean postQuery(String urlTo, HashMap<String, String> params, String filepath) {
        HttpURLConnection connection = null;
        DataOutputStream outputStream = null;

        String twoHyphens = "--";
        String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
        String lineEnd = "\r\n";

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 10 * 1024 * 1024;

        try {
            System.out.println(filepath + " ----------");
            System.out.println("urllllllllllllllllllll="+urlTo);

            URL url = new URL(urlTo);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

//            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            connection.setRequestProperty( "charset", "utf-8");

            outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            if (filepath != null && !filepath.equals("")) {
                outputStream.writeBytes("Content-Disposition: form-data; name=\"" + "file" + "\"; filename=\"" + filepath + "\"" + lineEnd);
//            outputStream.writeBytes("Content-Type: " + fileMimeType + lineEnd);
                outputStream.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);
                outputStream.writeBytes(lineEnd);
            }


            if (filepath != null && !filepath.equals("")) {
                System.out.println(filepath+"  *******");
                File file = new File(filepath);
                FileInputStream fileInputStream = new FileInputStream(file);

                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                while (bytesRead > 0) {
                    outputStream.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                outputStream.writeBytes(lineEnd);
            }
            // Upload POST Data
            Iterator<String> keys = params.keySet().iterator();
            for (String key : params.keySet()) {
                String value = params.get(key);
                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
                outputStream.writeBytes("Content-Type: text/plain" + lineEnd);
                outputStream.writeBytes(lineEnd);
                outputStream.writeUTF(value);
                outputStream.writeBytes(lineEnd);
            }
            System.out.println(filepath+"   9999999999999999999999");
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
            System.out.println(connection.getResponseCode()+"  ++++++++++++++++++++++");
            if (200 != connection.getResponseCode()) {
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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

