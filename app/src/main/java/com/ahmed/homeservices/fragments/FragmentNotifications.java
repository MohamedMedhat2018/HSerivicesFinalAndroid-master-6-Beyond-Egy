package com.ahmed.homeservices.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.ahmed.homeservices.R;
import com.ahmed.homeservices.activites.cutomer_or_worker.login.CustomerWorkerLoginActivity;
import com.ahmed.homeservices.activites.details.customer.CustomerPostDetails;
import com.ahmed.homeservices.activites.details.freelancer.FreelancerPostDetails;
import com.ahmed.homeservices.activites.meowbottomnavigaion.MainActivity;
import com.ahmed.homeservices.adapters.grid.CardFragmentPagerAdapter;
import com.ahmed.homeservices.adapters.rv.notifications.NotificationAdapter;
import com.ahmed.homeservices.adapters.view_pager.CardPagerAdapterAfterLogin;
import com.ahmed.homeservices.constants.Constants;
import com.ahmed.homeservices.fire_utils.RefBase;
import com.ahmed.homeservices.interfaces.OnNotificationClick;
import com.ahmed.homeservices.interfaces.OnWorkerActivated;
import com.ahmed.homeservices.messages.MsgFrmLoginToNotification;
import com.ahmed.homeservices.messages.MsgPushedNotification;
import com.ahmed.homeservices.models.CardItem;
import com.ahmed.homeservices.models.Notification;
import com.ahmed.homeservices.models.ShadowTransformer;
import com.ahmed.homeservices.models.User;
import com.ahmed.homeservices.models.orders.OrderRequest;
import com.ahmed.homeservices.utils.Utils;
import com.developer.kalert.KAlertDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.pixplicity.easyprefs.library.Prefs;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

//import com.google.android.gms.ads.AdListener;
//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;
//import com.google.android.gms.ads.MobileAds;
//import com.google.android.gms.ads.initialization.InitializationStatus;
//import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class FragmentNotifications extends Fragment implements OnNotificationClick,
        OnWorkerActivated,
        SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "FragmentNotifications";
    @BindView(R.id.rvNotifications)
    RecyclerView rvNotifications;
    @BindView(R.id.llLogin)
    LinearLayout llLogin;
    @BindView(R.id.viewPagerLogin)
    ViewPager viewPagerLogin;
    @BindView(R.id.tvNoNotifi)
    TextView tvNoNotifi;
    @BindView(R.id.progress)
    View progress;
    @BindView(R.id.includeLoginCustomer)
    View includeLoginCustomer;
    @BindView(R.id.includeLoginWorker)
    View includeLoginWorker;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    private List<Notification> notifications = new ArrayList<>();
    //    @BindView(R.id.adView)
//    AdView adView;
    private CardPagerAdapterAfterLogin mCardAdapter;
    private ShadowTransformer mCardShadowTransformer;
    private CardFragmentPagerAdapter mFragmentCardAdapter;
    private ShadowTransformer mFragmentCardShadowTransformer;
    private boolean mShowingFragments = false;
    private int delayMillis = 1000;
    private AlertDialog spotsDialog;
    private Intent intent;
    private String userId;

    private void showLoadingDialog() {
//        progressDialog = new ProgressDialog(getActivity());
//        progressDialog.setTitle("Loading...");
//        progressDialog.show();

        spotsDialog = Utils.getInstance().pleaseWait(getActivity());
        spotsDialog.setCanceledOnTouchOutside(true);
        spotsDialog.setCancelable(true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MsgPushedNotification event) {
//        Toast.makeText(getContext(), "Hey, my message" + event.isPushedOrNot(), Toast.LENGTH_SHORT).show();
//        setAdapterToLv();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true, priority = 1)
    public void onEvent(MsgFrmLoginToNotification event) {
//        Toast.makeText(getContext(), "Hey, my message" + event.isPushedOrNot(), Toast.LENGTH_SHORT).show();
//        setAdapterToLv();
        if (event != null) {
            checkUserType();
            EventBus.getDefault().removeAllStickyEvents();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_notifications, container, false);
        ButterKnife.bind(FragmentNotifications.this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //subscribeToTopic();
        //setAdapterToLv();
        checkUserType();
        initRecyclerView();
        accessWorkerCustomerLoginIncludes();

        handlingSwipeRefresh();


        loadAds();

    }

    private void handlingSwipeRefresh() {

        swipeRefreshLayout.setOnRefreshListener(this);
        new Utils(getActivity()).setColorSchemeToSwipeRefrseh(swipeRefreshLayout);

    }

    private void accessWorkerCustomerLoginIncludes() {
        if (Utils.customerOrWorker()) {
            includeLoginCustomer.setVisibility(View.VISIBLE);
            includeLoginWorker.setVisibility(View.GONE);
            includeLoginCustomer.findViewById(R.id.login_customer_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), CustomerWorkerLoginActivity.class);
                    getActivity().startActivity(intent);
                    Prefs.edit().putString(Constants.WORKER_OR_CUSTOMER, Constants.CUSTOMERS).apply();
                    getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);

                }
            });
            includeLoginCustomer.findViewById(R.id.tvCustomerSkipRegisteration).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    getActivity().startActivity(intent);
                    Prefs.edit().putString(Constants.WORKER_OR_CUSTOMER, Constants.CUSTOMERS).apply();
                    getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);

                }
            });

        } else {
            includeLoginCustomer.setVisibility(View.GONE);
            includeLoginWorker.setVisibility(View.VISIBLE);
            includeLoginWorker.findViewById(R.id.login_worker_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), CustomerWorkerLoginActivity.class);
                    getActivity().startActivity(intent);
                    Prefs.edit().putString(Constants.WORKER_OR_CUSTOMER, Constants.WORKERS).apply();
                    getActivity().overridePendingTransition(R.anim.enter, R.anim.exit);
                }
            });

        }


    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvNotifications.setLayoutManager(layoutManager);
        rvNotifications.setItemAnimator(new DefaultItemAnimator());
        rvNotifications.setHasFixedSize(false);
//        rvNotifications.addItemDecoration(new MyDividerItemDecoration(getActivity(),
//                layoutManager.getOrientation(), (int) .2));


    }

    private void checkUserType() {
        if (Prefs.contains(Constants.FIREBASE_UID)) {
            Log.e(TAG, "normal user: ");
            String id = Prefs.getString(Constants.FIREBASE_UID, "");
            llLogin.setVisibility(View.GONE);
            rvNotifications.setVisibility(View.VISIBLE);
            if (Utils.customerOrWorker()) {
                Log.e(TAG, "checkUserType: " + id);
//                loadNotificationCustomers("LZ1eBa2k0VNieyYplstV3YOXMBJ2");
                loadNotificationCustomers(id);
            } else {
                checkWorkerActivation();
                switch (Prefs.getString(Constants.WORKER_LOGGED_AS, "")) {
                    case Constants.CM:
                        Log.e(TAG, "CM: ");
                        showLoadDialog();
                        RefBase.notifiCm(id)
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                                            dataSnapshot.getRef().removeEventListener(this);
                                            swipeRefreshLayout.setRefreshing(false);
                                            notifications.clear();
                                            //spotsDialog.dismiss();
                                            progress.setVisibility(View.GONE);
                                            for (DataSnapshot snap :
                                                    dataSnapshot.getChildren()) {
                                                HashMap<String, Object> hashMap =
                                                        (HashMap<String, Object>) snap.getValue();
                                                Log.e(TAG, "onDataChange:1.1 " + hashMap.toString());
                                                String message = hashMap.get("message").toString();
                                                String title = hashMap.get("title").toString();
//                                String isShown = hashMap.get("shown").toString();
                                                boolean isShown = (boolean) hashMap.get("shown");
//                                String orderId = hashMap.get("orderId").toString();
                                                Log.e(TAG, "onDataChange:1.1 " + title + " " + message);
                                                Notification notification = new Notification();
                                                notification.setBody(message);
                                                notification.setNotificationId(snap.getKey());
                                                notification.setTitle(title);
                                                if (hashMap.get("orderId") != null) {
                                                    notification.setOrderId(hashMap.get("orderId").toString());
                                                }
                                                Log.e(TAG, "onDataChange: " + title);
//                                notification.setShown(TextUtils.equals(isShown, "true"));
                                                notification.setShown(isShown);
                                                notifications.add(notification);
                                            }
                                            setAdapter();

                                            tvNoNotifi.setVisibility(View.GONE);
                                        } else {

                                            progress.setVisibility(View.GONE);
//                                            spotsDialog.dismiss();
                                            Toasty.info(getActivity(), getActivity().getString(R.string.no_notification_yet)).show();
                                            tvNoNotifi.setVisibility(View.VISIBLE);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
//                        if (spotsDialog != null && spotsDialog.isShowing())
//                            spotsDialog.dismiss();
                        break;
                    case Constants.FREELANCER:
                        Log.e(TAG, "FREELANCER: ");
                        showLoadDialog();
                        Log.e(TAG, "checkUserType: " + id);
                        RefBase.notifiFreelance(id)
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                                            dataSnapshot.getRef().removeEventListener(this);
                                            swipeRefreshLayout.setRefreshing(false);
                                            notifications.clear();
                                            progress.setVisibility(View.GONE);


                                            String message = null, title = null;
                                            boolean isShown = false;
                                            Notification notification;


                                            for (DataSnapshot snap :
                                                    dataSnapshot.getChildren()) {
                                                HashMap<String, Object> hashMap =
                                                        (HashMap<String, Object>) snap.getValue();
                                                Log.e(TAG, "onDataChange:1.1 " + hashMap.toString());

                                                if (hashMap.get("message") != null) {
                                                    message = hashMap.get("message").toString();
                                                }
                                                if (hashMap.get("message") != null) {
                                                    title = hashMap.get("title").toString();
                                                }
                                                if (hashMap.get("message") != null) {
                                                    isShown = (boolean) hashMap.get("shown");
                                                }

//                                                Log.e(TAG, "onDataChange:1.1 " + title + " " + message);
                                                notification = new Notification();
                                                notification.setBody(message);
                                                notification.setNotificationId(snap.getKey());
                                                notification.setTitle(title);
                                                if (hashMap.get("orderId") != null) {
                                                    notification.setOrderId(hashMap.get("orderId").toString());
                                                }
                                                notification.setShown(isShown);


                                                notifications.add(notification);
                                            }
                                            setAdapter();


                                            tvNoNotifi.setVisibility(View.GONE);
                                        } else {

                                            progress.setVisibility(View.GONE);
//                                            spotsDialog.dismiss();
                                            Toasty.info(getActivity(), getActivity().getString(R.string.no_notification_yet)).show();
                                            tvNoNotifi.setVisibility(View.VISIBLE);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        progress.setVisibility(View.GONE);
                                    }
                                });
//                        if (spotsDialog != null && spotsDialog.isShowing())
//                            spotsDialog.dismiss();
                        break;

                    case Constants.COMPANY:
                        Log.e(TAG, "COMPANY: Noti ");
                        showLoadDialog();
                        Log.e(TAG, "checkUserType: C Noti" + id);
                        RefBase.notifiFreelance(id)
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                                            dataSnapshot.getRef().removeEventListener(this);
                                            swipeRefreshLayout.setRefreshing(false);
                                            notifications.clear();
                                            progress.setVisibility(View.GONE);


                                            String message = null, title = null;
                                            boolean isShown = false;
                                            Notification notification;


                                            for (DataSnapshot snap :
                                                    dataSnapshot.getChildren()) {
                                                HashMap<String, Object> hashMap =
                                                        (HashMap<String, Object>) snap.getValue();
                                                Log.e(TAG, "onDataChange:1.1 " + hashMap.toString());

                                                if (hashMap.get("message") != null) {
                                                    message = hashMap.get("message").toString();
                                                }
                                                if (hashMap.get("message") != null) {
                                                    title = hashMap.get("title").toString();
                                                }
                                                if (hashMap.get("message") != null) {
                                                    isShown = (boolean) hashMap.get("shown");
                                                }

//                                                Log.e(TAG, "onDataChange:1.1 " + title + " " + message);
                                                notification = new Notification();
                                                notification.setBody(message);
                                                notification.setNotificationId(snap.getKey());
                                                notification.setTitle(title);
                                                if (hashMap.get("orderId") != null) {
                                                    notification.setOrderId(hashMap.get("orderId").toString());
                                                }
                                                notification.setShown(isShown);


                                                notifications.add(notification);
                                            }
                                            setAdapter();


                                            tvNoNotifi.setVisibility(View.GONE);
                                        } else {

                                            progress.setVisibility(View.GONE);
//                                            spotsDialog.dismiss();
                                            Toasty.info(getActivity(), getActivity().getString(R.string.no_notification_yet)).show();
                                            tvNoNotifi.setVisibility(View.VISIBLE);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        progress.setVisibility(View.GONE);
                                    }
                                });
//                        if (spotsDialog != null && spotsDialog.isShowing())
//                            spotsDialog.dismiss();
                        break;

                }
            }


        } else {
            llLogin.setVisibility(View.VISIBLE);
            rvNotifications.setVisibility(View.GONE);
//            login();

            accessWorkerCustomerLoginIncludes();


        }
    }

    private void checkWorkerActivation() {
//        Common.checkIfWorkerActivatedOrNot(Prefs.getString(Constants.FIREBASE_UID, ""), this);
    }

    private void loadNotificationCustomers(String id) {

        showLoadDialog();
        Log.e(TAG, "loadNotificationCustomers:1.1 ");

        RefBase.notifiCustomer(id)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {

                            dataSnapshot.getRef().removeEventListener(this);
                            swipeRefreshLayout.setRefreshing(false);

                            notifications.clear();
                            //spotsDialog.dismiss();
                            progress.setVisibility(View.GONE);

                            String message = null, title = null;
                            boolean isShown = false;
                            Notification notification;


                            for (DataSnapshot snap :
                                    dataSnapshot.getChildren()) {
                                HashMap<String, Object> hashMap =
                                        (HashMap<String, Object>) snap.getValue();
                                Log.e(TAG, "onDataChange:1.1 " + hashMap.toString());

                                if (hashMap.get("message") != null) {
                                    message = hashMap.get("message").toString();
                                }
                                if (hashMap.get("message") != null) {
                                    title = hashMap.get("title").toString();
                                }
                                if (hashMap.get("message") != null) {
                                    isShown = (boolean) hashMap.get("shown");
                                }

//                                                Log.e(TAG, "onDataChange:1.1 " + title + " " + message);
                                notification = new Notification();
                                notification.setBody(message);
                                notification.setNotificationId(snap.getKey());
                                notification.setTitle(title);
                                if (hashMap.get("orderId") != null) {
                                    notification.setOrderId(hashMap.get("orderId").toString());
                                }
                                notification.setShown(isShown);


                                notifications.add(notification);
                            }
                            setAdapter();


                            tvNoNotifi.setVisibility(View.GONE);
                        } else {

                            //spotsDialog.dismiss();
                            progress.setVisibility(View.GONE);
//                            Toasty.info(getActivity(), getActivity().getString(R.string.no_notification_yet)).show();
                            tvNoNotifi.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
//        if (spotsDialog != null && spotsDialog.isShowing())
//            spotsDialog.dismiss();
    }

    private void showLoadDialog() {
//        spotsDialog = Utils.getInstance().pleaseWait(getActivity());
//        spotsDialog.setBody("Please wait");
//        spotsDialog.setCancelable(false);
//        spotsDialog.setCanceledOnTouchOutside(false);
//        spotsDialog.show();

        progress.setVisibility(View.VISIBLE);
        ((TextView) progress.findViewById(R.id.tvMessage)).setText(getActivity().getString(R.string.please_wait));

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        loadAds();


    }

    private void loadAds() {


////        2019-10-06 17:33:54.403 3591-3591/com.ahmed.homeservices I/Ads: Use AdRequest.Builder.
////                addTestDevice("B33070405DC9BA53BE845037294040C2") to get test ads on this device
//        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
//            @Override
//            public void onInitializationComplete(InitializationStatus initializationStatus) {
//                Log.e(TAG, "onInitializationComplete: ");
//            }
//        });
//
////        mAdView = findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder().build();
////        AdRequest adRequest = new AdRequest.Builder().addTestDevice("B33070405DC9BA53BE845037294040C2").build();
//        adView.loadAd(adRequest);
//        adView.setAdListener(new AdListener() {
//            @Override
//            public void onAdLoaded() {
//                // Code to be executed when an ad finishes loading.
//                Log.e(TAG, "onAdLoaded: " );
//            }
//
//            @Override
//            public void onAdFailedToLoad(int errorCode) {
//                // Code to be executed when an ad request fails.
//            }
//
//            @Override
//            public void onAdOpened() {
//                // Code to be executed when an ad opens an overlay that
//                // covers the screen.
//            }
//
//            @Override
//            public void onAdClicked() {
//                // Code to be executed when the user clicks on an ad.
//            }
//
//            @Override
//            public void onAdLeftApplication() {
//                // Code to be executed when the user has left the app.
//            }
//
//            @Override
//            public void onAdClosed() {
//                // Code to be executed when the user is about to return
//                // to the app after tapping on an ad.
//            }
//        });
    }

    private void setAdapter() {
        Collections.reverse(notifications);
        NotificationAdapter notificationAdapter =
                new NotificationAdapter(getActivity(), notifications, this::onNotificationClick);
        rvNotifications.setAdapter(notificationAdapter);
    }

    private void login() {
        mCardAdapter = new CardPagerAdapterAfterLogin(getActivity());
        mCardAdapter.addCardItem(new CardItem(R.string.title_1, R.string.text_1));
//        mCardAdapter.addCardItem(new CardItem(R.string.title_2, R.string.title_2));

        mFragmentCardAdapter = new CardFragmentPagerAdapter(getActivity().getSupportFragmentManager(),
                Utils.dpToPixels(2, getActivity()));

        mCardShadowTransformer = new ShadowTransformer(viewPagerLogin, mCardAdapter);
        mFragmentCardShadowTransformer = new ShadowTransformer(viewPagerLogin, mFragmentCardAdapter);

        viewPagerLogin.setAdapter(mCardAdapter);
        viewPagerLogin.setPageTransformer(false, mCardShadowTransformer);
//        viewPagerLogin.setOffscreenPageLimit(3);
//        viewPagerLogin.setPageMargin(1);

//        mCardShadowTransformer.enableScaling(true);
//        mFragmentCardShadowTransformer.enableScaling(true);


        //for confirming the user that there is an options
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                viewPagerLogin.setCurrentItem(1, true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        viewPagerLogin.setCurrentItem(0, true);
                    }
                }, delayMillis);
            }
        }, delayMillis);
    }

    @Override
    public void onNotificationClick(Notification notification, int pos) {
//        Log.e(TAG, "onNotificationClick: " + notification.getBody());
        userId = Prefs.getString(Constants.FIREBASE_UID, "");
//        showNotificationDetails(notification);
        showMoreDetails(notification, userId);
    }

    private void showMoreDetails(Notification notification, String userId) {
        if (Utils.customerOrWorker()) {
            Log.e(TAG, "normal user: ");
//            HashMap<String, Object> map = new HashMap<>();
//            map.put("shown", true);
            //        if (!notification.isShown()) {
//            changeItToShown(notification, userId);
            changeItToShown2(notification, userId);
//        }
        } else {
            switch (Prefs.getString(Constants.WORKER_LOGGED_AS, "")) {
//                case Constants.CM:
//                    if (!notification.isShown()) {
//                        changeItToShown(notification, userId);
//                    }
//                    break;
                case Constants.FREELANCER:
                    changeItToShownFreelancer(notification, userId);
                    break;
                case Constants.COMPANY:
                    changeItToShownCompany(notification, userId);
                    break;
            }
        }
    }

    private void showNotificationDetails(Notification notification) {
        if (getActivity() != null) {
            new KAlertDialog(getActivity(), KAlertDialog.SUCCESS_TYPE)
                    .setTitleText(notification.getTitle())
                    .setContentText(notification.getBody())
                    .setConfirmText(getString(R.string.preview_notification))
                    .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                        @Override
                        public void onClick(KAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();

                            showMoreDetails(notification, userId);
                        }
                    })
                    .setCancelText(getString(R.string.dialog_cancel))
                    .setCancelClickListener(new KAlertDialog.KAlertClickListener() {
                        @Override
                        public void onClick(KAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                        }
                    })
                    .show();

        }

    }

    private void changeItToShownFreelancer(Notification notification, String userId) {


//        spotsDialog.show();
        progress.setVisibility(View.VISIBLE);
        Log.e(TAG, "44444444444: " + notification.getNotificationId());

        Log.e(TAG, "changeItToShown2: " + userId + "- -" + notification.getNotificationId());

        RefBase.notifiFreelance(userId)
                .child(notification.getNotificationId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().removeEventListener(this);
                        progress.setVisibility(View.GONE);
                        if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                            HashMap<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
//                            Log.e(TAG, "ffffffffff: " + dataSnapshot.getKey());
                            Notification notif = dataSnapshot.getValue(Notification.class);
                            Log.e(TAG, "ffffffffff: " + notif.getOrderId());
                            Log.e(TAG, "onDataChange: " + dataSnapshot.toString());
                            if (notif != null) {
//                                notif.setShown(true);

                                dataSnapshot.getRef().child("shown").setValue(true);
                                Log.e(TAG, "666666677: " + notif.getOrderId());
                                //show the clicked order request from pushed notificat

                                String orderId = "";
                                if (map != null && map.get("orderId") != null) {
                                    orderId = map.get("orderId").toString();
                                }


                                //..............
//                                if (notif.getOrderId().length() != 0 && notif.getOrderId() != null) {
                                if (orderId.length() != 0 && orderId != null) {
//                                    progress.setVisibility(View.VISIBLE);
//                                    EventBus.getDefault().post(new MsgFromFreeToFrgOrderAndPosts());
                                    RefBase
                                            .refRequests()
                                            .orderByChild(Constants.ORDER_ID)
                                            .equalTo(orderId)
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    dataSnapshot.getRef().removeEventListener(this);
                                                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                                                        for (DataSnapshot dataSnapshot1 :
                                                                dataSnapshot.getChildren()) {
                                                            OrderRequest orderRequest = dataSnapshot1.getValue(OrderRequest.class);
                                                            RefBase.refUser(orderRequest.getCustomerId()).addValueEventListener(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                    dataSnapshot.getRef().removeEventListener(this);
                                                                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {

                                                                        showNotifiWithPreview(dataSnapshot, notification, new KAlertDialog.KAlertClickListener() {
                                                                            @Override
                                                                            public void onClick(KAlertDialog sDialog) {
                                                                                sDialog.dismissWithAnimation();

                                                                                User user = dataSnapshot.getValue(User.class);
                                                                                Intent intent = new Intent(getActivity(), FreelancerPostDetails.class);
                                                                                Bundle bundle = new Bundle();
                                                                                bundle.putSerializable(Constants.ORDER, orderRequest);
                                                                                bundle.putSerializable(Constants.USER, user);
                                                                                intent.putExtras(bundle);
                                                                                startActivity(intent);


                                                                            }
                                                                        });


                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                }
                                                            });

                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                } else {
//                                    progress.setVisibility(View.GONE);
                                    showNotifiWithoutPreview(notification);
                                }

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void changeItToShownCompany(Notification notification, String userId) {


//        spotsDialog.show();
        progress.setVisibility(View.VISIBLE);
        Log.e(TAG, "44444444444:222222 " + notification.getNotificationId());

        Log.e(TAG, "changeItToShown2:22222 " + userId + "- -" + notification.getNotificationId());

        RefBase.notifiFreelance(userId)
                .child(notification.getNotificationId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().removeEventListener(this);
                        progress.setVisibility(View.GONE);
                        if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                            HashMap<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
//                            Log.e(TAG, "ffffffffff: " + dataSnapshot.getKey());
                            Notification notif = dataSnapshot.getValue(Notification.class);
                            Log.e(TAG, "ffffffffff: " + notif.getOrderId());
                            Log.e(TAG, "onDataChange: " + dataSnapshot.toString());
                            if (notif != null) {
//                                notif.setShown(true);

                                dataSnapshot.getRef().child("shown").setValue(true);
                                Log.e(TAG, "666666677: " + notif.getOrderId());
                                //show the clicked order request from pushed notificat

                                String orderId = "";
                                if (map != null && map.get("orderId") != null) {
                                    orderId = map.get("orderId").toString();
                                }


                                //..............
//                                if (notif.getOrderId().length() != 0 && notif.getOrderId() != null) {
                                if (orderId.length() != 0 && orderId != null) {
//                                    progress.setVisibility(View.VISIBLE);
//                                    EventBus.getDefault().post(new MsgFromFreeToFrgOrderAndPosts());
                                    RefBase.refRequests()
                                            .orderByChild(Constants.ORDER_ID)
                                            .equalTo(orderId)
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    dataSnapshot.getRef().removeEventListener(this);
                                                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                                                        for (DataSnapshot dataSnapshot1 :
                                                                dataSnapshot.getChildren()) {
                                                            OrderRequest orderRequest = dataSnapshot1.getValue(OrderRequest.class);
                                                            RefBase.refUser(orderRequest.getCustomerId()).addValueEventListener(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                    dataSnapshot.getRef().removeEventListener(this);
                                                                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {

                                                                        showNotifiWithPreview(dataSnapshot, notification, new KAlertDialog.KAlertClickListener() {
                                                                            @Override
                                                                            public void onClick(KAlertDialog sDialog) {
                                                                                sDialog.dismissWithAnimation();

                                                                                User user = dataSnapshot.getValue(User.class);
                                                                                Intent intent = new Intent(getActivity(), FreelancerPostDetails.class);
                                                                                Bundle bundle = new Bundle();
                                                                                bundle.putSerializable(Constants.ORDER, orderRequest);
                                                                                bundle.putSerializable(Constants.USER, user);
                                                                                intent.putExtras(bundle);
                                                                                startActivity(intent);


                                                                            }
                                                                        });


                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                }
                                                            });

                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                } else {
//                                    progress.setVisibility(View.GONE);
                                    showNotifiWithoutPreview(notification);
                                }

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    private void changeItToShown2(Notification notification, String userId) {


//        spotsDialog.show();
        progress.setVisibility(View.VISIBLE);
        Log.e(TAG, "44444444444: " + notification.getNotificationId());

        Log.e(TAG, "changeItToShown2: " + userId + "- -" + notification.getNotificationId());

        RefBase.notifiCustomer(userId)
                .child(notification.getNotificationId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().removeEventListener(this);
                        progress.setVisibility(View.GONE);
                        if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                            HashMap<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
//                            Log.e(TAG, "ffffffffff: " + dataSnapshot.getKey());
                            Notification notif = dataSnapshot.getValue(Notification.class);
                            Log.e(TAG, "ffffffffff: " + notif.getOrderId());
                            Log.e(TAG, "onDataChange: " + dataSnapshot.toString());
                            if (notif != null) {
//                                notif.setShown(true);
                                Log.e(TAG, "onSuccess:1.1 " + notif.isShown());

                                dataSnapshot.getRef().child("shown").setValue(true)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.e(TAG, "onSuccess:1.2 " + notif.isShown());
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e(TAG, "onFailure: " + e.getMessage());
                                            }
                                        });
                                Log.e(TAG, "666666677: " + notif.getOrderId());
                                //show the clicked order request from pushed notificat

                                String orderId = "";
                                if (map != null && map.get("orderId") != null) {
                                    orderId = map.get("orderId").toString();
                                }


                                //..............
//                                if (notif.getOrderId().length() != 0 && notif.getOrderId() != null) {
                                if (orderId.length() != 0 && orderId != null) {
//                                    progress.setVisibility(View.VISIBLE);
                                    RefBase.refRequests()
//                                        .orderByChild(Constants.ORDER_ID)
//                                        .equalTo(notif.getOrderId())
                                            .child(orderId)
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    dataSnapshot.getRef().removeEventListener(this);
                                                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {


                                                        showNotifiWithPreview(dataSnapshot,
                                                                notification, new KAlertDialog.KAlertClickListener() {
                                                                    @Override
                                                                    public void onClick(KAlertDialog sDialog) {
                                                                        sDialog.dismissWithAnimation();

                                                                        Log.e(TAG, "666666677:    hr");
//                                                    OrderRequest orderRequest = dataSnapshot.getChildren().iterator().next().getValue(OrderRequest.class);
                                                                        OrderRequest orderRequest = dataSnapshot.getValue(OrderRequest.class);
//                                                    progress.setVisibility(View.GONE);
//                                                    spotsDialog.dismiss();
                                                                        Bundle bundle = new Bundle();
                                                                        intent = new Intent(getActivity(), CustomerPostDetails.class);
                                                                        bundle.putSerializable(Constants.ORDER, orderRequest);
                                                                        intent.putExtras(bundle);
                                                                        startActivity(intent);
                                                                    }
                                                                });


                                                    } else {
//                                                        progress.setVisibility(View.GONE);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                } else {
//                                    progress.setVisibility(View.GONE);
                                    showNotifiWithoutPreview(notification);
                                }


                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void showNotifiWithPreview(@NonNull DataSnapshot dataSnapshot, Notification notification,
                                       KAlertDialog.KAlertClickListener kAlertClickListener) {
        KAlertDialog pDialog = new KAlertDialog(getActivity(), KAlertDialog.SUCCESS_TYPE)
                .setTitleText(notification.getTitle())
                .setContentText(notification.getBody())
                .setConfirmText(getString(R.string.preview_notification))
                .setConfirmClickListener(kAlertClickListener)
                .setCancelText(getString(R.string.dialog_cancel))
                .setCancelClickListener(new KAlertDialog.KAlertClickListener() {
                    @Override
                    public void onClick(KAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                });

        pDialog.setCanceledOnTouchOutside(true);
        pDialog.setCancelable(true);
        pDialog.show();
    }

    private void showNotifiWithoutPreview(Notification notification) {

        new KAlertDialog(getActivity(), KAlertDialog.SUCCESS_TYPE)
                .setTitleText(notification.getTitle())
                .setContentText(notification.getBody())
                .setConfirmText(getString(R.string.ok))
                .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                    @Override
                    public void onClick(KAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })

                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkUserType();

    }

    private void changeItToShown(Notification notification, String userId) {
        RefBase.notifiCustomer(userId)
//                .child(notification.getNotificationId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            dataSnapshot.getRef().removeEventListener(this);
                            Log.e(TAG, "ffffffffff: " + dataSnapshot.getKey());
                            Notification notif = dataSnapshot.getValue(Notification.class);
                            Log.e(TAG, "ffffffffff: " + notif.getBody());
                            notif.setShown(true);
                            dataSnapshot.getRef().setValue(notif);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onWorkerActivated(boolean activationStatus) {
        Log.e(TAG, "onWorkerActivated: " + activationStatus);


    }

    @Override
    public void onRefresh() {
        //new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        }, 1400);
        checkUserType();


    }


}