package com.ahmed.homeservices.models;

import androidx.fragment.app.Fragment;

public class AdapterTabItem {

    private Fragment fragment;
    private String title;
    private int icon;

    public AdapterTabItem(Fragment fragment, String title, int icon) {
        this.fragment = fragment;
        this.title = title;
        this.icon = icon;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
