package com.usupov.autopark.model;

/**
 * Created by AzatYusupov on 15.07.2017.
 */

public class CustomHttpResponse {
    private int statusCode;
    private String bodyString;

    public int getStatusCode() {
        return this.statusCode;
    }
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
    public String getBodyString() {
        return this.bodyString;
    }
    public void setBodyString(String bodyString) {
        this.bodyString = bodyString;
    }
    public CustomHttpResponse(){}

    public CustomHttpResponse(int statusCode) {
        this.statusCode = statusCode;
        this.bodyString = "";
    }

    public CustomHttpResponse(int statusCode, String bodyString) {
        this.statusCode = statusCode;
        this.bodyString = bodyString;
    }
}
