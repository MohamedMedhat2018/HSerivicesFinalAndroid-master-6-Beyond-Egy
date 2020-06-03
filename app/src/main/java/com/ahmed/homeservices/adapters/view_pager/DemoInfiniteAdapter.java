package com.ahmed.homeservices.adapters.view_pager;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.ahmed.homeservices.R;
import com.ahmed.homeservices.interfaces.OnImageClicked;
import com.asksira.loopingviewpager.LoopingPagerAdapter;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

//import com.github.siyamed.shapeimageview.RoundedImageView;
//import com.ortiz.touchview.TouchImageView;

public class DemoInfiniteAdapter extends LoopingPagerAdapter<String> {

    private static final String TAG = "DemoInfiniteAdapter";
//    private List<String> listPhotos = new ArrayList<>();

    //    public DemoInfiniteAdapter(Context context, ArrayList<Integer> itemList, boolean isInfinite) {
//        super(context, itemList, isInfinite);
//        Log.e(TAG, "DemoInfiniteAdapter: "+ itemList.get(0) + " " + itemList.get(1) );
//    }
    private OnImageClicked onImageClicked;

//    public DemoInfiniteAdapter(Context context, ArrayList<String> itemList, boolean isInfinite, OnImageClicked onImageClicked) {
//        super(context, itemList, isInfinite);
////        Log.e(TAG, "DemoInfiniteAdapter: " + itemList.get(0) + " " + itemList.get(1));
//        this.onImageClicked = onImageClicked;
//
//    }

    public DemoInfiniteAdapter(Context context, ArrayList<String> itemList, boolean isInfinite) {
        super(context, itemList, isInfinite);
//        Log.e(TAG, "DemoInfiniteAdapter: " + itemList.get(0) + " " + itemList.get(1));
        Log.e(TAG, "DemoInfiniteAdapter: " + itemList.size() + itemList.get(0));
        this.onImageClicked = onImageClicked;

    }

    //This method will be triggered if the item View has not been inflated before.
    @Override
    protected View inflateView(int viewType, ViewGroup container, int listPosition) {
        return LayoutInflater.from(context).inflate(R.layout.item_pager, container, false);
    }

    //Bind your data with your item View here.
    //Below is just an example in the demo app.
    //You can assume convertView will not be null here.
    //You may also consider using a ViewHolder pattern.
    @Override
    protected void bindView(View convertView, int listPosition, int viewType) {
        String photoLink = itemList.get(listPosition);
//        ImageView imageView = convertView.findViewById(R.id.iv_attached_photo);
        RoundedImageView imageView = convertView.findViewById(R.id.iv_attached_photo);
//        TouchImageView imageView = convertView.findViewById(R.id.iv_attached_photo);
        ProgressBar progressBar = convertView.findViewById(R.id.progressBar);
//        ImageView imageView = convertView.findViewById(R.id.iv_customer_order_img);
//        Glide.with(convertView.getContext())
//                .load()
        Picasso.get().load(photoLink)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });

        YoYo.with(Techniques.FadeIn)
                .duration(600)
                .playOn(convertView);


//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onImageClicked.onImageClicked(imageView);
//            }
//        });


//        imageView.setOnTouchImageViewListener(new TouchImageView.OnTouchImageViewListener() {
//
//            @Override
//            public void onMove() {
//                PointF point = imageView.getScrollPosition();
//                RectF rect = imageView.getZoomedRect();
//                float currentZoom = imageView.getCurrentZoom();
//                boolean isZoomed = imageView.isZoomed();
////                scrollPositionTextView.setText("x: " + df.format(point.x) + " y: " + df.format(point.y));
////                zoomedRectTextView.setText("left: " + df.format(rect.left) + " top: " + df.format(rect.top)
////                        + "\nright: " + df.format(rect.right) + " bottom: " + df.format(rect.bottom));
////                currentZoomTextView.setText("getCurrentZoom(): " + currentZoom + " isZoomed(): " + isZoomed);
//            }
//        });



    }
}