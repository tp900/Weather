package com.tp900.weather;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.print.PrinterId;
import android.support.annotation.BoolRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tp900.weather.db.City;
import com.tp900.weather.db.County;
import com.tp900.weather.db.Province;
import com.tp900.weather.util.HttpUtil;
import com.tp900.weather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChooseAreaFragment extends Fragment {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY=1;
    public static final int LEVEL_COUNTY=2;
    private ProgressDialog progressDialog;
    private TextView titleText;
    private Button  backButton;
    private ListView listView;
    private ArrayAdapter<String > arrayAdapter;
    private List<String> dataList = new ArrayList<>();
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;
    private Province selectedProvince;
    private City selectedCity;
    private int currentLevel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area,container,false);
        titleText = view.findViewById(R.id.title_text);
        backButton = view.findViewById(R.id.back_button);
        listView = view.findViewById(R.id.list_view);
        arrayAdapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(arrayAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (currentLevel){
                    case LEVEL_PROVINCE:
                        selectedProvince = provinceList.get(position);
                        QueryCities();
                        break;
                    case LEVEL_CITY:
                        selectedCity= cityList.get(position);
                        QueryCounties();
                        break;
                    case LEVEL_COUNTY:
                        //Intent intent = new Intent(getActivity(),MainActivity.class);
                        County county = countyList.get(position);
                        //intent.putExtra("weatherId",county.getWeatherId());
                        //startActivity(intent);
                        MainActivity activity =(MainActivity)getActivity();
                        activity.RequestWeather(county.getWeatherId());
                        DrawerLayout drawerLayout = activity.findViewById(R.id.drawerlayout);
                        drawerLayout.closeDrawers();
                        break;
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               switch (currentLevel){
                   case LEVEL_COUNTY:
                       QueryCities();
                       break;
                   case LEVEL_CITY:
                       QueryProvinces();
                       break;

               }
            }
        });
        QueryProvinces();
    }
    private void QueryProvinces(){
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        if(null!=provinceList&&provinceList.size()>0){
            dataList.clear();
            for (Province proviince:provinceList) {
                dataList.add(proviince.getProvinceName());
            }
            arrayAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        }else{
            //从网络加载省份列表
            String address="http://guolin.tech/api/china/";
            QueryFormServer(address,LEVEL_PROVINCE);
        }
    }
    private void QueryCities(){
        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceid = ?",String.valueOf(selectedProvince.getId())).find(City.class);
        if(null!=cityList&&cityList.size()>0){
            dataList.clear();
            for (City city:cityList) {
                dataList.add(city.getCityName());
            }
            arrayAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        }else{
            //从网络加载城市列表
            String address="http://guolin.tech/api/china/"+selectedProvince.getProvinceCode();
            QueryFormServer(address,LEVEL_CITY);
        }

    }
    private void QueryCounties(){
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityid = ?",String.valueOf(selectedCity.getId())).find(County.class);
        if(null!=countyList&&countyList.size()>0){
            dataList.clear();
            for(County county:countyList){
                dataList.add(county.getCountyName());
            }
            arrayAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        }else{
            //从网络加载县级城市
            String address="http://guolin.tech/api/china/"+selectedProvince.getProvinceCode()+"/"+selectedCity.getCityCode();
            QueryFormServer(address,LEVEL_COUNTY);
        }
    }
    //从网络加载省、市、县
    private void QueryFormServer(String address, final int leve){
        ShowProgressDialog();
        HttpUtil.SendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CloseProgressDialog();
                        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                Boolean result = false;
                switch (leve){
                    case LEVEL_PROVINCE:
                        result = Utility.HandleProvinceResponse(responseText);
                        break;
                    case LEVEL_CITY:
                        result = Utility.HandleCityResponse(responseText,selectedProvince.getId());
                        break;
                    case LEVEL_COUNTY:
                        result= Utility.HandleCountyRespone(responseText,selectedCity.getId());
                        break;
                }
                if(result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CloseProgressDialog();
                            switch (leve){
                                case LEVEL_PROVINCE:
                                   QueryProvinces();
                                    break;
                                case LEVEL_CITY:
                                   QueryCities();
                                    break;
                                case LEVEL_COUNTY:
                                   QueryCounties();
                                    break;
                            }
                        }
                    });
                }
            }
        });
    }
    private void ShowProgressDialog(){
        if(progressDialog==null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    private void CloseProgressDialog(){
        if(null!=progressDialog){
            progressDialog.dismiss();
        }
    }
}
