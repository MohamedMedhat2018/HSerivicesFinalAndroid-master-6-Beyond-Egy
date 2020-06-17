package com.ahmed.homeservices.fragments;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.ahmed.homeservices.R;
import com.ahmed.homeservices.activites.cutomer_or_worker.login.CustomerWorkerLoginActivity;
import com.ahmed.homeservices.activites.details.cm_worker.CmOrderDetails;
import com.ahmed.homeservices.activites.details.customer.CustomerOrderDetails;
import com.ahmed.homeservices.activites.details.customer.CustomerPostDetails;
import com.ahmed.homeservices.activites.details.freelancer.FreelancerPostDetails;
import com.ahmed.homeservices.activites.meowbottomnavigaion.MainActivity;
import com.ahmed.homeservices.adapters.grid.CardFragmentPagerAdapter;
import com.ahmed.homeservices.adapters.rv.FreelancerPostsAdapter;
import com.ahmed.homeservices.adapters.rv.MyOrderCmWorkingAdapter;
import com.ahmed.homeservices.adapters.rv.MyOrderCustomerAdapter;
import com.ahmed.homeservices.adapters.view_pager.CardPagerAdapterAfterLogin;
import com.ahmed.homeservices.constants.Constants;
import com.ahmed.homeservices.fire_utils.RefBase;
import com.ahmed.homeservices.interfaces.OnItemClick;
import com.ahmed.homeservices.interfaces.OnMakeCallClick;
import com.ahmed.homeservices.messages.MsgFrmLoginToNotification;
import com.ahmed.homeservices.models.AdapterTabItem;
import com.ahmed.homeservices.models.CMWorker;
import com.ahmed.homeservices.models.CardItem;
import com.ahmed.homeservices.models.CmTask;
import com.ahmed.homeservices.models.Company;
import com.ahmed.homeservices.models.ConnectionFree;
import com.ahmed.homeservices.models.Location;
import com.ahmed.homeservices.models.LocationCompany;
import com.ahmed.homeservices.models.MsgEvtEditOrder;
import com.ahmed.homeservices.models.ShadowTransformer;
import com.ahmed.homeservices.models.User;
import com.ahmed.homeservices.models.orders.OrderRequest;
import com.ahmed.homeservices.utils.Utils;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetBuilder;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetMenuDialog;
import com.github.rubensousa.bottomsheetbuilder.adapter.BottomSheetItemClickListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pixplicity.easyprefs.library.Prefs;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.Manifest.permission.CALL_PHONE;


public class FragmentOrderAndPosts extends Fragment implements OnItemClick, SwipeRefreshLayout.OnRefreshListener, OnMakeCallClick {

    private static final String TAG = "FragmentOrderAndPosts";
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.llLogin)
    LinearLayout llLogin;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.viewPagerLogin)
    ViewPager viewPagerLogin;
    @BindView(R.id.llPagerHolder)
    LinearLayout llPagerHolder;

//    @BindView(R.id.segmentedButtonGroupCustomers)
//    SegmentedButtonGroup segmentedButtonGroupCustomers;
//    @BindView(R.id.segmentedBtnOrders)
//    SegmentedButton segmentedBtnOrders;
//    @BindView(R.id.segmentedBtnPosts)
//    SegmentedButton segmentedBtnPosts;
//    @BindView(R.id.segmentedButtonGroupWorkers)
//    SegmentedButtonGroup segmentedButtonGroupWorkers;
//    @BindView(R.id.segmentedBtnFinished)
//    SegmentedButton segmentedBtnFinished;
//    @BindView(R.id.segmentedBtnWorking)
//    SegmentedButton segmentedBtnWorking;
//    @BindView(R.id.segmentedButtonGroupFreelancer)
//    SegmentedButtonGroup segmentedButtonGroupFreelancer;


//    @BindView(R.id.MultiToggleCustomers)
//    MultiStateToggleButton multiToggleCustomers;
//    @BindView(R.id.MultiToggleFreelancer)
//    MultiStateToggleButton multiToggleFreelancer;
//    @BindView(R.id.MultiToggleCmWorkers)
//    MultiStateToggleButton multiToggleCmWorkers;


    @BindView(R.id.tablayoutCustomers)
    TabLayout tablayoutCustomers;
    @BindView(R.id.tablayoutWorkers)
    TabLayout tablayoutWorkers;
    @BindView(R.id.tablayoutFree)
    TabLayout tablayoutFree;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    ValueEventListener valueEventListener;
    @BindView(R.id.noOrdersOrPostsYet)
    TextView noOrdersOrPostsYet;
    @BindView(R.id.progress)
    View progress;
    @BindView(R.id.includeLoginCustomer)
    View includeLoginCustomer;
    @BindView(R.id.includeLoginWorker)
    View includeLoginWorker;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    boolean flag = true;
    boolean flag2 = true;
    //Hash map to hold all event listener for Queries
    private HashMap<Query, ValueEventListener> eventListenerHashMap = new HashMap<>();
    private Query query;
    private int[] tabIcons = {
            R.drawable.home,
            R.drawable.notification,
            R.drawable.star
    };
    private CardPagerAdapterAfterLogin mCardAdapter;
    private ShadowTransformer mCardShadowTransformer;
    private CardFragmentPagerAdapter mFragmentCardAdapter;
    //    @OnClick(R.id.btn_login)
//    public void btn_login(View v) {
//        startActivity(new Intent(getActivity(), CustomerWorkerLoginActivity.class));
//    }
    private ShadowTransformer mFragmentCardShadowTransformer;
    private boolean mShowingFragments = false;
    private int delayMillis = 1000;
    private List<AdapterTabItem> tabItems = new ArrayList<>();
    private List<OrderRequest> listFreePostCustomer = new ArrayList<>();
    private List<OrderRequest> listFreeFinishCustomer = new ArrayList<>();
    private List<OrderRequest> listFreeWorkingCustomer = new ArrayList<>();
    private ProgressDialog progressDialog;
    //customers
    private List<OrderRequest> listPending = new ArrayList<>();
    private List<OrderRequest> listCmWorking = new ArrayList<>();
    private List<OrderRequest> listCmFinishing = new ArrayList<>();
    private List<OrderRequest> listRejectedFromCustomer = new ArrayList<>();
    private List<OrderRequest> listRejectedFromCompany = new ArrayList<>();
    //workers
    private List<CmTask> listCmWorkingType = new ArrayList<>();
    private List<CmTask> listCmFinishedType = new ArrayList<>();
    //
    private List<OrderRequest> listFreeLancerPost = new ArrayList<>();
    private List<OrderRequest> listFreeLancerCompanyWorking = new ArrayList<>();
    private List<OrderRequest> listFreeLancerFinishing = new ArrayList<>();
    //    private List<CMWorker> listFreeLancerWorkers = new ArrayList<>();
    private List<CmTask> listFreeLancerWorkingPost = new ArrayList<>();
    private List<CmTask> listFreeLancerFinishedPost = new ArrayList<>();
    private List<User> users = new ArrayList<>();
    private List<OrderRequest> listCustomerBusiness = new ArrayList<>();
    private User userForCmWorker;
    private OrderRequest orderRequest;
    private Intent intent = null;
    private AlertDialog spotsDialog;
    private Context context;
    private int posToTab = 0;
    private BottomSheetMenuDialog menuEditPreview;
    private int posSelectedOrder;
    private String countryIdForCompanyFilter = null;
    private boolean countryActiveOrNot = false;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.fragment_orders_and_posts, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        if (getArguments() != null) {
            posToTab = getArguments().getInt(Constants.POS_TO_TAB, 0);
            Log.e(TAG, "POS_TO_TAB: " + posToTab);// = 1
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Log.e(TAG, "onViewCreated: ");
        //setCmTasksToFirebaseDb();
        initRecyclerview();
        accessSwipeRefresh();
        accessSegmentedControllButtons();
//        toggleBetweenLoginAndPreview();
//        initBottomSheetMemuDialog();

    }

    private void initBottomSheetMemuDialog() {

        if (Utils.customerOrWorker()) {
            if (tablayoutCustomers.getSelectedTabPosition() == 0) {
                Log.e(TAG, "initBottomSheetMemuDialog: customer in Pending Order ");

                menuEditPreview = new BottomSheetBuilder(getActivity(), null)
                        .setMode(BottomSheetBuilder.MODE_LIST)
//                .setMode(BottomSheetBuilder.MODE_GRID)
                        .addDividerItem()
                        .expandOnStart(true)
                        .setDividerBackground(R.color.grey_400)
                        .setBackground(R.drawable.ripple_grey)
                        .setMenu(R.menu.menu_edit_preview_order)
                        .setItemClickListener(new BottomSheetItemClickListener() {
                            @Override
                            public void onBottomSheetItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.preview:
                                        showMoreDetails(posSelectedOrder);
                                        Log.e(TAG, "initBottomSheetMemuDialog: preview ");
                                        break;
                                    case R.id.edit:
                                        editPendingRequests(posSelectedOrder);
                                        Log.e(TAG, "initBottomSheetMemuDialog: edit ");
                                        break;
                                }
                            }
                        })
                        .createDialog();

                menuEditPreview.show();
            } else {
                showMoreDetails(posSelectedOrder);
                Log.e(TAG, "initBottomSheetMemuDialog: Hello it's me Not pending  ");
            }
        } else {

            if (Utils.companyOrNot()) {
                showMoreDetails(posSelectedOrder);
                Log.e(TAG, "initBottomSheetMemuDialog2: Hello it's me I'm Customer  ot pending  ");
            } else {
                showMoreDetails(posSelectedOrder);
                Log.e(TAG, "initBottomSheetMemuDialog: Hello it's me I'm Customer  ot pending  ");
            }


        }
    }

    private void accessSwipeRefresh() {
        new Utils(getActivity()).setColorSchemeToSwipeRefrseh(swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    private void accessSegmentedControllButtons() {

//        toggleBetweenLoginAndPreview(0);

        tablayoutCustomers.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (flag2) {
                    Log.e(TAG, "onTabSelected: fuuuu1 ");
                    toggleBetweenLoginAndPreview(tab.getPosition());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

//        tablayoutWorkers.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                if (flag2) {
//                    toggleBetweenLoginAndPreview(tab.getPosition());
//                }
//
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });


        tablayoutFree.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.e(TAG, "onTabSelected: ");
                if (flag2) {
                    toggleBetweenLoginAndPreview(tab.getPosition());
                    Log.e(TAG, "onTabSelected: fuuuu2 ");

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

//        segmentedButtonGroupCustomers = getView().findViewById(R.id.segmentedButtonGroupCustomers);
//        segmentedBtnOrders = getView().findViewById(R.id.segmentedBtnOrders);
//        segmentedBtnPosts = getView().findViewById(R.id.segmentedBtnPosts);
//        segmentedButtonGroupWorkers = getView().findViewById(R.id.segmentedButtonGroupWorkers);
//        segmentedBtnFinished = getView().findViewById(R.id.segmentedBtnFinished);
//        segmentedBtnWorking = getView().findViewById(R.id.segmentedBtnWorking);
//        segmentedButtonGroupFreelancer = getView().findViewById(R.id.segmentedButtonGroupFreelancer);
//

    }

    @Override
    public void onResume() {
        super.onResume();

        if (swipeRefreshLayout != null) {
//            accessSegmentedControllButtons();
//            if(flag) {
            Log.e(TAG, "onResume: f1 ");
            onRefresh();
            flag2 = true;
//            }

        }

    }

    private void toggleBetweenLoginAndPreview(int position) {

        swipeRefreshLayout.setRefreshing(true);
        new Handler()
                .postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);

                    }
                }, 1000);///millis sec


        if (Prefs.getPreferences().contains(Constants.FIREBASE_UID)) {
            Log.e(TAG, "FIREBASE_UID:1.1 " + Prefs.getString(Constants.FIREBASE_UID, ""));
            llPagerHolder.setVisibility(View.VISIBLE);
            llLogin.setVisibility(View.GONE);
//            attachTableToViewPager();
            //attachTableToViewPager2();
//            Log.e(TAG, "toggleBetweenLoginAndPreview: " + Utils.customerOrWorker());
            if (Utils.customerOrWorker()) {
                Log.e(TAG, "toggleBetweenLoginAndPreview: AAAA");
                loadMyOrders();
                tablayoutWorkers.setVisibility(View.GONE);
                tablayoutFree.setVisibility(View.GONE);
                tablayoutCustomers.setVisibility(View.VISIBLE);
//                multiToggleFreelancer.setVisibility(View.GONE);
//                multiToggleCmWorkers.setVisibility(View.GONE);
//                multiToggleCustomers.setVisibility(View.VISIBLE);
//                segmentedButtonGroupCustomers.setOnPositionChangedListener(new SegmentedButtonGroup.OnPositionChangedListener() {
//                    @Override
//                    public void onPositionChanged(int position) {
                switch (position) {
                    case 0:
                        loadMyOrders();
                        break;
                    case 1:
                        //this
                        loadMyPosts();
                        //or this ????!!!!!!!!!
//                                loadFreelancerPosts();
                        break;
                }
//                    }
//                });


            } else {
//                Log.e(TAG, "toggleBetweenLoginAndPreview: BBBBB" );
//                segmentedButtonGroupCustomers.setVisibility(View.GONE);
//                segmentedButtonGroupWorkers.setVisibility(View.GONE);
//                segmentedButtonGroupFreelancer.setVisibility(View.VISIBLE);
                if (Prefs.contains(Constants.WORKER_LOGGED_AS)) {
                    Log.e(TAG, "toggleBetweenLoginAndPreview: BBBBB");
                    String type = Prefs.getString(Constants.WORKER_LOGGED_AS, "");
                    switch (type) {
//                        case Constants.CM:
//                            Log.e(TAG, "toggleBetweenLoginAndPreview: CM ");
//                            tablayoutWorkers.setVisibility(View.VISIBLE);
//                            tablayoutFree.setVisibility(View.GONE);
//                            tablayoutCustomers.setVisibility(View.GONE);
//
////                            segmentedButtonGroupCustomers.setVisibility(View.GONE);
////                            segmentedButtonGroupWorkers.setVisibility(View.VISIBLE);
////                            segmentedButtonGroupFreelancer.setVisibility(View.GONE);
//                            loadWorkingOrders();
//                            Log.e(TAG, "toggleBetweenLoginAndPreview: ");
////                            segmentedButtonGroupWorkers.setOnPositionChangedListener(new SegmentedButtonGroup.OnPositionChangedListener() {
////                                @Override
////                                public void onPositionChanged(int position) {
//                            switch (position) {
//                                case 0:
//                                    loadWorkingOrders();
//                                    Log.e(TAG, "onPositionChanged: ");
//                                    break;
//                                case 1:
//                                    loadFinishedOrders();
//                                    Log.e(TAG, "onPositionChanged:2 ");
//                                    break;
//                            }
//                                }
//                            });
//                            break;
                        case Constants.COMPANY:
                            Log.e(TAG, "toggleBetweenLoginAndPreview: Freelancer ");
//                            loadFreelancerPosts();


                            tablayoutWorkers.setVisibility(View.GONE);
                            tablayoutFree.setVisibility(View.VISIBLE);
                            tablayoutCustomers.setVisibility(View.GONE);


//                            segmentedButtonGroupCustomers.setVisibility(View.GONE);
//                            segmentedButtonGroupWorkers.setVisibility(View.GONE);
//                            segmentedButtonGroupFreelancer.setVisibility(View.VISIBLE);
//                            segmentedButtonGroupFreelancer.setOnPositionChangedListener(new SegmentedButtonGroup.OnPositionChangedListener() {
//                                @Override
//                                public void onPositionChanged(int position) {
                            switch (position) {
                                case 0:
                                    //loadFreelancerPosts();
                                    loadCompanyPosts();
                                    break;
                                case 1:
                                    Log.e(TAG, "onPositionChanged: " + "working Company");
//                                    loadFreelancerStillWorking();
                                    loadCompanyStillWorking2();
                                    break;
                                case 2:
                                    loadCompanyFinishedPosts();
                                    break;
//                                default:
//                                    loadFreelancerPosts();

                            }
//                                }
//                            });

                            break;

                        case Constants.FREELANCER:
                            Log.e(TAG, "toggleBetweenLoginAndPreview: Freelancer ");
//                            loadFreelancerPosts();


                            tablayoutWorkers.setVisibility(View.GONE);
                            tablayoutFree.setVisibility(View.VISIBLE);
                            tablayoutCustomers.setVisibility(View.GONE);


//                            segmentedButtonGroupCustomers.setVisibility(View.GONE);
//                            segmentedButtonGroupWorkers.setVisibility(View.GONE);
//                            segmentedButtonGroupFreelancer.setVisibility(View.VISIBLE);
//                            segmentedButtonGroupFreelancer.setOnPositionChangedListener(new SegmentedButtonGroup.OnPositionChangedListener() {
//                                @Override
//                                public void onPositionChanged(int position) {
                            switch (position) {
                                case 0:
                                    //loadFreelancerPosts();
                                    loadFreelancerPosts();
                                    break;
                                case 1:
                                    Log.e(TAG, "onPositionChanged: " + "working Freelancer");
//                                    loadFreelancerStillWorking();
                                    loadFreelancerStillWorking2();
                                    break;
                                case 2:
                                    loadFreelancerFinishedPosts();
                                    break;
//                                default:
//                                    loadFreelancerPosts();

                            }
//                                }
//                            });

                            break;


                    }
                } else {
                    Log.e(TAG, "toggleBetweenLoginAndPreview: yahoooooooooooo" + Prefs.contains(Constants.WORKER_LOGGED_AS));
                }
            }
        } else {
            Log.e(TAG, "toggleBetweenLoginAndPreview: YYY");
            llLogin.setVisibility(View.VISIBLE);
            llPagerHolder.setVisibility(View.GONE);
//            login();
            accessWorkerCustomerLoginIncludes();

        }

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

    private void loadFreelancerPosts() {
        listFreeLancerPost.clear();
        users.clear();
//        refreshAdapterFreelancers(new ArrayList<OrderRequest>(), null);
        refreshAdapterFreelancers(listFreeLancerPost, users);

        stopAllEventListeners();


        if (Prefs.contains(Constants.FIREBASE_UID)) {
            showLoadingDialog();
            String freelancerId = Prefs.getString(Constants.FIREBASE_UID, "");


            query = RefBase.refRequests()
                    .orderByChild(Constants.ORDER_STATE)
                    .equalTo(Constants.POST);
//                    .startAt("post")
//                    .endAt("post");
            Log.e(TAG, "loadFreelancerPosts: " + Constants.POST + "   " + freelancerId + "  " + Constants.ORDER_STATE);
            RefBase.refWorker(freelancerId)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            dataSnapshot.getRef().removeEventListener(this);
                            progress.setVisibility(View.GONE);
                            if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                                progress.setVisibility(View.GONE);
                                Log.e(TAG, "onDataChange: " + dataSnapshot.getChildrenCount());

                                CMWorker cmWorker = dataSnapshot.getValue(CMWorker.class);
                                Location freelancerLocation = cmWorker.getWorkerLocation();
                                String city = freelancerLocation.getCity();
                                String country = freelancerLocation.getCountryId();
                                String catId = cmWorker.getWorkerCategoryid();
                                Boolean freelancerStatusActivation = cmWorker.isWorkerStatusActivation();
                                Log.e(TAG, "onDataChange: post " + city + "  " + country + "  " + catId);
                                query.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        dataSnapshot.getRef().removeEventListener(this);
                                        Log.e(TAG, "onDataChange:11" + dataSnapshot.toString());
                                        if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                                            Log.e(TAG, "onDataChange: query " + dataSnapshot.getChildrenCount());
                                            listFreeLancerPost.clear();
                                            users.clear();
                                            refreshAdapterFreelancers(listFreeLancerPost, users);

                                            for (DataSnapshot dataSnapshot1 :
                                                    dataSnapshot.getChildren()) {
                                                Log.e(TAG, "onDataChange: " + dataSnapshot1.toString());
                                                OrderRequest orderRequest = dataSnapshot1.getValue(OrderRequest.class);
                                                Log.e(TAG, "onDataChange:1.1.1 " + orderRequest.getCategoryId());
                                                Log.e(TAG, "onDataChange:1.1.1 " + catId);
                                                //                                                Log.e(TAG, "onDataChange:1.2 " + freelancerLocation.getCity());
//                                                Log.e(TAG, "onDataChange:1.2 " + orderRequest.getLocation().getCity());
//                                                Log.e(TAG, "onDataChange:  city" + orderRequest.getLocation().getCity() );
//                                                Log.e(TAG, "onDataChange:  Country" + orderRequest.getLocation().getCountry() );
                                                if (TextUtils.equals(orderRequest.getCategoryId(), (catId)) &&
                                                        freelancerStatusActivation &&
//                                                        TextUtils.equals(orderRequest.getLocation().getCity(), city)&&
                                                        TextUtils.equals(orderRequest.getLocation().getCountryId(),
                                                                country)) {
//                                                    && orderRequest.getLocation() == freelancerLocation
                                                    Log.e(TAG, "onDataChange: Done El7 ");
                                                    Log.e(TAG, "onDataChange:1023 " + orderRequest.getCustomerId());

//                                                    refreshAdapterFreelancers(listFreeLancerPost, user);

                                                    RefBase.refUser(orderRequest.getCustomerId())
                                                            .addValueEventListener(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                    dataSnapshot.getRef().removeEventListener(this);
                                                                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                                                                        listFreeLancerPost.add(orderRequest);
                                                                        User user = dataSnapshot.getValue(User.class);
                                                                        users.add(user);
                                                                        Log.e(TAG, "onDataChange: 200 " + user.getUserName());
                                                                        Log.e(TAG, "onDataChange: 200 " + user.getUserPhoto());
                                                                        refreshAdapterFreelancers(listFreeLancerPost, users);
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                }
                                                            });
//                                                    listFreeLancerWorkers.add(cmWorker);
                                                }

                                            }

                                        }
                                        progress.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        progress.setVisibility(View.GONE);
                                    }
                                });
                                eventListenerHashMap.put(query, valueEventListener);
                            }
//                            dataSnapshot.getRef().removeEventListener(this);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            progress.setVisibility(View.GONE);

                        }
                    });


        }

//        if (spotsDialog.isShowing() && spotsDialog != null)
//                    progress.setVisibility(View.GONE);

    }

    //pass it ya zoz
    private void loadCompanyPosts() {
        listFreeLancerPost.clear();
        users.clear();
//        refreshAdapterFreelancers(new ArrayList<OrderRequest>(), null);
        refreshAdapterFreelancers(listFreeLancerPost, users);

        stopAllEventListeners();

        if (Prefs.contains(Constants.FIREBASE_UID)) {
            showLoadingDialog();
            String companyId = Prefs.getString(Constants.FIREBASE_UID, "");

            query = RefBase.refRequests()
                    .orderByChild(Constants.ORDER_STATE)
                    .equalTo(Constants.POST);
//                    .startAt("post")
//                    .endAt("post");
            Log.e(TAG, "loadFreelancerPosts: " + Constants.POST + "   " + companyId + "  " + Constants.ORDER_STATE);
            RefBase.refCompany(companyId)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            dataSnapshot.getRef().removeEventListener(this);
                            progress.setVisibility(View.GONE);
                            if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                                progress.setVisibility(View.GONE);
                                Log.e(TAG, "onDataChange: " + dataSnapshot.getChildrenCount());

//                                CMWorker cmWorker = dataSnapshot.getValue(CMWorker.class);
                                Company company = dataSnapshot.getValue(Company.class);
//                                Location freelancerLocation = cmWorker.getWorkerLocation();
                                LocationCompany companyLocation = company.getCompanyLocation();
                                String countryId = companyLocation.getCountryId();
                                boolean countryActiveOrNot2 = company.getCompanyStatusActivation();

                                Log.e(TAG, "onDataChange:255 " + countryActiveOrNot2);

                                List<String> catIds = company.getCompanyCategoryId();


                                RefBase.CountrySection.refLocGetCountry(countryId)
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                dataSnapshot.getRef().removeEventListener(this);
                                                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
//                                                      Log.e(TAG, "onDataChange__474" + dataSnapshot.getValue());
//                                                      Log.e(TAG, "onDataChange__474" + dataSnapshot.toString());
                                                    HashMap<String, Object> hm = (HashMap<String, Object>) dataSnapshot.getValue();
                                                    if (hm != null) {
//                                                        Log.e(TAG, "onDataChange__478" + hm.get(Constants.COUNTRY_STATUS_ACTIVATION));

//                                                        if ((countryIdForCompanyFilter = String.valueOf(hm.get(Constants.COUNTRY_ID))) != null) {
                                                        Log.e(TAG, "onDataChange__474" + countryId);
//                                                            if (hm.get(Constants.COUNTRY_STATUS_ACTIVATION) != null) {
////                                                                countryActiveOrNot = (boolean) hm.get(Constants.COUNTRY_STATUS_ACTIVATION);
////                                                                Log.e(TAG, "onDataChange__479" + countryActiveOrNot);
//                                                            }


                                                        //locationFetchedOrder.setCountry(countryName);

                                                        //                                String city = freelancerLocation.getCity();
//                                String country = freelancerLocation.getCountry();
//                                Log.e(TAG, "onDataChange: post " + city + "  " + country + "  " + catId);
                                                        query.addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                dataSnapshot.getRef().removeEventListener(this);
                                                                Log.e(TAG, "onDataChange:11" + dataSnapshot.toString());
                                                                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                                                                    Log.e(TAG, "onDataChange: query " + dataSnapshot.getChildrenCount());
                                                                    listFreeLancerPost.clear();
                                                                    users.clear();
                                                                    refreshAdapterFreelancers(listFreeLancerPost, users);

                                                                    for (DataSnapshot dataSnapshot1 :
                                                                            dataSnapshot.getChildren()) {
                                                                        Log.e(TAG, "onDataChange: " + dataSnapshot1.toString());
                                                                        OrderRequest orderRequest = dataSnapshot1.getValue(OrderRequest.class);
                                                                        Log.e(TAG, "onDataChange:1.1 " + orderRequest.getCategoryId());
                                                                        Log.e(TAG, "onDataChange:1.1 " + catIds);
                                                                        //                                                Log.e(TAG, "onDataChange:1.2 " + freelancerLocation.getCity());
                                                                        Log.e(TAG, "onDataChange:1.22 " + orderRequest.getCategoryId() + " and " + orderRequest.getLocation().getCountryId() + " and " + countryId + " and " + countryActiveOrNot2);
                                                                        Log.e(TAG, "onDataChange:  city" + orderRequest.getLocation().getCity());
                                                                        Log.e(TAG, "onDataChange:  Country" + orderRequest.getLocation().getCountry());

                                                                        assert catIds != null;
                                                                        for (int x = 0; x < catIds.size(); x++) {
                                                                            if (TextUtils.equals(orderRequest.getCategoryId(), (catIds.get(x))) &&
//                                                        TextUtils.equals(orderRequest.getLocation().getCity(), city)&&
                                                                                    TextUtils.equals(orderRequest.getLocation().getCountryId(),
                                                                                            countryId) && countryActiveOrNot2) {
//                                                    && orderRequest.getLocation() == freelancerLocation
                                                                                Log.e(TAG, "onDataChange: Done El7 ");
                                                                                Log.e(TAG, "onDataChange:1023 " + orderRequest.getCustomerId());
//                                                    refreshAdapterFreelancers(listFreeLancerPost, user);

                                                                                RefBase.refUser(orderRequest.getCustomerId())
                                                                                        .addValueEventListener(new ValueEventListener() {
                                                                                            @Override
                                                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                                dataSnapshot.getRef().removeEventListener(this);
                                                                                                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                                                                                                    listFreeLancerPost.add(orderRequest);
                                                                                                    User user = dataSnapshot.getValue(User.class);
                                                                                                    users.add(user);
                                                                                                    Log.e(TAG, "onDataChange: 200 " + user.getUserName());
                                                                                                    Log.e(TAG, "onDataChange: 200 " + user.getUserPhoto());
                                                                                                    refreshAdapterFreelancers(listFreeLancerPost, users);
                                                                                                }
                                                                                            }

                                                                                            @Override
                                                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                                            }
                                                                                        });
//                                                                                          listFreeLancerWorkers.add(cmWorker);
                                                                            }
                                                                        }

                                                                    }
                                                                }
                                                                progress.setVisibility(View.GONE);
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                progress.setVisibility(View.GONE);
                                                            }
                                                        });

//                                                        }
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });


                                eventListenerHashMap.put(query, valueEventListener);
                            }
//                            dataSnapshot.getRef().removeEventListener(this);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            progress.setVisibility(View.GONE);

                        }
                    });


        }

//        if (spotsDialog.isShowing() && spotsDialog != null)
//                    progress.setVisibility(View.GONE);

    }

    private void loadFreelancerStillWorking2() {

        listFreeLancerPost.clear();
        users.clear();
        refreshAdapterFreelancers(new ArrayList<OrderRequest>(), users);

        stopAllEventListeners();
        if (Prefs.contains(Constants.FIREBASE_UID)) {
            showLoadingDialog();
            String freelancerId = Prefs.getString(Constants.FIREBASE_UID, "");
            query = RefBase.refFreelancersConnection()
                    .orderByChild(Constants.FREE_LANCER_ID)
                    .equalTo(freelancerId);

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    dataSnapshot.getRef().removeEventListener(this);
                    Log.e(TAG, "onDataChange: workingFreeF" + dataSnapshot.toString());
                    progress.setVisibility(View.GONE);
                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                        Log.e(TAG, "onDataChange: query working F");
                        listFreeLancerCompanyWorking.clear();
                        users.clear();
                        refreshAdapterFreelancers(listFreeLancerCompanyWorking, users);
                        for (DataSnapshot dataSnapshot1 :
                                dataSnapshot.getChildren()) {
                            Log.e(TAG, "4444444444F: " + dataSnapshot1.toString());

                            ConnectionFree connectionFree = dataSnapshot1.getValue(ConnectionFree.class);

                            Log.e(TAG, "6666666666666666F: " + connectionFree.getState());

                            if (TextUtils.equals(connectionFree.getState(), Constants.FREELANCE_WORKING)) {
                                Log.e(TAG, "tttttttttttttttyyyyyyF: ");
                                RefBase
                                        .refRequests()
                                        .orderByChild(Constants.ORDER_ID)
                                        .equalTo(connectionFree.getRequestId())
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
                                                                    listFreeLancerCompanyWorking.add(orderRequest);
                                                                    User user = dataSnapshot.getValue(User.class);
                                                                    users.add(user);
                                                                    Log.e(TAG, "onDataChange: 200 F" + user.getUserName());
                                                                    Log.e(TAG, "onDataChange: 200 F" + user.getUserPhoto());
                                                                    refreshAdapterFreelancers(listFreeLancerCompanyWorking, users);
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
                            }


                        }
                    }
                    progress.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    progress.setVisibility(View.GONE);
                }
            });
            eventListenerHashMap.put(query, valueEventListener);
        }

    }

    //load still working posts for company
    private void loadCompanyStillWorking2() {

        listFreeLancerPost.clear();
        users.clear();
        refreshAdapterFreelancers(new ArrayList<OrderRequest>(), users);

        stopAllEventListeners();
        if (Prefs.contains(Constants.FIREBASE_UID)) {
            showLoadingDialog();
            String companyId = Prefs.getString(Constants.FIREBASE_UID, "");
            query = RefBase.refFreelancersConnection()
                    .orderByChild(Constants.FREE_LANCER_ID)
                    .equalTo(companyId);

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    dataSnapshot.getRef().removeEventListener(this);
                    Log.e(TAG, "onDataChange: workingCompany" + dataSnapshot.toString());
                    progress.setVisibility(View.GONE);
                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                        Log.e(TAG, "onDataChange: query working Company");
                        listFreeLancerCompanyWorking.clear();
                        users.clear();
                        refreshAdapterFreelancers(listFreeLancerCompanyWorking, users);
                        for (DataSnapshot dataSnapshot1 :
                                dataSnapshot.getChildren()) {
                            Log.e(TAG, "4444444444F: Company " + dataSnapshot1.toString());

                            ConnectionFree connectionFree = dataSnapshot1.getValue(ConnectionFree.class);

                            Log.e(TAG, "6666666666666666F: Company " + connectionFree.getState());

                            if (TextUtils.equals(connectionFree.getState(), Constants.FREELANCE_WORKING)) {
                                Log.e(TAG, "tttttttttttttttyyyyyyF: Company ");
                                RefBase.refRequests()
                                        .orderByChild(Constants.ORDER_ID)
                                        .equalTo(connectionFree.getRequestId())
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
                                                                    listFreeLancerCompanyWorking.add(orderRequest);
                                                                    User user = dataSnapshot.getValue(User.class);
                                                                    users.add(user);
                                                                    Log.e(TAG, "onDataChange: 200 Company" + user.getUserName());
                                                                    Log.e(TAG, "onDataChange: 200 Company" + user.getUserPhoto());
                                                                    refreshAdapterFreelancers(listFreeLancerCompanyWorking, users);
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
                            }


                        }
                    }
                    progress.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    progress.setVisibility(View.GONE);
                }
            });
            eventListenerHashMap.put(query, valueEventListener);
        }

    }

//    private void loadFreelancerStillWorking() {
//
////        listFreeLancerFinishing.clear();
////        listFreeLancerCompanyWorking.clear();
////        listFreeLancerPost.clear();
//        refreshAdapterFreelancers(new ArrayList<OrderRequest>(), null);
//
//        stopAllEventListeners();
//        if (Prefs.contains(Constants.FIREBASE_UID)) {
//            showLoadingDialog();
//            String freelancerId = Prefs.getString(Constants.FIREBASE_UID, "");
//            query = RefBase.refRequests()
//                    .orderByChild(Constants.ORDER_STATE)
//                    .equalTo(Constants.FREELANCE_WORKING);
////                    .startAt("post")
////                    .endAt("post");
////            Log.e(TAG, "loadFreelancerPosts: " + Constants.FREELANCE_WORKING + "   " + freelancerId + "  " + Constants.ORDER_STATE);
//            RefBase.refWorker(freelancerId)
//                    .addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            if (dataSnapshot.exists()) {
//                                progress.setVisibility(View.GONE);
//
//                                CMWorker cmWorker = dataSnapshot.getValue(CMWorker.class);
//                                Location freelancerLocation = cmWorker.getWorkerLocation();
//                                String city = freelancerLocation.getCity();
//                                String country = freelancerLocation.getCountry();
//                                String catId = cmWorker.getWorkerCategoryid();
//                                Log.e(TAG, "onDataChange: working " + city + "  " + country + "  " + catId);
//                                query.addValueEventListener(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                        Log.e(TAG, "onDataChange: working" + dataSnapshot.toString());
//                                        if (dataSnapshot.exists()) {
//                                            Log.e(TAG, "onDataChange: query working ");
//                                            listFreeLancerCompanyWorking.clear();
//                                            for (DataSnapshot dataSnapshot1 :
//                                                    dataSnapshot.getChildren()) {
//                                                Log.e(TAG, "onDataChange: " + dataSnapshot1.toString());
//                                                OrderRequest orderRequest = dataSnapshot1.getValue(OrderRequest.class);
//                                                Log.e(TAG, "onDataChange:1.1 working" + orderRequest.getCategoryId());
//                                                Log.e(TAG, "onDataChange:1.1 " + catId);
//                                                Log.e(TAG, "onDataChange:1.2 working " + freelancerLocation.getCity());
//                                                Log.e(TAG, "onDataChange:1.2 " + orderRequest.getLocation().getCity());
//                                                if (orderRequest.getCategoryId().equals(catId) && TextUtils.equals(orderRequest.getLocation().getCity(), city) && TextUtils.equals(orderRequest.getLocation().getCountry(), country)) {
////                                                    && orderRequest.getLocation() == freelancerLocation
////                                                    && orderRequest.getLocation().getCity().equals(freelancerLocation.getCity())
//                                                    Log.e(TAG, "onDataChange: Done 2 working ");
////                                                    Log.e(TAG, "onDataChange:102 " +orderRequest.getCustomerId() );
//                                                    listFreeLancerCompanyWorking.add(orderRequest);
//                                                    RefBase.refUser(orderRequest.getCustomerId()).addValueEventListener(new ValueEventListener() {
//                                                        @Override
//                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                                            if (dataSnapshot.exists()) {
//                                                                User user = dataSnapshot.getValue(User.class);
//                                                                Log.e(TAG, "onDataChange: 201 working " + user.getUserName());
//                                                                refreshAdapterFreelancers(listFreeLancerCompanyWorking, user);
//                                                            }
//                                                        }
//
//                                                        @Override
//                                                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                                        }
//                                                    });
////                                                    listFreeLancerWorkers.add(cmWorker);
//                                                }
//
//                                            }
//                                        }
//                                        progress.setVisibility(View.GONE);
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                                        progress.setVisibility(View.GONE);
//                                    }
//                                });
//                                eventListenerHashMap.put(query, valueEventListener);
//                            }
//
//                            dataSnapshot.getRef().removeEventListener(this);
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//                            progress.setVisibility(View.GONE);
//
//                        }
//                    });
//        }
//    }

//    private void loadFreelancerFinishedPosts() {
//        refreshAdapterFreelancers(new ArrayList<OrderRequest>(), null);//clear the recyclerview
//
//
////        listFreeLancerFinishing.clear();
////        listFreeLancerCompanyWorking.clear();
////        listFreeLancerPost.clear();
//
//        stopAllEventListeners();
//        if (Prefs.contains(Constants.FIREBASE_UID)) {
//            showLoadingDialog();
//            String freelancerId = Prefs.getString(Constants.FIREBASE_UID, "");
//            query = RefBase.refRequests()
//                    .orderByChild(Constants.ORDER_STATE)
//                    .equalTo(Constants.FREELANCE_FINISHED);
////                    .startAt("post")
////                    .endAt("post");
//            Log.e(TAG, "loadFreelancerPosts: " + Constants.FREELANCE_WORKING + "   " + freelancerId + "  " + Constants.ORDER_STATE);
//            Log.e(TAG, "loadFreelancerFinishedPosts: " + freelancerId);
//            RefBase.refWorker(freelancerId)
//                    .addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            if (dataSnapshot.exists()) {
//                                CMWorker cmWorker = dataSnapshot.getValue(CMWorker.class);
//                                Location freelancerLocation = cmWorker.getWorkerLocation();
//                                String city = freelancerLocation.getCity();
//                                String country = freelancerLocation.getCountry();
//                                String catId = cmWorker.getWorkerCategoryid();
//                                Log.e(TAG, "onDataChange: post " + city + "  " + country + "  " + catId);
//                                query.addValueEventListener(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                        Log.e(TAG, "onDataChange:11" + dataSnapshot.toString());
//                                        if (dataSnapshot.exists()) {
//                                            Log.e(TAG, "onDataChange: query ");
//                                            listFreeLancerFinishing.clear();
//                                            refreshAdapterFreelancers(listFreeLancerFinishing, user);
//                                            progress.setVisibility(View.GONE);
//                                            for (DataSnapshot dataSnapshot1 :
//                                                    dataSnapshot.getChildren()) {
//                                                Log.e(TAG, "onDataChange: " + dataSnapshot1.toString());
//                                                OrderRequest orderRequest = dataSnapshot1.getValue(OrderRequest.class);
//                                                Log.e(TAG, "onDataChange:1.1 " + orderRequest.getCategoryId());
//                                                Log.e(TAG, "onDataChange:1.1 " + catId);
//                                                Log.e(TAG, "onDataChange:1.2 " + orderRequest.getLocation().getCity());
//                                                Log.e(TAG, "onDataChange:1.2 " + orderRequest.getLocation().getCountry());
//
////                                                Log.e(TAG, "onDataChange:1.2 " + freelancerLocation.getCity());
////                                                Log.e(TAG, "onDataChange:1.2 " + orderRequest.getLocation().getCity());
//                                                if (orderRequest.getCategoryId().equals(catId) && TextUtils.equals(orderRequest.getLocation().getCity(), city) &&
//                                                        TextUtils.equals(orderRequest.getLocation().getCountry(), country)) {
////                                                    && orderRequest.getLocation() == freelancerLocation
//                                                    Log.e(TAG, "onDataChange: Done 2 ");
////                                                    Log.e(TAG, "onDataChange:102 " +orderRequest.getCustomerId() );
//
//                                                    listFreeLancerFinishing.add(orderRequest);
//                                                    RefBase.refUser(orderRequest.getCustomerId()).addValueEventListener(new ValueEventListener() {
//                                                        @Override
//                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                                            if (dataSnapshot.exists()) {
//                                                                user = dataSnapshot.getValue(User.class);
//                                                                Log.e(TAG, "onDataChange: 201 " + user.getUserName());
//                                                                refreshAdapterFreelancers(listFreeLancerFinishing, user);
//                                                            }
//                                                        }
//
//                                                        @Override
//                                                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                                        }
//                                                    });
////                                                    listFreeLancerWorkers.add(cmWorker);
//                                                }
//
//                                            }
//
//                                        }
//                                        progress.setVisibility(View.GONE);
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                                        progress.setVisibility(View.GONE);
//                                    }
//                                });
//                                eventListenerHashMap.put(query, valueEventListener);
//                            }
//                            dataSnapshot.getRef().removeEventListener(this);
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//                            progress.setVisibility(View.GONE);
//
//                        }
//                    });
//        }
//    }

    private void loadFreelancerFinishedPosts() {

        listFreeLancerPost.clear();
        users.clear();
        refreshAdapterFreelancers(new ArrayList<OrderRequest>(), null);//clear the recyclerview


        stopAllEventListeners();


        if (Prefs.contains(Constants.FIREBASE_UID)) {
            showLoadingDialog();
            String freelancerId = Prefs.getString(Constants.FIREBASE_UID, "");
            query = RefBase.refFreelancersConnection()
                    .orderByChild(Constants.FREE_LANCER_ID)
                    .equalTo(freelancerId);
//                    .startAt("post")
//                    .endAt("post");

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    dataSnapshot.getRef().removeEventListener(this);
                    progress.setVisibility(View.GONE);
                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                        Log.e(TAG, "onDataChange: Finished pro1 ");
                        listFreeLancerFinishing.clear();
                        users.clear();
                        refreshAdapterFreelancers(listFreeLancerFinishing, users);
                        progress.setVisibility(View.GONE);

                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            Log.e(TAG, "onDataChange: Finished pro2 ");
                            ConnectionFree connectionFree = dataSnapshot1.getValue(ConnectionFree.class);
                            if (connectionFree != null && TextUtils.equals(connectionFree.getState(), Constants.FREELANCE_FINISHED)) {
                                Log.e(TAG, "onDataChange: Finished pro3 ");
                                RefBase.refRequests(connectionFree.getRequestId()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        dataSnapshot.getRef().removeEventListener(this);
                                        if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                                            Log.e(TAG, "onDataChange: Finished pro4 ");
                                            Log.e(TAG, "onDataChange: 22.2 " + connectionFree.getCustomerId());
//                                            OrderRequest orderRequest = dataSnapshot.getChildren().iterator().next().getValue(OrderRequest.class);
                                            OrderRequest orderRequest = dataSnapshot.getValue(OrderRequest.class);
                                            RefBase.refUser(connectionFree.getCustomerId()).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    dataSnapshot.getRef().removeEventListener(this);
                                                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
//                                                        Log.e(TAG, "onDataChange: 203 " + user.getUserName());
                                                        User user = dataSnapshot.getValue(User.class);
                                                        users.add(user);
                                                        listFreeLancerFinishing.add(orderRequest);

                                                        Log.e(TAG, "onDataChange: 201 " + user.getUserName());
                                                        Log.e(TAG, "onDataChange: " + user.getUserName());
                                                        Log.e(TAG, "onDataChange: " + listFreeLancerFinishing.size());
                                                        Log.e(TAG, "onDataChange: 22.1 " + orderRequest.getCustomerId());
                                                        refreshAdapterFreelancers(listFreeLancerFinishing, users);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

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
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


//            Log.e(TAG, "loadFreelancerPosts: " + freelancerId + "  " + Constants.ORDER_STATE);
//            Log.e(TAG, "loadFreelancerFinishedPosts: " + freelancerId);
//            RefBase.refWorker(freelancerId)
//                    .addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            if (dataSnapshot.exists()) {
//                                CMWorker cmWorker = dataSnapshot.getValue(CMWorker.class);
//                                Location freelancerLocation = cmWorker.getWorkerLocation();
//                                String city = freelancerLocation.getCity();
//                                String country = freelancerLocation.getCountry();
//                                String catId = cmWorker.getWorkerCategoryid();
//                                Log.e(TAG, "onDataChange: post " + city + "  " + country + "  " + catId);
//                                query.addValueEventListener(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                        Log.e(TAG, "onDataChange:11" + dataSnapshot.toString());
//                                        if (dataSnapshot.exists()) {
//                                            Log.e(TAG, "onDataChange: query ");
//                                            listFreeLancerFinishing.clear();
//                                            progress.setVisibility(View.GONE);
//                                            for (DataSnapshot dataSnapshot1 :
//                                                    dataSnapshot.getChildren()) {
//                                                Log.e(TAG, "onDataChange: " + dataSnapshot1.toString());
//                                                OrderRequest orderRequest = dataSnapshot1.getValue(OrderRequest.class);
//                                                Log.e(TAG, "onDataChange:1.1 " + orderRequest.getCategoryId());
//                                                Log.e(TAG, "onDataChange:1.1 " + catId);
//                                                Log.e(TAG, "onDataChange:1.2 " + orderRequest.getLocation().getCity());
//                                                Log.e(TAG, "onDataChange:1.2 " + orderRequest.getLocation().getCountry());
//
////                                                Log.e(TAG, "onDataChange:1.2 " + freelancerLocation.getCity());
////                                                Log.e(TAG, "onDataChange:1.2 " + orderRequest.getLocation().getCity());
//                                                if (orderRequest.getCategoryId().equals(catId) && TextUtils.equals(orderRequest.getLocation().getCity(), city) &&
//                                                        TextUtils.equals(orderRequest.getLocation().getCountry(), country)) {
////                                                    && orderRequest.getLocation() == freelancerLocation
//                                                    Log.e(TAG, "onDataChange: Done 2 ");
////                                                    Log.e(TAG, "onDataChange:102 " +orderRequest.getCustomerId() );
//
//                                                    listFreeLancerFinishing.add(orderRequest);
//                                                    RefBase.refUser(orderRequest.getCustomerId()).addValueEventListener(new ValueEventListener() {
//                                                        @Override
//                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                                            if (dataSnapshot.exists()) {
//                                                                user = dataSnapshot.getValue(User.class);
//                                                                Log.e(TAG, "onDataChange: 201 " + user.getUserName());
//                                                                refreshAdapterFreelancers(listFreeLancerFinishing, user);
//                                                            }
//                                                        }
//
//                                                        @Override
//                                                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                                        }
//                                                    });
////                                                    listFreeLancerWorkers.add(cmWorker);
//                                                }
//
//                                            }
//
//                                        }
//                                        progress.setVisibility(View.GONE);
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                                        progress.setVisibility(View.GONE);
//                                    }
//                                });
//                                eventListenerHashMap.put(query, valueEventListener);
//                            }
//                            dataSnapshot.getRef().removeEventListener(this);
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//                            progress.setVisibility(View.GONE);
//
//                        }
//                    });
        }


//        if (Prefs.contains(Constants.FIREBASE_UID)) {
//            showLoadingDialog();
//            String freelancerId = Prefs.getString(Constants.FIREBASE_UID, "");
//            query = RefBase.refRequests()
//                    .orderByChild(Constants.ORDER_STATE)
//                    .equalTo(Constants.FREELANCE_FINISHED);
////                    .startAt("post")
////                    .endAt("post");
//            Log.e(TAG, "loadFreelancerPosts: " + Constants.FREELANCE_WORKING + "   " + freelancerId + "  " + Constants.ORDER_STATE);
//            Log.e(TAG, "loadFreelancerFinishedPosts: " + freelancerId);
//            RefBase.refWorker(freelancerId)
//                    .addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            if (dataSnapshot.exists()) {
//                                CMWorker cmWorker = dataSnapshot.getValue(CMWorker.class);
//                                Location freelancerLocation = cmWorker.getWorkerLocation();
//                                String city = freelancerLocation.getCity();
//                                String country = freelancerLocation.getCountry();
//                                String catId = cmWorker.getWorkerCategoryid();
//                                Log.e(TAG, "onDataChange: post " + city + "  " + country + "  " + catId);
//                                query.addValueEventListener(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                        Log.e(TAG, "onDataChange:11" + dataSnapshot.toString());
//                                        if (dataSnapshot.exists()) {
//                                            Log.e(TAG, "onDataChange: query ");
//                                            listFreeLancerFinishing.clear();
//                                            progress.setVisibility(View.GONE);
//                                            for (DataSnapshot dataSnapshot1 :
//                                                    dataSnapshot.getChildren()) {
//                                                Log.e(TAG, "onDataChange: " + dataSnapshot1.toString());
//                                                OrderRequest orderRequest = dataSnapshot1.getValue(OrderRequest.class);
//                                                Log.e(TAG, "onDataChange:1.1 " + orderRequest.getCategoryId());
//                                                Log.e(TAG, "onDataChange:1.1 " + catId);
//                                                Log.e(TAG, "onDataChange:1.2 " + orderRequest.getLocation().getCity());
//                                                Log.e(TAG, "onDataChange:1.2 " + orderRequest.getLocation().getCountry());
//
////                                                Log.e(TAG, "onDataChange:1.2 " + freelancerLocation.getCity());
////                                                Log.e(TAG, "onDataChange:1.2 " + orderRequest.getLocation().getCity());
//                                                if (orderRequest.getCategoryId().equals(catId) && TextUtils.equals(orderRequest.getLocation().getCity(), city) &&
//                                                        TextUtils.equals(orderRequest.getLocation().getCountry(), country)) {
////                                                    && orderRequest.getLocation() == freelancerLocation
//                                                    Log.e(TAG, "onDataChange: Done 2 ");
////                                                    Log.e(TAG, "onDataChange:102 " +orderRequest.getCustomerId() );
//
//                                                    listFreeLancerFinishing.add(orderRequest);
//                                                    RefBase.refUser(orderRequest.getCustomerId()).addValueEventListener(new ValueEventListener() {
//                                                        @Override
//                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                                            if (dataSnapshot.exists()) {
//                                                                user = dataSnapshot.getValue(User.class);
//                                                                Log.e(TAG, "onDataChange: 201 " + user.getUserName());
//                                                                refreshAdapterFreelancers(listFreeLancerFinishing, user);
//                                                            }
//                                                        }
//
//                                                        @Override
//                                                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                                        }
//                                                    });
////                                                    listFreeLancerWorkers.add(cmWorker);
//                                                }
//
//                                            }
//
//                                        }
//                                        progress.setVisibility(View.GONE);
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                                        progress.setVisibility(View.GONE);
//                                    }
//                                });
//                                eventListenerHashMap.put(query, valueEventListener);
//                            }
//                            dataSnapshot.getRef().removeEventListener(this);
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//                            progress.setVisibility(View.GONE);
//
//                        }
//                    });
//        }
    }

    private void loadCompanyFinishedPosts() {

        listFreeLancerPost.clear();
        users.clear();
        refreshAdapterFreelancers(new ArrayList<OrderRequest>(), null);//clear the recyclerview


        stopAllEventListeners();


        if (Prefs.contains(Constants.FIREBASE_UID)) {
            showLoadingDialog();
            String companyId = Prefs.getString(Constants.FIREBASE_UID, "");
            query = RefBase.refFreelancersConnection()
                    .orderByChild(Constants.FREE_LANCER_ID)
                    .equalTo(companyId);
//                    .startAt("post")
//                    .endAt("post");

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    dataSnapshot.getRef().removeEventListener(this);
                    progress.setVisibility(View.GONE);
                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                        Log.e(TAG, "onDataChange: Finished pro1 ");
                        listFreeLancerFinishing.clear();
                        users.clear();
                        refreshAdapterFreelancers(listFreeLancerFinishing, users);
                        progress.setVisibility(View.GONE);

                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            Log.e(TAG, "onDataChange: Finished pro2 ");
                            ConnectionFree connectionFree = dataSnapshot1.getValue(ConnectionFree.class);
                            if (connectionFree != null && TextUtils.equals(connectionFree.getState(), Constants.FREELANCE_FINISHED)) {
                                Log.e(TAG, "onDataChange: Finished pro3 ");
                                RefBase.refRequests(connectionFree.getRequestId()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        dataSnapshot.getRef().removeEventListener(this);
                                        if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                                            Log.e(TAG, "onDataChange: Finished pro4 ");
                                            Log.e(TAG, "onDataChange: 22.2 " + connectionFree.getCustomerId());
//                                            OrderRequest orderRequest = dataSnapshot.getChildren().iterator().next().getValue(OrderRequest.class);
                                            OrderRequest orderRequest = dataSnapshot.getValue(OrderRequest.class);
                                            RefBase.refUser(connectionFree.getCustomerId()).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    dataSnapshot.getRef().removeEventListener(this);
                                                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
//                                                        Log.e(TAG, "onDataChange: 203 " + user.getUserName());
                                                        User user = dataSnapshot.getValue(User.class);
                                                        users.add(user);
                                                        listFreeLancerFinishing.add(orderRequest);

                                                        Log.e(TAG, "onDataChange: 201 " + user.getUserName());
                                                        Log.e(TAG, "onDataChange: " + user.getUserName());
                                                        Log.e(TAG, "onDataChange: " + listFreeLancerFinishing.size());
                                                        Log.e(TAG, "onDataChange: 22.1 " + orderRequest.getCustomerId());
                                                        refreshAdapterFreelancers(listFreeLancerFinishing, users);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

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
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


//            Log.e(TAG, "loadFreelancerPosts: " + freelancerId + "  " + Constants.ORDER_STATE);
//            Log.e(TAG, "loadFreelancerFinishedPosts: " + freelancerId);
//            RefBase.refWorker(freelancerId)
//                    .addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            if (dataSnapshot.exists()) {
//                                CMWorker cmWorker = dataSnapshot.getValue(CMWorker.class);
//                                Location freelancerLocation = cmWorker.getWorkerLocation();
//                                String city = freelancerLocation.getCity();
//                                String country = freelancerLocation.getCountry();
//                                String catId = cmWorker.getWorkerCategoryid();
//                                Log.e(TAG, "onDataChange: post " + city + "  " + country + "  " + catId);
//                                query.addValueEventListener(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                        Log.e(TAG, "onDataChange:11" + dataSnapshot.toString());
//                                        if (dataSnapshot.exists()) {
//                                            Log.e(TAG, "onDataChange: query ");
//                                            listFreeLancerFinishing.clear();
//                                            progress.setVisibility(View.GONE);
//                                            for (DataSnapshot dataSnapshot1 :
//                                                    dataSnapshot.getChildren()) {
//                                                Log.e(TAG, "onDataChange: " + dataSnapshot1.toString());
//                                                OrderRequest orderRequest = dataSnapshot1.getValue(OrderRequest.class);
//                                                Log.e(TAG, "onDataChange:1.1 " + orderRequest.getCategoryId());
//                                                Log.e(TAG, "onDataChange:1.1 " + catId);
//                                                Log.e(TAG, "onDataChange:1.2 " + orderRequest.getLocation().getCity());
//                                                Log.e(TAG, "onDataChange:1.2 " + orderRequest.getLocation().getCountry());
//
////                                                Log.e(TAG, "onDataChange:1.2 " + freelancerLocation.getCity());
////                                                Log.e(TAG, "onDataChange:1.2 " + orderRequest.getLocation().getCity());
//                                                if (orderRequest.getCategoryId().equals(catId) && TextUtils.equals(orderRequest.getLocation().getCity(), city) &&
//                                                        TextUtils.equals(orderRequest.getLocation().getCountry(), country)) {
////                                                    && orderRequest.getLocation() == freelancerLocation
//                                                    Log.e(TAG, "onDataChange: Done 2 ");
////                                                    Log.e(TAG, "onDataChange:102 " +orderRequest.getCustomerId() );
//
//                                                    listFreeLancerFinishing.add(orderRequest);
//                                                    RefBase.refUser(orderRequest.getCustomerId()).addValueEventListener(new ValueEventListener() {
//                                                        @Override
//                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                                            if (dataSnapshot.exists()) {
//                                                                user = dataSnapshot.getValue(User.class);
//                                                                Log.e(TAG, "onDataChange: 201 " + user.getUserName());
//                                                                refreshAdapterFreelancers(listFreeLancerFinishing, user);
//                                                            }
//                                                        }
//
//                                                        @Override
//                                                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                                        }
//                                                    });
////                                                    listFreeLancerWorkers.add(cmWorker);
//                                                }
//
//                                            }
//
//                                        }
//                                        progress.setVisibility(View.GONE);
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                                        progress.setVisibility(View.GONE);
//                                    }
//                                });
//                                eventListenerHashMap.put(query, valueEventListener);
//                            }
//                            dataSnapshot.getRef().removeEventListener(this);
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//                            progress.setVisibility(View.GONE);
//
//                        }
//                    });
        }


//        if (Prefs.contains(Constants.FIREBASE_UID)) {
//            showLoadingDialog();
//            String freelancerId = Prefs.getString(Constants.FIREBASE_UID, "");
//            query = RefBase.refRequests()
//                    .orderByChild(Constants.ORDER_STATE)
//                    .equalTo(Constants.FREELANCE_FINISHED);
////                    .startAt("post")
////                    .endAt("post");
//            Log.e(TAG, "loadFreelancerPosts: " + Constants.FREELANCE_WORKING + "   " + freelancerId + "  " + Constants.ORDER_STATE);
//            Log.e(TAG, "loadFreelancerFinishedPosts: " + freelancerId);
//            RefBase.refWorker(freelancerId)
//                    .addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            if (dataSnapshot.exists()) {
//                                CMWorker cmWorker = dataSnapshot.getValue(CMWorker.class);
//                                Location freelancerLocation = cmWorker.getWorkerLocation();
//                                String city = freelancerLocation.getCity();
//                                String country = freelancerLocation.getCountry();
//                                String catId = cmWorker.getWorkerCategoryid();
//                                Log.e(TAG, "onDataChange: post " + city + "  " + country + "  " + catId);
//                                query.addValueEventListener(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                        Log.e(TAG, "onDataChange:11" + dataSnapshot.toString());
//                                        if (dataSnapshot.exists()) {
//                                            Log.e(TAG, "onDataChange: query ");
//                                            listFreeLancerFinishing.clear();
//                                            progress.setVisibility(View.GONE);
//                                            for (DataSnapshot dataSnapshot1 :
//                                                    dataSnapshot.getChildren()) {
//                                                Log.e(TAG, "onDataChange: " + dataSnapshot1.toString());
//                                                OrderRequest orderRequest = dataSnapshot1.getValue(OrderRequest.class);
//                                                Log.e(TAG, "onDataChange:1.1 " + orderRequest.getCategoryId());
//                                                Log.e(TAG, "onDataChange:1.1 " + catId);
//                                                Log.e(TAG, "onDataChange:1.2 " + orderRequest.getLocation().getCity());
//                                                Log.e(TAG, "onDataChange:1.2 " + orderRequest.getLocation().getCountry());
//
////                                                Log.e(TAG, "onDataChange:1.2 " + freelancerLocation.getCity());
////                                                Log.e(TAG, "onDataChange:1.2 " + orderRequest.getLocation().getCity());
//                                                if (orderRequest.getCategoryId().equals(catId) && TextUtils.equals(orderRequest.getLocation().getCity(), city) &&
//                                                        TextUtils.equals(orderRequest.getLocation().getCountry(), country)) {
////                                                    && orderRequest.getLocation() == freelancerLocation
//                                                    Log.e(TAG, "onDataChange: Done 2 ");
////                                                    Log.e(TAG, "onDataChange:102 " +orderRequest.getCustomerId() );
//
//                                                    listFreeLancerFinishing.add(orderRequest);
//                                                    RefBase.refUser(orderRequest.getCustomerId()).addValueEventListener(new ValueEventListener() {
//                                                        @Override
//                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                                            if (dataSnapshot.exists()) {
//                                                                user = dataSnapshot.getValue(User.class);
//                                                                Log.e(TAG, "onDataChange: 201 " + user.getUserName());
//                                                                refreshAdapterFreelancers(listFreeLancerFinishing, user);
//                                                            }
//                                                        }
//
//                                                        @Override
//                                                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                                        }
//                                                    });
////                                                    listFreeLancerWorkers.add(cmWorker);
//                                                }
//
//                                            }
//
//                                        }
//                                        progress.setVisibility(View.GONE);
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                                        progress.setVisibility(View.GONE);
//                                    }
//                                });
//                                eventListenerHashMap.put(query, valueEventListener);
//                            }
//                            dataSnapshot.getRef().removeEventListener(this);
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//                            progress.setVisibility(View.GONE);
//
//                        }
//                    });
//        }
    }

    //when u move from this fragment to another
    @Override
    public void onDetach() {
        super.onDetach();
        Log.e(TAG, "onDetach: ");
        stopAllEventListeners();
    }

    private void stopAllEventListeners() {
        if (!eventListenerHashMap.keySet().isEmpty()) {
            for (Query q : eventListenerHashMap.keySet()) {
                if (eventListenerHashMap.get(q) != null) {
                    q.removeEventListener(Objects.requireNonNull(eventListenerHashMap.get(q)));
                }
            }
            eventListenerHashMap.clear();
        }
    }

//    private void loadWorkingOrders() {
//        stopAllEventListeners();
//        if (Prefs.contains(Constants.FIREBASE_UID)) {
//            showLoadingDialog();
//            String workerId = Prefs.getString(Constants.FIREBASE_UID, "");
//            query = RefBase.refCmtTasks()
//                    .orderByChild(Constants.TYPE)
//                    .equalTo(Constants.CM_WORKING);
//
//            valueEventListener = query.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                    Log.e(TAG, "onDataChange: " + cmTask.getType());
//                    //clear last data
////                    Log.e(TAG, "onDataChange: 1" );
//                    listCmWorkingType.clear();
//                    //Log.e(TAG, "onDataChange: " + dataSnapshot.getChildrenCount());
//                    //Log.e(TAG, "onDataChange: " + dataSnapshot.getKey());//U916RqensGenvU2zcnDBl27GAM
//                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
//
//                        CmTask cmTask = dataSnapshot1.getValue(CmTask.class);
//
//                        if (TextUtils.equals(cmTask.getCustomerId(), workerId)) {
//                            listCmWorkingType.add(cmTask);
//                        }
//                    }
//                    //                            Log.e(TAG, "onDataChange: " + list.size());
////                    refreshAdapter(Utils.merge(listCmWorkingType));
//                    refreshAdapterWorker(Utils.merge(listCmWorkingType));
//                           progress.setVisibility(View.GONE);                    
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//                           progress.setVisibility(View.GONE);                    
//                }
//            });
//            eventListenerHashMap.put(query, valueEventListener);
//        }
//    }
//
//    private void loadFinishedOrders() {
//        stopAllEventListeners();
//        if (Prefs.contains(Constants.FIREBASE_UID)) {
//            showLoadingDialog();
//            String workerId = Prefs.getString(Constants.FIREBASE_UID, "");
//            Log.e(TAG, "loadFinishedOrders2: " + workerId);
//            Log.e(TAG, "loadFinishedOrders3: " + Constants.TYPE);
//            Log.e(TAG, "loadFinishedOrders4: " + Constants.CM_FINISHED);
//            query = RefBase.refCmtTasks(workerId)
//                    .orderByChild(Constants.TYPE)
//                    .equalTo(Constants.CM_FINISHED);
//            valueEventListener = query.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    //clear last data
////                    Log.e(TAG, "onDataChange: 1" );
//                    listCmFinishedType.clear();
//                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
//                        Log.e(TAG, "onDataChange: test2 " + dataSnapshot1.getChildrenCount());
//                        Log.e(TAG, "onDataChange:2 " + dataSnapshot1.getChildren());
//                        CmTask cmTask = dataSnapshot1.getValue(CmTask.class);
//                        listCmFinishedType.add(cmTask);
//                    }
//                    Log.e(TAG, "onDataChange: jjj " + listCmFinishedType.size());
////                    refreshAdapter(Utils.merge(listPending, listCmFinishing, listCmWorking, listRejectedFromCustomer, listRejectedFromCompany));
//                    refreshAdapterWorker(Utils.merge(listCmFinishedType));
//                           progress.setVisibility(View.GONE);                    
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//                           progress.setVisibility(View.GONE);                    
//                }
//            });
//            eventListenerHashMap.put(query, valueEventListener);
//        }
//    }

    private void initRecyclerview() {
//        recyclerView = view.findViewById(R.id.recyclerView);
        /*
        * .doesn't depend on the adapter content:
        mRecyclerView.setHasFixedSize(true);
        ...depends on the adapter content:
        mRecyclerView.setHasFixedSize(false);*/
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void loadMyOrders() {
        stopAllEventListeners();
//        segmentedButtonGroupCustomers.setPosition(0);
//        segmentedBtnOrders.setSelected(true);
        if (Prefs.contains(Constants.FIREBASE_UID)) {
            showLoadingDialog();
            String userId = Prefs.getString(Constants.FIREBASE_UID, "");
            query = RefBase
//                    .refRequests(userId)
                    .refRequests()
                    .orderByChild(Constants.ORDER_STATE)
                    .equalTo(Constants.CM_WORKING);
            valueEventListener = query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //clear last data
//                    Log.e(TAG, "onDataChange: 1" );
                    listCmWorking.clear();
                    Log.e(TAG, "onDataChange: " + dataSnapshot.getChildrenCount());
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        Log.e(TAG, "HHHZXHZHZHXZX " + dataSnapshot1.getChildren());
                        OrderRequest order = dataSnapshot1.getValue(OrderRequest.class);
//                        Log.e(TAG, "onDataChange:3 " + order.getCreationDate());
                        if (TextUtils.equals(order.getCustomerId(),
                                Prefs.getString(Constants.FIREBASE_UID, ""))) {
                            Log.e(TAG, "HHHZXHZHZHXZX NNN" + order.getCreationDate());
//                            Log.e(TAG, "onDataChange:3 " + order.getCategoryId());
                            listCmWorking.add(order);
                        }
                    }
//                            Log.e(TAG, "onDataChange: " + list.size());
                    refreshAdapter(Utils.merge(listPending,
                            listCmFinishing,
                            listCmWorking,
                            listRejectedFromCustomer,
                            listRejectedFromCompany), Constants.ORDER);
                    progress.setVisibility(View.GONE);


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    progress.setVisibility(View.GONE);
                }
            });
            eventListenerHashMap.put(query, valueEventListener);


            query = RefBase
//                    .refRequests(userId)
                    .refRequests()
                    .orderByChild(Constants.ORDER_STATE)
                    .equalTo(Constants.CM_FINISHED);
            valueEventListener = query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //clear last data
//                    Log.e(TAG, "onDataChange: 1" );
                    listCmFinishing.clear();
                    Log.e(TAG, "onDataChange: " + dataSnapshot.getChildrenCount());
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        Log.e(TAG, "onDataChange:2 " + dataSnapshot1.getChildren());
                        OrderRequest order = dataSnapshot1.getValue(OrderRequest.class);
//                        Log.e(TAG, "onDataChange:3 " + order.getCreationDate());
                        if (TextUtils.equals(order.getCustomerId(),
                                Prefs.getString(Constants.FIREBASE_UID, ""))) {
                            listCmFinishing.add(order);
                        }
                    }
//                            Log.e(TAG, "onDataChange: " + list.size());
                    refreshAdapter(Utils.merge(listPending,
                            listCmFinishing,
                            listCmWorking,
                            listRejectedFromCustomer,
                            listRejectedFromCompany), Constants.ORDER);
                    progress.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    progress.setVisibility(View.GONE);
                }
            });
            eventListenerHashMap.put(query, valueEventListener);

            query = RefBase
//                    .refRequests(userId)
                    .refRequests()
                    .orderByChild(Constants.ORDER_STATE)
                    .equalTo(Constants.PENDING);
            valueEventListener = query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //clear last data
//                    Log.e(TAG, "onDataChange: 1" );
                    listPending.clear();
                    Log.e(TAG, "onDataChange: " + dataSnapshot.getChildrenCount());
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        Log.e(TAG, "onDataChange:2 " + dataSnapshot1.getChildren());
                        OrderRequest order = dataSnapshot1.getValue(OrderRequest.class);
//                        Log.e(TAG, "onDataChange:3 .3" + order.getCreationDate());
                        if (TextUtils.equals(order.getCustomerId(),
                                Prefs.getString(Constants.FIREBASE_UID, ""))) {
                            Log.e(TAG, "onDataChange:3.1" + order.getCategoryId());

                            listPending.add(order);
                        }
                    }
//                            Log.e(TAG, "onDataChange: " + list.size());
                    refreshAdapter(Utils.merge(listPending,
                            listCmFinishing,
                            listCmWorking,
                            listRejectedFromCustomer,
                            listRejectedFromCompany), Constants.ORDER);
                    progress.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    progress.setVisibility(View.GONE);
                }
            });
            eventListenerHashMap.put(query, valueEventListener);
            query = RefBase
//                    .refRequests(userId)
                    .refRequests()
                    .orderByChild(Constants.ORDER_STATE)
                    .equalTo(Constants.REJECTED_FROM_CUSTOMER);
            valueEventListener = query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //clear last data
//                    Log.e(TAG, "onDataChange: 1" );
                    listRejectedFromCustomer.clear();
                    Log.e(TAG, "onDataChange: " + dataSnapshot.getChildrenCount());
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        Log.e(TAG, "onDataChange:2 " + dataSnapshot1.getChildren());
                        OrderRequest order = dataSnapshot1.getValue(OrderRequest.class);
                        Log.e(TAG, "onDataChange:3 " + order.getCreationDate());

                        if (TextUtils.equals(order.getCustomerId(),
                                Prefs.getString(Constants.FIREBASE_UID, ""))) {
                            listRejectedFromCustomer.add(order);
                        }

                    }
//                            Log.e(TAG, "onDataChange: " + list.size());
                    refreshAdapter(Utils.merge(listPending,
                            listCmFinishing,
                            listCmWorking,
                            listRejectedFromCustomer,
                            listRejectedFromCompany), Constants.ORDER);
                    progress.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    progress.setVisibility(View.GONE);
                }
            });
            eventListenerHashMap.put(query, valueEventListener);
            query = RefBase
//                    .refRequests(userId)
                    .refRequests()
                    .orderByChild(Constants.ORDER_STATE)
                    .equalTo(Constants.REJECTED_FROM_COMPANY);
            valueEventListener = query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //clear last data
//                    Log.e(TAG, "onDataChange: 1" );
                    listRejectedFromCompany.clear();
                    Log.e(TAG, "onDataChange: " + dataSnapshot.getChildrenCount());
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        Log.e(TAG, "onDataChange:2 " + dataSnapshot1.getChildren());
                        OrderRequest order = dataSnapshot1.getValue(OrderRequest.class);
                        Log.e(TAG, "onDataChange:3 " + order.getCreationDate());

                        if (TextUtils.equals(order.getCustomerId(),
                                Prefs.getString(Constants.FIREBASE_UID, ""))) {
                            listRejectedFromCompany.add(order);
                        }
                    }
//                            Log.e(TAG, "onDataChange: " + list.size());
                    refreshAdapter(Utils.merge(listPending,
                            listCmFinishing,
                            listCmWorking,
                            listRejectedFromCustomer,
                            listRejectedFromCompany), Constants.ORDER);
                    progress.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    progress.setVisibility(View.GONE);
                }
            });
            eventListenerHashMap.put(query, valueEventListener);
        } else {
            progress.setVisibility(View.GONE);
//            Toasty.warning(Objects.requireNonNull(getActivity()), "Please Login First").show();
//            startActivity(new Intent(getActivity(), CustomerWorkerLoginActivity.class));
        }
    }

    private void loadWorkingOrders() {
        stopAllEventListeners();
        if (Prefs.contains(Constants.FIREBASE_UID)) {
            showLoadingDialog();
            String workerId = Prefs.getString(Constants.FIREBASE_UID, "");
            query = RefBase.refCmtTasks()
                    .orderByChild(Constants.TYPE)
                    .equalTo(Constants.CM_WORKING);

            Log.e(TAG, "loadWorkingOrders: " + workerId);
            valueEventListener = query.addValueEventListener(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    listCmWorkingType.clear();
                    if (dataSnapshot.exists()) {
//                        Log.e(TAG, "onDataChange: Test12 " + dataSnapshot.getValue(CmTask.class));
//                        Log.e(TAG, "onDataChange: Test12 " + dataSnapshot.getValue());
//                    Log.e(TAG, "onDataChange: " + cmTask.getType());
                        //clear last data
//                    Log.e(TAG, "onDataChange: 1" );

                        //Log.e(TAG, "onDataChange: " + dataSnapshot.getChildrenCount());
                        //Log.e(TAG, "onDataChange: " + dataSnapshot.getKey());//U916RqensGenvU2zcnDBl27GAM
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            CmTask cmTask = dataSnapshot1.getValue(CmTask.class);
                            Log.e(TAG, "ddddddrrr: " + cmTask.getCustomerId());
                            Log.e(TAG, "ddddddrrr: " + cmTask.getCustomerId() + "  " + workerId);
                            if (TextUtils.equals(cmTask.getCmId(), workerId)) {
                                Log.e(TAG, "AAAAAAAA: " + workerId);
                                try {
                                    Log.e(TAG, "AAAAAAAA: " + cmTask.getTo());
                                    @SuppressLint("SimpleDateFormat") Date toDate = new SimpleDateFormat("yyyy-MM-dd").parse(cmTask.getTo());
                                    Date current = Calendar.getInstance().getTime();
                                    Log.e(TAG, "date: " + current.toString());
                                    Log.e(TAG, "date: " + toDate.toString());
//                                    if (toDate.equals(current)) {
                                    if (toDate.before(current)) {
//                                    if (toDate.after(current)) {
                                        //Log.e(TAG, "onDataChange: equals ");
                                        Log.e(TAG, "AAAAAAAA: equal");
                                        cmTask.setType(Constants.CM_FINISHED);
                                        RefBase.refCmtTasks(dataSnapshot1.getKey())
                                                .setValue(cmTask)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.e(TAG, "onSuccess: ");
                                                    }
                                                });

                                    } else {
                                        listCmWorkingType.add(cmTask);
                                        //Log.e(TAG, "onDataChange: not equals ");
                                        Log.e(TAG, "AAAAAAAA: not equal");
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }


//                                Log.e(TAG, "onDataChange: Test11 ");
//                                listCmWorkingType.add(cmTask);
                            }
                        }

                        //                            Log.e(TAG, "onDataChange: " + list.size());
//                    refreshAdapter(Utils.merge(listCmWorkingType));
                        refreshAdapterWorker(Utils.merge(listCmWorkingType));
                        if (listCmWorkingType.isEmpty()) {
                            progress.setVisibility(View.GONE);
                            noOrdersOrPostsYet.setVisibility(View.VISIBLE);
                            noOrdersOrPostsYet.setText(getActivity().getString(R.string.no_working_requests));
                        } else {
                            noOrdersOrPostsYet.setVisibility(View.GONE);
//                        noOrdersOrPostsYet.setText("No working requests yet");
                        }

                    } else {
                        refreshAdapterWorker(Utils.merge(listCmWorkingType));
                        if (listCmWorkingType.isEmpty()) {
                            noOrdersOrPostsYet.setVisibility(View.VISIBLE);
                            noOrdersOrPostsYet.setText(getActivity().getString(R.string.no_working_requests));
                        } else {
                            noOrdersOrPostsYet.setVisibility(View.GONE);
//                        noOrdersOrPostsYet.setText("No working requests yet");
                        }

                    }
                    progress.setVisibility(View.GONE);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    progress.setVisibility(View.GONE);
                }
            });
            eventListenerHashMap.put(query, valueEventListener);
        }
    }

    private void loadFinishedOrders() {
        stopAllEventListeners();
        if (Prefs.contains(Constants.FIREBASE_UID)) {
            showLoadingDialog();
            String workerId = Prefs.getString(Constants.FIREBASE_UID, "");
            Log.e(TAG, "loadFinishedOrders2: " + workerId);
            Log.e(TAG, "loadFinishedOrders3: " + Constants.TYPE);
            Log.e(TAG, "loadFinishedOrders4: " + workerId);
            query = RefBase.refCmtTasks()
                    .orderByChild(Constants.TYPE)
                    .equalTo(Constants.CM_FINISHED);
            valueEventListener = query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //clear last data
//                    Log.e(TAG, "onDataChange: 1" );
                    listCmFinishedType.clear();
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            Log.e(TAG, "onDataChange: test2 " + dataSnapshot1.getChildrenCount());
                            Log.e(TAG, "onDataChange:2 " + dataSnapshot1.getChildren());
                            CmTask cmTask = dataSnapshot1.getValue(CmTask.class);
                            listCmFinishedType.add(cmTask);
                        }
                        Log.e(TAG, "onDataChange: jjj " + listCmFinishedType.size());
//                    refreshAdapter(Utils.merge(listPending, listCmFinishing, listCmWorking, listRejectedFromCustomer, listRejectedFromCompany));
                        refreshAdapterWorker(Utils.merge(listCmFinishedType));
                        if (listCmFinishedType.isEmpty()) {
                            noOrdersOrPostsYet.setVisibility(View.VISIBLE);
                            noOrdersOrPostsYet.setText(getActivity().getString(R.string.no_finished_requests_yet));
                        } else {
                            noOrdersOrPostsYet.setVisibility(View.GONE);
//                        noOrdersOrPostsYet.setText("No working requests yet");
                        }
                        progress.setVisibility(View.GONE);

                    } else {
                        refreshAdapterWorker(Utils.merge(listCmFinishedType));
                        if (listCmFinishedType.isEmpty()) {
                            noOrdersOrPostsYet.setVisibility(View.VISIBLE);
                            noOrdersOrPostsYet.setText(getActivity().getString(R.string.no_finished_requests_yet));
                        } else {
                            noOrdersOrPostsYet.setVisibility(View.GONE);
//                        noOrdersOrPostsYet.setText("No working requests yet");
                        }
                    }
                    progress.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    progress.setVisibility(View.GONE);

                }
            });
            eventListenerHashMap.put(query, valueEventListener);
        }
    }

    private void loadMyPosts() {
        //Stop all listener
        stopAllEventListeners();
        if (Prefs.contains(Constants.FIREBASE_UID)) {
//            if (spotsDialog != null) {
//                if (spotsDialog.isShowing()) {
//                            progress.setVisibility(View.GONE);
//                } else {
//                    showLoadingDialog();
//                }
//            }
            showLoadingDialog();

            String userId = Prefs.getString(Constants.FIREBASE_UID, "");
            query = RefBase
//                    .refRequests(userId)
                    .refRequests()
                    .orderByChild(Constants.ORDER_STATE)
                    .equalTo(Constants.FREELANCE_FINISHED);

            valueEventListener = query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //clear last data
//                    Log.e(TAG, "onDataChange: 1" );
                    listFreeFinishCustomer.clear();
                    Log.e(TAG, "onDataChange: " + dataSnapshot.getChildrenCount());
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        Log.e(TAG, "onDataChange:2 " + dataSnapshot1.getChildren());
                        OrderRequest order = dataSnapshot1.getValue(OrderRequest.class);
                        Log.e(TAG, "onDataChange:3 " + order.getCreationDate());
                        if (TextUtils.equals(order.getCustomerId(), userId)) {
                            listFreeFinishCustomer.add(order);
                        }
                    }

                    //                            Log.e(TAG, "onDataChange: " + list.size());
                    refreshAdapter(Utils.merge(listFreeFinishCustomer, listFreeWorkingCustomer, listFreePostCustomer), Constants.POST);
//                    FreelancerPostsAdapter adapter = new FreelancerPostsAdapter(getActivity(),
//                            Utils.merge(listFreeFinishCustomer, listFreeWorkingCustomer, listFreePostCustomer),
//                            user = null, FragmentOrderAndPosts.this);
//                    recyclerView.setAdapter(adapter);


                    //        progress.setVisibility(View.GONE);
                    progress.setVisibility(View.GONE);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    //        progress.setVisibility(View.GONE);
                    progress.setVisibility(View.GONE);

                }
            });
            eventListenerHashMap.put(query, valueEventListener);


            query = RefBase
//                    .refRequests(userId)
                    .refRequests()
                    .orderByChild(Constants.ORDER_STATE)
                    .equalTo(Constants.FREELANCE_WORKING);

            valueEventListener = query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //clear last data
//                    Log.e(TAG, "onDataChange: 1" );
                    listFreeWorkingCustomer.clear();
                    Log.e(TAG, "onDataChange: " + dataSnapshot.getChildrenCount());
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        Log.e(TAG, "onDataChange:2 " + dataSnapshot1.getChildren());
                        OrderRequest order = dataSnapshot1.getValue(OrderRequest.class);
                        Log.e(TAG, "onDataChange:3 " + order.getCreationDate());

                        Log.e(TAG, "onDataChange: good " + userId);

                        if (TextUtils.equals(order.getCustomerId(), userId)) {
                            Log.e(TAG, "onDataChange: good " + userId);
                            listFreeWorkingCustomer.add(order);
                        }
                    }
//                            Log.e(TAG, "onDataChange: " + list.size());

                    refreshAdapter(Utils.merge(listFreeFinishCustomer, listFreeWorkingCustomer, listFreePostCustomer), Constants.POST);
                    //        progress.setVisibility(View.GONE);
                    progress.setVisibility(View.GONE);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    //        progress.setVisibility(View.GONE);
                    progress.setVisibility(View.GONE);

                }
            });
            eventListenerHashMap.put(query, valueEventListener);


            query = RefBase
//                    .refRequests(userId)
                    .refRequests()
                    .orderByChild(Constants.ORDER_STATE)
                    .equalTo(Constants.POST);

            valueEventListener = query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //clear last data
//                    Log.e(TAG, "onDataChange: 1" );
                    listFreePostCustomer.clear();
                    Log.e(TAG, "onDataChange: " + dataSnapshot.getChildrenCount());
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        Log.e(TAG, "onDataChange:2 " + dataSnapshot1.getChildren());
                        OrderRequest order = dataSnapshot1.getValue(OrderRequest.class);
                        Log.e(TAG, "onDataChange:3 " + order.getCreationDate());

                        if (TextUtils.equals(order.getCustomerId(), userId)) {
                            Log.e(TAG, "onDataChange: good " + userId);
                            listFreePostCustomer.add(order);
                        }

                    }
//                            Log.e(TAG, "onDataChange: " + list.size());
                    //Arrays.asList() empty Lists (Collection)
                    refreshAdapter(Utils.merge(listFreeFinishCustomer, listFreeWorkingCustomer, listFreePostCustomer), Constants.POST);
                    //        progress.setVisibility(View.GONE);
                    progress.setVisibility(View.GONE);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    progress.setVisibility(View.GONE);
                }
            });
            eventListenerHashMap.put(query, valueEventListener);


        } else {
//                    progress.setVisibility(View.GONE);
//            Toasty.warning(Objects.requireNonNull(getActivity()), "Please Login First").show();
//            startActivity(new Intent(getActivity(), CustomerWorkerLoginActivity.class));
        }
    }

    private void showLoadingDialog() {
//        progressDialog = new ProgressDialog(getActivity());
//        progressDialog.setTitle("Loading...");
//        progressDialog.show();


//        if (spotsDialog != null) {
//            if (spotsDialog.isShowing()) {
//                        progress.setVisibility(View.GONE);
//                spotsDialog = Utils.getInstance().pleaseWait(getActivity());
//                spotsDialog.setCanceledOnTouchOutside(true);
//                spotsDialog.setCancelable(true);
//                spotsDialog.show();
//            } else {
//                spotsDialog = Utils.getInstance().pleaseWait(getActivity());
//                spotsDialog.setCanceledOnTouchOutside(true);
//                spotsDialog.setCancelable(true);
//                spotsDialog.show();
//            }
//        }


        spotsDialog = Utils.getInstance().pleaseWait(getActivity());
        spotsDialog.setCanceledOnTouchOutside(true);
        spotsDialog.setCancelable(true);
//        spotsDialog.show();


        progress.setVisibility(View.VISIBLE);
        ((TextView) progress.findViewById(R.id.tvMessage)).setText(getActivity().getString(R.string.please_wait));

    }

    private void refreshAdapter(List<OrderRequest> list, String orderOrPost) {
        this.listCustomerBusiness = list;
        MyOrderCustomerAdapter adapter = new MyOrderCustomerAdapter(getActivity(), list, orderOrPost, this);
        recyclerView.setAdapter(adapter);


        if (list.isEmpty()) {
            noOrdersOrPostsYet.setVisibility(View.VISIBLE);
//            if (segmentedButtonGroupCustomers.getPosition() == 0) {
            if (tablayoutCustomers.getSelectedTabPosition() == 0) {
                noOrdersOrPostsYet.setText(getActivity().getString(R.string.no_orders));

            } else {
                noOrdersOrPostsYet.setText(getActivity().getString(R.string.no_posts));
            }


        } else {
            noOrdersOrPostsYet.setVisibility(View.GONE);
        }
//
//        //        progress.setVisibility(View.GONE);
//        progress.setVisibility(View.GONE);

//        accessSegmentedControllButtons();

    }

    private void refreshAdapterWorker(List<CmTask> list) {
        MyOrderCmWorkingAdapter adapter = new MyOrderCmWorkingAdapter(getActivity(), list, this);
        Log.e(TAG, "refreshAdapterWorker: " + list.size());
        recyclerView.setAdapter(adapter);
    }

    private void reloadData(User user) {
        users.add(user);
        notifyAll();
    }

    private void refreshAdapterFreelancers(List<OrderRequest> list, List<User> users) {
        FreelancerPostsAdapter adapter = new FreelancerPostsAdapter(getActivity(), list,
                users, this, this);
//        Log.e(TAG, "refreshAdapterWorker: " + list.size() + user);
        recyclerView.setAdapter(adapter);


        if (list.isEmpty()) {
            noOrdersOrPostsYet.setVisibility(View.VISIBLE);
            switch (tablayoutFree.getSelectedTabPosition()) {
                case 0:
                    noOrdersOrPostsYet.setText(context.getResources().getString(R.string.no_posts));
                    break;
                case 1:
                    noOrdersOrPostsYet.setText(context.getResources().getString(R.string.no_working_posts));
                    break;
                case 2:
                    noOrdersOrPostsYet.setText(context.getResources().getString(R.string.no_finished_jobs_yet));
                    break;
            }
        } else {
            noOrdersOrPostsYet.setVisibility(View.GONE);

        }

//        accessSegmentedControllButtons();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

        Log.e(TAG, "onStart: ");
//        toggleBetweenLoginAndPreview();
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
        viewPagerLogin.setOffscreenPageLimit(3);
        viewPagerLogin.setPageMargin(1);

        mCardShadowTransformer.enableScaling(true);
        mFragmentCardShadowTransformer.enableScaling(true);


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
    public void onClick(View v, int pos) {
        Log.e(TAG, "onClick: uuu");
        posSelectedOrder = pos;
        initBottomSheetMemuDialog();
    }

    private void showMoreDetails(int pos) {
        if (Prefs.contains(Constants.FIREBASE_UID)) {
            Log.e(TAG, "FIREBASE_UID: ");

            Bundle bundle = new Bundle();
            if (Utils.customerOrWorker()) {
                Log.e(TAG, "onClick: normal user" + pos);
                Log.e(TAG, "onClick: normal user");
//                OrderRequest orderRequest = listFreeLancerPost.get(pos);
                OrderRequest orderRequest = listCustomerBusiness.get(pos);
                Log.e(TAG, "onClick:" + orderRequest.getOrderId());
//                if (segmentedButtonGroupCustomers.getPosition() == 0) {
                if (tablayoutCustomers.getSelectedTabPosition() == 0) {
                    Log.e(TAG, "onClick: zoz ");
                    intent = new Intent(getActivity(), CustomerOrderDetails.class);
                } else {
                    Log.e(TAG, "onClick: zoz2 ");

                    intent = new Intent(getActivity(), CustomerPostDetails.class);

//                    Toast.makeText(getActivity(), "Post customrs", Toast.LENGTH_SHORT).show();
                }
                bundle.putSerializable(Constants.ORDER, orderRequest);
                bundle.putString(Constants.USER_TYPE, Constants.USER);

                //bundle.putSerializable(Constants.USER, user);
                intent.putExtras(bundle);
                startActivity(intent);
            } else {
                if (Prefs.contains(Constants.WORKER_LOGGED_AS)) {
                    String type = Prefs.getString(Constants.WORKER_LOGGED_AS, "");
                    switch (type) {
                        case Constants.CM:
                            List<CmTask> list = new ArrayList<>();
//                            switch (segmentedButtonGroupWorkers.getPosition()) {
                            switch (tablayoutWorkers.getSelectedTabPosition()) {
                                case 0://cm working
                                    list = listCmWorkingType;
                                    break;
                                case 1://cm finishing
                                    list = listCmFinishedType;
                                    break;
                            }

                            CmTask cmTask = list.get(pos);
                            Log.e(TAG, "onClick:" + cmTask.getCategoryId());
//                                    intent = new Intent(getActivity(), CmOrderDetails.class);
//                                    bundle.putSerializable(Constants.CM_TASK, cmTask);
//                                    bundle.putSerializable(Constants.CM_TASK, orderRequest);
//                                    bundle.putSerializable(Constants.CM_FOR_USER, userForCmWorker);
//                                    intent.putExtras(bundle);
                            Log.e(TAG, "onClick: " + cmTask.getRequestId());
//                            spotsDialog.show();
                            progress.setVisibility(View.VISIBLE);

                            RefBase.
                                    //refRequests(cmTask.getRequestId())
                                            refRequests()
                                    .orderByChild(Constants.ORDER_ID)
                                    .equalTo(cmTask.getRequestId())
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
//                                                            for (DataSnapshot snap :
//                                                                    dataSnapshot.getChildren()) {
                                                dataSnapshot.getRef().removeEventListener(this);
                                                OrderRequest orderRequest = dataSnapshot.getChildren()
                                                        .iterator().next()
                                                        .getValue(OrderRequest.class);
                                                Log.e(TAG, "ffffffffffff: " + orderRequest.getCustomerId());
                                                Log.e(TAG, "ffffffffffff: " + orderRequest.getOrderId());
                                                RefBase.refUser(orderRequest.getCustomerId())
                                                        .addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                if (dataSnapshot.exists()) {
                                                                    //        progress.setVisibility(View.GONE);
                                                                    progress.setVisibility(View.GONE);

                                                                    dataSnapshot.getRef().removeEventListener(this);
                                                                    userForCmWorker = dataSnapshot.getValue(User.class);
                                                                    //start activity
                                                                    Log.e(TAG, "iiii: " + userForCmWorker.getUserId());
                                                                    Log.e(TAG, "onClick:" + cmTask.getCategoryId());
                                                                    intent = new Intent(getActivity(), CmOrderDetails.class);
                                                                    bundle.putSerializable(Constants.CM_TASK, cmTask);
                                                                    bundle.putSerializable(Constants.ORDER_REQUEST, orderRequest);
                                                                    bundle.putSerializable(Constants.CM_FOR_USER, userForCmWorker);
//                                                                    bundle.putString(Constants.WORKING_OR_FINISHED,
//                                                                            String.valueOf(tablayoutWorkers.getSelectedTabPosition()));
                                                                    intent.putExtras(bundle);
                                                                    startActivity(intent);
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                                //        progress.setVisibility(View.GONE);
                                                                progress.setVisibility(View.GONE);

                                                            }
                                                        });

                                            }
//                                                        }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                            break;
                        case Constants.FREELANCER:
                            OrderRequest orderRequest = null;

//                            Log.e(TAG, "onClicjjjjjjk: " + orderRequest.getOrderId());
//                            switch (segmentedButtonGroupFreelancer.getPosition()) {
                            switch (tablayoutFree.getSelectedTabPosition()) {
                                case 0:
                                    Log.e(TAG, "cxvxcvxcvcx: " + "posts");
                                    orderRequest = listFreeLancerPost.get(pos);
                                    if (!listFreeLancerPost.isEmpty() && !users.isEmpty()) {
                                        intent = new Intent(getActivity(), FreelancerPostDetails.class);
                                        bundle.putSerializable(Constants.ORDER, orderRequest);
                                        bundle.putSerializable(Constants.USER, users.get(pos));
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                    }
                                    break;
                                case 1:
                                    orderRequest = listFreeLancerCompanyWorking.get(pos);
                                    if (!listFreeLancerCompanyWorking.isEmpty() && !users.isEmpty()) {
                                        intent = new Intent(getActivity(), FreelancerPostDetails.class);
                                        bundle.putSerializable(Constants.ORDER, orderRequest);
                                        bundle.putSerializable(Constants.USER, users.get(pos));
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                    }
                                    Log.e(TAG, "cxvxcvxcvcx: " + orderRequest.getOrderId());
                                    break;
                                case 2:
                                    Log.e(TAG, "cxvxcvxcvcx: " + "finish");
                                    orderRequest = listFreeLancerFinishing.get(pos);
                                    if (!listFreeLancerFinishing.isEmpty() && !users.isEmpty()) {
                                        intent = new Intent(getActivity(), FreelancerPostDetails.class);
                                        bundle.putSerializable(Constants.ORDER, orderRequest);
                                        bundle.putSerializable(Constants.USER, users.get(pos));
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                    }
                                    break;
                            }
//                            Log.e(TAG, "onClick:" + orderRequest.getOrderId());
                            break;
                        case Constants.COMPANY:
                            switch (tablayoutFree.getSelectedTabPosition()) {
                                case 0:
                                    Log.e(TAG, "cxvxcvxcvcx: " + "posts");
                                    orderRequest = listFreeLancerPost.get(pos);
                                    if (!listFreeLancerPost.isEmpty() && !users.isEmpty()) {
                                        intent = new Intent(getActivity(), FreelancerPostDetails.class);
                                        bundle.putSerializable(Constants.ORDER, orderRequest);
                                        bundle.putSerializable(Constants.USER, users.get(pos));
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                    }
                                    break;
                                case 1:
                                    orderRequest = listFreeLancerCompanyWorking.get(pos);
                                    if (!listFreeLancerCompanyWorking.isEmpty() && !users.isEmpty()) {
                                        intent = new Intent(getActivity(), FreelancerPostDetails.class);
                                        bundle.putSerializable(Constants.ORDER, orderRequest);
                                        bundle.putSerializable(Constants.USER, users.get(pos));
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                    }
                                    Log.e(TAG, "cxvxcvxcvcx: " + orderRequest.getOrderId());
                                    break;
                                case 2:
                                    Log.e(TAG, "cxvxcvxcvcx: " + "finish");
                                    orderRequest = listFreeLancerFinishing.get(pos);
                                    if (!listFreeLancerFinishing.isEmpty() && !users.isEmpty()) {
                                        intent = new Intent(getActivity(), FreelancerPostDetails.class);
                                        bundle.putSerializable(Constants.ORDER, orderRequest);
                                        bundle.putSerializable(Constants.USER, users.get(pos));
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                    }
                                    break;
                            }
//                            Log.e(TAG, "onClick:" + orderRequest.getOrderId());
                            break;
                    }


                }
            }
        }
    }

    private void editPendingRequests(int pos) {
        if (Prefs.contains(Constants.FIREBASE_UID)) {
            Log.e(TAG, "FIREBASE_UID: ");

            Bundle bundle = new Bundle();

            if (Utils.customerOrWorker()) {
                Log.e(TAG, "onClick: it's customer come from Edit pending request" + pos);
                OrderRequest orderRequest = listCustomerBusiness.get(pos);
                Log.e(TAG, "onClick: it's request" + orderRequest.getOrderPhotos().size());

//                for (int i = 0; i < orderRequest.getOrderPhotos().size(); i++) {
//                    Log.e(TAG, "editPendingRequests: " + orderRequest.getOrderPhotos().get(i));
//                }

//                EventBus.clearCaches();

                EventBus.getDefault().post(new MsgEvtEditOrder(true, orderRequest));

            }
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true, priority = 1)
    public void onEvent(MsgFrmLoginToNotification event) {
//        Toast.makeText(getContext(), "Hey, my message" + event.isPushedOrNot(), Toast.LENGTH_SHORT).show();
//        setAdapterToLv();
        if (event != null) {
//            toggleBetweenLoginAndPreview(tab.getPosition());
            toggleBetweenLoginAndPreview(0);
            Log.e(TAG, "onTabSelected: fuuuu3 ");
            EventBus.getDefault().removeAllStickyEvents();
//            Toast.makeText(getActivity(), "jjjjjjjjjjjj", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRefresh() {
        Log.e(TAG, "onRefresh: ");
        if (Utils.customerOrWorker()) {
            toggleBetweenLoginAndPreview(tablayoutCustomers.getSelectedTabPosition());
            Log.e(TAG, "onResume: f2 ");
            Log.e(TAG, "onTabSelected: fuuuu4 ");


        } else {
            listFreeLancerPost.clear();
            users.clear();
//        refreshAdapterFreelancers(new ArrayList<OrderRequest>(), null);
            refreshAdapterFreelancers(listFreeLancerPost, users);
            if (getArguments() != null) {
                toggleBetweenLoginAndPreview(1);
                Log.e(TAG, "onTabSelected: fuuuu5 ");
                tablayoutFree.setScrollPosition(1, 0, true);
                Log.e(TAG, "onRefresh: 1");
            } else {
                toggleBetweenLoginAndPreview(tablayoutFree.getSelectedTabPosition());
                Log.e(TAG, "onTabSelected: fuuuu6 ");
                Log.e(TAG, "onRefresh: 2");
            }
        }


        new Handler()
                .postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);

                    }
                }, 1300);///millis sec

    }

    @Override
    public void onCallClick(View v, int pos, String phoneNumber) {
        Log.e(TAG, "onSuccess: please work i need to sleep 2");
//        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",phoneNumber,null)));
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:+" + phoneNumber));

        if (ContextCompat.checkSelfPermission(context, CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            startActivity(intent);
        } else {
            requestPermissions(new String[]{CALL_PHONE}, 1);
        }

    }
}
