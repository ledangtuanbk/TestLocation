package com.ldt.tracklocationclient.utilities;

import android.util.Log;

/**
 * Created by ldt on 9/10/17.
 */

public class AppLog {
    public static void d(String TAG, String msg) {
        Log.d(TAG, msg);
    }

    public static void e(String TAG, String msg) {
        Log.e(TAG, msg);
    }
}
