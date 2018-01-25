package com.albertotorresgi.cityguide.utils;

import android.location.Location;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class Utils {
    private static String TAG="Utils";
    private static final float METERS_TO_MILES = 0.000621371f;

    public static float computeDistanceInMiles(Location location, String jsonString) throws JSONException {
        JSONObject locJSON = new JSONObject(jsonString).getJSONObject("geometry").getJSONObject("location");
        Location placeLoc = new Location("");
        placeLoc.setLatitude(locJSON.getDouble("lat"));
        placeLoc.setLongitude(locJSON.getDouble("lng"));
        return location.distanceTo(placeLoc) * METERS_TO_MILES;
    }
}