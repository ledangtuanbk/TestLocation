package com.ldt.tracklocationclient.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by ldt on 9/8/2017.
 */

public class SharedPrefUtils {

    public  static void saveString(Context context, String key,  String value){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public  static String loadString(Context context, String key,  String defaultValue){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String value = sharedPref.getString(key, defaultValue);
        return value;
    }
}
