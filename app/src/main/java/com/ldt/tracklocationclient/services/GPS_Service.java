package com.ldt.tracklocationclient.services;

/**
 * Created by ldt on 9/8/2017.
 */

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;

import android.support.v4.app.NotificationCompat;
import android.support.v4.math.MathUtils;
import android.util.Log;
import android.widget.Toast;

import com.ldt.tracklocationclient.R;
import com.ldt.tracklocationclient.controllers.UserController;
import com.ldt.tracklocationclient.entities.ResponseEntity;
import com.ldt.tracklocationclient.entities.UserLocationEntity;
import com.ldt.tracklocationclient.interfaces.IResponse;
import com.ldt.tracklocationclient.utilities.SharedPrefUtils;
import com.ldt.tracklocationclient.utilities.Utils;

/**
 * Created by filipp on 6/16/2016.
 */
public class GPS_Service extends Service {

    private static final String TAG = GPS_Service.class.getSimpleName();
    private LocationListener listener;
    private LocationManager locationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private long time = 0;
    private Location lastLocation = null;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: ");
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, "onLocationChanged: ");
                if (!checkCondition(location)) {
                    Log.d(TAG, "onLocationChanged: not valid");
                    return;
                }
                time = System.currentTimeMillis();
                lastLocation = location;
                UserLocationEntity userLocationEntity = new UserLocationEntity();
                userLocationEntity.setLatitude(location.getLatitude());
                userLocationEntity.setLongitude(location.getLongitude());
                userLocationEntity.setUserId(SharedPrefUtils.loadString(getApplicationContext(), getString(R.string.userId), ""));

                UserController<UserLocationEntity> controller = new UserController<>();
                controller.createUserLocation(userLocationEntity, new IResponse<UserLocationEntity>() {
                    @Override
                    public void onResponse(ResponseEntity<UserLocationEntity> response) {
                        if (response != null) {
                            UserLocationEntity result = response.getData();
                            if (result != null) {
                                Log.d(TAG, "onResponse: insert success");
                            } else {
                                Log.d(TAG, "insert not success");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.d(TAG, "onFailure: ");
                    }
                });

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Log.d(TAG, "onProviderDisabled: ");
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        };

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        //noinspection MissingPermission
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, listener);

    }

    private boolean checkCondition(Location location) {
        if (lastLocation == null || time == 0) return true;

        long newTime = System.currentTimeMillis();
        Log.d(TAG, "onLocationChanged: " + (newTime - time));
        if (newTime - time < 30 * 1000) return false;
        double d = Math.abs(location.getLatitude() - lastLocation.getLatitude()) + Math.abs(location.getLongitude() - lastLocation.getLongitude());
        if (d < 0.00001) {
            Log.d(TAG, "checkCondition d: " + d);
            return false;
        }

        return true;
    }

    private void showNotify() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");
        int mNotificationId = 001;
// Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
// Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
        if (locationManager != null) {
            //noinspection MissingPermission
            locationManager.removeUpdates(listener);
        }
    }
}
