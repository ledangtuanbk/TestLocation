package com.ldt.tracklocationclient.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.ldt.tracklocationclient.R;
import com.ldt.tracklocationclient.utilities.Utils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class WifiConnect extends AppCompatActivity {

    private static final String TAG = WifiConnect.class.getSimpleName();
    private WifiManager mWifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_connect);
        ButterKnife.bind(this);

//        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        registerReceiver(mWifiScanReceiver,
//                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
//        mWifiManager.startScan();
    }

    @OnClick(R.id.btnConnect)
    public void connect() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(TAG, "connect: ");
                    String networkSSID = "ISORA_DEV01";
                    String networkPass = "isora@1231";

                    WifiConfiguration conf = new WifiConfiguration();
                    conf.SSID = "\"" + networkSSID + "\"";   // Please note the quotes. String should contain ssid in quotes

                    conf.preSharedKey = "\"" + networkPass + "\"";

                    WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    wifiManager.addNetwork(conf);

                    List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
                    Log.d(TAG, "connect: " + list.size());
                    for (WifiConfiguration i : list) {
                        Log.d(TAG, "connect: " + Utils.toStringJson(i));
                        if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                            wifiManager.disconnect();
                            wifiManager.enableNetwork(i.networkId, true);
                            wifiManager.reconnect();

                            break;
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

    }

    private final BroadcastReceiver mWifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {

            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                List<ScanResult> mScanResults = mWifiManager.getScanResults();
                Log.d(TAG, "onReceive: " + mScanResults.size());
                for (ScanResult sc : mScanResults) {
                    Log.d(TAG, "onReceive: " + new Gson().toJson(sc));
                }
            }
        }
    };

}
