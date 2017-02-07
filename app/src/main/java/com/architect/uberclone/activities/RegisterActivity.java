package com.architect.uberclone.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.architect.uberclone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import javax.xml.datatype.Duration;

public class RegisterActivity extends Activity implements View.OnClickListener {

    public static final String TAG = RegisterActivity.class.getSimpleName();

    private FirebaseAuth mAuth;

    private DatabaseReference mDatabase;

    Button mButtonRegister;
    EditText mEditTextEmail;
    EditText mEditTextPassword;
    Switch mSwitchRiderOrDriver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
    }

    private void init() {
        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mButtonRegister = (Button) findViewById(R.id.bRegister);
        mButtonRegister.setOnClickListener(this);

        mEditTextEmail = (EditText) findViewById(R.id.et_Email);
        mEditTextPassword = (EditText) findViewById(R.id.et_Password);

        mSwitchRiderOrDriver = (Switch) findViewById(R.id.s_driverOrRider);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bRegister) {
            String _email = mEditTextEmail.getText().toString();
            String _password = mEditTextPassword.getText().toString();

            mAuth.createUserWithEmailAndPassword(_email, _password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Registered successfully", Toast.LENGTH_SHORT)
                                        .show();

                                if (mAuth.getCurrentUser() != null) {
                                    if (!mSwitchRiderOrDriver.isChecked()) {
                                        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("userType")
                                                .setValue("rider");
                                    } else {
                                        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("userType")
                                                .setValue("driver");
                                    }
                                }

                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            } else {
                                Log.e(TAG, String.format("onComplete: %s", task.getException()));
                            }
                        }
                    });
        }
    }
}
