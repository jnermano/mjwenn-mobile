package com.kasik.mjwenn.models;

/**
 * Created by Ermano on 6/10/2017.
 */

public class Cls_Menu {

    private int icon;
    private String title;
    private String desc;

    public Cls_Menu(){}
    public Cls_Menu(int i, String t, String d){
        icon = i;
        title = t;
        desc = d;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString(){return title;}
}
