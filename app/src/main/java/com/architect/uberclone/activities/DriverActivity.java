package com.architect.uberclone.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.icu.text.DecimalFormat;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.architect.uberclone.R;
import com.architect.uberclone.models.RequestLocation;
import com.architect.uberclone.utils.JSONParser;
import com.architect.uberclone.utils.PathFinder;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DriverActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final String TAG = DriverActivity.class.getSimpleName();
    public static final int REQUEST_CODE_PERMISSIONS = 1;
    public static final float DEFAULT_CAMERA_ZOOM = 3f;

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;

    private Marker mCurrentLocationMarker;
    private Marker mRequestLocationMarker;
    private LocationRequest mLocationRequest;

    private RequestLocation requestLocation;
    private Location currentLocation;

    private Thread thread;
    private String jsonData;

    TextView mTextViewDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver2);
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();

        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnectionSuspended(int i) {
        // TODO: 10/02/17
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // TODO: 10/02/17
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "onConnected: ");

        ActivityCompat.requestPermissions(
                this,
                new String[] { Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION },
                REQUEST_CODE_PERMISSIONS
        );

        if (currentLocation != null) {
            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Your location");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            mCurrentLocationMarker = mMap.addMarker(markerOptions);
        }

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(TAG, "onMapReady: ");
        mMap = googleMap;

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                LatLng _requestLocation = new LatLng(requestLocation.getLatitude(), requestLocation.getLongtitude());
                mRequestLocationMarker = mMap.addMarker(new MarkerOptions().position(_requestLocation).title("RequestLocation"));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(_requestLocation, DEFAULT_CAMERA_ZOOM));
                builder.include(_requestLocation);

                ActivityCompat.requestPermissions(
                        DriverActivity.this,
                        new String[]{ Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION },
                        REQUEST_CODE_PERMISSIONS
                );

                if (currentLocation != null) {
                    LatLng _currentLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    MarkerOptions options = new MarkerOptions().position(_currentLocation)
                            .title("Your Location")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

                    mCurrentLocationMarker = mMap.addMarker(options);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(_currentLocation, DEFAULT_CAMERA_ZOOM));

                    builder.include(_currentLocation);
                }

                LatLngBounds bounds = builder.build();
                int padding = 10;
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                mMap.moveCamera(cameraUpdate);
                mMap.animateCamera(cameraUpdate);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionsResult: ");

        if (requestCode == REQUEST_CODE_PERMISSIONS && grantResults.length > 0) {
            for (int grantResult : grantResults)
                if (grantResult == PackageManager.PERMISSION_DENIED)
                    return;

            if (mGoogleApiClient != null) {
                try {
                    mMap.setMyLocationEnabled(true);
                    currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

                    mTextViewDistance.setText("Distance: " +
                            String.valueOf(new DecimalFormat("#.00").format(
                                    DriverActivity.calculationByDIstance(
                                        new LatLng(requestLocation.getLatitude(), requestLocation.getLongtitude()),
                                        new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())
                                    )
                                )
                            ) + " miles");

                    calculateAndDrawPath();
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                } catch (SecurityException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        /**
         *
         * if (mCurrentLocationMarker != null)
             mCurrentLocationMarker.remove();

             LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
             MarkerOptions markerOptions = new MarkerOptions();
             markerOptions.position(latLng);
             markerOptions.title("Current Position");
             markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));

             mCurrentLocationMarker = mMap.addMarker(markerOptions);

             CameraPosition cameraPosition = new CameraPosition.Builder()
             .target(latLng)
             .zoom(14)
             .build();

             mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
         */
    }

    // ------------------------------------- Helper methods ------------------------------------------------------------------

    @TargetApi(Build.VERSION_CODES.N)
    public static double calculationByDIstance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c;
    }

    private void init() {

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        this.requestLocation = getIntent().getParcelableExtra("RequestLocation");

        mTextViewDistance = (TextView) findViewById(R.id.textViewDistance);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    private void calculateAndDrawPath() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                setJsonData(JSONParser.getJSONFromUrl(
                        PathFinder.makeURL(
                                currentLocation.getLatitude(), currentLocation.getLongitude(),
                                requestLocation.getLatitude(), requestLocation.getLongtitude()
                        )
                ));
            }
        });

        thread.start();
        drawPath(jsonData);
    }

    private void drawPath(String result) {
        try {
            if (result != null) {
                final JSONObject jsonObject = new JSONObject(result);
                JSONArray routesArray = jsonObject.getJSONArray("routes");
                JSONObject routes = routesArray.getJSONObject(0);
                JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
                String encodedString = overviewPolylines.getString("points");

                List<LatLng> listCoordinates = decodePoly(encodedString);
                Polyline line = mMap.addPolyline(
                        new PolylineOptions()
                                .addAll(listCoordinates)
                                .width(12)
                                .color(Color.parseColor("#05b1fb"))
                                .geodesic(true)
                );
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng( (((double) lat / 1E5)),
                    (((double) lng / 1E5) ));
            poly.add(p);
        }

        return poly;
    }
}
