package com.ldt.tracklocationclient.activities;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
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
import com.ldt.tracklocationclient.services.GPS_Service;
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
        boolean hasInternet = Utils.checkInternet(this);
        if (!hasInternet) {

        }
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
                if(response==null){
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

    private void hideApp() {
        Intent testIntent = new Intent();
        testIntent.setAction(CUSTOM_INTENT);
        this.sendBroadcast(testIntent);
        PackageManager p = getPackageManager();
        ComponentName componentName = new ComponentName(this, LoginActivity.class);
        p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }

}
