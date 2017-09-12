package com.ldt.tracklocationclient.activities;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ldt.tracklocationclient.R;
import com.ldt.tracklocationclient.controllers.UserController;
import com.ldt.tracklocationclient.entities.ResponseEntity;
import com.ldt.tracklocationclient.entities.UserLocationEntity;
import com.ldt.tracklocationclient.helper.DirectionsJSONParser;
import com.ldt.tracklocationclient.interfaces.IResponse;
import com.ldt.tracklocationclient.interfaces.UserService;
import com.ldt.tracklocationclient.utilities.DateHelper;
import com.ldt.tracklocationclient.utilities.DateTimeFormat;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
                            if (results.size() < 2) return;

                            for (int index = 0;index<results.size();index++){
                                UserLocationEntity entity = results.get(index);
                                LatLng point = new LatLng(entity.getLatitude(), entity.getLongitude());
                                MarkerOptions markerOptions =new MarkerOptions().position(point)
                                        .title(DateHelper.dateToString(results.get(index).getTime(), DateTimeFormat.DateTime));
                                if(index==0){
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                }else  if(index==results.size()-1){
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                }else{
                                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                                }
                                googleMap.addMarker(markerOptions);
                            }

//
//                            UserLocationEntity loc = results.get(0);
//                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(loc.getLatitude(), loc.getLongitude()), 12.0f));

                            String url = getDirectionsUrl(results);


                            LocationViewer.DownloadTask downloadTask = new LocationViewer.DownloadTask();

                            // Start downloading json data from Google Directions API
                            downloadTask.execute(url);

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

    private String getDirectionsUrl(List<UserLocationEntity> positions) {


        UserLocationEntity orgin = positions.get(0);
        UserLocationEntity dest = positions.get(positions.size() - 1);
        // Origin of route
        String str_origin = "origin=" + orgin.getLatitude() + "," + orgin.getLongitude();

        // Destination of route
        String str_dest = "destination=" + dest.getLatitude() + "," + dest.getLongitude();


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // waypoints is smaller than 23 point
        int step = positions.size()/20 + 1;
        if (positions.size() >= 3) {
            UserLocationEntity firstPoint = positions.get(1);
            parameters += "&waypoints=via:" + firstPoint.getLatitude() + "," + firstPoint.getLongitude();
        }
        for (int i = 2; i < positions.size() - 1; i+=step) {
            UserLocationEntity temp = positions.get(i);
            parameters += "|via:" + temp.getLatitude() + "," + temp.getLongitude();
        }
        parameters += "&key=AIzaSyA1dsxB7wWpIQybWe7cT7VEXw7_pyD9SyE";
        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        Log.d(TAG, "getDirectionsUrl: " + url);
        return url;
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d(TAG, "doInBackground: data" + data);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            LocationViewer.ParserTask parserTask = new LocationViewer.ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d(TAG, e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(2);
                lineOptions.color(Color.RED);

            }
            if (lineOptions != null)
                // Drawing polyline in the Google Map for the i-th route
                googleMap.addPolyline(lineOptions);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(20.9897858, 105.7893306), 16.0f));
        }
    }
}
