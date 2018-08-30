package com.tp900.weather.util;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.tp900.weather.db.City;
import com.tp900.weather.db.County;
import com.tp900.weather.db.Province;
import com.tp900.weather.gson.Weather;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class Utility {
    public static boolean HandleProvinceResponse(String response){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray jsonArray= new JSONArray(response);
               // Gson gson=new Gson();
               // List<Province> provinces = gson.fromJson(response,new TypeToken<List<Province>>(){}.getType());
                if(null !=jsonArray&&jsonArray.length()>0){
                    for (int i = 0 ;i<jsonArray.length();i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Province province = new Province();
                        province.setProvinceCode(jsonObject.getInt("id"));
                        province.setProvinceName(jsonObject.getString("name"));
                        province.save();
                    }
                }
                return true;
            }catch (Exception ex){
                ex.printStackTrace();
            }

        }
      return false;
    }
    public static boolean HandleCityResponse(String response,int provinceId){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray jsonArray= new JSONArray(response);
                // Gson gson=new Gson();
                // List<Province> provinces = gson.fromJson(response,new TypeToken<List<Province>>(){}.getType());
                if(null !=jsonArray&&jsonArray.length()>0){
                    for (int i = 0 ;i<jsonArray.length();i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        City city = new City();
                        city.setCityCode(jsonObject.getInt("id"));
                        city.setCityName(jsonObject.getString("name"));
                        city.setProvinceId(provinceId);
                        city.save();
                    }
                }
                return true;
            }catch (Exception ex){
                ex.printStackTrace();
            }

        }
        return false;
    }
    public static boolean HandleCountyRespone(String response,int cityId){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray jsonArray= new JSONArray(response);
                // Gson gson=new Gson();
                // List<Province> provinces = gson.fromJson(response,new TypeToken<List<Province>>(){}.getType());
                if(null !=jsonArray&&jsonArray.length()>0){
                    for (int i = 0 ;i<jsonArray.length();i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        County county = new County();
                        county.setCityId(cityId);
                        county.setCountyName(jsonObject.getString("name"));
                        county.setWeatherId(jsonObject.getString("weather_id"));
                        county.save();
                    }
                }
                return true;
            }catch (Exception ex){
                ex.printStackTrace();
            }

        }
        return false;
    }
    public static Weather HandleWeatherResponse(String response){
        try{
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather6");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent,Weather.class);
        }catch (Exception ex){ex.printStackTrace();}
        return null;
    }
}
