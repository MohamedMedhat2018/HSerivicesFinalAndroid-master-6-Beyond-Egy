package com.ahmed.homeservices.adapters.rv;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ahmed.homeservices.R;
import com.ahmed.homeservices.interfaces.OnRecyclerItemClicked;
import com.ahmed.homeservices.models.Category;
import com.ahmed.homeservices.models.orders.OrderRequest;
import com.ahmed.homeservices.utils.Utils;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.List;

public class SelectCatAdapter extends RecyclerView.Adapter<SelectCatAdapter.MyViewHolder> {

    private static final String TAG = "SelectCatAdapter";
    private List<Category> categories;
    private OnRecyclerItemClicked onRecyclerItemClicked;
    private Type type = new TypeToken<OrderRequest>() {
    }.getType();

    public SelectCatAdapter(List<Category> categories, OnRecyclerItemClicked onRecyclerItemClicked) {
        this.categories = categories;
        this.onRecyclerItemClicked = onRecyclerItemClicked;
    }

    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_category_grid_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NotNull MyViewHolder holder, int position) {
        Category category = null;

//        if (Prefs.getPreferences().contains(Constants.ORDER)) {
//            Log.e(TAG, "onBindViewHolder: uuu" );
//            OrderRequest order = new Gson().fromJson(Prefs.getString(Constants.ORDER, ""), type);
//            category = order.getCategory();
        //Log.e(TAG, "editOrderIfExist: " + category.getCategoryName() );
//            for (int i = 0; i < rvSelectCat.getChildCount(); i++) {
//            for (int i = 0; i < categories.size(); i++) {
//                View view = rvSelectCat.getChildAt(i);
//                if (TextUtils.equals(category.getCategoryName(), categories.get(i).getCategoryName())) {
//                    view.findViewById(R.id.llChecked).setVisibility(View.VISIBLE);
//                    LottieAnimationView lottieAnimationView = view.findViewById(R.id.lottie_layer_name);
//                    Log.e(TAG, "editOrderIfExist: " + category.getCategoryName());
//                    lottieAnimationView.playAnimation();
//                    break;
//                }
//            }
//        } else {
//            Log.e(TAG, "onBindViewHolder: yyyyy " );

        category = categories.get(position);
        Log.e(TAG, "category zzz " + category);

//        }

        if (category != null) {
            if (category.isVisible()) {
                holder.llChecked.setVisibility(View.VISIBLE);
            } else {
                holder.llChecked.setVisibility(View.GONE);
            }


            if (category.getCategoryIcon() != null && !TextUtils.isEmpty(category.getCategoryIcon()) &&
                    category.getCategoryIcon() != "") {
                //        holder.imageViewIcon.setImageResource(category.getCategoryIcon());
                Picasso.get().load(category.getCategoryIcon()).into(holder.imageViewIcon, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
            }

//        holder.imageViewIcon.setImageResource(category.getCategoryIcon());
            holder.tvCatTitle.setText(Utils.getCatName(category));
            if (category.getCategoryIcon() != null && category.getCategoryIcon() != "" && !TextUtils.isEmpty(category.getCategoryIcon())) {
                Picasso.get().load(category.getCategoryIcon()).into(holder.imageViewIcon, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
            }

            Category finalCategory = category;
            holder.ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onRecyclerItemClicked != null) {
                        onRecyclerItemClicked.onClick(holder.itemView, position);
                        Log.e(TAG, "onClick: 3");
                        finalCategory.setVisible(!finalCategory.isVisible());
                        reload(finalCategory);
                    }
                }
            });

            YoYo.with(Techniques.FadeIn)
                    .duration(600)
                    .playOn(holder.itemView);

        }
    }

    private void reload(Category categoryCompare) {
        for (Category category :
                this.categories) {
            Log.e(TAG, "reload: ");
            if (Utils.getCatName(categoryCompare) != category.getCategoryName()) {
                category.setVisible(false);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvCatTitle;
        //        ImageView imageViewIcon;
        ImageView imageViewIcon;
        LinearLayout llChecked;
        LinearLayout ll;
        ProgressBar progressBar;


        MyViewHolder(View view) {
            super(view);
//            this.setIsRecyclable(false);
            progressBar = view.findViewById(R.id.progressBar);
            imageViewIcon = view.findViewById(R.id.ivIcon);
            tvCatTitle = view.findViewById(R.id.tvTitle);
            llChecked = view.findViewById(R.id.llChecked);
            ll = view.findViewById(R.id.ll);

        }
    }


}