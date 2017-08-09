package com.kasik.mjwenn.models;

/**
 * Created by Ermano on 6/3/2017.
 * cards type
 */

public class IDCard {
    private String id;
    private String card;
    private String desc;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString(){
        return card;
    }
}
