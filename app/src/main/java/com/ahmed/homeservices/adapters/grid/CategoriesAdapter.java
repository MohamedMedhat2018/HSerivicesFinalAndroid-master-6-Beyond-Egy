package com.ahmed.homeservices.adapters.grid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ahmed.homeservices.R;
import com.ahmed.homeservices.constants.Constants;
import com.ahmed.homeservices.models.Category;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.pixplicity.easyprefs.library.Prefs;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CategoriesAdapter extends BaseAdapter {

    private static final String TAG = "CategoriesAdapter";
    private Category category;
    private Context mContext;
    //    private Category[] categories;
    private ArrayList<Category> categoryArrayList = new ArrayList<>();
//    private ArrayList<Category> categories = new ArrayList<>();

//
//    public CategoriesAdapter(Context context, ArrayList<Category> categoryArrayList) {
//        this.mContext = context;
//        this.categoryArrayList = categoryArrayList;
//    }


    //    public CategoriesAdapter(Context context, Category[] categories) {
//        this.mContext = context;
//        this.categories = categories;
//    }
    private OnItemClickListener onItemClickListener;

    public CategoriesAdapter(Context context, ArrayList<Category> categoryArrayList, OnItemClickListener onItemClickListener) {
        this.mContext = context;
        this.categoryArrayList = categoryArrayList;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getCount() {
        //return categories.length;
        return categoryArrayList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;

    }

    @Override
    public Category getItem(int position) {
        return categoryArrayList.get(position);
    }

//    private boolean v = Boolean.valueOf(false);

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //final Category category = categories[position];
        Category category = categoryArrayList.get(position);
        ViewHolderCat viewHolderCat;
        Map<String, Object> map = new HashMap<>();
        final boolean[] v = {false};

        // view holder pattern
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.layout_category_grid_item, null);
            viewHolderCat = new ViewHolderCat(convertView);

            map.put(Constants.KEY_VIEW_HOLDER, viewHolderCat);
            map.put(Constants.KEY_CAT, category);


            convertView.setTag(map);
        } else {
//            viewHolderCat = (ViewHolderCat) convertView.getTag();

            Map map1 = (Map) convertView.getTag();
            viewHolderCat = (ViewHolderCat) map1.get(Constants.KEY_VIEW_HOLDER);
//            map1.get(Constants.KEY_CAT);
        }


//        viewHolderCat.imageViewIcon.setImageResource(category.getCategoryIcon());
        if (category.getCategoryIcon() != null && category.getCategoryIcon() != "") {
            Picasso.get().load(category.getCategoryIcon()).into(viewHolderCat.imageViewIcon, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {

                }
            });
        }
        viewHolderCat.imageViewIcon.setTag(category);

        viewHolderCat.tvCatTitle.setText(category.getCategoryName());
//        if (viewHolderCat.llChecked.getTag() == Constants.VISIBLE) {
//            viewHolderCat.llChecked.setVisibility(View.VISIBLE);
//            Log.e(TAG, "getView: 1" );
//        } else {
//            if (viewHolderCat.llChecked.getTag() == Constants.GONE) {
//                viewHolderCat.llChecked.setVisibility(View.GONE);
//                Log.e(TAG, "getView: 2" );
//            }
//        }
//        if(Prefs.contains(Constants.POS_SELECTED)){
//            if(position == Prefs.getInt(Constants.POS_SELECTED, 0)){
//                viewHolderCat.llChecked.setVisibility(View.VISIBLE);
//            }
//        }else{
//            viewHolderCat.llChecked.setVisibility(View.GONE);
//        }
//        convertView.findViewById(R.id.llChecked).setVisibility(category.isVisible() ? View.VISIBLE : View.GONE);
//        convertView.findViewById(R.id.llChecked).setVisibility(v[0] ? View.VISIBLE : View.GONE);

        if (Prefs.contains(Constants.KEY_CAT)) {
            //visible
            Log.e(TAG, "getView: " + Prefs.getPreferences()
                    .getString(Constants.KEY_CAT, ""));
            if (TextUtils.equals(category.getCategoryName(), Prefs.getString(Constants.KEY_CAT, ""))) {
                viewHolderCat.llChecked.setVisibility(View.VISIBLE);
                Log.e(TAG, "getView:  visible");
            } else {
                viewHolderCat.llChecked.setVisibility(View.GONE);
            }
        } else {
            //not visible
            viewHolderCat.llChecked.setVisibility(View.GONE);
            Log.e(TAG, "getView:  invisi");
        }


        View finalConvertView = convertView;
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Log.e(TAG, "onClick: ");
                Map map = (Map) finalConvertView.getTag();
                Category category = (Category) map.get(Constants.KEY_CAT);
                //CategoriesAdapter.this.category.setVisible(true);
                onItemClickListener.OnItemClicked(finalConvertView, category);
//                    EventBus.getDefault().post(category);

            }
        });

        YoYo.with(Techniques.FadeIn)
                .duration(600)
                .playOn(convertView);


        return convertView;
    }

    public interface OnItemClickListener {
        void OnItemClicked(View v, Category category);
    }

    public class ViewHolderCat {
        TextView tvCatTitle;
        ImageView imageViewIcon;
        LinearLayout llChecked;

        ViewHolderCat(View convertView) {

            imageViewIcon = convertView.findViewById(R.id.ivIcon);
            tvCatTitle = convertView.findViewById(R.id.tvTitle);
            llChecked = convertView.findViewById(R.id.llChecked);


//            convertView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
////                Log.e(TAG, "onClick: " );
//                    Map map = (Map) convertView.getTag();
//                    Category category = (Category) map.get(Constants.KEY_CAT);
//
//
//                    onItemClickListener.OnItemClicked(convertView, category);
////                    EventBus.getDefault().post(category);
//                }
//            });

        }


    }

}
