package com.ahmed.homeservices.activites.details.customer;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.ahmed.homeservices.R;
import com.ahmed.homeservices.adapters.view_pager.DemoInfiniteAdapter;
import com.ahmed.homeservices.constants.Constants;
import com.ahmed.homeservices.fire_utils.RefBase;
import com.ahmed.homeservices.interfaces.OnImageClicked;
import com.ahmed.homeservices.models.orders.OrderRequest;
import com.ahmed.homeservices.utils.Utils;
import com.asksira.loopingviewpager.LoopingViewPager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.rd.PageIndicatorView;

import java.io.Serializable;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CustomerOrderDetails extends AppCompatActivity implements Serializable, OnImageClicked {

    private static final String TAG = "CustomerOrderDetails";
    @BindView(R.id.looping_view_pager_post)
    LoopingViewPager loopingViewPager;
    @BindView(R.id.tv_customer_order_category)
    TextView tv_customer_order_category;
    @BindView(R.id.tv_customer_order_coast)
    TextView tv_customer_order_coast;
    @BindView(R.id.tv_customer_order_time)
    TextView tv_customer_order_time;
    @BindView(R.id.tv_order_start_date)
    TextView tv_order_start_date;
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
    @BindView(R.id.tv_customer_order_worker_name)
    TextView tv_customer_order_worker_name;
    @BindView(R.id.rb_customer_order_rate)
    RatingBar rb_customer_order_rate;
    @BindView(R.id.ll_customer_order_rate)
    LinearLayout ll_customer_order_rate;
    @BindView(R.id.carYourRequestWith)
    CardView carYourRequestWith;

    @BindView(R.id.cardNoImages)
    View cardNoImages;

    Bundle extras;
    OrderRequest orderRequest;
    DemoInfiniteAdapter demoInfiniteAdapter;
    @BindView(R.id.toolbarEditProfile)
    Toolbar toolbar;
    @BindView(R.id.indicatorView)
    PageIndicatorView indicatorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_order_details);
        ButterKnife.bind(this);
        accessViews();
        accessToolbar();
    }

    private void accessToolbar() {
        toolbar.setNavigationOnClickListener(view -> {
            //finish();
            onBackPressed();
        });
        toolbar.setTitle(getString(R.string.details_order));
    }

    private void accessViews() {

//        Fresca.initialize(this);

        extras = getIntent().getExtras();

        if (extras != null) {
            Log.e(TAG, "onCreate: extra ");
            orderRequest = (OrderRequest) extras.getSerializable(Constants.ORDER);
//            Log.e(TAG, "onCreate: " + orderRequest.getCreationDate() );

            assert orderRequest != null;
            switch (orderRequest.getState()) {
                case Constants.PENDING:
                    carYourRequestWith.setVisibility(View.GONE);
                    break;

            }

            Log.e(TAG, "accessViews: " + orderRequest.getOrderId());


//            RefBase.refCmtTasks()
//                    .orderByChild(Constants.REQUEST_ID)
//                    .equalTo(orderRequest.getOrderId())
////                    .child(orderRequest.getOrderId())
//                    .addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            if (dataSnapshot.exists()) {
//                                dataSnapshot.getRef().removeEventListener(this);
//                                for (DataSnapshot dataSnapshot1 :
//                                        dataSnapshot.getChildren()) {
//                                    Log.e(TAG, "fffffff:111");
//                                    Log.e(TAG, "gggggggg: " + dataSnapshot1.toString());
////                                    CmTask cmTask = dataSnapshot1.getValue(CmTask.class);
////                                    Log.e(TAG, "fffffff " + cmTask.getCategoryId());
////                                    Log.e(TAG, "fffffff:112 " + cmTask.getTo());
////                                    Log.e(TAG, "fffffff:112 " + cmTask.getCustomerComment());
////                                    Log.e(TAG, "fffffff:112 " + cmTask.getRequestId());
//                                    HashMap<String, Object> map = (HashMap<String, Object>) dataSnapshot1.getValue();
//                                    if (map != null) {
//                                        String customerId = (String) map.get("customerId");
//                                        if (customerId != null) {
//                                            Log.e(TAG, "customesddsdrId: " + customerId);
//                                            RefBase.refWorker(customerId).addValueEventListener(new ValueEventListener() {
//                                                @Override
//                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                                    dataSnapshot.getRef().removeEventListener(this);
//                                                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
//                                                        CMWorker cmWorker = dataSnapshot.getValue(CMWorker.class);
//                                                        Log.e(TAG, "onDataChange:113 ");
////                                                        tv_customer_order_worker_name.setText("" + cmWorker.getWorkerNameInEnglish());
//                                                        tv_customer_order_worker_name.setText("" + cmWorker.getWorkerNameInEnglish());
//                                                    }
//                                                }
//
//                                                @Override
//                                                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                                }
//                                            });
//                                        }
//
//                                    }
//
//                                }
//
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                        }
//                    });


//            ArrayList<String> staticPhotos = new ArrayList<>();
//            staticPhotos.add(String.valueOf(R.drawable.seyana_logo_no_bg));
//            staticPhotos.add(R.drawable.customer2);
//            staticPhotos.add(R.drawable.customer3);

            if (orderRequest.getOrderPhotos().isEmpty()) {
//                ArrayList<String> staticPhotos = new ArrayList<>();
//                staticPhotos.add(String.valueOf(R.drawable.seyana_logo_no_bg));
                Log.e(TAG, "accessViews: default img  ");
                cardNoImages.setVisibility(View.VISIBLE);

//                Uri uri = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/drawable/seyana_logo_no_bg");
//                Log.e(TAG, "accessViews: default img  " + uri);
//                try {
//                    InputStream stream = getContentResolver().openInputStream(uri);
//                    String total = new String(ByteStreams.toByteArray(stream));
//                    staticPhotos.add(total);
//                    Log.e(TAG, "accessViews: default img 2");
//
//
////                    demoInfiniteAdapter = new DemoInfiniteAdapter(this, staticPhotos, true, this);
//                    demoInfiniteAdapter = new DemoInfiniteAdapter(this, staticPhotos, true);
//                    loopingViewPager.setAdapter(demoInfiniteAdapter);
//                    Log.e(TAG, "onCreate:2 " + staticPhotos.isEmpty());
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }


//
            } else {
                cardNoImages.setVisibility(View.GONE);
//                demoInfiniteAdapter = new DemoInfiniteAdapter(this, orderRequest.getOrderPhotos(), true, this);
                demoInfiniteAdapter = new DemoInfiniteAdapter(this, orderRequest.getOrderPhotos(), true);
                loopingViewPager.setAdapter(demoInfiniteAdapter);
                Utils.attachIndicatiorToViewPager(loopingViewPager, indicatorView);
            }

            RefBase.refCategories()
//                    .orderByChild(C)
//                    .equalTo(orderRequest.getCategoryId())
                    .child(orderRequest.getCategoryId())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            dataSnapshot.getRef().removeEventListener(this);
                            if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
//                                Category category = dataSnapshot.getValue(Category.class);
                                HashMap<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                                if (map.get(Constants.CAT_NAME) != null) {
                                    String catName = (String) map.get(Constants.CAT_NAME);
                                    tv_customer_order_category.setText(catName);
                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


            Log.e(TAG, "onCreate:1 " + orderRequest.getOrderPhotos());
            tv_customer_order_coast.setText(orderRequest.getCost() + "");
            tv_customer_order_time.setText(orderRequest.getCreationTime());
            tv_order_start_date.setText(orderRequest.getCreationDate());
            tv_customer_ordr_state.setText(Utils.setFirstUpperCharOfWord(orderRequest.getState()));
            tv_customer_order_country.setText(orderRequest.getLocation().getCountry());
            tv_customer_order_city.setText(orderRequest.getLocation().getCity());
//            tv_customer_order_area.setText(orderRequest.getLocation().getArea());
            tv_customer_order_location_address.setText(orderRequest.getLocationAddress());
            tv_customer_order_description.setText(orderRequest.getOrderDescription());

            Log.e(TAG, "accessViews: " + orderRequest.getState());


        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);


    }

//    @Override
////    protected void onStart() {
////        super.onStart();
////        if (!EventBus.getDefault().isRegistered(this)){
////            EventBus.getDefault().register(this);
////        }
////    }
////
////    @Override
////    protected void onStop() {
////        super.onStop();
////        if (EventBus.getDefault().isRegistered(this)){
////            EventBus.getDefault().unregister(this);
////        }
////
////    }

//    @OnClick(R.id.ivEditOrderToolbar)
//    public void ivEditOrderToolbar(View v){
//        editOrderNow();
//    }

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

    @Override
    public void onImageClicked(ImageView imageView) {
        Log.e(TAG, "onImageClicked: ");


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_edit_order, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.edit_order_before_post_state:
//                editOrderNow();
//                break;
//
//        }
        return super.onOptionsItemSelected(item);
    }

    private void editOrderNow() {
        Toast.makeText(this, "Edit Order", Toast.LENGTH_SHORT).show();
//                EventBus.getDefault().post();
//        EventBus.getDefault().postSticky(new MsgEvtEditOrder(orderRequest));
        finish();
    }
}
