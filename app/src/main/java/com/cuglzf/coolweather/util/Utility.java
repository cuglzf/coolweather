package com.cuglzf.coolweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.cuglzf.coolweather.model.City;
import com.cuglzf.coolweather.model.CoolWeatherDB;
import com.cuglzf.coolweather.model.County;
import com.cuglzf.coolweather.model.Province;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by LZF on 2016/9/22.
 */
public class Utility {

    /**
     *  解析和处理服务器返回的省级数据
     */
    public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB , String response){

        if (TextUtils.isEmpty(response)){
            return false ;
        }
        try{
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(response));
            int eventType = xmlPullParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT){
                String tagName = xmlPullParser.getName();
                switch (eventType){
                    //开始解析某个节点
                    case XmlPullParser.START_TAG :{
                        if ("city".equals(tagName)){
                            Province province = new Province() ;
                            province.setProvinceName(xmlPullParser.getAttributeValue(null,"quName"));
                            province.setProvinceCode(xmlPullParser.getAttributeValue(null,"pyName"));
                            coolWeatherDB.saveProvince(province);
                        }
                        break;
                    }
                    default:
                        break;
                }
                eventType = xmlPullParser.next();
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true ;
    }

    /**
     *  解析和处理服务器返回的市级数据
     */
    public  static final boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB , String response , int provinceId){

        if (TextUtils.isEmpty(response)){
            return false ;
        }
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(response));
            int eventType = xmlPullParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT){
                String tagName = xmlPullParser.getName();
                switch (eventType){
                    case XmlPullParser.START_TAG :{
                        if ("city".equals(tagName)){
                            City city = new City();
                            city.setCityName(xmlPullParser.getAttributeValue(null,"cityname"));
                            city.setCityCode(xmlPullParser.getAttributeValue(null,"pyName"));
                            city.setProvinceId(provinceId);
                            coolWeatherDB.saveCity(city);
                        }
                        break;
                    }
                    default:
                        break;
                }
                eventType = xmlPullParser.next();
            }

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     *  解析和处理服务器返回的县级数据
     */
    public  static final boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB , String response , int ctiyId){

        if (TextUtils.isEmpty(response)){
            return false ;
        }
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(response));
            int eventType = xmlPullParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT){
                String tagName = xmlPullParser.getName();
                switch (eventType){
                    case XmlPullParser.START_TAG :{
                        if ("city".equals(tagName)){
                            County county = new County();
                            county.setCountyName(xmlPullParser.getAttributeValue(null,"cityname"));
                            county.setCountyCode(xmlPullParser.getAttributeValue(null,"pyName"));
                            county.setCityId(ctiyId);
                            coolWeatherDB.saveCounty(county);
                        }
                        break;
                    }
                    default:
                        break;
                }
                eventType = xmlPullParser.next();
            }

        }catch (Exception e){
            e.printStackTrace();
            return false ;
        }
        return true ;
    }
    /**
     *  解析服务器返回的XML数据，并将解析出的数据储存在本地
     */
    public static void handleWeatherResponse(Context context , String response ,String cityCode , String countyName){

        if (TextUtils.isEmpty(response)){
            return  ;
        }
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(response));
            int eventType = xmlPullParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT){
                String tagName = xmlPullParser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG: {
                        if ("city".equals(tagName) ) {
                            String getCountyName = xmlPullParser.getAttributeValue(null, "centername");
                            if (getCountyName.equals(countyName)) {
                                String weatherCode = xmlPullParser.getAttributeValue(null, "url");
                                String temp1 = xmlPullParser.getAttributeValue(null, "tem1");
                                String temp2 = xmlPullParser.getAttributeValue(null, "tem2");
                                String weatherDesp = xmlPullParser.getAttributeValue(null, "stateDetailed");
                                String publishTime = xmlPullParser.getAttributeValue(null, "time");
                                saveWeatherInfo(context, cityCode, countyName, weatherCode, temp1, temp2, weatherDesp, publishTime);
                            }
                        }
                        break;
                    }
                    default:
                        break;
                }
                eventType = xmlPullParser.next();
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.d("mytest","handleWeatherResponse Error!");
        }
    }

    /**
     *   将服务器返回的所有天气信息储存到SharedPreferences文件中
     */
     public static void saveWeatherInfo(Context context ,String belongCity, String cityName , String weatherCode, String temp1,
                                        String temp2 , String weatherDesp , String publishTime){
         SimpleDateFormat sdf = new SimpleDateFormat("yyy年M月d日", Locale.CHINA);
         SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
         editor.putBoolean("city_selected",true);
         editor.putString("belong_city",belongCity);
         editor.putString("city_name",cityName);
         editor.putString("weather_code",weatherCode);
         editor.putString("temp1",temp1);
         editor.putString("temp2",temp2);
         editor.putString("weather_desp",weatherDesp);
         editor.putString("publish_time",publishTime);
         editor.putString("current_date",sdf.format(new Date()));
         editor.commit();
     }

}
