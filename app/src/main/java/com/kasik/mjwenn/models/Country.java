package com.kasik.mjwenn.models;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ermano on 6/3/2017.
 * country of avaliablity
 */

public class Country {
    private String id;
    private String country;
    private String code;
    private List<Region> regions = new ArrayList<>();


    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    @Exclude
    public List<Region> getRegions() {
        return regions;
    }

    @Exclude
    public void setRegions(List<Region> regions) {
        this.regions = regions;
    }

    @Override
    public String toString(){
        return country;
    }
}
