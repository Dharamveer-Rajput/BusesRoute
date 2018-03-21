package com.busesroute.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by dharamveer on 21/3/18.
 */

public class SharedPrefer {

    Context context;
     SharedPreferences sharedPreferences;

    String PRE_LAT;
    String PRE_LNG;


    public SharedPrefer(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences("lat", Context.MODE_PRIVATE);;

    }

    public String getPRE_LAT() {
        PRE_LAT = sharedPreferences.getString("PRE_LAT",PRE_LAT);

        return PRE_LAT;
    }

    public void setPRE_LAT(String PRE_LAT) {
        this.PRE_LAT = PRE_LAT;
        sharedPreferences.edit().putString("PRE_LAT",PRE_LAT).commit();

    }

    public String getPRE_LNG() {
        PRE_LNG = sharedPreferences.getString("PRE_LNG",PRE_LNG);

        return PRE_LNG;
    }

    public void setPRE_LNG(String PRE_LNG) {
        this.PRE_LNG = PRE_LNG;
        sharedPreferences.edit().putString("PRE_LNG",PRE_LNG).commit();

    }
}
