package com.albertotorresgi.cityguide.services;

import android.content.Context;
import android.location.Location;

import com.albertotorresgi.cityguide.models.ApiResponse;
import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyApi {
    private static OkHttpClient client = new OkHttpClient();

    public static void customGet(Context context, final String url, final Map<String, String> params, final int type_request, final Location location){

        Thread thread = new Thread(new Runnable(){
            public void run() {
                try {
                    Response response=get(url, params);
                    String jsonData = response.body().string();
                    EventBus.getDefault().post(new ApiResponse(response.code(),type_request, location, jsonData));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public static Response get(String url, Map<String, String> params) throws IOException {

        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(url);
        for (String param : params.keySet()) {
            urlBuilder.append(URLEncoder.encode(param))
                    .append("=")
                    .append(URLEncoder.encode(params.get(param)))
                    .append("&");
        }

        Request request = new Request.Builder()
                .url(urlBuilder.toString())
                .build();

        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Response response = client.newCall(request).execute();
        return response;
    }

}
