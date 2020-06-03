package com.ahmed.homeservices.adapters.spinners;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ahmed.homeservices.R;
import com.ahmed.homeservices.models.Category;
import com.ahmed.homeservices.utils.Utils;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

//public class CustomAdapter extends BaseAdapter implements SpinnerAdapter {
public class CustomAdapter extends ArrayAdapter<Category> {

//    private Context mContext;
//    private List<Category> categories = new ArrayList<>();

//    public CustomAdapter(Context context, int resource, int textViewResourceId) {
//        super(context, 0, textViewResourceId);
//
//    }

    //    public CustomAdapter(Context context, int textViewResourceId) {
//        super(context, textViewResourceId);
//
//    }
    public CustomAdapter(@NonNull Context context, List<Category> categories) {
        super(context, 0, categories);
//        this.mContext = context;
//        this.categories = categories;
    }

    @SuppressLint("InflateParams")
    @NotNull
    @Override
    public View getView(int position, View convertView, @NotNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

//    @Override
//    public int getCount() {
////        return super.getCount();
//        return this.categories.size();
//    }

//    @Override
//    public Category getItem(int pos) {
//        return categories.get(pos);
//    }

    @Override
    public View getDropDownView(int position, View convertView, @NotNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @SuppressLint("InflateParams")
    private View initView(int position, View convertView, ViewGroup parent) {
//        Category category = this.categories.get(position);

        // view holder pattern
        if (convertView == null) {
//            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            final LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(R.layout.layout_category_spinner_item, null);

//            final ImageView imageViewIcon = convertView.findViewById(R.id.ivIcon);
//            final TextView tvTitle = convertView.findViewById(R.id.tvTitle);

//            final ViewHolder viewHolder = new ViewHolder(imageViewIcon, tvTitle);
//            convertView.setTag(viewHolder);
        }


        ImageView imageViewIcon = convertView.findViewById(R.id.ivIcon);
        TextView tvTitle = convertView.findViewById(R.id.tvTitle);

        Category category = getItem(position);

        if (category != null) {
            //        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
//            viewHolder.ivIcon.setImageResource(category.getCategoryIcon());
//            viewHolder.tvTitle.setText(category.getCategoryName());

//            imageViewIcon.setImageResource(category.getCategoryIcon());
//            imageViewIcon.setImageResource(category.getCategoryIcon());
//            if(category.getCategoryIcon() != null && category.getCategoryIcon() != ""){
            //        holder.imageViewIcon.setImageResource(category.getCategoryIcon());
            if (category.getCategoryIcon() != null &&
                    category.getCategoryIcon() != "" && !TextUtils.isEmpty(category.getCategoryIcon())) {
                Picasso.get().load(category.getCategoryIcon()).into(imageViewIcon, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
            } else {

            }

            tvTitle.setText(Utils.getCatName(category));

        }

        YoYo.with(Techniques.FadeIn)
                .duration(600)
                .playOn(convertView);

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {
        ImageView ivIcon;
        TextView tvTitle;

        ViewHolder(ImageView ivIcon, TextView tvTitle) {
            this.ivIcon = ivIcon;
            this.tvTitle = tvTitle;
        }
    }


}