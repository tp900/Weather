package com.tp900.weather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpUtil {
    //get 请求
    public static void SendOkHttpRequest(String address,okhttp3.Callback callback){
        OkHttpClient okHttpClient= new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        okHttpClient.newCall(request).enqueue(callback);
    }
    //post请求
    public static void  SendOkHttpRequest(String address, RequestBody requestBody,okhttp3.Callback callback){
        OkHttpClient okHttpClient= new OkHttpClient();
        Request request = new Request.Builder().url(address).post(requestBody).build();
        okHttpClient.newCall(request).enqueue(callback);
    }
}
