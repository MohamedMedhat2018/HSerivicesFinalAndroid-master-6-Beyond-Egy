package com.ahmed.homeservices.interfaces.multi_select.category;

import android.view.View;

import com.ahmed.homeservices.models.Category;

public interface OnCategorySelected {
    void onCategorySelected(View v, Category category, int pos);

}
