package com.ldt.tracklocationclient.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.ldt.tracklocationclient.R;
import com.ldt.tracklocationclient.controllers.UserController;
import com.ldt.tracklocationclient.entities.ResponseEntity;
import com.ldt.tracklocationclient.entities.UserLocationEntity;
import com.ldt.tracklocationclient.interfaces.IResponse;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ClientActivity extends AppCompatActivity {

    private FusedLocationProviderClient mFusedLocationClient;
    private static final String TAG = ClientActivity.class.getSimpleName();

    @BindView(R.id.etUserId)
    EditText etUserId;
    @BindView(R.id.etLatitude)
    EditText etLatitude;
    @BindView(R.id.etLongitude)
    EditText etLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        requestPermissions();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @OnClick(R.id.button)
    public void submit() {
        Log.d(TAG, "submit: ");
        getLocation();
    }

    private void requestPermissions() {
        Log.d(TAG, "requestPermissions: ");
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                Log.d(TAG, "requestPermissions: Should we show an explanation");

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.
                Log.d(TAG, "requestPermissions");
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_LOCATION);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    final int MY_PERMISSIONS_LOCATION = 1;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onRequestPermissionsResult: " + grantResults.length);
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    checkLocation();
                } else {
                    Log.d(TAG, "onRequestPermissionsResult: " + grantResults.length);
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void checkLocation() {
        final Context context = this;
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setMessage(context.getResources().getString(R.string.gps_network_not_enabled));
            dialog.setPositiveButton(context.getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(myIntent);
                }
            });
            dialog.setNegativeButton(context.getString(R.string.Cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                }
            });
            dialog.show();
        }
    }

    private void getLocation() {
        Log.d(TAG, "getLocation: ");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "getLocation: permission is not granted");
            return;
        }
        Log.d(TAG, "get last Location: ");
        mFusedLocationClient.getLastLocation().addOnCompleteListener(this, new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Log.d(TAG, "onComplete: ");
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: ");
            }
        }).addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                Log.d(TAG, "onSuccess: ");
                if (location != null)
                    insertUserLocation(location);
                else Log.d(TAG, "onSuccess: + location is null");
            }
        });

    }

    private void insertUserLocation(Location location) {

        UserLocationEntity userLocationEntity = new UserLocationEntity();
        userLocationEntity.setLatitude(userLocationEntity.getLatitude());
        userLocationEntity.setLongitude(userLocationEntity.getLongitude());
        userLocationEntity.setUserId(etUserId.getText().toString());

        UserController<UserLocationEntity> controller = new UserController<>();
        controller.createUserLocation(userLocationEntity, new IResponse<UserLocationEntity>() {
            @Override
            public void onResponse(ResponseEntity<UserLocationEntity> response) {
                UserLocationEntity result = response.getData();
                if(result!=null){
                    Log.d(TAG, "onResponse: insert success");
                }else{
                    Log.d(TAG, "insert not success");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d(TAG, "onFailure: ");
            }
        });
    }
}
