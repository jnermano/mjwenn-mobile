package com.kasik.mjwenn.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kasik.mjwenn.R;
import com.kasik.mjwenn.models.Cls_Menu;

import java.util.List;

/**
 * Created by Ermano on 6/10/2017.
 */

public class AdapterSettings extends ArrayAdapter<Cls_Menu> {

    List<Cls_Menu> menus;
    Context context;
    LayoutInflater inflater;

    private class ViewHolder{
        ImageView img;
        TextView title;
        TextView desc;
    }

    public AdapterSettings(@NonNull Context c, @LayoutRes int resource, @NonNull List<Cls_Menu> objects) {
        super(c, resource, objects);
        context = c;
        menus = objects;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void add(@Nullable Cls_Menu object) {
        super.add(object);
        menus.add(object);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Nullable
    @Override
    public Cls_Menu getItem(int position) {
        return super.getItem(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.adapter_settings, parent, false);

            holder.title = (TextView) convertView.findViewById(R.id.adapter_settings_tv_title);
            holder.img = (ImageView) convertView.findViewById(R.id.adapter_settings_img_icon);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Cls_Menu m = menus.get(position);

        holder.title.setText(m.getTitle());
        try {
            holder.img.setImageResource(m.getIcon());
        }catch (Exception ex){
            ex.printStackTrace();
        }

        return  convertView;
    }
}
