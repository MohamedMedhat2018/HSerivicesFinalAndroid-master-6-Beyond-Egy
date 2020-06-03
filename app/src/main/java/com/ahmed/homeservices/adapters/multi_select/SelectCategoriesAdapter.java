package com.ahmed.homeservices.adapters.multi_select;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ahmed.homeservices.R;
import com.ahmed.homeservices.interfaces.multi_select.category.OnCategorySelected;
import com.ahmed.homeservices.models.Category;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SelectCategoriesAdapter extends RecyclerView.Adapter<SelectCategoriesAdapter.CategoryViewHolder> {

    private static final String TAG = "SelectCategoriesAdapter";
    private List<Category> categories;
    private OnCategorySelected onCategorySelected;

    public SelectCategoriesAdapter(List<Category> categories, OnCategorySelected onCategorySelected) {
        this.categories = categories;
        this.onCategorySelected = onCategorySelected;
    }

    @NotNull
    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_category_item, parent, false);
        return new CategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NotNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);

        holder.tvTitle.setText(category.getCategoryName());
        Picasso.get().load(category.getCategoryIcon())
                .into(holder.ivIcon, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });


        holder.llCategoryHolder.setOnClickListener(new View.OnClickListener() {
            //        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (position > 0)
                onCategorySelected.onCategorySelected(v, category, position);
                Log.e(TAG, "Category selected " + category);
            }
        });

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }


    class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        ImageView ivIcon;
        View llCategoryHolder;

        CategoryViewHolder(View view) {
            super(view);
            tvTitle = view.findViewById(R.id.tvTitle);
            ivIcon = view.findViewById(R.id.ivIcon);
            llCategoryHolder = view.findViewById(R.id.llCategoryHolder);

        }
    }


}