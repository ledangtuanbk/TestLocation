package com.ldt.tracklocationclient.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.ldt.tracklocationclient.R;
import com.ldt.tracklocationclient.controllers.UserController;
import com.ldt.tracklocationclient.entities.ResponseEntity;
import com.ldt.tracklocationclient.entities.TestUserEntity;
import com.ldt.tracklocationclient.interfaces.IResponse;
import com.ldt.tracklocationclient.interfaces.InternetResult;
import com.ldt.tracklocationclient.services.GPS_Service;
import com.ldt.tracklocationclient.utilities.AppLog;
import com.ldt.tracklocationclient.utilities.SharedPrefUtils;
import com.ldt.tracklocationclient.utilities.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    @BindView(R.id.etUserId)
    EditText etUserId;
    public static final String CUSTOM_INTENT = "jason.wei.custom.intent.action.TEST";
    private BroadcastReceiver broadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        Utils.checkInternet(new InternetResult() {
            @Override
            public void result(boolean hasInternet) {
                Log.d(TAG, "result: " + hasInternet);
                if (!hasInternet) {
                    processNoInternet();
                }else {
                    checkPermissions();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    Log.d(TAG, "\n" + intent.getExtras().get("coordinates"));

                }
            };
        }
        registerReceiver(broadcastReceiver, new IntentFilter("location_update"));
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
    }

    @OnClick(R.id.btnLogin)
    public void login() {
        Log.d(TAG, "login: login success");
        String userId = etUserId.getText().toString();
        UserController controller = new UserController();

        controller.loginOrRegister(userId, new IResponse<TestUserEntity>() {
            @Override
            public void onResponse(ResponseEntity<TestUserEntity> response) {
                if (response == null) {
                    return;
                }
                TestUserEntity data = response.getData();
                if (data != null) {
                    Log.d(TAG, "onResponse: login thanh cong");
                    Toast.makeText(LoginActivity.this, "Login thanh cong, thoat app sau 1s", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "run: start service");
                            SharedPrefUtils.saveString(getApplicationContext(), getString(R.string.userId), etUserId.getText().toString());
                            Intent i = new Intent(getApplicationContext(), GPS_Service.class);
                            startService(i);
                            LoginActivity.this.finish();
                            System.exit(0);
                        }
                    }, 1000);
                } else {
                    Log.d(TAG, "onResponse: login khong thanh cong");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d(TAG, "onFailure: login error");
            }
        });
    }


    /**
     * Show dialog when don't have internet connection
     */
    private void processNoInternet() {
        AppLog.d(TAG, "processNoInternet: ");
        new AlertDialog.Builder(this).setTitle(R.string.information)
                .setMessage(R.string.no_internet)
                .setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                    }
                })
                .show();
    }


    private void checkPermissions() {
        Log.d(TAG, "checkPermissions: ");

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.d(TAG, "checkPermissions: Should we show an explanation");
                new AlertDialog.Builder(this).setTitle(R.string.information)
                        .setMessage("May khong the chay ung dung neu nhu khong cho phep permission nay, cho phep?")
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Log.d(TAG, "onClick: ");
                                requestPermissions();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
            } else {
                Log.d(TAG, "checkPermissions: requestPermissions");
                requestPermissions();
            }
        }
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                MY_PERMISSIONS_LOCATION);
    }

    final int MY_PERMISSIONS_LOCATION = 1;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_LOCATION: {
                Log.d(TAG, "onRequestPermissionsResult: " + grantResults.length);
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onRequestPermissionsResult: " + grantResults.length);
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

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
}
