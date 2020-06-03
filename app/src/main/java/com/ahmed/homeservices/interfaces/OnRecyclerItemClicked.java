package com.ahmed.homeservices.interfaces;

import android.view.View;

import com.ahmed.homeservices.models.Category;

public interface OnRecyclerItemClicked {
    void onClick(View v, int pos);

    //    void onClick(View v);
    interface FireUI {
        void onClick(View v, int pos, Category category);
    }

}

