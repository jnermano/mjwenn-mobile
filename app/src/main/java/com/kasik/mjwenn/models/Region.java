package com.kasik.mjwenn.models;

import com.google.firebase.database.Exclude;

/**
 * Created by Ermano on 6/3/2017.
 * region by country
 */

public class Region {
    private String id;
    private String country;
    private String region;
    private String code;

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString(){
        return region;
    }
}
