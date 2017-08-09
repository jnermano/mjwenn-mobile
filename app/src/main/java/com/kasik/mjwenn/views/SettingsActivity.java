package com.kasik.mjwenn.views;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.kasik.mjwenn.R;
import com.kasik.mjwenn.adapters.AdapterSettings;
import com.kasik.mjwenn.models.Cls_Menu;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        listView = (ListView) findViewById(R.id.settings_lv_set);
        listView.setOnItemClickListener(this);

        List<Cls_Menu> menus = new ArrayList<>();
        menus.add(new Cls_Menu(R.drawable.user, getResources().getString(R.string.account), ""));
        menus.add(new Cls_Menu(R.drawable.id_card, getResources().getString(R.string.announcements), ""));
        menus.add(new Cls_Menu(R.drawable.question_mark, getResources().getString(R.string.help), ""));

        listView.setAdapter(new AdapterSettings(this, R.layout.adapter_settings, menus));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
