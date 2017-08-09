package com.kasik.mjwenn.views;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kasik.mjwenn.R;
import com.kasik.mjwenn.models.Address;
import com.kasik.mjwenn.models.Announcement;
import com.kasik.mjwenn.models.Country;
import com.kasik.mjwenn.models.IDCard;
import com.kasik.mjwenn.models.Region;
import com.kasik.mjwenn.utils.RequestHandler;
import com.kasik.mjwenn.utils.Session;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class NewAnnoucement extends AppCompatActivity {

    final static String TAG = "AnnouncementDetails";
    final static int LOGIN_ACTIVITY = 101;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private EditText edt_lname, edt_fname, edt_cardid, edt_name, edt_phone, edt_email;
    private Spinner spi_type_ann, spi_cards, spi_address, spi_country, spi_region;
    private LinearLayout ll_address;
    private AutoCompleteTextView edt_address;

    private Announcement announcement;
    private Address selectAddress;

    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_annoucement);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();

        dialog = new ProgressDialog(this, AlertDialog.THEME_HOLO_LIGHT);
        dialog.setIndeterminate(true);
        dialog.setMessage("Saving ...");

        if (mAuth.getCurrentUser() == null) {
            Intent i = new Intent(NewAnnoucement.this, LoginActivity.class);
            startActivityForResult(i, LOGIN_ACTIVITY);
            Log.d(TAG, "onAuthStateChanged:signed_out");
        }

        initCompos();
        announcement = new Announcement();
        if (Session.getAnnouncement() != null) {
            announcement = Session.getAnnouncement();
            fillCompos();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void initCompos() {
        edt_lname = (EditText) findViewById(R.id.new_ann_edt_lname);
        edt_fname = (EditText) findViewById(R.id.new_ann_edt_fname);
        edt_cardid = (EditText) findViewById(R.id.new_ann_edt_cardid);
        edt_address = (AutoCompleteTextView) findViewById(R.id.new_ann_edt_address);

        edt_address.setAdapter(new ArrayAdapter<Address>(
                this,
                android.R.layout.simple_list_item_1,
                Session.getAddresses()
        ));

        edt_address.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectAddress = ((Address) parent.getItemAtPosition(position));
                announcement.setAddress(selectAddress);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        edt_phone = (EditText) findViewById(R.id.new_ann_edt_tel);
        edt_email = (EditText) findViewById(R.id.new_ann_edt_email);

        ll_address = (LinearLayout) findViewById(R.id.new_ann_ll_address);

        spi_type_ann = (Spinner) findViewById(R.id.new_ann_spi_type);

        spi_cards = (Spinner) findViewById(R.id.new_ann_spi_card);
        spi_cards.setAdapter(new ArrayAdapter<IDCard>(this, android.R.layout.simple_list_item_1, Session.getCards()));

        spi_address = (Spinner) findViewById(R.id.new_ann_spi_address);
        spi_address.setAdapter(new ArrayAdapter<Address>(this, android.R.layout.simple_list_item_1, Session.getAddresses()));

        spi_country = (Spinner) findViewById(R.id.new_ann_spi_country);
        spi_country.setAdapter(new ArrayAdapter<Country>(this, android.R.layout.simple_list_item_1, Session.getCountries()));
        spi_country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spi_region.setAdapter(new ArrayAdapter<Region>(
                        NewAnnoucement.this,
                        android.R.layout.simple_list_item_1,
                        ((Country) parent.getItemAtPosition(position)).getRegions()
                ));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spi_region = (Spinner) findViewById(R.id.new_ann_spi_region);

        spi_address.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (((Address) parent.getItemAtPosition(position)).getId().equals("0")) {
                    ll_address.setVisibility(View.VISIBLE);
                } else
                    ll_address.setVisibility(View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    private void fillCompos() {

        spi_type_ann.setSelection(announcement.getType());

        for (int i = 0; i < spi_cards.getCount(); i++){
            if (((IDCard)spi_cards.getItemAtPosition(i)).getCard().equals(announcement.getCard())){
                spi_cards.setSelection(i);
                break;
            }
        }

        edt_fname.setText(announcement.getFirstname());
        edt_lname.setText(announcement.getLastname());
        edt_cardid.setText(announcement.getCardid());

        for (int i = 0; i < spi_address.getCount(); i++){
            if (((Address)spi_address.getItemAtPosition(i)).getAddress().equals(announcement.getAddr())){
                spi_address.setSelection(i);
                break;
            }
        }

    }

    private void setValues() {
        boolean goterrors = false;

        announcement.setType(spi_type_ann.getSelectedItemPosition());
        announcement.setCard(((IDCard) spi_cards.getSelectedItem()).getCard());
        announcement.setLastname(edt_lname.getText().toString());
        if (announcement.getLastname().length() < 3) {
            goterrors = true;
            edt_lname.setError("Check Lastname");
        }
        announcement.setFirstname(edt_fname.getText().toString());
        if (announcement.getFirstname().length() < 3) {
            goterrors = true;
            edt_fname.setError("Check Firstname");
        }

        announcement.setCardid(edt_cardid.getText().toString());
        if (announcement.getCardid().length() < 3) {
            goterrors = true;
            edt_cardid.setError("Check Card ID");
        }

        if (((Address) spi_address.getSelectedItem()).getId().equals("0")) {

            if (announcement.getAddress() == null)
                announcement.setAddress(new Address());

            Address address = announcement.getAddress();

            address.setCountry(((Country) spi_country.getSelectedItem()).getCountry());
            address.setRegion(((Region) spi_region.getSelectedItem()).getRegion());
            address.setAddress(edt_address.getText().toString());
            address.setTelephone(edt_phone.getText().toString());
            address.setEmail(edt_email.getText().toString());

            if (address.getAddress().length() < 3) {
                goterrors = true;
                edt_address.setError("Check Address");
            }

        } else
            announcement.setAddress(((Address) spi_address.getSelectedItem()));

        if (!goterrors) {

            dialog.show();

            announcement.setAddr(announcement.getAddress().getAddress());

            if (announcement.getId() == null) {
                announcement.setDatecreated(Session.getDateStamp());
                announcement.setId(FirebaseDatabase.getInstance().getReference("/user_announcements/" + mAuth.getCurrentUser().getUid()).push().getKey());
                announcement.setFilter(announcement.getType() + "0HT" + Session.getSearchableString(announcement.getFirstname() + " " + announcement.getLastname()) );
            }

            Map<String, Object> childUpdates = new HashMap<>();
            //childUpdates.put("/announcements/" + announcement.getId(), announcement);
            childUpdates.put("/user_announcements/" + mAuth.getCurrentUser().getUid() + "/" + announcement.getId(), announcement);

            if (((Address) spi_address.getSelectedItem()).getId().equals("0")) {
                childUpdates.put("/user_addresses/" + mAuth.getCurrentUser().getUid() + "/" + announcement.getId(), announcement.getAddress());
            }

            FirebaseDatabase.getInstance().getReference().updateChildren(
                    childUpdates,
                    new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Session.dialogInfo(NewAnnoucement.this, "ERROR!!!", "Error while submitting the announcement");
                            } else {
                                Toast.makeText(NewAnnoucement.this, "Success", Toast.LENGTH_LONG).show();
                                //createShortLink();
                                setResult(RESULT_OK);
                                finish();
                            }
                        }
                    }
            );
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.new_menu_action_finish) {
            setValues();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case LOGIN_ACTIVITY:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this, "Connected", Toast.LENGTH_LONG).show();
                } else {
                    setResult(RESULT_CANCELED);
                    finish();
                }
                break;

        }

    }

    private void createShortLink() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {

                try {
                    String url = "https://firebasedynamiclinks.googleapis.com/v1/shortLinks?key=AIzaSyCUTCSR2CbqlYGXkXBGHpGZxLsK_VY1J6M";

                    JSONObject jsonObject = new JSONObject();/*,
                            androidInfo = new JSONObject(),
                            socialMetaTagInfo = new JSONObject(),
                            iosInfo = new JSONObject(),
                            suffix = new JSONObject();

                    suffix.put("option", "SHORT");

                    iosInfo.put("iosFallbackLink", String.format(Locale.US,"https://mjwenn.com/id/%s", announcement.getId() ));

                    socialMetaTagInfo.put("socialTitle", announcement.toString());
                    socialMetaTagInfo.put("socialDescription", announcement.getCard());
                    socialMetaTagInfo.put("socialImageLink", "https://firebasestorage.googleapis.com/v0/b/mjwenn-9f4f5.appspot.com/o/credit-card.png?alt=media&token=ebb0b635-e929-48c0-87c6-f0a379d33233");

                    androidInfo.put("androidPackageName", getPackageName());

                    jsonObject.put("dynamicLinkDomain", "https://exc23.app.goo.gl/");
                    jsonObject.put("link", String.format(Locale.US,"https://mjwenn.com/id/%s", announcement.getId() ));
                    jsonObject.put("androidInfo", androidInfo);
                    jsonObject.put("iosInfo", iosInfo);
                    jsonObject.put("socialMetaTagInfo", socialMetaTagInfo);
                    jsonObject.put("suffix", suffix);*/


                    Uri uri = (new Uri.Builder()
                            .scheme("https")
                            .authority("exc23.app.goo.gl")
                            .path("/")
                            .appendQueryParameter("link", String.format(Locale.US, "https://mjwenn.com/id/%s", announcement.getId()))
                            .appendQueryParameter("apn", getPackageName())
                            .appendQueryParameter("ifl", String.format(Locale.US, "https://mjwenn.com/id/%s", announcement.getId()))
                            .appendQueryParameter("socialTitle", announcement.toString())
                            .appendQueryParameter("socialDescription", announcement.getCard())
                            .appendQueryParameter("socialImageLink", "https://firebasestorage.googleapis.com/v0/b/mjwenn-9f4f5.appspot.com/o/credit-card.png?alt=media&token=ebb0b635-e929-48c0-87c6-f0a379d33233")
                            .appendQueryParameter("option", "SHORT")
                    ).build();

/*
String.format(
        Locale.US,
        "https://exc23.app.goo.gl/?link=https://mjwenn.com/id/%s&apn=com.kasik.mjwenn&ifl=https://mjwenn.com/id/%s",
        announcement.getId(),
        announcement.getId()
)
 */

                    jsonObject.put(
                            "longDynamicLink",
                            uri.toString()
                            );

                    String options = jsonObject.toString();

                    Log.d(TAG, options);
                    String result = new RequestHandler().sendPostJSONRequest(url, options);
                    Log.d(TAG, result);

                    JSONObject jsonres = new JSONObject(result);
                    Log.d(TAG, jsonres.getString("shortLink"));

                    announcement.setSharelink(jsonres.getString("shortLink"));
                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("/announcements/" + announcement.getId(), announcement);
                    childUpdates.put("/user_announcements/" + mAuth.getCurrentUser().getUid() + "/" + announcement.getId(), announcement);

                    FirebaseDatabase.getInstance().getReference().updateChildren(
                            childUpdates,
                            new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    dialog.dismiss();
                                    if (databaseError != null) {
                                        Session.dialogInfo(NewAnnoucement.this, "ERROR!!!", "Error while submitting the announcement");
                                    } else {
                                        setResult(RESULT_OK);
                                        finish();
                                    }
                                }
                            }
                    );

                    return null;
                } catch (JSONException e) {
                    dialog.dismiss();
                    e.printStackTrace();
                    return null;
                }
            }
        }.execute();
    }


}
