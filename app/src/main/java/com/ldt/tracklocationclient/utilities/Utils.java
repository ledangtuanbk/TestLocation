package com.ldt.tracklocationclient.utilities;

import android.content.Context;
import android.os.AsyncTask;
import android.text.format.DateFormat;
import android.util.Log;

import com.google.gson.Gson;
import com.ldt.tracklocationclient.interfaces.InternetResult;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by ldt on 1/22/17.
 */

public class Utils {
    private static final String TAG = Utils.class.getSimpleName();
    /**
     * @return
     * @author: tuanld
     */
    public static void checkInternet(final InternetResult internetResult) {
        try {
            new AsyncTask<Void, Void, Boolean>() {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected Boolean doInBackground(Void... voids) {
                    InetAddress ipAddr = null; //You can replace it with your name
                    try {
                        ipAddr = InetAddress.getByName("google.com");
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    if (ipAddr != null) {
                        return !ipAddr.equals("");
                    } else return false;
                }

                @Override
                protected void onPostExecute(Boolean isInternet) {
                    super.onPostExecute(isInternet);

                    AppLog.d(TAG, "onPostExecute isInternet: " + isInternet);
                    internetResult.result(isInternet);
                }
            }.execute();

        } catch (Exception e) {
            e.printStackTrace();
            internetResult.result(false);

        }

    }

    public  static String toStringJson(Object obj){
        return new Gson().toJson(obj);
    }




}
