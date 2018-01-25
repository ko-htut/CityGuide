package com.albertotorresgi.cityguide.models;

import android.location.Location;

public class ApiResponse {

    private String response;
    private int code;
    private int type_request;
    private Location location;

    public ApiResponse() {
    }

    public ApiResponse(int response_code, int type_request, Location location, String response) {
        this.response = response;
        this.code = response_code;
        this.type_request = type_request;
        this.location = location;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getType_request() {
        return type_request;
    }

    public void setType_request(int type_request) {
        this.type_request = type_request;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
