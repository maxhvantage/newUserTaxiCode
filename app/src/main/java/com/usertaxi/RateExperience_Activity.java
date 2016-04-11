package com.usertaxi;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.usertaxi.beans.Allbeans;
import com.usertaxi.service.DriverService;
import com.usertaxi.service.DrivermoveService;
import com.usertaxi.utils.AppConstants;
import com.usertaxi.utils.AppPreferences;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

/**
 * Created by Eyon on 11/9/2015.
 */
public class RateExperience_Activity extends AppCompatActivity {
    Button mbtn_panic, mbtn_rate, panicconfirm, mskipbtn;
    TextView textrate, textname, textmobilenumber, textcompanyname, texttaxinumber, mtextnamehead, mtextcompanyhead, mtextmobilehead, mtexttexinumhead, mtextdescription;
    RatingBar ratingBar;
    EditText meditcomment;
    float ratingbarvalue;
    String comment, panictaxirequest;
    Dialog dialog;
    RadioButton rd1, rd2, rd3;
    ImageButton cross;
    ImageView mdriverimage;
    public static String drivermobile;
    public static String drivername;
    public static String drivercompanyname = "";
    public static String drivertexinumber = "";
    String driverimage = "";
    public static String drivertaxiname = "";
    public static RateExperience_Activity instance = null;

    Typeface tf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rateexperience);
        AppPreferences.setApprequestTaxiScreen(RateExperience_Activity.this, false);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.BLACK);
        setSupportActionBar(toolbar);
        instance = this;

        DriverService.shouldContinue = false;
        DrivermoveService.shouldContinue = false;

        String title = getString(R.string.title_activity_rateexperience);
        getSupportActionBar().setTitle(title);
        mbtn_panic = (Button) findViewById(R.id.btn_panic);
        mbtn_rate = (Button) findViewById(R.id.button_rate);
        mskipbtn = (Button) findViewById(R.id.button_skip);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        meditcomment = (EditText) findViewById(R.id.editText);
        textname = (TextView) findViewById(R.id.name_text);
        textmobilenumber = (TextView) findViewById(R.id.mobile_text);
        textcompanyname = (TextView) findViewById(R.id.companyname);
        texttaxinumber = (TextView) findViewById(R.id.taxinumber);
        mtextnamehead = (TextView) findViewById(R.id.namehead);
        mtextcompanyhead = (TextView) findViewById(R.id.companyhead);
        mtextmobilehead = (TextView) findViewById(R.id.mobilehead);
        mtexttexinumhead = (TextView) findViewById(R.id.taxiplatthead);
        mtextdescription = (TextView) findViewById(R.id.textcontent);
        textrate = (TextView) findViewById(R.id.ratetext);
        mdriverimage = (ImageView) findViewById(R.id.driver_image);

        Drawable stars = ratingBar.getProgressDrawable();
        DrawableCompat.setTint(stars, getResources().getColor(R.color.colorbutton));
        tf = Typeface.createFromAsset(this.getAssets(),
                "Montserrat-Regular.ttf");
        mbtn_panic.setTypeface(tf);
        mbtn_rate.setTypeface(tf);
        meditcomment.setTypeface(tf);
        textname.setTypeface(tf);
        textmobilenumber.setTypeface(tf);
        textcompanyname.setTypeface(tf);
        texttaxinumber.setTypeface(tf);
        mtextnamehead.setTypeface(tf);
        mtextcompanyhead.setTypeface(tf);
        mtextmobilehead.setTypeface(tf);
        mtexttexinumhead.setTypeface(tf);
        mtextdescription.setTypeface(tf);
        mskipbtn.setTypeface(tf);
        textrate.setTypeface(tf);


        Intent intent = getIntent();

        drivermobile = intent.getStringExtra("driver_mobile");
        drivername = intent.getStringExtra("driver_name");
        drivercompanyname = intent.getStringExtra("driver_companyname");
        drivertexinumber = intent.getStringExtra("driver_taxinumber");
        driverimage = intent.getStringExtra("driver_image");

        textname.setText(drivername);
        textmobilenumber.setText(drivermobile);
        textcompanyname.setText(drivercompanyname);
        texttaxinumber.setText(drivertexinumber);

        try {
            if (driverimage == null || driverimage.equalsIgnoreCase("")) {
                mdriverimage.setImageResource(R.drawable.ic_action_user);

            } else {
                Picasso.with(getApplicationContext()).load(driverimage)
                        .error(R.drawable.ic_action_user)
                        .resize(200, 200)
                        .into(mdriverimage);
            }
        }catch(NullPointerException e){
            e.printStackTrace();
        }



        String data = intent.getStringExtra("data");
        Log.d("datavalue", data + "");
        if (data != null) {

            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(data);
                drivermobile = jsonObject.getString("mobile");
                drivername = jsonObject.getString("username");
                drivercompanyname = jsonObject.getString("taxicompany");
                drivertaxiname = jsonObject.getString("vehicalname");
                drivertexinumber = jsonObject.getString("vehicle_number");
                driverimage = jsonObject.getString("driverImage");

                textname.setText(drivername);
                textmobilenumber.setText(drivermobile);
                textcompanyname.setText(drivercompanyname);
                texttaxinumber.setText(drivertexinumber);
                Picasso.with(getApplicationContext()).load(driverimage)
                        .error(R.drawable.ic_action_user)
                        .resize(200, 200)
                        .into(mdriverimage);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mbtn_panic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiatePopupWindowpanictaxi();

            }
        });
        mskipbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeintent = new Intent(RateExperience_Activity.this, DrawerMainActivity.class);
                startActivity(homeintent);
                finish();

            }
        });
        mbtn_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                meditcomment.setError(null);
                comment = meditcomment.getText().toString();
                ratingbarvalue = ratingBar.getRating();

                Allbeans allbeans = new Allbeans();
                allbeans.setComment(comment);
                allbeans.setRatingvalue(ratingbarvalue);
                new CommentTripAsynch(allbeans).execute();


            }
        });
    }


    private class CommentTripAsynch extends AsyncTask<Void, Void, String> {
        private ProgressDialog mProgressDialog;
        Allbeans allbeans;
        private JSONObject jsonObj;
        ArrayList<Integer> catogariesid;
        private int status = 0;

        public CommentTripAsynch(Allbeans allbeans) {
            this.allbeans = allbeans;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(RateExperience_Activity.this);
            mProgressDialog.setMessage("Please wait...");
            mProgressDialog.show();

        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                HttpParams httpParameters = new BasicHttpParams();
                ConnManagerParams.setTimeout(httpParameters,
                        AppConstants.NETWORK_TIMEOUT_CONSTANT);
                HttpConnectionParams.setConnectionTimeout(httpParameters,
                        AppConstants.NETWORK_CONNECTION_TIMEOUT_CONSTANT);
                HttpConnectionParams.setSoTimeout(httpParameters,
                        AppConstants.NETWORK_SOCKET_TIMEOUT_CONSTANT);

                HttpClient httpclient = new DefaultHttpClient();
                System.out.println("feedback value : ------------ "
                        + AppConstants.FEEDBACKTRIP);
                HttpPost httppost = new HttpPost(AppConstants.FEEDBACKTRIP);
                jsonObj = new JSONObject();
                jsonObj.put("customerComment", allbeans.getComment());
                jsonObj.put("customerRating", allbeans.getRatingvalue());
                jsonObj.put("trip_id", AppPreferences.getTripId(RateExperience_Activity.this));
                jsonObj.put("feedback_latitude", AppPreferences.getLatutude(RateExperience_Activity.this));
                jsonObj.put("feedback_longitude", AppPreferences.getLongitude(RateExperience_Activity.this));
                jsonObj.put("custmer_id", AppPreferences.getCustomerId(RateExperience_Activity.this));
                jsonObj.put("account_type", "7");
                JSONArray jsonArray = new JSONArray();
                jsonArray.put(jsonObj);

                Log.d("json value:---------", jsonArray.toString());


                StringEntity se = null;

                try {
                    se = new StringEntity(jsonArray.toString(),"UTF-8");

//                    se.setContentEncoding(new BasicHeader(
//                            HTTP.CONTENT_ENCODING, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Log.v("json : ", jsonArray.toString(2));
                System.out.println("Sent JSON is : " + jsonArray.toString());
                httppost.setEntity(se);
                HttpResponse response = null;

                response = httpclient.execute(httppost);

                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new InputStreamReader(response
                            .getEntity().getContent(), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String jsonString = "";
                try {
                    jsonString = reader.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("JSONString response is : " + jsonString);
                if (jsonString != null) {
                    if (jsonString.contains("result")) {
                        JSONObject jsonObj = new JSONObject(jsonString);
                        jsonString = jsonObj.getString("result");
                        // JSONArray jsonChildArray = jsonObj
                        // .getJSONArray("result");
                        if (jsonObj.getString("status").equalsIgnoreCase("200")) {
                            System.out
                                    .println("--------- message 200 got ----------");
                            status = 200;
                            return jsonString;
                        } else if (jsonObj.getString("status")
                                .equalsIgnoreCase("404")) {
                            status = 404;
                            return jsonString;
                        } else if (jsonObj.getString("status")
                                .equalsIgnoreCase("500")) {
                            status = 500;
                            return jsonString;
                        }
                    }
                }

            } catch (ConnectTimeoutException e) {
                System.out.println("Time out");
                status = 600;
            } catch (SocketTimeoutException e) {
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mProgressDialog.dismiss();
            Log.d("status", status + "");
            if (status == 200) {
                Intent intent = new Intent(RateExperience_Activity.this, DrawerMainActivity.class);
                startActivity(intent);
                finish();

            } else {
                Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.network_error), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        }

    }

    private View.OnClickListener cancle_btn_click_listener = new View.OnClickListener() {
        public void onClick(View v) {
            dialog.dismiss();
        }
    };

    private void initiatePopupWindowpanictaxi() {
        try {
            dialog = new Dialog(RateExperience_Activity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
            dialog.setContentView(R.layout.panic_popup);
            cross = (ImageButton) dialog.findViewById(R.id.cross);
            cross.setOnClickListener(cancle_btn_click_listener);
            rd1 = (RadioButton) dialog.findViewById(R.id.radioButton1);
            rd2 = (RadioButton) dialog.findViewById(R.id.radioButton2);
            rd3 = (RadioButton) dialog.findViewById(R.id.radioButton3);
            panicconfirm = (Button) dialog.findViewById(R.id.btn_savechange);
            TextView txt = (TextView) dialog.findViewById(R.id.textView);
            TextView textheader = (TextView) dialog.findViewById(R.id.popup_text);
            Typeface tf = Typeface.createFromAsset(this.getAssets(),
                    "Montserrat-Regular.ttf");
            rd1.setTypeface(tf);
            rd2.setTypeface(tf);
            rd3.setTypeface(tf);
            panicconfirm.setTypeface(tf);
            txt.setTypeface(tf);
            textheader.setTypeface(tf);
            panicconfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (rd1.isChecked()) {
                        panictaxirequest = getResources().getString(R.string.prompt_rate_exp_one);

                    } else if (rd2.isChecked()) {
                        panictaxirequest = getResources().getString(R.string.prompt_rate_exp_two);

                    } else if (rd3.isChecked()) {
                        panictaxirequest = getResources().getString(R.string.prompt_rate_exp_three);

                    }


                    Allbeans allbeans = new Allbeans();

                    allbeans.setPanictaxirequest(panictaxirequest);

                    new PanicTaxiAsynch(allbeans).execute();


                }
            });
            dialog.show();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()) {

            case R.id.radioButton1:
                if (checked)

                    break;
            case R.id.radioButton2:
                if (checked)
                    break;

            case R.id.radioButton3:
                if (checked)
                    break;
        }
    }

    private class PanicTaxiAsynch extends AsyncTask<Void, Void, String> {
        private ProgressDialog mProgressDialog;
        Allbeans allbeans;
        private JSONObject jsonObj;
        private int status = 0;

        public PanicTaxiAsynch(Allbeans allbeans) {
            this.allbeans = allbeans;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(RateExperience_Activity.this);
            mProgressDialog.setMessage("Please wait...");
            mProgressDialog.show();

        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                HttpParams httpParameters = new BasicHttpParams();
                ConnManagerParams.setTimeout(httpParameters,
                        AppConstants.NETWORK_TIMEOUT_CONSTANT);
                HttpConnectionParams.setConnectionTimeout(httpParameters,
                        AppConstants.NETWORK_CONNECTION_TIMEOUT_CONSTANT);
                HttpConnectionParams.setSoTimeout(httpParameters,
                        AppConstants.NETWORK_SOCKET_TIMEOUT_CONSTANT);

                HttpClient httpclient = new DefaultHttpClient();
                System.out.println("PANIC Taxi Value : -------------"
                        + AppConstants.PANIC);
                HttpPost httppost = new HttpPost(AppConstants.PANIC);
                jsonObj = new JSONObject();
                jsonObj.put("uid", AppPreferences.getCustomerId(RateExperience_Activity.this));
                jsonObj.put("trip_id", AppPreferences.getTripId(RateExperience_Activity.this));
                jsonObj.put("panictaxirequest", allbeans.getPanictaxirequest());
                jsonObj.put("account_type", "7");


                JSONArray jsonArray = new JSONArray();
                jsonArray.put(jsonObj);

                Log.d("json value:---------", jsonArray.toString());


                StringEntity se = null;

                try {
                    se = new StringEntity(jsonArray.toString(),"UTF-8");


                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Log.v("json : ", jsonArray.toString(2));
                System.out.println("Sent JSON is : " + jsonArray.toString());
                httppost.setEntity(se);
                HttpResponse response = null;

                response = httpclient.execute(httppost);

                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new InputStreamReader(response
                            .getEntity().getContent(), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String jsonString = "";
                try {
                    jsonString = reader.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("JSONString response is : " + jsonString);
                if (jsonString != null) {
                    if (jsonString.contains("result")) {
                        JSONObject jsonObj = new JSONObject(jsonString);
                        jsonString = jsonObj.getString("result");
                        // JSONArray jsonChildArray = jsonObj
                        // .getJSONArray("result");
                        if (jsonObj.getString("status").equalsIgnoreCase("200")) {
                            System.out
                                    .println("--------- message 200 got ----------");
                            status = 200;
                            return jsonString;
                        } else if (jsonObj.getString("status")
                                .equalsIgnoreCase("404")) {
                            status = 404;
                            return jsonString;
                        } else if (jsonObj.getString("status")
                                .equalsIgnoreCase("500")) {
                            status = 500;
                            return jsonString;
                        }
                    }
                }

            } catch (ConnectTimeoutException e) {
                System.out.println("Time out");
                status = 600;
            } catch (SocketTimeoutException e) {
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mProgressDialog.dismiss();
            Log.d("status", status + "");
            if (status == 200) {
              //  Toast.makeText(getApplicationContext(), "Your Panic request sent successfully", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            } else {
                Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.network_error), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        }

    }

    @Override
    public void onBackPressed() {

    }
}
