package com.ahmed.homeservices.adapters.firebase_adapters;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmed.homeservices.R;
import com.ahmed.homeservices.constants.Constants;
import com.ahmed.homeservices.interfaces.OnRecyclerItemClicked;
import com.ahmed.homeservices.models.Category;
import com.ahmed.homeservices.models.orders.OrderRequest;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.gson.Gson;
import com.pixplicity.easyprefs.library.Prefs;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class FireFetchCategoryAdapter extends FirebaseRecyclerAdapter<Category, FireFetchCategoryAdapter.ViewHolderCats> {


    private static final String TAG = "FireFetchCategoryAdap";
    private String categoryId = "";
    private String editMode = Constants.Modes.NO_FROM_USER_OR_ABOVE;
    private OnRecyclerItemClicked.FireUI onRecyclerItemClicked;
    private Gson gson = new Gson();

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options options hnold model and reference for Query
     */
    public FireFetchCategoryAdapter(@NonNull FirebaseRecyclerOptions options, OnRecyclerItemClicked.FireUI onRecyclerItemClicked) {
        super(options);
        this.onRecyclerItemClicked = onRecyclerItemClicked;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
        Log.e(TAG, "setCategoryId: zoz b2a " + categoryId);
    }

    @NonNull
    @Override
    public ViewHolderCats onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_category_grid_item, parent, false);
        ViewHolderCats viewHolderCat = new ViewHolderCats(v);
        return viewHolderCat;
    }

    //position: the position of the raw in table
    @Override
    protected void onBindViewHolder(@NonNull ViewHolderCats holder, int position, @NonNull Category category) {


//        if (category.isVisible()) {
//            holder.llChecked.setVisibility(View.VISIBLE);
//        } else {
//            holder.llChecked.setVisibility(View.GONE);
//        }


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
        if (Prefs.contains(Constants.SELECTED_LANG)) {
            String selectedLanguage = Prefs.getString(Constants.SELECTED_LANG, "");
            Log.e(TAG, "onBindViewHolder: " + selectedLanguage);

            if (TextUtils.equals(selectedLanguage, Constants.LANG_ARABIC)) {
                holder.tvCatTitle.setText(category.getCategoryNameArabic());
            } else {
                holder.tvCatTitle.setText(category.getCategoryName());

            }
        }
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

//        Category finalCategory = category;
        holder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onRecyclerItemClicked != null) {
//                    onRecyclerItemClicked.onClick(holder.itemView, position);
                    //put the position that u click with the position that come from Firebase
                    onRecyclerItemClicked.onClick(holder.itemView, position, category);
                    Log.e(TAG, "onClick: 3");
//                    finalCategory.setVisible(!finalCategory.isVisible());
//                    reload(finalCategory);

                }
            }
        });


        Log.e(TAG, "onBindViewHolder:1 " + getCategoryId() + ", " + category.getCategoryId());

        if (getCategoryId() != null && TextUtils.equals(getCategoryId(), category.getCategoryId())) {
            //do something
            Log.e(TAG, "onBindViewHolder:2 " + getCategoryId() + ", " + category.getCategoryId());
            holder.llChecked.setVisibility(View.VISIBLE);
            switch (editMode) {
                case Constants.Modes.EDIT_CASHED_ORDER:
//                    OrderRequest orderRequest = new OrderRequest();
                    OrderRequest orderRequest = gson.fromJson(Prefs.getString(Constants.ORDER, ""), OrderRequest.class);
                    orderRequest.setCategoryId(getCategoryId());
                    Prefs.edit().putString(Constants.ORDER, gson.toJson(orderRequest, OrderRequest.class)).apply();
                    break;
                case Constants.Modes.EDIT_PENDING_ORDER:

                    break;
                case Constants.Modes.NO_FROM_USER_OR_ABOVE:

                    break;

            }

        }
        //set the cat id
        //the new in this stuff
        //get the auto generated key for each row using position of each raw
        Log.e(TAG, "onBindViewHolder: 12121" + category.getCategoryId() + " and " + categoryId);
        if (categoryId != null)
            category.setCategoryId(getRef(position).getKey());
        Log.e(TAG, "onBindViewHolder: 1212" + getRef(position).getKey());


//        YoYo.with(Techniques.FadeIn)
//                .duration(600)
//                .playOn(holder.itemView);


    }

    public String getEditMode() {
        return editMode;
    }

    public void setEditMode(String editMode) {
        this.editMode = editMode;
    }

    //RecyclerView.ViewHolder: it's build in that inflate your view with build in fireBase ViewHolder using Super()
    static class ViewHolderCats extends RecyclerView.ViewHolder {

        TextView tvCatTitle;
        ImageView imageViewIcon;
        LinearLayout llChecked;
        LinearLayout ll;
        ProgressBar progressBar;

        ViewHolderCats(@NonNull View itemView) {
            // Super(YourItem) THAT u want to send it to parent to create a view one like it
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
            imageViewIcon = itemView.findViewById(R.id.ivIcon);
            tvCatTitle = itemView.findViewById(R.id.tvTitle);
            llChecked = itemView.findViewById(R.id.llChecked);
            ll = itemView.findViewById(R.id.ll);

        }


    }


//    private void reload(Category categoryCompare) {
//        for (Category category :
//                this.categories) {
//            Log.e(TAG, "reload: ");
//            if (categoryCompare.getCategoryName() != category.getCategoryName()) {
//                category.setVisible(false);
//            }
//        }
//        notifyDataSetChanged();
//    }


}