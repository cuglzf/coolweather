package com.cuglzf.coolweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cuglzf.coolweather.R;
import com.cuglzf.coolweather.service.AutoUpdateService;
import com.cuglzf.coolweather.util.HttpCallbackListener;
import com.cuglzf.coolweather.util.HttpUtil;
import com.cuglzf.coolweather.util.Utility;

/**
 * Created by LZF on 2016/9/28.
 */
public class WeatherActivity extends Activity implements View.OnClickListener{

    /**
     *  切换城市按钮
     */
    private Button switchCity;

    /**
     *  更新天气按钮
     */
    private Button refreshWeather;

    private LinearLayout weatherInfoLayout;
    /**
     *  用于显示城市名
     */
    private TextView cityNameText;
    /**
     *  用于显示发布时间
     */
    private TextView publishText;
    /**
     *  用于显示天气描述信息
     */
    private TextView weatherDespText;
    /**
     *  用于显示气温1：最低温度
     */
    private TextView temp1Text;
    /**
     *  用于显示气温2：最高温度
     */
    private TextView temp2Text;
    /**
     *  用于显示当前日期
     */
    private TextView currentDateText;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);
        //初始化各控件
        weatherInfoLayout = (LinearLayout)findViewById(R.id.weather_info_layout);
        cityNameText = (TextView)findViewById(R.id.city_name);
        publishText = (TextView)findViewById(R.id.publish_text);
        weatherDespText = (TextView)findViewById(R.id.weather_desp);
        temp1Text = (TextView)findViewById(R.id.temp1);
        temp2Text = (TextView)findViewById(R.id.temp2);
        currentDateText = (TextView)findViewById(R.id.current_date);
        String cityCode = getIntent().getStringExtra("city_code");
        String countyName = getIntent().getStringExtra("county_name");
        if (!TextUtils.isEmpty(cityCode)){
            //有县级代号时就去查询天气
            publishText.setText("同步中...");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
            queryWeatherCode(cityCode,countyName);
        }else {
            //没有县级代号时就直接显示本地天气
            showWeather();
        }
        switchCity = (Button)findViewById(R.id.switch_city);
        refreshWeather = (Button)findViewById(R.id.refresh_weather);
        switchCity.setOnClickListener(this);
        refreshWeather.setOnClickListener(this);
    }

    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.switch_city :
                Intent intent = new Intent(this,ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity",true);
                startActivity(intent);
                finish();
                break;
            case R.id.refresh_weather :
                publishText.setText("同步中...");
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
               // String weatherCode = prefs.getString("weather_code","");
                String cityCode = prefs.getString("belong_city","");
                String countyName = prefs.getString("city_name","");
                if (!TextUtils.isEmpty(countyName)){
                    queryWeatherCode(cityCode,countyName);
                }
                break;
            default:
                break;
        }
    }

    /**
     *  查询县级代号所属城市所对应的天气代号
     */
    private void queryWeatherCode(String cityCode,String countyName){
        String address = "http://flash.weather.com.cn/wmaps/xml/" + cityCode + ".xml";
        queryFromServer(address,cityCode,countyName);
    }


    /**
     *  根据传入的地址和类型去向服务器查询天气代号或者天气信息
     */
    private void queryFromServer(final String address ,final String cityCode, final String countyName){
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                    if (!TextUtils.isEmpty(response)){
                        //处理服务器返回的天气信息
                        Utility.handleWeatherResponse(WeatherActivity.this , response ,cityCode,countyName);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showWeather();
                            }
                        });

                    }

            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishText.setText("同步失败");
                    }
                });

            }
        });
    }

    /**
     *  从SharedPreferences文件中读取储存的天气信息，并显示到界面上
     */
    private void showWeather(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        cityNameText.setText(prefs.getString("city_name",""));
        temp1Text.setText(prefs.getString("temp1","")+"℃");
        temp2Text.setText(prefs.getString("temp2","")+"℃");
        weatherDespText.setText(prefs.getString("weather_desp",""));
        publishText.setText("今天" + prefs.getString("publish_time","") + "发布");
        currentDateText.setText(prefs.getString("current_date",""));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this , AutoUpdateService.class);
        Log.d("mytest","AutoUpdateService begin");
        startService(intent);
    }
}
