package com.ahmed.homeservices.adapters.multi_select;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ahmed.homeservices.R;
import com.ahmed.homeservices.interfaces.multi_select.city.OnCitySelected;
import com.ahmed.homeservices.models.City;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SelectCitiesAdapter extends RecyclerView.Adapter<SelectCitiesAdapter.CityViewHolder> {

    private static final String TAG = "SelectCitiesAdapter";
    private List<City> cities;
    private OnCitySelected onCitySelected;

    public SelectCitiesAdapter(List<City> cities, OnCitySelected onCitySelected) {
        this.cities = cities;
        this.onCitySelected = onCitySelected;
    }

    @NotNull
    @Override
    public CityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_city_item, parent, false);
        return new CityViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NotNull CityViewHolder holder, int position) {
        City selectedCity = cities.get(position);

        holder.tvCity.setText(selectedCity.getCityName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (position > 0)
                onCitySelected.onCitySelected(v, selectedCity, position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return cities.size();
    }


    class CityViewHolder extends RecyclerView.ViewHolder {
        TextView tvCity;

        CityViewHolder(View view) {
            super(view);
//            this.setIsRecyclable(false);
            tvCity = view.findViewById(R.id.tvCity);

        }
    }


}