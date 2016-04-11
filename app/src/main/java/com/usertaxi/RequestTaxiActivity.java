package com.usertaxi;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.usertaxi.beans.Allbeans;
import com.usertaxi.gcmclasses.Config;
import com.usertaxi.utils.AppConstants;
import com.usertaxi.utils.AppPreferences;
import com.usertaxi.utils.JSONfunctions;

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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class RequestTaxiActivity extends AppCompatActivity implements LocationListener, AdapterView.OnItemClickListener {

    LocationManager locationManager;
    Location location;
    boolean isGPSEnabled, isNetworkEnabled, canGetLocation;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;
    public static GoogleMap map;
    static Dialog dialog = null;
    Button btn_ok;
    ImageButton cross;
    double demodestinationlatitute;
    double demodestinationlangitude;
    String fulladdressdestination;
    Snackbar msnSnackbar;
    LatLng sorceLatLng;
    //////////places api///////////
    double lat, lon;

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    AutoCompleteTextView sourcetext, destinationtext, mstreetname;
    // ------------ make your specific key ------------
    private static final String API_KEY = "AIzaSyAQ5Se4Tu1LmgZDAwQ-mguA73x__s8HsTY";
    private JSONObject jsonObj;
    JSONArray jsonarray;
    private ProgressDialog mProgressDialog;
    EditText mstreetnumber, mstreetdescription;
    String streetname, streetnumber, stringdescription, companyname, source, destination, tripdate, triptime;
    ArrayList<String> companylist;
    ArrayList<Allbeans> allbeanses;
    Spinner mySpinner;
    String cityname = "";
    public static   String sorcelatitude = "";
    public static  String sourcelangitude = "";
    LatLng destinationLatLng;
    LatLng destinationLatLng_new;
    double cameraPositionLat, cameraPositionLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_taxi);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(Color.BLACK);
        String title = getString(R.string.title_activity_request_taxi);
        getSupportActionBar().setTitle(title);
        Intent in = getIntent();
        cityname = in.getStringExtra("city");
        sorcelatitude = in.getStringExtra("sorcelatitude");
        Log.d("reciverlati",sorcelatitude);
        sourcelangitude = in.getStringExtra("sourcelongitude");
        Log.d("reciverlati",sourcelangitude);
        mstreetname = (AutoCompleteTextView) findViewById(R.id.street_name);
        mstreetnumber = (EditText) findViewById(R.id.strret_number);
        mstreetdescription = (EditText) findViewById(R.id.description);
        destinationtext = (AutoCompleteTextView) findViewById(R.id.street_destination);
        mySpinner = (Spinner) findViewById(R.id.spinner);
        mstreetname.setText(cityname);

       if(mstreetname!=null){
           Allbeans allbeans = new Allbeans();
           allbeans.setSourcerequest(source);
           allbeans.setTripdate(tripdate);
           allbeans.setTriptime(triptime);
           allbeans.setSourcelatitute(new Double(sorcelatitude));
           allbeans.setSourcelongitude(new Double(sourcelangitude));
           new SpinnerJSON(allbeans).execute();
       }

        Button confirm_btn = (Button) findViewById(R.id.confirmbutton);
        Typeface tf = Typeface.createFromAsset(this.getAssets(),
                "Montserrat-Regular.ttf");

        confirm_btn.setTypeface(tf);
        mstreetname.setTypeface(tf);
        mstreetnumber.setTypeface(tf);
        mstreetdescription.setTypeface(tf);
        destinationtext.setTypeface(tf);
        confirm_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mstreetname.setError(null);
                mstreetnumber.setError(null);
                mstreetdescription.setError(null);
                streetname = mstreetname.getText().toString();
                sorceLatLng = getLocationFromAddress(RequestTaxiActivity.this, streetname);
                streetnumber = mstreetnumber.getText().toString();
                //   source = sourcetext.getText().toString().trim();
                destination = destinationtext.getText().toString().trim();
                LatLng latLng = getLocationFromAddress(RequestTaxiActivity.this, destination);
                if (latLng == null) {
                    destinationLatLng = destinationLatLng_new;
                } else {
                    destinationLatLng = latLng;
                }
                stringdescription = mstreetdescription.getText().toString().trim();
                companyname = mySpinner.getSelectedItem().toString();
                if (streetname.length() == 0) {
                    mstreetname.setError(getString(R.string.error_field_required));

                } else if (streetnumber.length() == 0) {
                    mstreetnumber.setError(getString(R.string.error_field_required));

                } else if (stringdescription.length() == 0) {
                    mstreetdescription.setError(getString(R.string.error_field_required));

                }
//                else if (destinationtext.length() == 0) {
//                    destinationtext.setError(getString(R.string.error_field_required));
//
//                }
                else if (sorceLatLng == null) {
                    Snackbar.make(view,getResources().getString(R.string.source_adressverify), Snackbar.LENGTH_LONG).show();
                }
//                else if (destinationLatLng == null) {
//                    initiatePopupWindowdestinationtaxi();
//                    Snackbar.make(view,getResources().getString(R.string.destination_adressverify), Snackbar.LENGTH_LONG).show();
//                }
                else {
                    Allbeans allbeans = new Allbeans();
                    allbeans.setStreetname(streetname);
                    allbeans.setStreetnumber(streetnumber);
                    allbeans.setStreetdescription(stringdescription);
                    allbeans.setCompanyname(companyname);
                    allbeans.setDestinationresuest(destination);
                    allbeans.setTripdate(tripdate);
                    allbeans.setTriptime(triptime);
                    allbeans.setSourcelatitute(sorceLatLng.latitude);
                    allbeans.setSourcelongitude(sorceLatLng.longitude);

//                    if (destinationLatLng.latitude == 0.0) {
//                        allbeans.setDestinationlatitude(demodestinationlatitute);
//                    } else {
//                        allbeans.setDestinationlatitude(destinationLatLng.latitude);
//                    }
//
//                    if (destinationLatLng.longitude == 0.0) {
//                        allbeans.setDestinationlongitude(demodestinationlangitude);
//                    } else {
//                        allbeans.setDestinationlongitude(destinationLatLng.longitude);
//                    }


                    new RequestTaxiAsynch(allbeans).execute();
                }

            }
        });
        mstreetname.setAdapter(new GooglePlacesAutocompleteAdapter(this,
                R.layout.list_item));
        destinationtext.setAdapter(new GooglePlacesAutocompleteAdapter(this,
                R.layout.list_item));
        mstreetname.setOnItemClickListener(this);
        destinationtext.setOnItemClickListener(this);

        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(getResources().getColor(R.color.colorblack), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        System.out.println(("Latitude:" + location.getLatitude()));
        lon = location.getLongitude();
        System.out.println(("Longitude:" + location.getLongitude()));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


    }


    private class RequestTaxiAsynch extends AsyncTask<Void, Void, String> {
        Allbeans allbeans;
        Context context;
        ArrayList<Integer> catogariesid;
        private int status = 0;

        public RequestTaxiAsynch(Allbeans allbeans) {
            this.allbeans = allbeans;

        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(RequestTaxiActivity.this);
            mProgressDialog.setMessage(getResources().getString(R.string.progressmsg));
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
                System.out.println("REQUEST TAXI VALUE : ------------ "
                        + AppConstants.REQUESTTAXI);
                HttpPost httppost = new HttpPost(AppConstants.REQUESTTAXI);
                jsonObj = new JSONObject();
                jsonObj.put("customer_id", AppPreferences.getCustomerId(RequestTaxiActivity.this));

                jsonObj.put("sourceaddress", allbeans.getStreetname());
                jsonObj.put("streetnumber", allbeans.getStreetnumber());
                jsonObj.put("tripType", "userapp");
                jsonObj.put("destinationaddress", allbeans.getDestinationresuest());
                jsonObj.put("tripdate", allbeans.getTripdate());
                jsonObj.put("triptime", allbeans.getTriptime());
                jsonObj.put("description", allbeans.getStreetdescription());
                jsonObj.put("companyname", allbeans.getCompanyname());
                jsonObj.put("regId", getDeviceId());
                jsonObj.put("currentlatitute", AppPreferences.getLatutude(RequestTaxiActivity.this));
                jsonObj.put("currentlongitude", AppPreferences.getLongitude(RequestTaxiActivity.this));
                jsonObj.put("sourcelatitute", sorcelatitude);
                jsonObj.put("sourcelongitude", sourcelangitude);
                jsonObj.put("destination_lat", allbeans.getDestinationlatitude());
                jsonObj.put("destination_lon", allbeans.getDestinationlongitude());
                jsonObj.put("account_type", "7");
                jsonObj.put("device_type", "ANDROID");



                JSONArray jsonArray = new JSONArray();
                jsonArray.put(jsonObj);

                Log.d("json value:----", jsonArray.toString());


                StringEntity se = null;
                try {
                    se = new StringEntity(jsonArray.toString(), "UTF-8");

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
                        JSONObject jsonObject = jsonObj.getJSONObject("result");
                        AppPreferences.setTripId(RequestTaxiActivity.this, jsonObject.getString("id"));
                        AppPreferences.setApprequestTaxiScreen(RequestTaxiActivity.this, true);
                        Log.d("trip-id", AppPreferences.getTripId(RequestTaxiActivity.this));

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
//                Toast.makeText(getApplicationContext(),
//                        "Request sent successfully", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(RequestTaxiActivity.this,
                        ShowTaxi_InMap.class);

                intent.putExtra("sorcelatitude", sorcelatitude);
                intent.putExtra("sourcelongitude", sourcelangitude);
                intent.putExtra("fulladdress", cityname);
                startActivity(intent);
                AppPreferences.setSorclat(RequestTaxiActivity.this, sorcelatitude);
                AppPreferences.setSrcLng(RequestTaxiActivity.this, sourcelangitude);
                AppPreferences.setSorcAdd(RequestTaxiActivity.this, cityname);
                finish();
            } else {

//                TextView tv = (TextView) (msnSnackbar.getView()).findViewById((android.support.design.R.id.snackbar_text),"Check Your Network Connection",Snackbar.LENGTH_LONG);
//                Typeface font = Typeface.createFromAsset(RequestTaxiActivity.this.getAssets(), "fonts/font_file.ttf");
//                tv.setTypeface(font);

                Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.network_error), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();


            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.request, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
//            Intent intent = new Intent(RequestTaxiActivity.this, DrawerMainActivity.class);
//            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //////////////////Findlocation///////////
    public static ArrayList<String> autocomplete(String input) {
        ArrayList<String> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE
                    + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            //  sb.append("&components=country:in");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());
            System.out.println("Google<<<<<<<<<<<URL: " + url);
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e("LOG_TAG", "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e("LOG_TAG", "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<String>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                System.out.println(predsJsonArray.getJSONObject(i).getString(
                        "description"));
                System.out
                        .println("============================================================");
                resultList.add(predsJsonArray.getJSONObject(i).getString(
                        "description"));
            }
        } catch (JSONException e) {
            Log.e("LOG_TAG", "Cannot process JSON results", e);
        }

        return resultList;
    }


    public class GooglePlacesAutocompleteAdapter extends ArrayAdapter<String>
            implements Filterable {
        private ArrayList<String> resultList;

        public GooglePlacesAutocompleteAdapter(Context context,
                                               int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return resultList.get(index);
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.

                        System.out.println("------------adapter calling ---------------");

                        resultList = autocomplete(constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint,
                                              Filter.FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }
//////////////////end places api code////////////////////

    ///////////spinnerjson///////////////////

    // fatch company name JSON file AsyncTask
    private class SpinnerJSON extends AsyncTask<Void, Void, Void> {
        Allbeans allbeans;
        ProgressDialog progressDialog;
        public SpinnerJSON(Allbeans allbeans){
            this.allbeans = allbeans;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(RequestTaxiActivity.this);
            progressDialog.show();
            progressDialog.setMessage(getResources().getString(R.string.load_company));
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                HttpParams httpParameters = new BasicHttpParams();
                ConnManagerParams.setTimeout(httpParameters,
                        AppConstants.NETWORK_TIMEOUT_CONSTANT);
                HttpConnectionParams.setConnectionTimeout(httpParameters,
                        AppConstants.NETWORK_CONNECTION_TIMEOUT_CONSTANT);
                HttpConnectionParams.setSoTimeout(httpParameters,
                        AppConstants.NETWORK_SOCKET_TIMEOUT_CONSTANT);

                allbeanses = new ArrayList<Allbeans>();
                companylist = new ArrayList<String>();
                companylist.add("ANY COMPANY");
                jsonObj = JSONfunctions.getJSONfromURL(AppConstants.COMPANYNAME, allbeans);


                try {
                    jsonarray = jsonObj.getJSONArray("result");
                    for (int i = 0; i < jsonarray.length(); i++) {
                        jsonObj = jsonarray.getJSONObject(i);
                        Allbeans allbeans = new Allbeans();
                        allbeans.setCompanyname(companyname);
                        allbeanses.add(allbeans);
                        companylist.add(jsonObj.optString("companyName"));
                      //  companylist.add(jsonObj.optString("id"));
                        AppPreferences.setCompanyId(RequestTaxiActivity.this, jsonObj.optString("companyId"));
                        Log.d("companyid-",AppPreferences.getCompanyId(RequestTaxiActivity.this));

                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }catch (Exception e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            mySpinner.setAdapter(new ArrayAdapter<String>(RequestTaxiActivity.this, android.R.layout.simple_spinner_dropdown_item, companylist));
            progressDialog.dismiss();


        }
    }


    public String getDeviceId() {

        GCMRegistrar.checkDevice(this.getApplicationContext());
        GCMRegistrar.checkManifest(this.getApplicationContext());
        final String regId = GCMRegistrar.getRegistrationId(this
                .getApplicationContext());

        // Check if regid already presents
        if (regId.equals("")) {
            Log.i("GCM K", "--- Regid = ''" + regId);
            GCMRegistrar.register(this.getApplicationContext(),
                    Config.GOOGLE_SENDER_ID);
            AppPreferences.setDeviceid(this, regId);
            return regId;
        } else {

            if (GCMRegistrar.isRegisteredOnServer(this)) {

                Log.i("GCM K2", "--- Regid = ''" + regId);
            } else {
                Log.i("GCM K3", "--- Regid = ''" + regId);

            }
            AppPreferences.setDeviceid(this, regId);
            return regId;
        }

    }


    public static LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            Log.d("LocationLATLONG", address.toString() + " ::: " + strAddress);
            if (address == null || address.size() == 0) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return p1;
    }
//////////////////new popup implimention/////////////////////

    private View.OnClickListener cancle_btn_click_listener = new View.OnClickListener() {
        public void onClick(View v) {
            dialog.dismiss();
        }
    };

    public void initiatePopupWindowdestinationtaxi() {
        boolean status = false;
        if (dialog == null) {
            status = true;
        }
        if (status) {
            dialog = new Dialog(RequestTaxiActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
            dialog.setContentView(R.layout.destination_popup);
        }
        cross = (ImageButton) dialog.findViewById(R.id.cross_popup);
        cross.setOnClickListener(cancle_btn_click_listener);
        btn_ok = (Button) dialog.findViewById(R.id.button_ok);
        TextView txt = (TextView) dialog.findViewById(R.id.popup_text);
        Typeface tf = Typeface.createFromAsset(this.getAssets(),
                "Montserrat-Regular.ttf");
        txt.setTypeface(tf);
        btn_ok.setTypeface(tf);
//        GoogleMap gmap =
        try {
            map = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapview)).getMap();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (map == null) {
            try {
                map = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapview)).getMap();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        getLocation();
        LatLng loc = new LatLng(lat, lon);
        MarkerOptions marker2 = new MarkerOptions().position(loc);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15));
//        marker2.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                map.getCameraPosition();
                cameraPosition = map.getCameraPosition();
                Log.d("postionnnnn", "" + cameraPosition.target);
                LatLng latLng = cameraPosition.target;
                Log.d("postionnnnn", "" + latLng.longitude + " : " + latLng.latitude);
                cameraPositionLat = cameraPosition.target.latitude;
                cameraPositionLong = cameraPosition.target.longitude;
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("cameraPositionLat : " + cameraPositionLat + "cameraPositionLong" + cameraPositionLong);
                if (cameraPositionLat == 0.0 || cameraPositionLong == 0.0) {
                    Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.address_selection), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(RequestTaxiActivity.this, Locale.getDefault());
                    try {
                        addresses = geocoder.getFromLocation(cameraPositionLat, cameraPositionLong, 1);
                        String destizipcodee = "";// Here 1 represent max location result to returned, by documents it recommended 1 to 5
                        try {
                            demodestinationlatitute = (addresses.get(0).getLatitude());
                            demodestinationlangitude = (addresses.get(0).getLongitude());
                            destinationLatLng_new = new LatLng(demodestinationlatitute, demodestinationlangitude);
//                        Log.d("demodestinationlatitute", destinationLatLng.latitude+"");
                            if (addresses.get(0).getAddressLine(3) != null) {
                                destizipcodee = "," + addresses.get(0).getAddressLine(3);
                            }
                            fulladdressdestination = addresses.get(0).getAddressLine(0) + "," + addresses.get(0).getAddressLine(1) + ","
                                    + addresses.get(0).getAddressLine(2) + destizipcodee;
                            Log.d("fulladdresss", fulladdressdestination);


                        } catch (IndexOutOfBoundsException e) {
                            e.printStackTrace();
                            demodestinationlatitute = 0.0;
                            demodestinationlangitude = 0.0;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // destinationtext.setText(fulladdressdestination);
                    dialog.dismiss();
                }

            }
        });

        dialog.show();

    }


    public Location getLocation() {
        try {
            map.setMyLocationEnabled(true);
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

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


}

