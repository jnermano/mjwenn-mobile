package com.kasik.mjwenn.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.kasik.mjwenn.models.Address;
import com.kasik.mjwenn.models.Announcement;
import com.kasik.mjwenn.models.Country;
import com.kasik.mjwenn.models.IDCard;
import com.kasik.mjwenn.models.Region;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by Ermano on 6/3/2017.
 * easy way to access elements
 */

public class Session {

    private static Announcement announcement;
    private static List<Address> addresses;
    private static List<Country> countries;
    private static List<Region> regions;
    private static List<IDCard> cards;

    public static Announcement getAnnouncement() {
        return announcement;
    }

    public static void setAnnouncement(Announcement announcement) {
        Session.announcement = announcement;
    }

    public static List<Address> getAddresses() {
        return addresses;
    }

    public static void setAddresses(List<Address> addresses) {
        Session.addresses = addresses;
    }

    public static List<Country> getCountries() {
        return countries;
    }

    public static void setCountries(List<Country> countries) {
        Session.countries = countries;
    }

    public static List<Region> getRegions() {
        return regions;
    }

    public static void setRegions(List<Region> regions) {
        Session.regions = regions;
    }

    public static List<IDCard> getCards() {
        return cards;
    }

    public static void setCards(List<IDCard> cards) {
        Session.cards = cards;
    }

    public static String getDateStamp(){
        Calendar calender = Calendar.getInstance();
        return String.format(Locale.US,
                "%02d/%02d/%04d",
                calender.get(Calendar.DAY_OF_MONTH),
                calender.get(Calendar.MONTH)+1,
                calender.get(Calendar.YEAR));
    }

    public static void dialogInfo(Context context, String title, String msg) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public static String getSearchableString(String s){
        return s.replaceAll("\\s+","").replaceAll("[^A-Za-z0-9]", "-").toLowerCase();
    }
}
