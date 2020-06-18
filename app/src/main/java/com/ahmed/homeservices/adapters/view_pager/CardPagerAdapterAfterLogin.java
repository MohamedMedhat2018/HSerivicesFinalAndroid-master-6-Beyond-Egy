package com.ahmed.homeservices.adapters.view_pager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.PagerAdapter;

import com.ahmed.homeservices.R;
import com.ahmed.homeservices.activites.cutomer_or_worker.login.CustomerWorkerLoginActivity;
import com.ahmed.homeservices.activites.meowbottomnavigaion.MainActivity;
import com.ahmed.homeservices.adapters.grid.CardAdapter;
import com.ahmed.homeservices.constants.Constants;
import com.ahmed.homeservices.models.CardItem;
import com.ahmed.homeservices.utils.Utils;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.ArrayList;
import java.util.List;

public class CardPagerAdapterAfterLogin extends PagerAdapter implements CardAdapter {

        private static final String TAG = "CardPagerAdapter";
        private Context context;
        private List<CardView> mViews;
        private List<CardItem> mData;
        private float mBaseElevation;

        public CardPagerAdapterAfterLogin(Context applicationContext) {
            mData = new ArrayList<>();
            mViews = new ArrayList<>();
            this.context = applicationContext;
        }

        public void addCardItem(CardItem item) {
            mViews.add(null);
            mData.add(item);
        }

        public float getBaseElevation() {
            return mBaseElevation;
        }

        @Override
        public CardView getCardViewAt(int position) {
            return mViews.get(position);
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = null;

            if (Utils.customerOrWorker()) {
                view = LayoutInflater.from(container.getContext())
                        .inflate(R.layout.layout_customers_no_skip, container, false);
            } else {
                view = LayoutInflater.from(container.getContext())
                        .inflate(R.layout.layout_worker, container, false);
            }

            container.addView(view);
            bind(position, mData.get(position), view);
            CardView cardView = view.findViewById(R.id.cardView);

            Log.e(TAG, "instantiateItem: " + position);

            if (mBaseElevation == 0) {
                mBaseElevation = cardView.getCardElevation();
            }

            cardView.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);
            mViews.set(position, cardView);


            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
            mViews.set(position, null);
        }

        private void bind(int position, CardItem item, View view) {
            if (Utils.customerOrWorker()) {


                view.findViewById(R.id.login_customer_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, CustomerWorkerLoginActivity.class);
                        context.startActivity(intent);
                        Prefs.edit().putString(Constants.WORKER_OR_CUSTOMER, Constants.CUSTOMERS).apply();
                        ((Activity) context).overridePendingTransition(R.anim.enter, R.anim.exit);

                    }
                });
                view.findViewById(R.id.tvCustomerSkipRegisteration).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, MainActivity.class);
                        context.startActivity(intent);
                        Prefs.edit().putString(Constants.WORKER_OR_CUSTOMER, Constants.CUSTOMERS).apply();
                        ((Activity) context).overridePendingTransition(R.anim.enter, R.anim.exit);

                    }
                });

            } else {

                view.findViewById(R.id.login_worker_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.e(TAG, "onClick: test 1.2");
                        Intent intent = new Intent(context, CustomerWorkerLoginActivity.class);
                        context.startActivity(intent);
                        Prefs.edit().putString(Constants.WORKER_OR_CUSTOMER, Constants.WORKERS).apply();
                        ((Activity) context).overridePendingTransition(R.anim.enter, R.anim.exit);
                    }
                });

            }
        }

//    void callPhone() {
//
//        Intent callIntent = new Intent(Intent.ACTION_CALL); //use ACTION_CALL class
//        callIntent.setData(Uri.parse("tel:+201156749640"));    //this is the sms number calling
//        //check permission
//        //If the device is running Android 6.0 (API level 23) and the app's targetSdkVersion is 23 or higher,
//        //the system asks the user to grant approval.
//        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//            //request permission from user if the app hasn't got the required permission
//            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE},   //request specific permission from user
//
//                    10);
//            return;
//        } else {     //have got permission
//            try {
//                context.startActivity(callIntent);  //call activity and make sms call
//            } catch (android.content.ActivityNotFoundException ex) {
//                Toast.makeText(getApplicationContext(), "your Activity is not founded", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

    }