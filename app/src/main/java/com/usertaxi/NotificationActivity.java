package com.usertaxi;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Eyon-dev on 12/1/2015.
 */
public class NotificationActivity extends AppCompatActivity {
    TextView notificationtext;
    ImageView imagenotification;
    String NOTIFICATION_TEXT,NOTIFICATION_IMAGE;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        String title = getString(R.string.title_activity_notification);
        toolbar.setTitleTextColor(Color.BLACK);
        getSupportActionBar().setTitle(title);


        notificationtext=(TextView)findViewById(R.id.text_notification);
        imagenotification=(ImageView) findViewById(R.id.image_notification);
        Typeface tf=Typeface.createFromAsset(NotificationActivity.this.getAssets(),"Montserrat-Regular.ttf");
        notificationtext.setTypeface(tf);
        Intent intent = getIntent();
        String data = intent.getStringExtra("data");
        Log.d("datavalue", data + "");
        if (data != null) {

            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(data);
                NOTIFICATION_TEXT= jsonObject.getString("adminmessage");
                NOTIFICATION_IMAGE= jsonObject.getString("image");


            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        notificationtext.setText(NOTIFICATION_TEXT);
        Picasso.with(getApplicationContext()).load(NOTIFICATION_IMAGE)
                .error(R.drawable.ic_action_user)
                .resize(300, 300)
                .into(imagenotification);

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.request, menu);
//        return true;
//    }
//
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == android.R.id.home) {
//            finish();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        finish();
//    }
}
