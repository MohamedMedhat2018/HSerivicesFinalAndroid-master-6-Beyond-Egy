package com.ahmed.homeservices.interfaces;

import android.view.View;

public interface OnPhotoClosed {
    //    void onPhotoClosed(View convertView, int position);
    void onPhotoClosed(View convertView, int position, String modeType);
}