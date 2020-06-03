package com.ahmed.homeservices.activites.details.cm_worker;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ahmed.homeservices.R;
import com.ahmed.homeservices.adapters.view_pager.DemoInfiniteAdapter;
import com.ahmed.homeservices.constants.Constants;
import com.ahmed.homeservices.fire_utils.RefBase;
import com.ahmed.homeservices.models.CmTask;
import com.ahmed.homeservices.models.Rate;
import com.ahmed.homeservices.models.User;
import com.ahmed.homeservices.models.orders.OrderRequest;
import com.ahmed.homeservices.utils.Utils;
import com.asksira.loopingviewpager.LoopingViewPager;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.rd.PageIndicatorView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CmOrderDetails extends AppCompatActivity implements Serializable {

    private static final String TAG = "CmOrderDetails";
    @BindView(R.id.looping_view_pager_post)
    LoopingViewPager loopingViewPager;
    @BindView(R.id.llIndicator)
    LinearLayout llIndicator;
    //    @BindView(R.id.tv_customer_order_category)
//    TextView tv_customer_order_category;
    @BindView(R.id.tv_customer_order_coast)
    TextView tv_customer_order_coast;
    //    @BindView(R.id.tv_customer_order_time)
//    TextView tv_customer_order_time;
//    @BindView(R.id.tv_order_start_date)
//    TextView tv_order_start_date;
    @BindView(R.id.tv_customer_ordr_state)
    TextView tv_customer_ordr_state;
    //    @BindView(R.id.tv_customer_order_area)
//    TextView tv_customer_order_area;
    @BindView(R.id.tv_customer_order_city)
    TextView tv_customer_order_city;
    @BindView(R.id.tv_customer_order_country)
    TextView tv_customer_order_country;
    @BindView(R.id.tv_customer_order_location_address)
    TextView tv_customer_order_location_address;
    @BindView(R.id.tv_customer_order_description)
    TextView tv_customer_order_description;
    @BindView(R.id.tv_customer_order_name_cm)
    TextView tv_customer_order_name_cm;
    @BindView(R.id.rb_customer_order_rate)
    RatingBar rb_customer_order_rate;
    @BindView(R.id.ll_customer_order_rate)
    LinearLayout ll_customer_order_rate;

    @BindView(R.id.iv_customer_order_img_cm)
//    ImageView iv_customer_order_img_cm;
            CircularImageView iv_customer_order_img_cm;
    @BindView(R.id.tv_customer_order_start_date)
    TextView tv_customer_order_start_date;
    @BindView(R.id.tv_customer_order_end_date)
    TextView tv_customer_order_end_date;

    Bundle extras;
    CmTask cmTask;
    OrderRequest orderRequest;

    User user;
    DemoInfiniteAdapter demoInfiniteAdapter;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
//    @BindView(R.id.test1)
//    LinearLayout test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cm_order_details);
        ButterKnife.bind(this);

        toolbar.setNavigationOnClickListener(view -> {
            //finish();
            onBackPressed();
        });

    }

    @BindView(R.id.indicatorView)
    PageIndicatorView indicatorView;

//    @Nullable
//    @Override
//    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
//        Log.e(TAG, "onCreateView: 11.11 " + Resources.getSystem().getConfiguration().locale.getLanguage() + Locale.getDefault().getLanguage());
//
//
//        if (Resources.getSystem().getConfiguration().locale.getLanguage().equals("en") && Locale.getDefault().getLanguage().equals("ar")){
////            tetx1.setLayoutDirection();
//
//            Log.e(TAG, "onCreateView: tessst"  );
//
////            test.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
//
//        }
//
//
//        return super.onCreateView(name, context, attrs);
//    }

    Intent intent;

    private void accessViews() {

//        Fresca.initialize(this);

        intent = getIntent();
        extras = getIntent().getExtras();
        Log.e(TAG, "onCreate: " + extras.toString());
        if (extras != null) {
            Log.e(TAG, "onCreate: ");
            cmTask = (CmTask) extras.getSerializable(Constants.CM_TASK);
            orderRequest = (OrderRequest) extras.getSerializable(Constants.ORDER_REQUEST);
            user = (User) extras.getSerializable(Constants.CM_FOR_USER);
//            Log.e(TAG, "onCreate: " + orderRequest.getCreationDate() );

//            RefBase.refRequests(cmTask.get)

            ArrayList<String> staticPhotos = new ArrayList<>();
//            staticPhotos.add(R.drawable.customer1);
//            staticPhotos.add(R.drawable.customer2);
//            staticPhotos.add(R.drawable.customer3);


//

            if (user != null) {
                Picasso.get().load(user.getUserPhoto()).into(iv_customer_order_img_cm, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
                tv_customer_order_name_cm.setText(user.getUserName());
            }


            if (orderRequest != null) {
//                if (intent.hasExtra(Constants.WORKING_OR_FINISHED)) {
                switch (orderRequest.getState()) {
                    case Constants.CM_WORKING:
                        //cm working clicked item
                        Log.e(TAG, "accessViews: 0");
                        ll_customer_order_rate.setVisibility(View.GONE);
                        break;
                    case Constants.CM_FINISHED:
                        //cm finished clicked item
                        Log.e(TAG, "accessViews: 1");
                        if (cmTask != null && cmTask.getRate().length() > 1) {
                            RefBase.rate(cmTask.getRate()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        Rate rate = dataSnapshot.getValue(Rate.class);
                                        ll_customer_order_rate.setVisibility(View.VISIBLE);
                                        rb_customer_order_rate.setRating(Float.parseFloat(rate.getNumberOfStars()));
                                        Log.e(TAG, "onDataChange: Rate 222" + rate.getNumberOfStars());
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });
                        }
                        break;

                }
//            }


                if (orderRequest.getOrderPhotos().isEmpty()) {
                    demoInfiniteAdapter = new DemoInfiniteAdapter(this, staticPhotos, true);
                    loopingViewPager.setAdapter(demoInfiniteAdapter);
//                    Log.e(TAG, "accessViews: tessst2 " );
                    llIndicator.setVisibility(View.GONE);
                    Log.e(TAG, "onCreate:2 " + staticPhotos.isEmpty());
                } else {
                    demoInfiniteAdapter = new DemoInfiniteAdapter(this, orderRequest.getOrderPhotos(), true);
//                    Log.e(TAG, "accessViews: tessst " + orderRequest.getOrderPhotos() );
                    loopingViewPager.setAdapter(demoInfiniteAdapter);

                }
//            tv_customer_order_category.setText(orderRequest.getCategory().getTitlegetTitle());
                Log.e(TAG, "onCreate:1 " + orderRequest.getOrderPhotos());
                Log.e(TAG, "onCreate:1 " + cmTask.getCost());
                tv_customer_order_coast.setText(cmTask.getCost() + " AED");
//            tv_customer_order_time.setText(orderRequest.getCreationTime());
//            tv_order_start_date.setText(orderRequest.getCreationDate());
//                tv_customer_ordr_state.setText(orderRequest.getState());
                tv_customer_ordr_state.setText(Utils.setFirstUpperCharOfWord(orderRequest.getState()));
                tv_customer_order_country.setText(orderRequest.getLocation().getCountry());
                tv_customer_order_city.setText(orderRequest.getLocation().getCity());
//            tv_customer_order_area.setText(orderRequest.getLocation().getArea());
                tv_customer_order_location_address.setText(orderRequest.getLocationAddress());
                tv_customer_order_description.setText(orderRequest.getOrderDescription());


//                RefBase.refCategories()
////                    .orderByChild(C)
////                    .equalTo(orderRequest.getCategoryId())
//                        .child(orderRequest.getCategoryId())
//                        .addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                if(dataSnapshot.exists()){
////                                Category category = dataSnapshot.getValue(Category.class);
//                                    HashMap<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
//                                    if(map.get(Constants.CAT_NAME) != null){
//                                        String catName = (String) map.get(Constants.CAT_NAME);
//                                        tv_customer_ordr_state.setText(catName);
//                                    }
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                            }
//                        });


            }


            if (cmTask != null) {
                tv_customer_order_start_date.setText(cmTask.getFrom());
                tv_customer_order_end_date.setText(cmTask.getTo());
//            rb_customer_order_rate.setRating((float) cmTask.getRate());


            }

        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        accessViews();
        Utils.attachIndicatiorToViewPager(loopingViewPager, indicatorView);
    }


    @Override
    protected void onResume() {
        super.onResume();
        loopingViewPager.resumeAutoScroll();
    }

    @Override
    protected void onPause() {
        loopingViewPager.pauseAutoScroll();
        super.onPause();
    }

}

