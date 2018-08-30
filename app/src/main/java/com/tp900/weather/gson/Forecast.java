package com.tp900.weather.gson;

import com.google.gson.annotations.SerializedName;

public class Forecast {
    @SerializedName("cond_txt_d")
    public String info;
    @SerializedName("date")
    public String UDate;
    @SerializedName("tmp_max")
    public String MaxTmp;
    @SerializedName("tmp_min")
    public String MinTmp;
}
