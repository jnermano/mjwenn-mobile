package com.kasik.mjwenn.views;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kasik.mjwenn.R;
import com.kasik.mjwenn.models.Announcement;
import com.kasik.mjwenn.utils.Session;

import java.util.Locale;

public class AnnouncementDetails extends AppCompatActivity {

    final static String TAG = "Details Activity";
    
    Announcement announcement;
    DatabaseReference mRef;
    ValueEventListener valueEventListener;

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final float scale = AnnouncementDetails.this.getResources().getDisplayMetrics().density;
        final int adWidth = AnnouncementDetails.this.getResources().getDisplayMetrics().widthPixels;
        AdSize adSize = new AdSize((int) (adWidth / scale), 150);

        MobileAds.initialize(getApplicationContext(), getResources().getString(R.string.banner_ad_unit_id));
        mAdView = (AdView) findViewById(R.id.details_announcament_adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Session.getAnnouncement() == null){
            setResult(RESULT_CANCELED);
            finish();
        }

        final ProgressDialog dialog = new ProgressDialog(this, android.app.AlertDialog.THEME_HOLO_LIGHT);
        dialog.setIndeterminate(true);
        dialog.setMessage("Loading data ...");
        dialog.show();


        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                announcement = dataSnapshot.getValue(Announcement.class);
                dialog.dismiss();
                if (announcement != null) {
                    announcement.setId(dataSnapshot.getKey());
                    fillCompos();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        try {
            mRef = FirebaseDatabase.getInstance().getReference("announcements").child(Session.getAnnouncement().getId());
            mRef.addValueEventListener(valueEventListener);
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Toast.makeText(AnnouncementDetails.this, "Ad loaded", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                Toast.makeText(AnnouncementDetails.this, "Ad can't be loaded, " + i, Toast.LENGTH_LONG).show();
            }
        });


    }
    
    private void fillCompos(){
        try {

            ((TextView) findViewById(R.id.ann_tv_name)).setText(announcement.toString());
            ((TextView) findViewById(R.id.ann_tv_card_type)).setText(announcement.getCard());
            ((TextView) findViewById(R.id.ann_tv_date)).setText(String.format(Locale.US, getString(R.string.publish_date), announcement.getDatecreated()));

            ((TextView) findViewById(R.id.ann_tv_address)).setText(String.format(Locale.US, "  %s", announcement.getAddress().toString()));
            ((TextView) findViewById(R.id.ann_tv_tel)).setText(String.format(Locale.US, "  %s", announcement.getAddress().getTelephone()));
            ((TextView) findViewById(R.id.ann_tv_email)).setText(String.format(Locale.US, "  %s", announcement.getAddress().getEmail()));


        } catch (Exception e) {
            e.printStackTrace();
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("ERROR!!!");
            alertDialog.setMessage(getString(R.string.nofound));
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            setResult(RESULT_CANCELED);
                            finish();
                        }
                    });
            alertDialog.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_announcement, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share_ann) {
            Intent sendIntent = new Intent();
            String msg = String.format(Locale.US, "https://exc23.app.goo.gl/?link=http://mjwenn.com/id/%s&apn=com.kasik.mjwenn&ifl=http://mjwenn.com/id/%s", announcement.getId(), announcement.getId());
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, (announcement.getSharelink()!=null? announcement.getSharelink():msg));
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onBackPressed(){
        Session.setAnnouncement(null);
        if (mRef != null)
            mRef.removeEventListener(valueEventListener);
        super.onBackPressed();

    }

    @Override
    public void onStop(){
        Session.setAnnouncement(null);
        if (mRef != null)
            mRef.removeEventListener(valueEventListener);
        super.onStop();
    }

    @Override
    public void onDestroy(){
        Session.setAnnouncement(null);
        if (mRef != null)
            mRef.removeEventListener(valueEventListener);
        super.onDestroy();
    }



}
