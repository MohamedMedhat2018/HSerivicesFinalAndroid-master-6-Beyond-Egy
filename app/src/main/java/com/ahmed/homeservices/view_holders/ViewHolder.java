package com.ahmed.homeservices.view_holders;

import android.view.View;
import android.widget.ImageView;

import com.ahmed.homeservices.R;


// Your "view holder" that holds references to each subview
public class ViewHolder {
    public ImageView ivClose;
    private ImageView ivPhoto;


    public ViewHolder(ImageView ivPhoto, ImageView ivClose) {
        this.ivClose = ivClose;
        this.ivPhoto = ivPhoto;
    }

    public ViewHolder(View convertView) {
        ivClose = convertView.findViewById(R.id.ivClose);
        ivPhoto = convertView.findViewById(R.id.ivPhoto);
    }

}