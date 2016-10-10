package com.cuglzf.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.cuglzf.coolweather.util.HttpCallbackListener;
import com.cuglzf.coolweather.util.HttpUtil;
import com.cuglzf.coolweather.util.Utility;

/**
 * Created by LZF on 2016/10/6.
 */
public class AutoUpdateService extends Service {

    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    public int onStartCommand(Intent intent , int flags , int startId){
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateWeather();
            }
        }).start();
        AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
        int anHour = 8*60*60*1000 ; //这是8小时的毫秒数
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour ;
        Intent i = new Intent(this , AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getBroadcast(this , 0 , i , 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent , flags , startId);
    }

    /**
     *  更新天气信息
     */
    private void updateWeather(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final String cityCode = prefs.getString("belong_city","");
        final String countyName = prefs.getString("city_name","");
        String address = "http://flash.weather.com.cn/wmaps/xml/" + cityCode + ".xml";
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Utility.handleWeatherResponse(AutoUpdateService.this , response ,cityCode, countyName);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }
}
