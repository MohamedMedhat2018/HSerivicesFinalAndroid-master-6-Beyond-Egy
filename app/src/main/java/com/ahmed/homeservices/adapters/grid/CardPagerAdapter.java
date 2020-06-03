package com.ahmed.homeservices.adapters.grid;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.PagerAdapter;

import com.ahmed.homeservices.R;
import com.ahmed.homeservices.activites.cutomer_or_worker.login.CustomerWorkerLoginActivity;
import com.ahmed.homeservices.activites.meowbottomnavigaion.MainActivity;
import com.ahmed.homeservices.constants.Constants;
import com.ahmed.homeservices.models.CardItem;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.florent37.viewtooltip.ViewTooltip;
import com.pixplicity.easyprefs.library.Prefs;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.github.florent37.viewtooltip.ViewTooltip.ALIGN.CENTER;
import static com.github.florent37.viewtooltip.ViewTooltip.Position.BOTTOM;


public class CardPagerAdapter extends PagerAdapter implements CardAdapter {

    private static final String TAG = "CardPagerAdapter";
    private Context context;
    private List<CardView> mViews;
    private List<CardItem> mData;
    private float mBaseElevation;
    private Handler handler;
    private Runnable r;
    private Activity activity;
    private int duration = 900;

//    public CardPagerAdapter(Activity activity) {
//        mData = new ArrayList<>();
//        mViews = new ArrayList<>();
//        this.activity = activity;
//    }

    public CardPagerAdapter(Context applicationContext) {
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
    public boolean isViewFromObject(@NotNull View view, @NotNull Object object) {
        return view == object;
    }

    @NotNull
    @Override
    public Object instantiateItem(@NotNull ViewGroup container, int position) {
        View view = null;
        switch (position) {
            case 0:
                view = LayoutInflater.from(container.getContext())
                        .inflate(R.layout.layout_customers, container, false);
                break;
            case 1:
                view = LayoutInflater.from(container.getContext())
                        .inflate(R.layout.layout_worker, container, false);
                break;
            case 2:
                view = LayoutInflater.from(container.getContext())
                        .inflate(R.layout.layout_company, container, false);
                break;
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


        YoYo.with(Techniques.FadeIn)
                .duration(600)
                .playOn(view);


        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, @NotNull Object object) {
        container.removeView((View) object);
        mViews.set(position, null);
    }

    private void bind(int position, CardItem item, View view) {
        switch (position) {
            case 0:
//                if (Prefs.getPreferences().contains(Constants.HINT_CUSTOMER)) {
//                    view.findViewById(R.id.login_as_customer_hint).setVisibility(View.GONE);
//                } else {
//                    view.findViewById(R.id.login_as_customer_hint).setVisibility(View.VISIBLE);
//                    YoYo.with(Techniques.BounceIn)
//                            .duration(duration)
//                            .onEnd(new YoYo.AnimatorCallback() {
//                                @Override
//                                public void call(Animator animator) {
////                                view.findViewById(R.id.login_as_customer_hint).setVisibility(View.VISIBLE);
//                                }
//                            })
//                            .playOn(view.findViewById(R.id.login_as_customer_hint));
//                    view.findViewById(R.id.btn_lets_go).setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            YoYo.with(Techniques.FadeOutDown)
//                                    .duration(duration)
//                                    .onEnd(new YoYo.AnimatorCallback() {
//                                        @Override
//                                        public void call(Animator animator) {
//                                            view.findViewById(R.id.login_as_customer_hint).setVisibility(View.GONE);
//                                            Prefs.edit().putString(Constants.HINT_CUSTOMER, "").apply();
//                                        }
//                                    })
//                                    .playOn(view.findViewById(R.id.login_as_customer_hint));
//                        }
//                    });
//                }


                //---------------------------

                view.findViewById(R.id.login_customer_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((Activity) context).finish();
                        Intent intent = new Intent(context, CustomerWorkerLoginActivity.class);
                        context.startActivity(intent);
                        Prefs.edit().putString(Constants.WORKER_OR_CUSTOMER, Constants.CUSTOMERS).apply();
                        ((Activity) context).overridePendingTransition(R.anim.enter, R.anim.exit);

                    }
                });
                view.findViewById(R.id.tvCustomerSkipRegisteration).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((Activity) context).finish();
                        Intent intent = new Intent(context, MainActivity.class);
                        context.startActivity(intent);
                        Prefs.edit().putString(Constants.WORKER_OR_CUSTOMER, Constants.CUSTOMERS).apply();
                        ((Activity) context).overridePendingTransition(R.anim.enter, R.anim.exit);

                    }
                });

//                TextView tv = view.findViewById(R.id.tvLoginCustomer);
//                showToolTips(activity, tv, activity.getString(R.string.hint_user));


                //Disabling SHimmer anim as the Memory
//                ShimmerLayout shimmerLayout = view.findViewById(R.id.shimmerLayout);
//                shimmerLayout.startShimmerAnimation();


//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                            shimmerLayout.setShimmerColor(context.getColor(R.color.transparent));
//                        }else {
//                            shimmerLayout.setShimmerColor(ContextCompat.getColor(context, R.color.transparent));
//                        }
//                        shimmerLayout.setVisibility(View.GONE);
//                    }
//                }, 2000);


//                ScrollView scrollView = view.findViewById(R.id.scrollViewCustomer);
//                handler = new Handler();
//                r = new Runnable() {
//                    public void run() {
//                        scrollView.smoothScrollTo(0, 500);
//
//                    }
//                };
//                handler.postDelayed(r, 200);

                break;
            case 1:
//                if (Prefs.getPreferences().contains(Constants.HINT_WORKER)) {
//                    view.findViewById(R.id.login_as_worker_hint).setVisibility(View.GONE);
//                } else {
//                    view.findViewById(R.id.login_as_worker_hint).setVisibility(View.VISIBLE);
//                    view.findViewById(R.id.btn_lets_go).setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            YoYo.with(Techniques.FadeOutDown)
//                                    .duration(duration)
//                                    .onEnd(new YoYo.AnimatorCallback() {
//                                        @Override
//                                        public void call(Animator animator) {
//                                            view.findViewById(R.id.login_as_worker_hint).setVisibility(View.GONE);
//                                            Prefs.edit().putString(Constants.HINT_WORKER, "").apply();
//                                        }
//                                    })
//                                    .playOn(view.findViewById(R.id.login_as_worker_hint));
//                        }
//                    });
//                }
                view.findViewById(R.id.login_worker_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((Activity) context).finish();
                        Intent intent = new Intent(context, CustomerWorkerLoginActivity.class);
                        context.startActivity(intent);
                        ((Activity) context).overridePendingTransition(R.anim.enter, R.anim.exit);
                        Prefs.edit().putString(Constants.WORKER_OR_CUSTOMER, Constants.WORKERS).apply();

                    }
                });

//                shimmerLayout = view.findViewById(R.id.shimmerLayout);
//                shimmerLayout.startShimmerAnimation();


//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        shimmerLayout.stopShimmerAnimation();
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                            shimmerLayout.setShimmerColor(context.getColor(R.color.transparent));
//                        }else {
//                            shimmerLayout.setShimmerColor(ContextCompat.getColor(context, R.color.transparent));
//                        }
//                        shimmerLayout.setVisibility(View.GONE);
//
//                    }
//                }, 2000);
//                scrollView = view.findViewById(R.id.scrollViewWorker);
//                handler = new Handler();
//                r = new Runnable() {
//                    public void run() {
//                        scrollView.smoothScrollTo(0, 500);
//
//                    }
//                };
//                handler.postDelayed(r, 200);
                break;

            case 2:
//                if (Prefs.getPreferences().contains(Constants.HINT_COMPANY)) {
//                    view.findViewById(R.id.login_as_company_hint).setVisibility(View.GONE);
//                } else {
//                    view.findViewById(R.id.login_as_company_hint).setVisibility(View.VISIBLE);
//                    view.findViewById(R.id.btn_lets_go).setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            YoYo.with(Techniques.FadeOutDown)
//                                    .duration(duration)
//                                    .onEnd(new YoYo.AnimatorCallback() {
//                                        @Override
//                                        public void call(Animator animator) {
//                                            view.findViewById(R.id.login_as_company_hint).setVisibility(View.GONE);
//                                            Prefs.edit().putString(Constants.HINT_COMPANY, "").apply();
//                                        }
//                                    })
//                                    .playOn(view.findViewById(R.id.login_as_company_hint));
//                        }
//                    });
//                }
                view.findViewById(R.id.login_company_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((Activity) context).finish();
                        Intent intent = new Intent(context, CustomerWorkerLoginActivity.class);
                        context.startActivity(intent);
                        ((Activity) context).overridePendingTransition(R.anim.enter, R.anim.exit);
                        Prefs.edit().putString(Constants.WORKER_OR_CUSTOMER, Constants.COMPANIES).apply();

                    }
                });
                break;
        }
    }

    private void showToolTips(Activity activity, View view, String text) {
        ViewTooltip
                .on(activity, view)
                .autoHide(true, 4000)
                .clickToHide(true)
                .align(CENTER)
                .position(BOTTOM)
                .text(text)
                .textColor(Color.WHITE)
                .color(Color.parseColor("#FF8469"))
                .corner(10)
                .arrowWidth(15)
                .arrowHeight(15)
                .distanceWithView(0)
                //change the opening animation
                .animation(new ViewTooltip.FadeTooltipAnimation())
                .animation(new ViewTooltip.TooltipAnimation() {
                    @Override
                    public void animateEnter(View view, Animator.AnimatorListener animatorListener) {

                    }

                    @Override
                    public void animateExit(View view, Animator.AnimatorListener animatorListener) {

                    }
                })
                //listeners
                .onDisplay(new ViewTooltip.ListenerDisplay() {
                    @Override
                    public void onDisplay(View view) {

                    }
                })
                .onHide(new ViewTooltip.ListenerHide() {
                    @Override
                    public void onHide(View view) {

                    }
                })
                .show();

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
