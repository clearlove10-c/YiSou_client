package com.smlz.yisounews.util;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.smlz.yisounews.entity.HttpResult;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public abstract class HttpResultListener implements Callback {
    @Override
    public void onFailure(@NonNull Call call, IOException e) {
        onResult(new HttpResult(400, e.getMessage(), null));
    }

    @Override
    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
        if (response.code() == 200 && response.body() != null) {
            onResult(new Gson().fromJson(response.body().string(), HttpResult.class));
        } else {
            onResult(new HttpResult(400, String.valueOf(response.code()), null));
        }
    }

    /**
     * http请求返回结果
     **/
    protected abstract void onResult(HttpResult httpResult);
}
