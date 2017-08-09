package com.kasik.mjwenn.models;

import com.google.firebase.database.Exclude;

/**
 * Created by Ermano on 6/3/2017.
 * annonce & alert
 */

public class Announcement {

    private String id;
    private int type;
    private String stype;
    private String card;
    private String name;
    private String firstname;
    private String lastname;
    private String cardid;
    private int status;
    private String addr;
    private String sharelink;

    private String datecreated;
    private String datereturned;

    private String filter;  //00HTfirstnamelastname

    private Address address;


    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getStype() {
        return stype;
    }

    public void setStype(String stype) {
        this.stype = stype;
    }

    public String getSharelink() {
        return sharelink;
    }

    public void setSharelink(String sharelink) {
        this.sharelink = sharelink;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getCardid() {
        return cardid;
    }

    public void setCardid(String cardid) {
        this.cardid = cardid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getDatecreated() {
        return datecreated;
    }

    public void setDatecreated(String datecreated) {
        this.datecreated = datecreated;
    }

    public String getDatereturned() {
        return datereturned;
    }

    public void setDatereturned(String datereturned) {
        this.datereturned = datereturned;
    }


    @Override
    public String toString() {
        return firstname + " " + lastname;
    }
}
