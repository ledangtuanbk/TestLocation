package com.ldt.tracklocationclient.utilities;

import android.content.Context;
import android.util.Log;

/**
 * Created by ldt on 1/22/17.
 */

public class Utils {
    private static final String TAG = Utils.class.getSimpleName();

    public static boolean checkInternet(Context context) {
        ServiceManager serviceManager = new ServiceManager(context);
        if (serviceManager.isNetworkAvailable()) {
            Log.d(TAG, "checkInternet: true");
            return true;
        } else {
            Log.d(TAG, "checkInternet: false");
            return false;
        }
    }
}
