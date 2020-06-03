package com.ahmed.homeservices.adapters.grid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ahmed.homeservices.R;
import com.ahmed.homeservices.constants.Constants;
import com.ahmed.homeservices.interfaces.OnPhotoClick;
import com.ahmed.homeservices.interfaces.OnPhotoClosed;
import com.ahmed.homeservices.models.ModeEdit;
import com.ahmed.homeservices.view_holders.ViewHolder;
import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

public class AttachPhotosAdapter extends BaseAdapter {

    private static final String TAG = "AttachPhotosAdapter";
    private OnPhotoClick onPhotoClick = null;
    private Context mContext;
    private OnPhotoClosed onPhotoClosed;
    private boolean normal = false;
    private boolean preview = false;
    private boolean editCashedOrder = false;
    private boolean editPendingOrder = false;

    //private List<Uri> uris = new ArrayList<>();
    private List<String> paths = new ArrayList<>();
    private List<ModeEdit> pathsWithModes = new ArrayList<>();
    private int count = 10;

    public AttachPhotosAdapter(List<String> paths, Context context, OnPhotoClick onPhotoClick, OnPhotoClosed onPhotoClosed, boolean normal) {
        this.mContext = context;
        this.onPhotoClick = onPhotoClick;
        this.onPhotoClosed = onPhotoClosed;
        this.normal = normal;
        this.paths = paths;
    }


    public AttachPhotosAdapter(Context context, List<String> paths, OnPhotoClick onPhotoClick, OnPhotoClosed onPhotoClosed, boolean editMode) {
        this.mContext = context;
        this.editCashedOrder = editMode;
        this.paths = paths;
        this.onPhotoClick = onPhotoClick;
        this.onPhotoClosed = onPhotoClosed;
    }

    public AttachPhotosAdapter(Context context, List<String> paths, boolean previewMode) {
        this.mContext = context;
        this.preview = previewMode;
        this.paths = paths;
    }

    public AttachPhotosAdapter(Context context, List<ModeEdit> pathsWithModes, boolean modeEdit, OnPhotoClick onPhotoClick, OnPhotoClosed onPhotoClosed) {
        this.mContext = context;
        this.editPendingOrder = modeEdit;
//        this.paths = paths;
        this.pathsWithModes = pathsWithModes;
        this.onPhotoClick = onPhotoClick;
        this.onPhotoClosed = onPhotoClosed;
    }

    @Override
    public int getCount() {
//        if (editCashedOrder || preview) {
//            count = paths.size();
//        }
        if (preview) {
            count = paths.size();
        }
        return count;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @SuppressLint("InflateParams")
    @Override
    //position that come from getCount() = 10
    public View getView(int position, View convertView, ViewGroup parent) {
        //final Category category = categories[position];
        ViewHolder viewHolder;

//        convertView : it's the parent (Grid View)


        // view holder pattern
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.layout_attached_photos_grid_item3, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            //it's prevent view from recreate after swap so every view created inflate it with a tag.
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (normal) {
            Log.e(TAG, "getView: test else case ");

            if (paths.get(position) != null /*&& paths.get(position).trim().length() != 0*/) {

                viewHolder.ivClose.setVisibility(View.VISIBLE);
                convertView.findViewById(R.id.ivPhoto2).setVisibility(View.GONE);
//                convertView.findViewById(R.id.ivClose).setTag(Integer.valueOf(position));
                RoundedImageView roundedImageView = convertView.findViewById(R.id.ivPhoto);
                roundedImageView.setVisibility(View.VISIBLE);

                Glide.with(mContext).load(Uri.parse(paths.get(position)))
                        .into(roundedImageView);

                View finalConvertView = convertView;
                viewHolder.ivClose.setOnClickListener(view -> {
                    onPhotoClosed.onPhotoClosed(finalConvertView, position, Constants.Modes.NORMAL);
                });

            } else {
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onPhotoClick.onPhotoClick(view, position, Constants.Modes.NORMAL);
                    }
                });
            }

        } else if (preview) {
            Log.e(TAG, "getView: test preview case ");
            viewHolder.ivClose.setVisibility(View.GONE);
            convertView.findViewById(R.id.ivPhoto2).setVisibility(View.GONE);
            RoundedImageView roundedImageView = convertView.findViewById(R.id.ivPhoto);

            Glide.with(mContext).load(Uri.parse(paths.get(position)))
                    .into(roundedImageView);
            roundedImageView.setVisibility(View.VISIBLE);
//            Log.e(TAG, "getView: image " + roundedImageView.toString() + "path " +Uri.parse(paths.get(position)));

        } else if (editCashedOrder) {
            Log.e(TAG, "getView: test edit Cashed Order case ");
            if (paths.get(position) != null /*&& paths.get(position).trim().length() != 0*/) {
                viewHolder.ivClose.setVisibility(View.VISIBLE);
                convertView.findViewById(R.id.ivPhoto2).setVisibility(View.GONE);
//                convertView.findViewById(R.id.ivClose).setTag(Integer.valueOf(position));
                RoundedImageView roundedImageView = convertView.findViewById(R.id.ivPhoto);
                roundedImageView.setVisibility(View.VISIBLE);

                Glide.with(mContext).load(Uri.parse(paths.get(position)))
                        .into(roundedImageView);
                View finalConvertView2 = convertView;
                viewHolder.ivClose.setOnClickListener(view -> onPhotoClosed.onPhotoClosed(finalConvertView2, position, Constants.Modes.EDIT_CASHED_ORDER));
            } else {
                convertView.setOnClickListener(view -> onPhotoClick.onPhotoClick(view, position, Constants.Modes.EDIT_CASHED_ORDER));

            }

        } else if (editPendingOrder) {
            Log.e(TAG, "getView:1.1 " + position);

            ModeEdit modeEdit = pathsWithModes.get(position);

            Log.e(TAG, "getView: test editPendingOrder case ");

//            if (paths.get(position) != null /*&& paths.get(position).trim().length() != 0*/) {
            if (modeEdit.getPath() != null /*&& paths.get(position).trim().length() != 0*/) {
                Log.e(TAG, "paths " + pathsWithModes.size());

                viewHolder.ivClose.setVisibility(View.VISIBLE);
                convertView.findViewById(R.id.ivPhoto2).setVisibility(View.GONE);
                RoundedImageView roundedImageView = convertView.findViewById(R.id.ivPhoto);
                roundedImageView.setVisibility(View.VISIBLE);
                switch (modeEdit.getMode()) {
                    case Constants.Modes.FROM_ABOVE:
                        Glide.with(mContext).load(modeEdit.getPath())
                                .into(roundedImageView);
                        break;
                    case Constants.Modes.FROM_USER_DEVICE:
                        Glide.with(mContext).load(Uri.parse(modeEdit.getPath()))
                                .into(roundedImageView);
                        break;
                }
                View finalConvertView3 = convertView;
                viewHolder.ivClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.e(TAG, "onClick: Remove ");
                        onPhotoClosed.onPhotoClosed(finalConvertView3, position, Constants.Modes.EDIT_PENDING_ORDER);
                    }
                });


            } else {
                Log.e(TAG, "getView: else");

                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.e(TAG, "onClick: add ");
                        onPhotoClick.onPhotoClick(view, position, Constants.Modes.EDIT_PENDING_ORDER);
                    }
                });

            }

        }


        YoYo.with(Techniques.FadeIn)
                .duration(600)
                .playOn(convertView);


        return convertView;
    }


    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
