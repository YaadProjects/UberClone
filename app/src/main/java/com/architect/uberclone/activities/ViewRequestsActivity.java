package com.architect.uberclone.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.DecimalFormat;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.architect.uberclone.R;
import com.architect.uberclone.models.Request;
import com.architect.uberclone.models.RequestLocation;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewRequestsActivity extends Activity implements AdapterView.OnItemClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = ViewRequestsActivity.class.getSimpleName();
    public static final int REQUEST_CODE_PERMISSIONS = 1;

    private DatabaseReference mDatabaseRef;
    private DatabaseReference mUsersRef;
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;

    private FirebaseListAdapter<Request> mListAdapter;

    private List<RequestLocation> requestLocations;

    private Location currentLocation;
    private RequestLocation requestLocation;
    private LocationRequest mLocationRequest;

    ListView mListViewRequests;
    TextView _distance;

    private void init() {
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("requests");
        mUsersRef =  FirebaseDatabase.getInstance().getReference().child("users");

        mAuth = FirebaseAuth.getInstance();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mListViewRequests = (ListView) findViewById(R.id.listViewRequests);

        requestLocations = new ArrayList<RequestLocation>();

        mListAdapter = new FirebaseListAdapter<Request>(
                this, Request.class, R.layout.template_request, mDatabaseRef
        ) {
            @TargetApi(Build.VERSION_CODES.N)
            @Override
            protected void populateView(View v, Request model, int position) {
                final TextView _requesterName = (TextView) v.findViewById(R.id.textView_name);

                mUsersRef.child(model.getRequester_uid()).child("name").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String name = (String) dataSnapshot.getValue();

                        if (name != null && _requesterName != null)
                            _requesterName.setText(name);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "onCancelled: " + databaseError.getMessage() );
                    }
                });

                requestLocation = new RequestLocation(model.getLat(),model.getLon());
                requestLocations.add(requestLocation);

                TextView _location = (TextView) v.findViewById(R.id.textViewLocation);
                _location.setText(requestLocation.toString());

                _distance = (TextView) v.findViewById(R.id.textViewDistance);
                if (currentLocation != null) {
                    _distance.setText("Distance: " + String.valueOf(
                            new DecimalFormat("#.00").format(DriverActivity.calculationByDIstance(
                                    new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    new LatLng(requestLocation.getLatitude(), requestLocation.getLongtitude()))
                            )) + "miles");
                }
            }
        };

        mListViewRequests.setAdapter(mListAdapter);
        mListViewRequests.setOnItemClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_requests);
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getApplicationContext(), DriverActivity.class);
        intent.putExtra("RequestLocation", requestLocations.get(position));
        startActivity(intent);
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSIONS && grantResults.length > 0) {
            for (int i = 0; i < grantResults.length; i++)
                if (grantResults[i] == PackageManager.PERMISSION_DENIED)
                    return;

            try {
                if (mGoogleApiClient != null) {
                    currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    _distance.setText("Distance: " + String.valueOf(
                         new DecimalFormat("#.00").format(DriverActivity.calculationByDIstance(
                                 new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                 new LatLng(requestLocation.getLatitude(), requestLocation.getLongtitude()))
                         )) + "miles");
                }

            } catch (SecurityException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION },
                REQUEST_CODE_PERMISSIONS);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}