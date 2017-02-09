package com.architect.uberclone.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.architect.uberclone.R;
import com.architect.uberclone.models.Request;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewRequestsActivity extends Activity {

    public static final String TAG = ViewRequestsActivity.class.getSimpleName();

    private DatabaseReference mDatabaseRef;
    private FirebaseAuth mAuth;

    private FirebaseListAdapter<Request> mListAdapter;
    
    ListView mListViewRequests;

    private void init() {
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("requests");

        mAuth = FirebaseAuth.getInstance();

        mListAdapter = new FirebaseListAdapter<Request>(
                this, Request.class, R.layout.template_request, mDatabaseRef
        ) {
            @Override
            protected void populateView(View v, Request model, int position) {


                ((TextView) v.findViewById(R.id.textViewLatitude)).setText(String.valueOf(model.getLat()));
                ((TextView) v.findViewById(R.id.textViewLongtitude)).setText(String.valueOf(model.getLon()));
            }
        };

        mListViewRequests = (ListView) findViewById(R.id.listViewRequests);
        mListViewRequests.setAdapter(mListAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_requests);
        init();
    }
}
