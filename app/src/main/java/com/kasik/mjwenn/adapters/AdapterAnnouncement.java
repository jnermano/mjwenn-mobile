package com.kasik.mjwenn.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.kasik.mjwenn.R;
import com.kasik.mjwenn.models.Announcement;

import java.util.List;

/**
 * Created by Ermano N. Joseph on 8/4/2017.
 * adapter so i can add pub
 */

public class AdapterAnnouncement extends ArrayAdapter<Announcement> {

    List<Announcement> announcements;
    Context context;
    LayoutInflater inflater;


    public AdapterAnnouncement(@NonNull Context c, @LayoutRes int resource, @NonNull List<Announcement> objects) {
        super(c, resource, objects);

        context = c;
        announcements = objects;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void add(@Nullable Announcement object) {
        super.add(object);
        notifyDataSetChanged();
    }

    @Override
    public void remove(@Nullable Announcement object) {
        super.remove(object);
        notifyDataSetChanged();
    }

    @Override
    public void insert(@Nullable Announcement object, int index) {
        super.insert(object, index);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return announcements.size();
    }


    @Override
    public Announcement getItem(int i) {
        return announcements.get(i);
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        Announcement announcement = announcements.get(i);

        if (!announcement.getId().equals("pub")){
            view = inflater.inflate(R.layout.adapter_announcement, viewGroup, false);

            ((TextView) view.findViewById(R.id.adapter_ann_name)).setText(announcement.toString());
            ((TextView) view.findViewById(R.id.adapter_ann_card_type)).setText(announcement.getCard());
            try {
                ((TextView) view.findViewById(R.id.adapter_ann_location)).setText(announcement.getAddress().toString());
            } catch (Exception e) {
                ((TextView) view.findViewById(R.id.adapter_ann_location)).setText(announcement.getAddr());
                e.printStackTrace();
            }
        }else{

            view = inflater.inflate(R.layout.native_express_ads_container, viewGroup, false);
            NativeExpressAdView adView = (NativeExpressAdView)view.findViewById(R.id.adView);
            AdRequest request = new AdRequest.Builder().build();
            adView.loadAd(request);
        }

        return  view;
    }



}
