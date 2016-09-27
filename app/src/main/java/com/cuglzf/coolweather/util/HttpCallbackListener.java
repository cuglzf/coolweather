package com.cuglzf.coolweather.util;

/**
 * Created by LZF on 2016/9/22.
 */
public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
