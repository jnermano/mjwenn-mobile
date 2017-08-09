package com.kasik.mjwenn.views;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.kasik.mjwenn.R;
import com.kasik.mjwenn.models.Country;
import com.kasik.mjwenn.models.User;
import com.kasik.mjwenn.utils.Session;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {
    final static String TAG = "Signup Activity";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    EditText edt_name, edt_pseudo, edt_password, edt_re_passord;
    TextView tv_error;
    Spinner spi_country;

    private int status = 0;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        status = 0;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        initCompos();

    }

    void initCompos(){
        tv_error = (TextView) findViewById(R.id.signup_tv_error);

        edt_name = (EditText) findViewById(R.id.signup_edt_name);
        edt_pseudo = (EditText) findViewById(R.id.signup_edt_pseudo);
        edt_password = (EditText) findViewById(R.id.signup_edt_password);
        edt_re_passord = (EditText) findViewById(R.id.signup_edt_re__password);

        ((Button) findViewById(R.id.signup_btn_connect)).setOnClickListener(this);

        spi_country = (Spinner) findViewById(R.id.signup_spi_country);
        spi_country.setAdapter(new ArrayAdapter<Country>(this, android.R.layout.simple_list_item_1, Session.getCountries()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.signup_btn_connect:
                loginEmailPassword();
                break;
        }
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

    void loginEmailPassword(){

        boolean gotError = false;

        String error = "", email = edt_pseudo.getText().toString(),
                password = edt_password.getText().toString(),
                re_password = edt_re_passord.getText().toString(),
                name = edt_name.getText().toString();

        if (email.length() < 6){
            gotError = true;
            error += getResources().getString(R.string.email_too_short) + "\n";
            edt_pseudo.setError(getResources().getString(R.string.email_too_short));
        }


        if (!password.equals(re_password)){
            gotError = true;
            error += getResources().getString(R.string.password_must_match) + "\n";
            edt_password.setError(getResources().getString(R.string.password_must_match));
            edt_re_passord.setError(getResources().getString(R.string.password_must_match));
        }

        if (password.length() < 6){
            gotError = true;
            error += getResources().getString(R.string.password_lenght) + "\n";
            edt_password.setError(getResources().getString(R.string.password_lenght));
            edt_re_passord.setError(getResources().getString(R.string.password_lenght));
        }

        if (!gotError){

            dialog = new ProgressDialog(this, AlertDialog.THEME_HOLO_LIGHT);
            dialog.setIndeterminate(true);
            dialog.setMessage("Auth ...");
            dialog.setCancelable(false);
            dialog.show();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");

                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(edt_name.getText().toString())
                                        .setPhotoUri(Uri.parse("https://firebasestorage.googleapis.com/v0/b/mjwenn-9f4f5.appspot.com/o/user_64.png?alt=media&token=b26aaa8a-1c33-43ce-b24d-06e17e5fbe5d"))
                                        .build();

                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                User u = new User();
                                u.setCountry(((Country) spi_country.getSelectedItem()).getCode());
                                u.setNotificationid(FirebaseInstanceId.getInstance().getToken());
                                u.setUid(user.getUid());
                                Map<String, Object> update = new HashMap<>();
                                update.put("users/" +  user.getUid(), u);
                                FirebaseDatabase.getInstance().getReference().updateChildren(update, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        if (databaseError != null){
                                            dialog.dismiss();
                                            Log.d(TAG, "User extend profile updated.");
                                        }else {
                                            Log.d(TAG, "User extend profile updated.");
                                            status += 1;
                                            finish_activity();
                                        }
                                    }
                                });

                                user.updateProfile(profileUpdates)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(TAG, "User profile updated.");
                                                    status += 1;
                                                    finish_activity();
                                                }else {
                                                    dialog.dismiss();
                                                    Log.d(TAG, "User profile update failed.");
                                                    tv_error.setVisibility(View.VISIBLE);

                                                    try {
                                                        tv_error.setText(task.getException().getMessage());
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                        });

                            } else {
                                dialog.dismiss();
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(SignupActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();

                                tv_error.setVisibility(View.VISIBLE);

                                try {
                                    tv_error.setText(task.getException().getMessage());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    });
        }else {
            tv_error.setVisibility(View.VISIBLE);
            tv_error.setText(error);
        }


    }

    private void finish_activity(){
        if (status  == 2){
            dialog.dismiss();
            setResult(RESULT_OK);
            finish();
        }
    }

}
