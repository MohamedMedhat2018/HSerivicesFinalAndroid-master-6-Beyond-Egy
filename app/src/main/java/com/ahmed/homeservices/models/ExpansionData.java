package com.ahmed.homeservices.models;

import android.view.View;

//for header and container (Model num 1)
public class ExpansionData {
    public int imageIcon;
    public String title;
    private View childToAdd; //container

    public ExpansionData(int imageIcon, String title, View view) {
        this.imageIcon = imageIcon;
        this.title = title;
        this.childToAdd = view;
    }

    public int getImageIcon() {
        return imageIcon;
    }

    public void setImageIcon(int imageIcon) {
        this.imageIcon = imageIcon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public View getChildToAdd() {
        return childToAdd;
    }

    public void setChildToAdd(View childToAdd) {
        this.childToAdd = childToAdd;
    }

}