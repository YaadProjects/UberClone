package com.architect.uberclone.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.textservice.TextInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.architect.uberclone.R;
import com.architect.uberclone.models.Request;
import com.architect.uberclone.models.RequestLocation;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewRequestsActivity extends Activity implements AdapterView.OnItemClickListener {

    public static final String TAG = ViewRequestsActivity.class.getSimpleName();

    private DatabaseReference mDatabaseRef;
    private FirebaseAuth mAuth;

    private FirebaseListAdapter<Request> mListAdapter;

    private List<RequestLocation> requestLocations;
    
    ListView mListViewRequests;

    private void init() {
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("requests");

        mAuth = FirebaseAuth.getInstance();

        mListViewRequests = (ListView) findViewById(R.id.listViewRequests);

        requestLocations = new ArrayList<RequestLocation>();

        mListAdapter = new FirebaseListAdapter<Request>(
                this, Request.class, R.layout.template_request, mDatabaseRef
        ) {
            @Override
            protected void populateView(View v, Request model, int position) {
                RequestLocation requestLocation = new RequestLocation(model.getLat(),model.getLon());
                requestLocations.add(requestLocation);

                TextView textView = (TextView) v.findViewById(R.id.textViewLocation);
                textView.setText(requestLocation.toString());
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getApplicationContext(), DriverActivity.class);
        intent.putExtra("RequestLocation", requestLocations.get(position));
        startActivity(intent);
    }
}
