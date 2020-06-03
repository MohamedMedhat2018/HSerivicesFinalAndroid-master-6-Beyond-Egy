package com.ahmed.homeservices.view_holders;

import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


// Your "view holder" that holds references to each subview
public class VHAttachedFiles {
    public final ImageView imageView;
    public final FloatingActionButton fabUpload;
    public ImageView catImageIcon;
    public TextView catTitle;
    public FloatingActionButton fabClose = null;


    public VHAttachedFiles(ImageView imageView, FloatingActionButton fabClose, FloatingActionButton fabUpload) {
        this.fabClose = fabClose;
        this.fabUpload = fabUpload;
        this.imageView = imageView;
    }


    public VHAttachedFiles(ImageView imageView, FloatingActionButton fabUpload, ImageView catImageIcon, TextView catTitle) {
        this.catImageIcon = catImageIcon;
        this.catTitle = catTitle;
        this.fabUpload = fabUpload;
        this.imageView = imageView;
    }

}