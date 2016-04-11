package com.usertaxi;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.squareup.picasso.Picasso;
import com.usertaxi.beans.Allbeans;
import com.usertaxi.service.DriverService;
import com.usertaxi.utils.AppConstants;
import com.usertaxi.utils.AppPreferences;
import com.usertaxi.utils.DirectionsJSONParser;
import com.usertaxi.utils.Function;

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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Eyon on 11/9/2015.
 */
public class TaxiOntheWay_Activity extends AppCompatActivity implements LocationListener {
    Button btn_cancel, mbtn_call, btn_sendmesssage;
    PopupWindow pwindo;
    RadioButton rd1, rd2, rd3, rd4, rd5, rd6;
    String canceltaxirequest, sendmessage;
    ImageButton cross, mcross;
    ImageView mdriverimage;
    Dialog dialog;
    LatLng loc2;
    double lat, lon;
    GoogleMap map;
    LocationManager locationManager;
    Location location;
    boolean isGPSEnabled, isNetworkEnabled, canGetLocation;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;
    TextView textheader, txt_header, textname, textmobilenumber, textcompanyname,
            texttaxinumber, mtextnamehead, mtextcompanyhead, mtextmobilehead, mtexttexinumhead,medriverinsurance,mdriverlicense;
    public static String drivermobile = "";
    public static String driverid = "";
    public static String drivername = "";
    public static String drivercompanyname = "";
    public static String drivertexinumber = "";
    public static String driverimage = "";
    public static String drivertaxiname = "";
    public static String SourceAddress = "";

    public static String driverinsurance= "";
    public static String driverlicense = "";
    double driverlatitude, driverlongitude,sourcelatitude,sourcelongitude;
    FloatingActionsMenu fab_menu;
    Marker marker1 = null;
    ArrayList<LatLng> moveMarkerList;
    LatLng l1,l2;

    public  static TaxiOntheWay_Activity taxiOntheWay_activity_instance = null;
    public static boolean taxiOnTheWay = false;

    @Override
    protected void onStop() {
        taxiOnTheWay = false;
        Log.d("activityTest","taxiOnthewaty");
        super.onStop();
    }
//    @Override
//    protected void onResume() {
//        super.onResume();
//        taxiOnTheWay=true;
//
//        try{
//            System.out.println(" Notification message : "+AppPreferences.getNotificationMessage(getApplicationContext()));
//            if(!AppPreferences.getNotificationMessage(getApplicationContext()).equalsIgnoreCase(""))
//            {
//                Intent intent = new Intent(this, TaxiArrived_Acitivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.putExtra("data", AppPreferences.getNotificationMessage(getApplicationContext()));
////                try {
////                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
////                    Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
////                    r.play();
////                } catch (Exception e) {
////                    e.printStackTrace();
////                }
//                startActivity(intent);
//                GCMIntentService.mNotificationManager.cancel(GCMIntentService.NOTIFICATION_ID);
//                AppPreferences.setNotificationMessage(getApplicationContext(), "");
//                finish();
//            }
//        }catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taxiontheway);

        taxiOntheWay_activity_instance = this;
        AppPreferences.setApprequestTaxiScreen(TaxiOntheWay_Activity.this, true);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.BLACK);
        setSupportActionBar(toolbar);
        String title = getString(R.string.title_activity_taxidetail);
        getSupportActionBar().setTitle(title);
        fab_menu = (FloatingActionsMenu) findViewById(R.id.fab_menu);
        FloatingActionButton fab_msg = (FloatingActionButton) findViewById(R.id.fab_message);
        FloatingActionButton fab_call = (FloatingActionButton) findViewById(R.id.fab_call);
        FloatingActionButton fab_cancel = (FloatingActionButton) findViewById(R.id.fab_cancel);

        textheader = (TextView) findViewById(R.id.textheader);
        textname = (TextView) findViewById(R.id.name_text);
        textmobilenumber = (TextView) findViewById(R.id.mobile_text);
        textcompanyname = (TextView) findViewById(R.id.companyname);
        texttaxinumber = (TextView) findViewById(R.id.taxinumber);
        mtextnamehead = (TextView) findViewById(R.id.namehead);
        mtextcompanyhead = (TextView) findViewById(R.id.companyhead);
        mtextmobilehead = (TextView) findViewById(R.id.mobilehead);
        mtexttexinumhead = (TextView) findViewById(R.id.taxiplatthead);
        mdriverlicense = (TextView) findViewById(R.id.driverlicense);
        medriverinsurance = (TextView) findViewById(R.id.driverinsurance);
        mdriverimage=(ImageView)findViewById(R.id.driver_image);


        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        getLocation();

        Typeface tf = Typeface.createFromAsset(this.getAssets(),
                "Montserrat-Regular.ttf");
        textheader.setTypeface(tf);
        mtextnamehead.setTypeface(tf);
        mtextcompanyhead.setTypeface(tf);
        mtextmobilehead.setTypeface(tf);
        mtexttexinumhead.setTypeface(tf);
        textname.setTypeface(tf);
        textmobilenumber.setTypeface(tf);
        textcompanyname.setTypeface(tf);
        texttaxinumber.setTypeface(tf);
        mdriverlicense.setTypeface(tf);
        medriverinsurance.setTypeface(tf);

        Intent intent = getIntent();
        String data = intent.getStringExtra("data");
        Log.d("data value", data + "");

        //caceldialog();
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(data);
            AppPreferences.setAcceptdriverId(TaxiOntheWay_Activity.this, jsonObject.getString("driverId"));
            Log.d("driverid---", AppPreferences.getAcceptdriverId(TaxiOntheWay_Activity.this));
            drivermobile = jsonObject.getString("mobile");
            drivername = jsonObject.getString("username");
            drivercompanyname = jsonObject.getString("taxicompany");
            drivertaxiname = jsonObject.getString("vehicalname");
            drivertexinumber = jsonObject.getString("vehicle_number");
            //driverlatitude = jsonObject.getDouble("latitude");
            //driverlongitude = jsonObject.getDouble("longitude");
            driverimage = jsonObject.getString("driverImage");
            SourceAddress = jsonObject.getString("source_address");
            sourcelatitude = jsonObject.getDouble("source_latitude");
            sourcelongitude = jsonObject.getDouble("source_longitude");
            driverlicense = jsonObject.getString("driverlicense");
            driverinsurance = jsonObject.getString("driverinsurance");

//            Log.d("longitudeeeee:------", String.valueOf(jsonObject.getDouble("longitude")));

            final LatLng loc = new LatLng(new Double(sourcelatitude), new Double(sourcelongitude));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15));
            final MarkerOptions marker = new MarkerOptions().position(loc).title(SourceAddress);
          //  marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_three));
            map.addMarker(marker);

            textname.setText(drivername);
            textmobilenumber.setText(drivermobile);
            textcompanyname.setText(drivercompanyname);
            texttaxinumber.setText(drivertexinumber);
            mdriverlicense.setText(driverlicense);
            medriverinsurance.setText(driverinsurance);

           if(mdriverlicense.length()==0){
                mdriverlicense.setVisibility(View.GONE);
            }
            if(medriverinsurance.length()==0){
                medriverinsurance.setVisibility(View.GONE);
            }
            //taxiname.setText(drivertaxiname);

            Intent intent1 = new Intent(TaxiOntheWay_Activity.this, DriverService.class);
            intent1.putExtra("driverId", jsonObject.getString("driverId"));
            startService(intent1);

if(driverimage.equalsIgnoreCase("")){
    mdriverimage.setImageResource(R.drawable.ic_action_user);
}else {
    Picasso.with(getApplicationContext()).load(driverimage)
            .error(R.drawable.ic_action_user)
            .resize(200, 200)
            .into(mdriverimage);
}


             Timer timer;
             TimerTask task;
            int delay = 10000;
            int period = 10000;

            timer = new Timer();

            timer.scheduleAtFixedRate(task = new TimerTask() {
                public void run() {

     runOnUiThread(new Runnable() {
    @Override
    public void run() {

        loc2 = new LatLng(new Double(AppPreferences.getCurrentlat(TaxiOntheWay_Activity.this)),
                new Double(AppPreferences.getCurrentlong(TaxiOntheWay_Activity.this)));

        if(marker1==null){
            marker1 = map.addMarker(new MarkerOptions()
                    .position(loc2)
                    .title(drivername)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.drivertaxi)));

        }
        animateMarker(marker1, loc2, false);

         /* try{
              String url = Function.getDirectionsUrl(new LatLng(Double.parseDouble(AppPreferences.getPreviouslat(getApplicationContext())),
                      Double.parseDouble(AppPreferences.getPreviouslong(getApplicationContext()))),
                      new LatLng(Double.parseDouble(AppPreferences.getCurrentlat(getApplicationContext())),
                              Double.parseDouble(AppPreferences.getCurrentlong(getApplicationContext()))));
              RoutesDownloadTask downloadTask = new RoutesDownloadTask(TaxiOntheWay_Activity.this);
              downloadTask.execute(url);
          }catch(Exception e)
          {
              e.printStackTrace();
          }*/
    }
        });
                }
            }, delay, period);



        } catch (JSONException e) {
            e.printStackTrace();
        }
/////////////notification dataend///////////////

        fab_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiatePopupWindowcanceltaxi();

            }
        });
        fab_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiatePopupWindowsendmesage();
            }
        });
        fab_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + drivermobile));
                startActivity(callIntent);
            }
        });

        moveMarkerList = new ArrayList<LatLng>();
//        moveRoadMarker();
    }

    public void moveRoadMarker()
    {
//        Timer moveRoadMarkertimer = new Timer();
 //       long markerDelay = 1000,markerPeriod = 1000;

//        moveRoadMarkertimer.scheduleAtFixedRate(new TimerTask() {
//            public void run() {
//
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
                        loc2 = new LatLng(new Double(AppPreferences.getCurrentlat(TaxiOntheWay_Activity.this)),
                                new Double(AppPreferences.getCurrentlong(TaxiOntheWay_Activity.this)));
                        if(marker1==null){
                            marker1 = map.addMarker(new MarkerOptions()
                                    .position(loc2)
                                    .title(drivername)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.drivertaxi)));

                        }
                        if(moveMarkerList != null && moveMarkerList.size() > 0)
                        {
                            for(int m=0;m<moveMarkerList.size();m++)
                            {
                                System.out.println(" list lat long ############## "+moveMarkerList.get(m));
                                animateMarker(marker1, moveMarkerList.get(m), false);
//                                moveMarkerList.remove(0);
                            }
                            moveMarkerList.clear();
                        }else
                        {
                            animateMarker(marker1, new LatLng(Double.parseDouble(AppPreferences.getCurrentlat(getApplicationContext())),
                                    Double.parseDouble(AppPreferences.getCurrentlong(getApplicationContext()))), false);
                        }
//                    }
//                });
//            }
//        }, markerDelay, markerPeriod);

    }

    public class RoutesDownloadTask  extends AsyncTask<String, Void, String> {

        Context context;
        String distanceTime;

        public RoutesDownloadTask(Context context){
            this.context = context;
        }

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data ", url[0]);
            }catch(Exception e){
                Log.d("Background Task", e.toString());
            }
            return data;
        }


        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);


        }

        private String downloadUrl(String strUrl) throws IOException {
            String data = "";
            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            try{
                URL url = new URL(strUrl);

                // Creating an http connection to communicate with url
                urlConnection = (HttpURLConnection) url.openConnection();

                // Connecting to url
                urlConnection.connect();

                // Reading data from url
                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                StringBuffer sb  = new StringBuffer();

                String line = "";
                while( ( line = br.readLine())  != null){
                    sb.append(line);
                }

                data = sb.toString();

                br.close();

            }catch(Exception e){
                //Log.d("Exception while downloading url", e.toString());
            }finally{
                iStream.close();
                urlConnection.disconnect();
            }
            return data;
        }

        public class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> > {

            // Parsing the data in non-ui thread
            @Override
            protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

                JSONObject jObject;
                List<List<HashMap<String, String>>> routes = null;
                try{
                    jObject = new JSONObject(jsonData[0]);
                    DirectionsJSONParser parser = new DirectionsJSONParser();

                    // Starts parsing data
                    routes = parser.parse(jObject);
                }catch(Exception e){
                    e.printStackTrace();
                }
                return routes;
            }

            // Executes in UI thread, after the parsing process
            @Override
            protected void onPostExecute(List<List<HashMap<String, String>>> result) {
                PolylineOptions lineOptions = null;
                ArrayList<LatLng> points = null;
                String distance = "";
                String duration = "";

                if(result.size()<1){
                    //Toast.makeText(ParserTask.this, "No Points", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Traversing through all the routes
                for(int i=0;i<result.size();i++){
//                    lineOptions = new PolylineOptions();
                    points = new ArrayList<LatLng>();
                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.get(i);
                    // Fetching all the points in i-th route
                    for(int j=0;j<path.size();j++){
                        HashMap<String,String> point = path.get(j);

                        if(j==0){    // Get distance from the list
                            distance = (String)point.get("distance");
                            continue;
                        }else if(j==1){ // Get duration from the list
                            duration = (String)point.get("duration");
                            continue;
                        }

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);
                        points.add(position);
                    }
                    System.out.println(" point list is : ##################### "+points);
                    if(points != null && points.size() > 0)
                    {
                        for(int l=0;l<points.size();l++)
                        {
                            moveMarkerList.add(points.get(l));
                        }
                    }else
                    {
                        moveMarkerList.add(new LatLng(Double.parseDouble(AppPreferences.getCurrentlat(getApplicationContext())), Double.parseDouble(AppPreferences.getCurrentlong(getApplicationContext()))));
                    }
                            moveRoadMarker();
                }
            }
        }
    }

    private void animateMarker(final Marker marker, final LatLng toPosition,
                               final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = map.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng =  proj.fromScreenLocation(startPoint);//marker1.getPosition();
        final long duration = 600;

        final Interpolator interpolator = new LinearInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
             //   map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, lng)));
            }
        });


        AppPreferences.setPreviouslat(getApplicationContext(), AppPreferences.getCurrentlat(getApplicationContext()));
        AppPreferences.setPreviouslong(getApplicationContext(), AppPreferences.getCurrentlong(getApplicationContext()));
    }



    private View.OnClickListener cancle_btn_click_listener = new View.OnClickListener() {
        public void onClick(View v) {
            dialog.dismiss();
        }
    };

    private void initiatePopupWindowcanceltaxi() {
        try {
            dialog = new Dialog(TaxiOntheWay_Activity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
            dialog.setContentView(R.layout.canceltaxi_popup);
            cross = (ImageButton) dialog.findViewById(R.id.cross);
            cross.setOnClickListener(cancle_btn_click_listener);
            rd1 = (RadioButton) dialog.findViewById(R.id.radioButton);
            rd2 = (RadioButton) dialog.findViewById(R.id.radioButton2);
            rd3 = (RadioButton) dialog.findViewById(R.id.radioButton3);
            btn_cancel = (Button) dialog.findViewById(R.id.btn_acceptor);
            TextView txt = (TextView) dialog.findViewById(R.id.textView);
            textheader = (TextView) dialog.findViewById(R.id.popup_text);
            Typeface tf = Typeface.createFromAsset(this.getAssets(),
                    "Montserrat-Regular.ttf");
            rd1.setTypeface(tf);
            rd2.setTypeface(tf);
            rd3.setTypeface(tf);
            btn_cancel.setTypeface(tf);
            txt.setTypeface(tf);
            textheader.setTypeface(tf);
            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (rd1.isChecked()) {
                        canceltaxirequest = getResources().getString(R.string.prompt_canceltaxione);

                    } else if (rd2.isChecked()) {
                        canceltaxirequest = getResources().getString(R.string.prompt_cancel_reason_two);

                    } else if (rd3.isChecked()) {
                        canceltaxirequest = getResources().getString(R.string.prompt_cancel_reason_three);

                    }


                    Allbeans allbeans = new Allbeans();
                    allbeans.setCanceltaxirequest(canceltaxirequest);

                    new CancelTaxiAsynch(allbeans).execute();


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
            case R.id.radioButton:
                if (checked)

                    break;
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


    private class CancelTaxiAsynch extends AsyncTask<Void, Void, String> {
        private ProgressDialog mProgressDialog;
        Allbeans allbeans;
        private JSONObject jsonObj;
        ArrayList<Integer> catogariesid;
        private int status = 0;

        public CancelTaxiAsynch(Allbeans allbeans) {
            this.allbeans = allbeans;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(TaxiOntheWay_Activity.this);
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
                System.out.println("Cancel Taxi Value : ------------ "
                        + AppConstants.CANCELTAXI);
                HttpPost httppost = new HttpPost(AppConstants.CANCELTAXI);
                jsonObj = new JSONObject();
                jsonObj.put("uid", AppPreferences.getCustomerId(TaxiOntheWay_Activity.this));
                jsonObj.put("trip_id", AppPreferences.getTripId(TaxiOntheWay_Activity.this));

                jsonObj.put("canceltaxirequest", allbeans.getCanceltaxirequest());
                jsonObj.put("account_type", "7");

                JSONArray jsonArray = new JSONArray();
                jsonArray.put(jsonObj);

                Log.d("json value:---------", jsonArray.toString());


                StringEntity se = null;

                try {
                    se = new StringEntity(jsonArray.toString());

                    se.setContentEncoding(new BasicHeader(
                            HTTP.CONTENT_ENCODING, "UTF-8"));
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
             //   Toast.makeText(getApplicationContext(), "Your request sent successfully", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(TaxiOntheWay_Activity.this, DrawerMainActivity.class);
                startActivity(intent);
                finish();
            } else {

                Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.network_error), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();


            }
        }

    }

    private void initiatePopupWindowsendmesage() {
        try {
            dialog = new Dialog(TaxiOntheWay_Activity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
            dialog.setContentView(R.layout.sendmesssage_popup);


            Button mbtn_sendmesssage = (Button) dialog.findViewById(R.id.btn_acceptor);
            Button mbtn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
            rd4 = (RadioButton) dialog.findViewById(R.id.radioButton1);
            rd5 = (RadioButton) dialog.findViewById(R.id.radioButton2);
            rd6 = (RadioButton) dialog.findViewById(R.id.radioButton3);
            mcross = (ImageButton) dialog.findViewById(R.id.cross);
            txt_header = (TextView) dialog.findViewById(R.id.popup_text);
            mcross.setOnClickListener(cancle_btn_click_listener);
            mbtn_cancel.setOnClickListener(cancle_btn_click_listener);
            Typeface tf = Typeface.createFromAsset(this.getAssets(),
                    "Montserrat-Regular.ttf");
            rd4.setTypeface(tf);
            rd5.setTypeface(tf);
            rd6.setTypeface(tf);
            mbtn_sendmesssage.setTypeface(tf);
            txt_header.setTypeface(tf);
            mbtn_cancel.setTypeface(tf);
            mbtn_sendmesssage.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    if (rd4.isChecked()) {
                        sendmessage = getResources().getString(R.string.prompt_message_one);

                    } else if (rd5.isChecked()) {
                        sendmessage = getResources().getString(R.string.prompt_message_two);

                    } else if (rd6.isChecked()) {
                        sendmessage = getResources().getString(R.string.prompt_message_three);

                    }

                    Allbeans allbeans = new Allbeans();

                    allbeans.setSendmessage(sendmessage);

                    new SendmessageAsynch(allbeans).execute();


                }
            });
            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class SendmessageAsynch extends AsyncTask<Void, Void, String> {
        private ProgressDialog mProgressDialog;
        Allbeans allbeans;
        private JSONObject jsonObj;
        ArrayList<Integer> catogariesid;
        private int status = 0;

        public SendmessageAsynch(Allbeans allbeans) {
            this.allbeans = allbeans;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(TaxiOntheWay_Activity.this);
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
                System.out.println("SEND MESSAGE VALUE : ------------ "
                        + AppConstants.SENDMESSAGE);
                HttpPost httppost = new HttpPost(AppConstants.SENDMESSAGE);
                jsonObj = new JSONObject();
                jsonObj.put("uid", AppPreferences.getCustomerId(TaxiOntheWay_Activity.this));
                jsonObj.put("message", allbeans.getSendmessage());
                jsonObj.put("tripId", AppPreferences.getTripId(TaxiOntheWay_Activity.this));
                jsonObj.put("account_type", "7");
                JSONArray jsonArray = new JSONArray();
                jsonArray.put(jsonObj);
                Log.d("json value:---------", jsonArray.toString());


                StringEntity se = null;

                try {
                    se = new StringEntity(jsonArray.toString());

                    se.setContentEncoding(new BasicHeader(
                            HTTP.CONTENT_ENCODING, "UTF-8"));
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
             //   Toast.makeText(getApplicationContext(), "Your message sent successfully", Toast.LENGTH_LONG).show();
                dialog.dismiss();

//                      finish();
            } else {
//                Toast.makeText(getApplicationContext(),
//                        "Please check your network connection", Toast.LENGTH_LONG).show();
                Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.network_error), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        }

    }

    public Location getLocation() {
        try {
            map.setMyLocationEnabled(true);
            locationManager = (LocationManager) this
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            lat = location.getLatitude();
                            lon = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                lat = location.getLatitude();
                                lon = location.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
        lat = location.getLatitude();
        System.out.println("location latitude:-------" + location.getLatitude());
        lon = location.getLongitude();
        System.out.println("location latitude:-------" + location.getLongitude());

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onBackPressed() {

    }

//    public void caceldialog() {
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(TaxiOntheWay_Activity.this);
////builder.setTitle(getString(R.string.cancel_trip));
////builder.setIcon(R.drawable.ic_launcher);
//        LayoutInflater inflater = getLayoutInflater();
//        View header = inflater.inflate(R.layout.dialog_heading, null);
//        TextView textView = (TextView) header.findViewById(R.id.text);
//        ImageView icon = (ImageView) header.findViewById(R.id.icon);
//        icon.setImageResource(R.drawable.ic_launcher);
//        textView.setText("Taxi On The Way");
//        builder.setCustomTitle(header);
//        builder.setCancelable(false);
//
//
//
//        builder.setPositiveButton("Ok",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
////                        AppPreferences.setTripId(DrawerMainActivity.this, "");
////                        AppPreferences.setDriverId(DrawerMainActivity.this, "");
//                        dialog.dismiss();
//                    }
//                });
//
//        builder.show();
//    }



    @Override
    protected void onPause() {

        Log.d("activityTest","pause");
        super.onPause();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        Log.d("activityTest","chanFoc");
        taxiOnTheWay = true;
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        taxiOntheWay_activity_instance = null;
    }
}
