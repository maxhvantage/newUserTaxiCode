package com.usertaxi;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.PopupWindow;

import com.bugsnag.android.Bugsnag;
import com.usertaxi.classes.GPSTracker;
import com.usertaxi.utils.AppPreferences;
import com.usertaxi.utils.FusedLocationService;
import android.Manifest;
public class SplashScreenActivity extends AppCompatActivity {
    Button ok_btn;
    Button cancle_btn;
    PopupWindow pwindo;
     GPSTracker gps;
    private static int SPLASH_TIME_OUT = 3000;
    public static SplashScreenActivity instance = null;
    int count = 0;
    double lat = 0.0, lon = 0.0;
    //FusedLocationService fusedLocationService;
    private static final String[] LOCATION_PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        instance = this;
        //fusedLocationService = new FusedLocationService(this);
        gps = new GPSTracker(SplashScreenActivity.this);
        gps.getLocation();
        if(Build.VERSION.SDK_INT >= 23) {
            requestPermissions(LOCATION_PERMS, 0);
        }
        final Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {

                    if (gps.getLatitude() == 0.0 && gps.getLongitude() == 0.0) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                showSettingsAlert();
                            }
                        });

                    } else {
                        if (AppPreferences.getCustomerId(SplashScreenActivity.this) == 0) {
                            Intent intent = new Intent(SplashScreenActivity.this, RegisterActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            if (AppPreferences.getApprequestTaxiScreen(getApplicationContext())) {
                                Intent intent = new Intent(SplashScreenActivity.this, ShowTaxi_InMap.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Intent intent = new Intent(SplashScreenActivity.this, DrawerMainActivity.class);
                                startActivity(intent);
                               finish();
                            }

                        }
                    }
                }
            }

        };
        timerThread.start();

        Bugsnag.init(this);

    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (canAccessLocation()) {
            gps = new GPSTracker(SplashScreenActivity.this);
            gps.getLocation();
            Log.d("checkLocation1111", gps.getLatitude() + " : " + gps.getLongitude());
        }
    }
    private boolean canAccessLocation() {
        return(hasPermission(Manifest.permission.ACCESS_FINE_LOCATION));
    }
    @TargetApi(Build.VERSION_CODES.M)
    private boolean hasPermission(String perm) {
        return(PackageManager.PERMISSION_GRANTED==checkSelfPermission(perm));
    }




    public void showSettingsAlert() {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this);
        alertDialog.setTitle(getResources().getString(R.string.gps_settingg));
        alertDialog.setMessage(getResources().getString(R.string.gps_alert));

        alertDialog.setPositiveButton(getResources().getString(R.string.gps_setting), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                dialog.cancel();
            }
        });
        alertDialog.setNegativeButton(getResources().getString(R.string.prompt_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }
}

