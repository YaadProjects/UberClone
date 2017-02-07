package com.architect.uberclone.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.architect.uberclone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends Activity implements View.OnClickListener {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private Button mButtonLogin;
    private Button mButtonLoginExternalProvider;
    private Button mButtonRegister;
    private EditText mEditTextEmail;
    private EditText getmEditTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonLogin)
            performLogin();
        else if (v.getId() == R.id.buttonExternalLogin)
            startActivity(new Intent(getApplicationContext(), LoginWithExternalProviderActivity.class));
        else if (v.getId() == R.id.buttonRegister) {
            Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(intent);
        }
    }

    private void performLogin() {
        String _email = mEditTextEmail.getText().toString();
        String _password = getmEditTextPassword.getText().toString();

        mAuth.signInWithEmailAndPassword(_email, _password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {
                            Log.e(TAG, String.format("onComplete: %s", task.getException()));
                        }
                    }
                });
    }

    private void init() {
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
            }
        };

        mButtonLogin = (Button) findViewById(R.id.buttonLogin);
        mButtonLogin.setOnClickListener(this);

        mButtonLoginExternalProvider = (Button) findViewById(R.id.buttonExternalLogin);
        mButtonLoginExternalProvider.setOnClickListener(this);

        mButtonRegister = (Button) findViewById(R.id.buttonRegister);
        mButtonRegister.setOnClickListener(this);

        mEditTextEmail = (EditText) findViewById(R.id.editText_email);
        getmEditTextPassword = (EditText) findViewById(R.id.editText_password);
    }
}
