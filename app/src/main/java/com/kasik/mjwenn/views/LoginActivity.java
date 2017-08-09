package com.kasik.mjwenn.views;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kasik.mjwenn.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    final static String TAG = "Login Activity";
    final static int SIGNUP_ACTIVITY = 101;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    EditText edt_pseudo, edt_password;
    TextView tv_error;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    setResult(RESULT_OK);
                    finish();
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }

            }
        };

        initCompos();

    }

    private void initCompos() {
        edt_pseudo = (EditText) findViewById(R.id.login_edt_pseudo);
        edt_password = (EditText) findViewById(R.id.login_edt_password);

        tv_error = (TextView) findViewById(R.id.login_tv_error);

        ((Button) findViewById(R.id.login_btn_connect)).setOnClickListener(this);
        ((TextView) findViewById(R.id.login_tv_signup)).setOnClickListener(this);
        ((TextView) findViewById(R.id.login_tv_forgot)).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()) {
            case R.id.login_btn_connect:
                initConnexion();
                break;
            case R.id.login_tv_signup:
                i = new Intent(this, SignupActivity.class);
                startActivityForResult(i, SIGNUP_ACTIVITY);
                break;
            case R.id.login_tv_forgot:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SIGNUP_ACTIVITY){
            if (resultCode == RESULT_OK){
                setResult(RESULT_OK);
                finish();
            }
        }
    }

    private void initConnexion() {
        String email = edt_pseudo.getText().toString();
        String password = edt_password.getText().toString();
        boolean gotError = false;

        if (email.length() < 5) {
            gotError = true;
            edt_pseudo.setError(getResources().getString(R.string.email_too_short));
        }

        if (password.length() < 5) {
            gotError = true;
            edt_password.setError(getResources().getString(R.string.password_too_short));
        }

        if (!gotError) {

            final ProgressDialog dialog = new ProgressDialog(this, AlertDialog.THEME_HOLO_LIGHT);
            dialog.setIndeterminate(true);
            dialog.setMessage("Auth ...");
            dialog.show();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                            dialog.dismiss();

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "signInWithEmail:failed", task.getException());
                                Toast.makeText(LoginActivity.this, "Auth has failed", Toast.LENGTH_SHORT).show();
                                tv_error.setVisibility(View.VISIBLE);
                                try {
                                    tv_error.setText(task.getException().getMessage());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                setResult(RESULT_OK);
                                finish();
                            }

                        }
                    });
        }

    }
}
