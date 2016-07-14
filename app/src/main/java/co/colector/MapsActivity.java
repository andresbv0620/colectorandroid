package co.colector;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import co.colector.R;
import co.colector.helpers.PreferencesManager;

public class MapsActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GoogleApiClient mGoogleClient;
    public static final String TAG = MapsActivity.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private LocationRequest mLocationRequest;
    private ProgressBar progressBarLoader;
    private double currentLongitude = 0.0;
    private double currentLatitude = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        mGoogleClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        //creating locationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setInterval(60 * 1000)       //10 seconds
                .setFastestInterval(1 * 1000);//1 second
        progressBarLoader = (ProgressBar) findViewById(R.id.progressBarMap);
        setUpToolbar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        checkNetwork();
        mGoogleClient.connect();
    }

    private void setUpToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setTitle(getString(R.string.location));
        }
    }

    private void checkNetwork() {
        LocationManager mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            gps_enabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gps_enabled && !network_enabled) {
            //notify user
            AlertDialog.Builder gpsAlert = new AlertDialog.Builder(this);
            gpsAlert.setMessage("Please enable location settings on your device. Don't worry, we'll take you there :D");
            gpsAlert.setPositiveButton("Take me there", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent settings = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(settings);
                }
            });
            gpsAlert.setCancelable(false);
            gpsAlert.show();
        }
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                //setUpMap();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleClient, this);
            mGoogleClient.disconnect();
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Location services connected");
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleClient, mLocationRequest, this);
            Toast.makeText(this, "Refreshing...", Toast.LENGTH_SHORT).show();
        } else {
            handleNewLocation(location);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title("Im here!")
                .draggable(true);

        PreferencesManager.getInstance().setCoordinates(String.valueOf(currentLatitude), String.valueOf(currentLongitude));

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker arg0) {
                // TODO Auto-generated method stub
                Log.d("System out", "onMarkerDragStart..." + arg0.getPosition().latitude + "..." + arg0.getPosition().longitude);
            }

            @SuppressWarnings("unchecked")
            @Override
            public void onMarkerDragEnd(Marker arg0) {
                // TODO Auto-generated method stub
                Log.d("System out", "onMarkerDragEnd..." + arg0.getPosition().latitude + "..." + arg0.getPosition().longitude);
                PreferencesManager.getInstance().setCoordinates(String.valueOf(arg0.getPosition().latitude), String.valueOf(arg0.getPosition().longitude));
                mMap.animateCamera(CameraUpdateFactory.newLatLng(arg0.getPosition()));
            }

            @Override
            public void onMarkerDrag(Marker arg0) {
                // TODO Auto-generated method stub
                Log.i("System out", "onMarkerDrag...");
            }
        });

        mMap.addMarker(options);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15.0f));
        mMap.setMyLocationEnabled(true);
        progressBarLoader.setVisibility(View.GONE);
    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }
}