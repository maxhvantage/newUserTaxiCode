package com.usertaxi;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Eyon-dev on 12/4/2015.
 */
public class DemoMap extends AppCompatActivity implements LocationListener {


    GoogleMap map;
    LatLng mylocation;
    double lat, lon;
    LocationManager locationManager;
    Location location;
    boolean isGPSEnabled, isNetworkEnabled, canGetLocation;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;
    Button btn_confirm, mbtn_boarded;
    PopupWindow pwindo;
    RadioButton rd1, rd2, rd3;
    String canceltaxirequest;
    TextView txt, textheader;
    ImageButton cross;
    CameraPosition ne;
    double ne1;
    double sw;
    double sw2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demomap);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final String title = getString(R.string.prompt_login);
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        getLocation();
        LatLng loc = new LatLng(lat, lon);
        // LatLng loc2 = new LatLng(22.7355507, 75.8828052);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15));
//        ne = map.getCameraPosition();
        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                map.getCameraPosition();
                cameraPosition = map.getCameraPosition();
                Log.d("postionnnnn", "" + cameraPosition.target);
                LatLng latLng = cameraPosition.target;
                Log.d("postionnnnn", "" + latLng.longitude + " : " + latLng.latitude);

                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(DemoMap.this, Locale.getDefault());

                try {
                    addresses = geocoder.getFromLocation(cameraPosition.target.latitude, cameraPosition.target.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    String address = addresses.get(0).getAddressLine(0);// If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    Log.d("address", address);
                    String city = addresses.get(0).getLocality();
                    Log.d("cityname", city);
                    String state = addresses.get(0).getAdminArea();
                    Log.d("state", state);
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();
                    String knownName = addresses.get(0).getFeatureName();
                    String latitutesource = String.valueOf(addresses.get(0).getLatitude());
                    String langitudesource = String.valueOf(addresses.get(0).getLongitude());

                    String fulladdress = address + "," + city + "," + state + "," + country;
                    Log.d("fulladdresss", fulladdress);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


//        MarkerOptions marker = new MarkerOptions().position(loc).title("First Marker ");
//        map.addMarker(marker);

//        MarkerOptions marker2 = new MarkerOptions().position(loc2).title("Second Marker");
//        marker2.icon(BitmapDescriptorFactory.fromResource(R.drawable.drivertaxi));
//        map.addMarker(marker2);


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
        System.out.println("location longitude:-------" + location.getLongitude());

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


    // Getting Google Play availability status


}
