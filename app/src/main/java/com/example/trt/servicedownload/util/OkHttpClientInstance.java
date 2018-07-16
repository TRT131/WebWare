package com.example.trt.servicedownload.util;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by Trt on 2018/6/24.
 */

public class OkHttpClientInstance {
    public static OkHttpClient instance;
    public OkHttpClientInstance() {
    }
    public static OkHttpClient getInstance(){
        if (instance==null){
            synchronized (OkHttpClientInstance.class){
                if (instance==null){
                    instance=new OkHttpClient().newBuilder()
                            .connectTimeout(10, TimeUnit.SECONDS)
                            .readTimeout(10,TimeUnit.SECONDS)
                            .writeTimeout(10, TimeUnit.SECONDS)
                            .build();
                }
            }
        }
        return instance;
    }
}
