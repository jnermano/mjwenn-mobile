package com.kasik.mjwenn;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kasik.mjwenn.adapters.AdapterAnnouncement;
import com.kasik.mjwenn.models.Address;
import com.kasik.mjwenn.models.Announcement;
import com.kasik.mjwenn.models.Country;
import com.kasik.mjwenn.models.IDCard;
import com.kasik.mjwenn.models.Region;
import com.kasik.mjwenn.utils.Session;
import com.kasik.mjwenn.views.AccountActivity;
import com.kasik.mjwenn.views.AnnouncementDetails;
import com.kasik.mjwenn.views.NewAnnoucement;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        SearchView.OnQueryTextListener{

    final static String TAG = "Main Activity";

    private final static int NEW_ANN = 111;

    private FirebaseAnalytics mFirebaseAnalytics;
    private LinearLayoutManager mLinearLayoutManager;

    private RecyclerView mRecyclerView;
    private FirebaseRecyclerAdapter<Announcement, AnnouncementHolder>
            mFirebaseAdapter;

    private ListView listView;
    private AdapterAnnouncement adapterAnnouncement;
    private List<Announcement> announcements;

    private int loaded = 0;
    private GoogleApiClient mGoogleApiClient;

    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Build GoogleApiClient with AppInvite API for receiving deep links
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(AppInvite.API)
                .build();
        mGoogleApiClient.connect();
        checkDeepLink();

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.main_fab_add);
        fab.setOnClickListener(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.main_recyclerview);
        mLinearLayoutManager = new LinearLayoutManager(this);
        //mLinearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        listView = (ListView) findViewById(R.id.main_listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Announcement a = ((Announcement)parent.getItemAtPosition(position));
                if (!a.getId().equals("pub")){
                    Session.setAnnouncement(a);
                    Intent i = new Intent(MainActivity.this, AnnouncementDetails.class);
                    startActivity(i);
                }

            }
        });

        announcements = new ArrayList<>();
        adapterAnnouncement = new AdapterAnnouncement(this, R.layout.adapter_announcement, announcements);
        listView.setAdapter(adapterAnnouncement);
        adapterAnnouncement.setNotifyOnChange(true);

        loadParameters();
        //fillRecycler("");
        fillListView("");



    }

    private void fillListView(final String ss){

        if (ss.length() > 0)
            adapterAnnouncement.clear();

        FirebaseDatabase.getInstance().getReference("announcements")
                .orderByChild("filter")
                .startAt("00HT" + ss)
                .endAt("00HT" + ss + "\uf8ff") // + s + "\uf8ff"
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Announcement a = dataSnapshot.getValue(Announcement.class);
                        if (a != null){
                            if (a.toString().contains(ss) || (a.getLastname() + " " + a.getFirstname()).contains(ss)){
                                a.setId(dataSnapshot.getKey());
                                adapterAnnouncement.insert(a, 0);
                                Log.v(TAG, "nb anns : " + adapterAnnouncement.getCount());

                            }
                        }
                        if (adapterAnnouncement.getCount() % 3 == 0){
                            Announcement ann = new Announcement();
                            ann.setId("pub");
                            adapterAnnouncement.insert(ann, 0);
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        Announcement a = dataSnapshot.getValue(Announcement.class);
                        if (a != null){
                            if (a.toString().contains(ss) || (a.getLastname() + " " + a.getFirstname()).contains(ss)){
                                a.setId(dataSnapshot.getKey());
                                for (Announcement ann:announcements){
                                    if (ann.getId().equals(a.getId())){
                                        ann.setLastname(a.getLastname());
                                        ann.setFirstname(a.getFirstname());
                                        ann.setCard(a.getCard());
                                        ann.setAddr(a.getAddr());
                                        ann.setAddress(a.getAddress());
                                        break;
                                    }
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapterAnnouncement.notifyDataSetChanged();
                                    }
                                });
                                Log.v(TAG, "nb anns : " + announcements.size());
                            }
                        }
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        Announcement a = dataSnapshot.getValue(Announcement.class);
                        if (a != null){
                            Log.v(TAG, a.toString());
                            if (a.toString().contains(ss) || (a.getLastname() + " " + a.getFirstname()).contains(ss)){
                                a.setId(dataSnapshot.getKey());
                                for (Announcement ann:announcements){
                                    if (ann.getId().equals(a.getId())){
                                        adapterAnnouncement.remove(ann);
                                        break;
                                    }
                                }

                                Log.v(TAG, "nb anns : " + announcements.size());
                            }
                        }
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void fillRecycler(String s) {
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Announcement, AnnouncementHolder>(
                Announcement.class,
                R.layout.adapter_announcement,
                AnnouncementHolder.class,
                FirebaseDatabase.getInstance().getReference("announcements")
                        .orderByChild("filter")
                        .startAt("00HT" + s)
                        .endAt("00HT" + s + "\uf8ff")
        ) {
            @Override
            protected void populateViewHolder(AnnouncementHolder viewHolder, final Announcement model, int position) {
                model.setId(getRef(position).getKey());
                viewHolder.tv_name.setText(model.toString());
                viewHolder.tv_card.setText(model.getCard());
                try {
                    viewHolder.tv_address.setText(model.getAddress().toString());
                } catch (Exception e) {
                    viewHolder.tv_address.setText(model.getAddr());
                    e.printStackTrace();
                }

                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Session.setAnnouncement(model);
                        Intent i = new Intent(MainActivity.this, AnnouncementDetails.class);
                        startActivity(i);
                    }
                });
            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
            }
        });

        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mFirebaseAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_main_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setBackgroundResource(android.R.drawable.edit_text);
        searchView.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        ((EditText)searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text)).setTextColor(Color.BLUE);
        ((EditText)searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text)).setHintTextColor(Color.GRAY);

        searchView.setOnQueryTextListener(this);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_main_invite:
                Intent sendIntent = new Intent();
                String msg = getString(R.string.invite_msg) + "https://exc23.app.goo.gl/Y2QC";
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, msg);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                return true;
            case R.id.action_main_account:
                Intent i = new Intent(this, AccountActivity.class);
                startActivity(i);
                return true;
        }

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_fab_add:
                Session.setAnnouncement(null);
                newAnnoucement();
                break;
        }
    }

    private void newAnnoucement() {
        Intent i = new Intent(this, NewAnnoucement.class);
        startActivityForResult(i, NEW_ANN);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String search = Session.getSearchableString(newText);
        Log.d(TAG, "search : " + search);
        fillListView(search);
        return false;
    }


    private static class AnnouncementHolder extends RecyclerView.ViewHolder {
        public TextView tv_name, tv_card, tv_address;
        public View view;

        public AnnouncementHolder(View itemView) {
            super(itemView);
            view = itemView;
            tv_name = (TextView) view.findViewById(R.id.adapter_ann_name);
            tv_card = (TextView) view.findViewById(R.id.adapter_ann_card_type);
            tv_address = (TextView) view.findViewById(R.id.adapter_ann_location);
        }
    }

    private void loadParameters() {
        Session.setCountries(new ArrayList<Country>());
        Session.setRegions(new ArrayList<Region>());
        Session.setCards(new ArrayList<IDCard>());
        Session.setAddresses(new ArrayList<Address>());

        final ProgressDialog dialog = new ProgressDialog(this, AlertDialog.THEME_HOLO_LIGHT);
        dialog.setIndeterminate(true);
        dialog.setMessage("Loading data ...");
        dialog.show();

        loaded = 0;

        DatabaseReference mCountriesRef = FirebaseDatabase.getInstance().getReference("countries");

        mCountriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "count : " + dataSnapshot.getChildrenCount());
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    Log.d(TAG, "count : " + d.getChildrenCount());
                    Country country = d.getValue(Country.class);
                    if (country != null) {
                        country.setId(d.getKey());
                        Session.getCountries().add(country);
                        getRegions(country);
                    }
                }
                isDataLoaded(dialog);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        DatabaseReference mCardsRef = FirebaseDatabase.getInstance().getReference("cards");
        mCardsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    IDCard c = d.getValue(IDCard.class);
                    if (c != null) {
                        c.setId(d.getKey());
                        Session.getCards().add(c);
                    }
                }
                isDataLoaded(dialog);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            DatabaseReference mAddressesRef = FirebaseDatabase.getInstance().getReference("user_addresses");
            mAddressesRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot d : dataSnapshot.getChildren()) {
                                Address c = d.getValue(Address.class);
                                if (c != null) {
                                    c.setId(d.getKey());
                                    boolean is_in = false;
                                    for (Address ad:Session.getAddresses()){
                                        if (ad.getId().equals(c.getId())){
                                            is_in = true;
                                            //TODO : update address
                                            break;
                                        }
                                    }

                                    if (!is_in)
                                        Session.getAddresses().add(c);
                                }
                            }

                            isDataLoaded(dialog);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        } else {
            isDataLoaded(dialog);
        }

        Address a = new Address();
        a.setId("0");
        a.setAddress("New");
        Session.getAddresses().add(a);


    }

    private void getRegions(final Country c){
        DatabaseReference mRegRef = FirebaseDatabase.getInstance().getReference("regions");
        mRegRef.child(c.getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG, "count regions : " + dataSnapshot.getChildrenCount());
                        List<Region> regions = new ArrayList<Region>();
                        for (DataSnapshot d: dataSnapshot.getChildren()){
                            Region r = d.getValue(Region.class);
                            if (r != null)
                                regions.add(r);
                        }
                        for (Country cc : Session.getCountries()){
                            if (cc.getId().equals(c.getId())){
                                cc.setRegions(regions);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void isDataLoaded(ProgressDialog dialog) {
        loaded += 1;
        if (loaded >= 3){
            try {
                dialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //Toast.makeText(this, "Status " + loaded + "/3", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        checkDeepLink();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void checkDeepLink(){
        // Check if this app was launched from a deep link. Setting autoLaunchDeepLink to true
        // would automatically launch the deep link if one is found.
        boolean autoLaunchDeepLink = false;
        AppInvite.AppInviteApi.getInvitation(mGoogleApiClient, this, autoLaunchDeepLink)
                .setResultCallback(
                        new ResultCallback<AppInviteInvitationResult>() {
                            @Override
                            public void onResult(@NonNull AppInviteInvitationResult result) {
                                if (result.getStatus().isSuccess()) {
                                    // Extract deep link from Intent
                                    Intent intent = result.getInvitationIntent();
                                    String deepLink = AppInviteReferral.getDeepLink(intent);

                                    Log.d(TAG, deepLink);

                                    try {
                                        String link[] = deepLink.split("/");
                                        Session.setAnnouncement(new Announcement());
                                        Session.getAnnouncement().setId(link[link.length-1]);
                                        Intent i = new Intent(MainActivity.this, AnnouncementDetails.class);
                                        startActivity(i);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                } else {
                                    Log.d(TAG, "getInvitation: no deep link found.");
                                }
                            }
                        });
    }

}
