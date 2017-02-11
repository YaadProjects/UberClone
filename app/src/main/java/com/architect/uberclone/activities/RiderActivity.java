package com.architect.uberclone.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.architect.uberclone.R;
import com.architect.uberclone.models.Request;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class RiderActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final String TAG = RiderActivity.class.getSimpleName();
    public static final int REQUEST_CODE_PERMISSIONS = 1;

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;

    private Marker mLocationMarker;
    private LocationRequest mLocationRequest;

    private Location currentLocation;

    Button mButtonRequest;
    TextView mTextViewStatus;

    private boolean isRequested = false;
    private String requestId;

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void setupMapIfNeeded() {
        if (mMap == null)
            ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
    }

    private void init() {
        mButtonRequest = (Button) findViewById(R.id.buttonRequest);
        mButtonRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestUber();
            }
        });

        mTextViewStatus = (TextView) findViewById(R.id.textViewStatus);
    }

    private void requestUber() {
        if (!isRequested) {
            Request request = new Request(currentLocation.getLatitude(), currentLocation.getLongitude(),
                    FirebaseAuth.getInstance().getCurrentUser().getUid(), null);

            DatabaseReference _requestRef = FirebaseDatabase.getInstance().getReference().child("requests").push();
            requestId = _requestRef.getKey();
            _requestRef.setValue(request);

            mButtonRequest.setText(R.string.uber_cancel);
            mTextViewStatus.setText(R.string.uber_on_the_way);
            isRequested = true;
        } else {
            DatabaseReference _requestRef = FirebaseDatabase.getInstance().getReference().child("requests");
            _requestRef.child(requestId).removeValue();

            mButtonRequest.setText(R.string.request_uber);
            mTextViewStatus.setText("");
            isRequested = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider);
        setupMapIfNeeded();
        init();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_CODE_PERMISSIONS);

        buildGoogleApiClient();
        mGoogleApiClient.connect();
    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1 && grantResults.length > 0) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED)
                    return;
            }

            mMap.setMyLocationEnabled(true);
            currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Toast.makeText(this,"onCONNECTED",Toast.LENGTH_SHORT).show();
        Log.i(TAG, "onConnected: ");

        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_CODE_PERMISSIONS);


        if (currentLocation != null) {
            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Your location");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            mLocationMarker = mMap.addMarker(markerOptions);
        }

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this,"onConnectionSuspended", Toast.LENGTH_SHORT).show();
        Log.i(TAG, "onConnectionSuspended: ");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this,"onConnectionFailed",Toast.LENGTH_SHORT).show();
        Log.i(TAG, "onConnectionFailed: ");
    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(this,"onLocationChanged",Toast.LENGTH_SHORT).show();
        Log.i(TAG, "onLocationChanged: ");

        if (mLocationMarker != null)
            mLocationMarker.remove();

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));

        mLocationMarker = mMap.addMarker(markerOptions);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .zoom(14)
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}