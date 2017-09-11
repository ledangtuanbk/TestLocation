package com.ldt.tracklocationclient.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ldt.tracklocationclient.R;
import com.ldt.tracklocationclient.controllers.UserController;
import com.ldt.tracklocationclient.entities.ResponseEntity;
import com.ldt.tracklocationclient.entities.UserLocationEntity;
import com.ldt.tracklocationclient.interfaces.IResponse;
import com.ldt.tracklocationclient.interfaces.UserService;
import com.ldt.tracklocationclient.utilities.DateHelper;
import com.ldt.tracklocationclient.utilities.DateTimeFormat;

import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LocationViewer extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = LocationViewer.class.getSimpleName();
    private GoogleMap googleMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_viewer);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        this.googleMap = googleMap;
        getLocations();

    }

    private void getLocations() {

        String userId = getIntent().getStringExtra(getResources().getString(R.string.userId));
        long startTime = getIntent().getLongExtra(getResources().getString(R.string.startTime), 0);
        long endTime = getIntent().getLongExtra(getResources().getString(R.string.endTime), Long.MAX_VALUE);
        Log.d(TAG, "getLocations() called" + userId);
        Log.d(TAG, "getLocations() called" + startTime);
        Log.d(TAG, "getLocations() called" + endTime);
        UserController<List<UserLocationEntity>> controller = new UserController<>();
        controller.getUserLocation(userId, startTime, endTime,
                new IResponse<List<UserLocationEntity>>() {
                    @Override
                    public void onResponse(ResponseEntity<List<UserLocationEntity>> response) {
                        List<UserLocationEntity> results = response.getData();
                        if (results != null) {
                            Log.d(TAG, "onResponse: " + results.size());
                            if (results.size() == 0) return;
                            for (UserLocationEntity entity : results) {
                                LatLng sydney = new LatLng(entity.getLatitude(), entity.getLongitude());
                                googleMap.addMarker(new MarkerOptions().position(sydney)
                                        .title(DateHelper.dateToString(entity.getTime(), DateTimeFormat.DateTime)));
                            }

                            UserLocationEntity loc = results.get(0);
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(loc.getLatitude(), loc.getLongitude()), 12.0f));
                        } else {
                            Log.d(TAG, "onResponse: null");
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Log.e(TAG, "onFailure: " + t.getMessage());
                    }
                });
    }
}
