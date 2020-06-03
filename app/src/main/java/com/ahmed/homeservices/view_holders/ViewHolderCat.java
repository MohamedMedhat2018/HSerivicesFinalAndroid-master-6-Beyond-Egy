package com.ahmed.homeservices.view_holders;

import android.widget.ImageView;
import android.widget.TextView;

// Your "view holder" that holds references to each subview
public class ViewHolderCat {
    public final TextView tvTitle;
    public final ImageView imageViewIcon;
    public final ImageView ivCheckBox;


    public ViewHolderCat(TextView tvTitle, ImageView imageViewIcon, ImageView ivCheckBox) {
        this.tvTitle = tvTitle;
        this.imageViewIcon = imageViewIcon;
        this.ivCheckBox = ivCheckBox;
    }


//    public ViewHolderCat(View view, TextView tvTitle, ImageView imageViewIcon, ImageView ivCheckBox) {
//        this.tvTitle = tvTitle;
//        this.imageViewIcon = imageViewIcon;
//        this.ivCheckBox = ivCheckBox;
//
//
////        view.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
//////                Log.e(TAG, "onClick: " );
////                onItemClickListener.OnItemClicked(view, category);
////            }
////        });
//
//    }


}