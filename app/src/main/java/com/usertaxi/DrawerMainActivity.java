package com.usertaxi;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.usertaxi.classes.FragmentDrawer;


/**
 * Created by Eyon on 11/9/2015.
 */
public class DrawerMainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {
    Toolbar toolbar;
    private FragmentDrawer drawerFragment;
    public static DrawerMainActivity mainActivityInstance = null;
    Dialog dialog;
    public static boolean drawerActivity = false;

    @Override
    protected void onStop() {
        drawerActivity = false;
        Log.d("activityTest", "drawer");
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
      drawerActivity = true;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newdrawer_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar); // Attaching the layout
        setSupportActionBar(toolbar); // Setting toolbar as the ActionBar with
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String title = getString(R.string.title_activity_home);
        toolbar.setTitleTextColor(Color.BLACK);
        getSupportActionBar().setTitle(title);
        //initiatePopupWindowemailveryfy();
//        if (AppPreferences.getVerifyemail(getApplicationContext())) {
//            initiatePopupWindowemailveryfy();
//            AppPreferences.setVerifyemail(getApplicationContext(), false);
//        }
        Typeface tf = Typeface.createFromAsset(DrawerMainActivity.this.getAssets(),
                "Montserrat-Regular.ttf");

        String data = getIntent().getStringExtra("notificationData");
        if (data != null) {
            caceldialog(data);

        }
        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
        drawerFragment.setDrawerListener(this);
        mainActivityInstance = this;

        Fragment fragment = new MainFragment();
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container_body, fragment).commit();

        }



    }


    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {
        Fragment fragment = null;

        String title = getString(R.string.title_activity_home);

        switch (position) {

            case 0:
                //  fragment = new MainFragment();
                break;

            case 1:
                Intent intent2 = new Intent(DrawerMainActivity.this, UserProfileAcivity.class);
                startActivity(intent2);
                break;
            case 2:
                Intent intent3 = new Intent(DrawerMainActivity.this, TripHistory_Activity.class);
                startActivity(intent3);

                break;


        }
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();
            // set the toolbar title
            getSupportActionBar().setTitle(title);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainActivityInstance = null;
    }


    public void caceldialog(String data) {

        AlertDialog.Builder builder = new AlertDialog.Builder(DrawerMainActivity.this);
//builder.setTitle(getString(R.string.cancel_trip));
//builder.setIcon(R.drawable.ic_launcher);
        LayoutInflater inflater = getLayoutInflater();
        View header = inflater.inflate(R.layout.dialog_heading, null);
        TextView textView = (TextView) header.findViewById(R.id.text);
        ImageView icon = (ImageView) header.findViewById(R.id.icon);
        icon.setImageResource(R.drawable.ic_launcher);
        textView.setText("Cancel Trip");
        builder.setCustomTitle(header);
        builder.setCancelable(false);

            builder.setMessage(data);


        builder.setPositiveButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
//                        AppPreferences.setTripId(DrawerMainActivity.this, "");
//                        AppPreferences.setDriverId(DrawerMainActivity.this, "");
                        dialog.dismiss();
                    }
                });

        builder.show();
    }

    @Override
    public void onBackPressed() {

        finish();
    }
}

