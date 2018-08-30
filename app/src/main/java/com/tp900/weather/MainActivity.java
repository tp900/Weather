package com.tp900.weather;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tp900.weather.gson.Forecast;
import com.tp900.weather.gson.Weather;
import com.tp900.weather.service.AutoUpdateWeather;
import com.tp900.weather.util.HttpUtil;
import com.tp900.weather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private TextView title_city;
    private TextView title_update_time;
    private LinearLayout forecast_layout;
    private TextView changeCity;
    private DrawerLayout drawerLayout;
    private ImageView bgimg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        title_city = findViewById(R.id.title_city);
        title_update_time = findViewById(R.id.title_update_time);
        forecast_layout = findViewById(R.id.forecast_layout);
        changeCity = findViewById(R.id.changecity);
        drawerLayout = findViewById(R.id.drawerlayout);
        bgimg = findViewById(R.id.bgimg);
        title_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Main2Activity.class);
                startActivity(intent);
            }
        });
        changeCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        Intent intent = getIntent();
        String weatherId = intent.getStringExtra("weatherId");
        if(null==weatherId || weatherId ==""){
            weatherId = "CN101280601";
        }
        RequestWeather(weatherId);
        LoadBackgroun();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

    }
    public void RequestWeather(String weatherId){
        String url = String.format("https://free-api.heweather.com/s6/weather/forecast?location="+weatherId+"&key=7b749c0161d947f78108b2899f81df1c");
        HttpUtil.SendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "fail", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final Weather weather =  Utility.HandleWeatherResponse(response.body().string());
               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       ShowWeather(weather);
                       //Toast.makeText(MainActivity.this, weather .status, Toast.LENGTH_SHORT).show();
                   }
               });
            }
        });
    }
    private void ShowWeather(Weather weather){
        title_city.setText(weather.basic.location);
        title_update_time.setText(weather.update.loc);
        forecast_layout.removeAllViews();
        for(Forecast forecast:weather.forecasts){
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item,forecast_layout,false);
            Log.e("1",forecast.UDate);
            TextView date_text =view.findViewById(R.id.date_text);
            Log.e("2",forecast.info);
            TextView info_text = view.findViewById(R.id.info_text);
            Log.e("3",forecast.MaxTmp);
            TextView max_text = view.findViewById(R.id.max_text);
            Log.e("4",forecast.MinTmp);
            TextView min_text = view.findViewById(R.id.min_text);
            date_text.setText(forecast.UDate);
            info_text.setText(forecast.info);
            max_text.setText(forecast.MaxTmp);
            min_text.setText(forecast.MinTmp);
            forecast_layout.addView(view);
        }
        Intent intent = new Intent(this, AutoUpdateWeather.class);
        startService(intent);
    }
    private void LoadBackgroun(){
        String url = "http://guolin.tech/api/bing_pic";
        HttpUtil.SendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String path = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(MainActivity.this).load(path).into(bgimg);
                    }
                });
            }
        });
    }
}
