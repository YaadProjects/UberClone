package com.architect.uberclone.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.architect.uberclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends Activity implements View.OnClickListener, ValueEventListener {

    public static final String TAG = MainActivity.class.getSimpleName();

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private DatabaseReference mDatabase;

    private String userType;

    Button mButtonLogout;
    Button mButtonGotoAppropriateActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        userType = dataSnapshot.getValue().toString();
        mButtonGotoAppropriateActivity.setEnabled(true);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        // TODO: 06/02/17 Notify user for databaseError
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonLogout)
            mAuth.signOut();
        else if (v.getId() == R.id.buttonGotoAppropriateScreen) {
            if (userType.equals("rider")) {
                startActivity(new Intent(getApplicationContext(), RiderActivity.class));
            } else if (userType.equals("driver")) {
                startActivity(new Intent(getApplicationContext(), ViewRequestsActivity.class));
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mAuthListener != null)
            mAuth.removeAuthStateListener(mAuthListener);
    }

    private void init() {
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Toast.makeText(getApplicationContext(), "Logged out successfully", Toast.LENGTH_SHORT)
                            .show();

                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                }
            }
        };

        mButtonLogout = (Button) findViewById(R.id.buttonLogout);
        mButtonLogout.setOnClickListener(this);

        mButtonGotoAppropriateActivity = (Button) findViewById(R.id.buttonGotoAppropriateScreen);
        mButtonGotoAppropriateActivity.setOnClickListener(this);
        mButtonGotoAppropriateActivity.setEnabled(false);

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null)
            mDatabase.child("users").child(user.getUid()).child("userType").addValueEventListener(this);
    }
}
