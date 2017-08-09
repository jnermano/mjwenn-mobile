package com.kasik.mjwenn.services;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ermano N. Joseph on 8/9/2017.
 * update client id
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    final static String TAG = "InstanceIDService";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token){
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            Map<String, Object> value = new HashMap<>();
            value.put("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/notificationid/", token);
            FirebaseDatabase.getInstance().getReference().updateChildren(value, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Log.d(TAG, "refreshed token failed");
                    } else {
                        Log.d(TAG, "refreshed token success");
                    }
                }
            });
        }
    }
}
