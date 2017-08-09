package com.kasik.mjwenn.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kasik.mjwenn.R;
import com.kasik.mjwenn.adapters.AdapterAccountMenu;
import com.kasik.mjwenn.models.Cls_Menu;
import com.kasik.mjwenn.views.ProfileUpdateActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ermano on 6/29/2017.
 * profile user
 */

public class FragmentProfile extends Fragment {
    private static final String TAG = "Fragment profile";

    Context context;

    List<Cls_Menu> menus;
    AdapterAccountMenu adapterAccountMenu;
    ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        context = v.getContext();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        menus = new ArrayList<>();
        adapterAccountMenu = new AdapterAccountMenu(context, R.layout.adapter_account_menu, menus);

        listView = (ListView) v.findViewById(R.id.frag_profile_list_menu);
        listView.setAdapter(adapterAccountMenu);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                action_menu(position);
            }
        });


        adapterAccountMenu.add(new Cls_Menu(R.drawable.user_64, user.getDisplayName(), user.getEmail()));
        adapterAccountMenu.add(new Cls_Menu(R.drawable.key_64, getResources().getString(R.string.password), getResources().getString(R.string.password)));



        //setHasOptionsMenu(true);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_profile, menu);  // Use filter.xml from step 1
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    private void action_menu(int position){
        Intent i;
        switch (position){
            case 0:
                i = new Intent(context, ProfileUpdateActivity.class);
                startActivity(i);
                break;
            case 1:
                break;
        }
    }
}


