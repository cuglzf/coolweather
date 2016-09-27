package com.cuglzf.coolweather.util;

import android.text.TextUtils;

import com.cuglzf.coolweather.model.City;
import com.cuglzf.coolweather.model.CoolWeatherDB;
import com.cuglzf.coolweather.model.County;
import com.cuglzf.coolweather.model.Province;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;

/**
 * Created by LZF on 2016/9/22.
 */
public class Utility {

    /**
     *  解析和处理服务器返回的省级数据
     */
    public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB , String response){

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

}
