package com.ahmed.homeservices.adapters.grid;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ahmed.homeservices.R;
import com.ahmed.homeservices.interfaces.OnPhotoClick;
import com.ahmed.homeservices.view_holders.ViewHolder;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

public class PhotosPreviewAdapter extends BaseAdapter {

    private OnPhotoClick onPhotoClick = null;
    private Context mContext;
    private List<Uri> uris = new ArrayList<>();

    public PhotosPreviewAdapter(Context context, List<Uri> uris) {
        this.mContext = context;
        this.uris = uris;
    }

    @Override
    public int getCount() {
        return uris.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Uri getItem(int position) {
        return uris.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //final Category category = categories[position];
        ViewHolder viewHolder;


        Uri uri = uris.get(position);

        // view holder pattern
//        if (convertView == null) {
//            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
//            convertView = layoutInflater.inflate(R.layout.layout_attached_photos_grid_item3, null);
//            viewHolder = new ViewHolder(convertView);
//            convertView.setTag(viewHolder);
//        } else {
//            viewHolder = (ViewHolder) convertView.getTag();
//
//        }

        final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(R.layout.layout_attached_photos_grid_item3, null);


        convertView.findViewById(R.id.ivPhoto2).setVisibility(View.GONE);

        RoundedImageView roundedImageView = convertView.findViewById(R.id.ivPhoto);
        roundedImageView.setVisibility(View.VISIBLE);
        roundedImageView.setImageURI(uri);


        YoYo.with(Techniques.FadeIn)
                .duration(600)
                .playOn(convertView);


        return convertView;
    }


}
