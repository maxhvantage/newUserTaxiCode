package com.usertaxi.service;

import android.app.IntentService;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.usertaxi.MainFragment;
import com.usertaxi.R;
import com.usertaxi.ShowTaxi_InMap;
import com.usertaxi.beans.Allbeans;
import com.usertaxi.classes.GPSTracker;
import com.usertaxi.utils.AppConstants;
import com.usertaxi.utils.AppPreferences;
import com.usertaxi.utils.JSONParser;

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
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Eyon on 12/14/2015.
 */
public class DriverService extends IntentService {
    String TAG = "GetDriverServices";
    GPSTracker gps;
    private Timer timer;
    private TimerTask task;

    ArrayList<Allbeans> DriverList;
    ArrayList<Integer> tripIdList;
    ArrayList<Long> idList;
    ResultReceiver resultReceiver;
    public static volatile boolean shouldContinue = true;
    String driverId;
    ///////////////
   // GoogleMap map;
   // ArrayList<Allbeans> al;
    public static String DRIVERNAME, DRIVERMOBILE, DRIVERID;
    double DRIVERLATITUDE, DRIVERLONGITUDE;
    LatLng loc2;

    public DriverService() {
        super(DriverService.class.getName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        gps = new GPSTracker(this);
        gps.getLocation();
        DriverList = new ArrayList<Allbeans>();
        idList = new ArrayList<Long>();




    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        driverId = intent.getStringExtra("driverId");
        System.out.println("***********service command******************");
      new FetchMovedriverData().execute();
        return super.onStartCommand(intent, flags, startId);


    }



    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        driverId = intent.getStringExtra("driverId");
        //  tripIdList = db.getTripId();
        System.out.println("***********service******************");

        new FetchMovedriverData().execute();

    }

    @Override
    protected void onHandleIntent(Intent intent) {

        driverId = intent.getStringExtra("driverId");

        int delay = 5000;
        int period = 5000;

        timer = new Timer();

        timer.scheduleAtFixedRate(task = new TimerTask() {
            public void run() {


                new FetchMovedriverData().execute();
                if (shouldContinue == false) {
                    timer.cancel();
                    task.cancel();
                    stopSelf();
                    return;
                }
            }
        }, delay, period);


    }

    private class FetchMovedriverData extends AsyncTask<Void, Void, String> {
        ProgressDialog dialog;
        int status=0;


        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
//            dialog = new ProgressDialog(getActivity());
//            dialog.setMessage("Loading...");
//            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... arg0) {
            // TODO Auto-generated method stub
            try {
                HttpParams httpParameters = new BasicHttpParams();
                ConnManagerParams.setTimeout(httpParameters,
                        AppConstants.NETWORK_TIMEOUT_CONSTANT);

                HttpConnectionParams.setConnectionTimeout(httpParameters,
                        AppConstants.NETWORK_CONNECTION_TIMEOUT_CONSTANT);
                HttpConnectionParams.setSoTimeout(httpParameters,
                        AppConstants.NETWORK_SOCKET_TIMEOUT_CONSTANT);

                HttpClient httpclient = new DefaultHttpClient();
                System.out.println("REQUEST TAXI VALUE : ------------ "
                        + AppConstants.DRIVERCURRENTINFO);
                HttpPost httppost = new HttpPost(AppConstants.DRIVERCURRENTINFO);
                JSONObject jsonObj1 = new JSONObject();
                jsonObj1.put("customer_id", AppPreferences.getCustomerId(DriverService.this));
                jsonObj1.put("driverId", driverId);
                JSONArray jsonArray = new JSONArray();
                jsonArray.put(jsonObj1);
                Log.d("json value:----", jsonArray.toString());

                StringEntity se = null;
                try {
                    se = new StringEntity(jsonArray.toString());

                    se.setContentEncoding(new BasicHeader(
                            HTTP.CONTENT_ENCODING, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Log.v("json : ", jsonArray.toString(2));
                System.out.println("SENT JSON is : " + jsonArray.toString());
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
                        JSONArray array = jsonObj.getJSONArray("result");
                        //  AppPreferences.setTripId(getActivity(), jsonObject.getString("id"));
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject jo = array.getJSONObject(i);

                            DRIVERID = jo.getString("driver_id");
                            DRIVERLATITUDE = jo.getDouble("prelatitude");
                            DRIVERLONGITUDE = jo.getDouble("prelongitude");

                            AppPreferences.setCurrentlat(DriverService.this,String.valueOf(DRIVERLATITUDE));
                            AppPreferences.setCurrentlong(DriverService.this,String.valueOf(DRIVERLONGITUDE));
                            if(AppPreferences.getPreviouslat(getApplicationContext()).equalsIgnoreCase("0.0")){
                                AppPreferences.setPreviouslat(getApplicationContext(), jo.getString("prelatitude"));
                                AppPreferences.setPreviouslong(getApplicationContext(), jo.getString("prelongitude"));
                            }

                        }


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
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("ServiceTask", "Stop");
        //timer.cancel();
        // task.cancel();
        //stopSelf();
    }

}
