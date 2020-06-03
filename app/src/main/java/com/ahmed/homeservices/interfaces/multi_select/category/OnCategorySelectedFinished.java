package com.ahmed.homeservices.interfaces.multi_select.category;

import com.ahmed.homeservices.models.Category;

import java.util.List;

public interface OnCategorySelectedFinished {
    void onCategorySelectedFinished(List<Category> result);

    void onCategorySelectedCancelled(String error);

}
