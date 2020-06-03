package com.ahmed.homeservices.interfaces.multi_select.city;

import com.ahmed.homeservices.models.City;

import java.util.List;

public interface OnCitySelectedFinished {
    //    void onCitySelectedFinished(String result);
    void onCitySelectedFinished(List<City> cities);

    void onCitySelectedCancelled(String error);

}
