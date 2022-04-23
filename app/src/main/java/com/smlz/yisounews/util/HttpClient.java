package com.smlz.yisounews.util;

import android.util.Log;
import android.util.Pair;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpClient {
    private static final String TAG = "HttpClient";
    private static HttpClient httpClient;
    private int connectTimeout = 30, readTimeout = 30, writeTimeout = 30;

    public static HttpClient getClient() {
        Log.d(TAG, "getClient: ");
//        if (httpClient == null)
        httpClient = new HttpClient();
        return httpClient;
    }

    private OkHttpClient okHttpClient;

    public void init(int connectTimeout, int readTimeout, int writeTimeout) {
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
        this.writeTimeout = writeTimeout;
        //信任规则全部信任
        okHttpClient = new OkHttpClient.Builder() //
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)//设置连接超时时间
                .readTimeout(readTimeout, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                .hostnameVerifier((hostname, session) -> true)
                .build();
    }

    private void checkClient() {
        if (okHttpClient == null) {
            init(connectTimeout, readTimeout, writeTimeout);
        }

    }

    /**
     * 执行GET请求
     **/
    public void doGetRequest(String requestUrl, Map<String, String> parameters, HttpResultListener httpRequestListener) {
        StringBuilder urlBuilder = new StringBuilder(requestUrl);
        if (parameters != null) {
            urlBuilder.append("?");
            int index = 0;
            for (String key : parameters.keySet()) {
                if (index != 0)
                    urlBuilder.append("&").append(key).append("=").append(parameters.get(key));
                else
                    urlBuilder.append(key).append("=").append(parameters.get(key));
                index++;
            }
        }
        doHttpRequest(new Request.Builder().url(urlBuilder.toString()).build(), httpRequestListener);
    }

    /**
     * 执行POST表单请求
     **/

    public Call doPostFormRequest(String requestUrl, Map<String, String> parameters, HttpResultListener httpResultListener) {
        FormBody.Builder formBuilder = new FormBody.Builder();
        if (parameters != null)
            for (String key : parameters.keySet()) {
                formBuilder.add(key, Objects.requireNonNull(parameters.get(key)));
            }
        return doHttpRequest(new Request.Builder().url(requestUrl).post(formBuilder.build()).build(), httpResultListener);
    }

    public void doPostRequest(String requestUrl, Pair<String, File> fileParam, String fileType, String fileName, Map<String, String> stringParam, HttpResultListener httpResultListener) {
        RequestBody fileBody = RequestBody.create(MediaType.parse(fileType), fileParam.second);
        MultipartBody.Builder builder=new MultipartBody.Builder();
        builder.setType(MultipartBody.ALTERNATIVE);
        builder.addFormDataPart(fileParam.first, fileName, fileBody);
        for(String key : stringParam.keySet()){
            builder.addFormDataPart(key,stringParam.get(key));
        }
        RequestBody requestBody=builder.build();
        doHttpRequest(new Request.Builder().url(requestUrl).post(requestBody).build(), httpResultListener);
    }


    /**
     * 执行POST Json请求
     **/
    public void doPostJsonRequest(String requestUrl, String json, HttpResultListener httpResultListener) {
        RequestBody requestBody = FormBody.create(MediaType.parse("application/json; charset=utf-8"), json == null ? "" : json);
        doHttpRequest(new Request.Builder().url(requestUrl).post(requestBody).build(), httpResultListener);
    }

    /**
     * 执行HTTP请求
     **/
    private Call doHttpRequest(Request request, Callback callback) {
        checkClient();
        Call call = okHttpClient.newCall(request);
        call.enqueue(callback);
        return call;
    }
}