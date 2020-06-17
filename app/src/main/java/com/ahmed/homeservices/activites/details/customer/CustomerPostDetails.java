package com.ahmed.homeservices.activites.details.customer;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmed.homeservices.R;
import com.ahmed.homeservices.activites.meowbottomnavigaion.MainActivity;
import com.ahmed.homeservices.adapters.rv.comments.CommentAdapter;
import com.ahmed.homeservices.adapters.view_pager.DemoInfiniteAdapter;
import com.ahmed.homeservices.constants.Constants;
import com.ahmed.homeservices.fire_utils.RefBase;
import com.ahmed.homeservices.models.CMWorker;
import com.ahmed.homeservices.models.Comment;
import com.ahmed.homeservices.models.Company;
import com.ahmed.homeservices.models.ConnectionFree;
import com.ahmed.homeservices.models.Rate;
import com.ahmed.homeservices.models.User;
import com.ahmed.homeservices.models.orders.OrderRequest;
import com.ahmed.homeservices.utils.Utils;
import com.asksira.loopingviewpager.LoopingViewPager;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.rd.PageIndicatorView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

import static android.Manifest.permission.CALL_PHONE;

public class CustomerPostDetails extends AppCompatActivity implements CommentAdapter.OnAcceptClicked, CommentAdapter.OnCallClicked {

    private static final String TAG = "CustomerPostDetails";
    //    @BindView(R.id.tv_customer_order_category)
//    TextView tv_customer_order_category;
//    @BindView(R.id.tv_customer_order_coast)
//    TextView tv_customer_order_coast;
    @BindView(R.id.iv_customer_post_img)
    ImageView iv_customer_post_img;
    @BindView(R.id.tv_customer_post_name)
    TextView tv_customer_post_name;
    @BindView(R.id.tv_customer_post_city)
    TextView tv_customer_post_city;
    @BindView(R.id.tv_customer_post_country)
    TextView tv_customer_post_country;
    @BindView(R.id.looping_view_pager_post)  //need an adapter
            LoopingViewPager looping_view_pager_post;
    @BindView(R.id.tv_customer_post_category)
    TextView tv_customer_post_category;
    @BindView(R.id.tv_post_start_date)
    TextView tv_post_start_date;
    @BindView(R.id.tv_customer_post_time)
    TextView tv_customer_post_time;
    @BindView(R.id.tv_customer_order_description)
    TextView tv_customer_order_description;
    @BindView(R.id.rv_comments)
    RecyclerView rv_comments;
    @BindView(R.id.et_comment)
    EditText etWriteComment;
    DemoInfiniteAdapter demoInfiniteAdapter;
    @BindView(R.id.iv_send_commnet)
    ImageView ivSendComment;
    List<Comment> listComments = new ArrayList<>();
    OrderRequest orderRequest;
    User user;
    @BindView(R.id.add_comment)
    View llSendComment;
    @BindView(R.id.tvNoCommentsYet)
    TextView tvNoCommentsYet;
    @BindView(R.id.llComments)
    LinearLayout llComments;
    String exist;
    @BindView(R.id.indicatorView)
    PageIndicatorView indicatorView;
    @BindView(R.id.tv_customer_post_location_address)
    TextView tv_customer_post_location_address;
    @BindView(R.id.tv_customer_post_city_loc)
    TextView tv_customer_post_city_loc;
    @BindView(R.id.tv_customer_post_country_loc)
    TextView tv_customer_post_country_loc;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.ll_customer_order_rate)
    LinearLayout ll_customer_order_rate;
    @BindView(R.id.rb_customer_order_rate)
    RatingBar rb_customer_order_rate;
    @BindView(R.id.tv_customer_order_comment_rate)
    TextView tv_customer_order_comment_rate;
    @BindView(R.id.tvEditOrderToolbar3)
    TextView cancelOrder;

    Boolean cancel = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freelancer_post_details);
        ButterKnife.bind(this);
        accessToolbar();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "onStart: CustomerPostDetails ");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (getIntent() != null) {
            if (getIntent().hasExtra(Constants.TYPE)) {
                Log.e(TAG, "onPostCreate:  1111 ");
                String passedOrderId = getIntent().getStringExtra(Constants.TYPE);
                if (passedOrderId != null) {
                    RefBase.refRequests()
                            .child(passedOrderId)
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    dataSnapshot.getRef().removeEventListener(this);
                                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                                        Log.e(TAG, "666666677:    hr");
                                        //                                   OrderRequest orderRequest = dataSnapshot.getChildren().iterator().next().getValue(OrderRequest.class);
                                        orderRequest = dataSnapshot.getValue(OrderRequest.class);
                                        llSendComment.setVisibility(View.GONE);
                                        ll_customer_order_rate.setVisibility(View.GONE);
                                        accessFields();
                                        addTextWatcherToEditTextSendComment();
                                        addingTheImagesToLoopViewPager();
//        Utils.attachIndicatiorToViewPager(looping_view_pager_post, indicatorView);
                                        getRateFromCustomer();
                                    } else {
//                                        progress.setVisibility(View.GONE);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }

            } else {
                Log.e(TAG, "onPostCreate: 4444 ");
                llSendComment.setVisibility(View.GONE);
                ll_customer_order_rate.setVisibility(View.GONE);
                getPassedData();
                onCancel();
                accessFields();
                addTextWatcherToEditTextSendComment();
                addingTheImagesToLoopViewPager();
//        Utils.attachIndicatiorToViewPager(looping_view_pager_post, indicatorView);
                getRateFromCustomer();
            }
        }
    }

    private void onCancel() {

        cancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "cancel request ");

                if (orderRequest.getState().equals(Constants.POST) || orderRequest.getState().equals(Constants.PENDING)) {
                    Map<String, Object> mapState = new HashMap<>();
                    mapState.put(Constants.ORDER_STATE, Constants.ORDER_STATE_CANCELLED);

                    RefBase.refRequests(orderRequest.getOrderId())
                            .updateChildren(mapState)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.e(TAG, " cancel check: onCancel befor" + cancel);
                                    cancel = true;
                                    Log.e(TAG, " cancel check: onCancel after" + cancel);

                                    finish();
                                }
                            });
                }

            }
        });
    }

    private void getPassedData() {
        Bundle extras = getIntent().getExtras();
        Log.e(TAG, "onCreate: extra " + extras.toString());
        if (extras != null) {
            Log.e(TAG, "onCreate: ");
            orderRequest = (OrderRequest) extras.getSerializable(Constants.ORDER);
            String type = extras.getString(Constants.USER_TYPE);
            Log.e(TAG, "onCreate: Type is " + type);


            if (type.equals(Constants.USER)) {
                if (orderRequest.getState().equals(Constants.POST) || orderRequest.getState().equals(Constants.PENDING)) {
                    cancelOrder.setVisibility(View.VISIBLE);
                } else {
                    cancelOrder.setVisibility(View.GONE);
                    cancel = true;
                }
            } else {
                cancelOrder.setVisibility(View.GONE);
                cancel = true;
            }
//            user = (User) extras.getSerializable(Constants.USER);
//
//            Picasso.get().load(user.getUserfPhoto()).into(iv_customer_post_img, new Callback() {
//                @Override
//                public void onSuccess() {
//                }
//                @Override
//                public void onError(Exception e) {
//                }
//            });
//            iv_customer_post_img.setImageResource(user.getUserPhoto());
//            tv_customer_post_name.setText(user.getUserName());
//            tv_customer_post_city.setText(orderRequest.getLocation().getCity());
//            tv_customer_post_country.setText(orderRequest.getLocation().getCountry());
            Log.e(TAG, "43242423: " + orderRequest.getCategoryId());
        }
    }

    private void accessToolbar() {
        toolbar.setNavigationOnClickListener(view -> {
            //finish();

//            Prefs.edit().putString(Constants.CLEAR, "").apply();
            onBackPressed();

        });
        toolbar.setTitle(getString(R.string.details_post));
//        setSupportActionBar(toolbar);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
//        Prefs.edit().putString(Constants.CLEAR, "").apply();
//        startActivity(new Intent(getApplicationContext(), LanguageActivity.class));
        startActivity(new Intent(getApplicationContext(), MainActivity.class));

    }

    @Override
    protected void onResume() {
        super.onResume();
        looping_view_pager_post.resumeAutoScroll();
    }

    @Override
    protected void onPause() {
        looping_view_pager_post.pauseAutoScroll();
        super.onPause();
    }

    @BindView(R.id.cardNoImages)
    View cardNoImages;

    //    @BindView(R.id.llIndicator)
//    LinearLayout llIndicator;
    @BindView(R.id.llIndicator)
    LinearLayout llIndicator;

    private void addingTheImagesToLoopViewPager() {
        if (orderRequest != null && !orderRequest.getOrderPhotos().isEmpty()) {
            ArrayList<String> photos = orderRequest.getOrderPhotos();
            for (String s :
                    photos) {
                Log.e(TAG, "fjfjfjfjfj:  " + s);
            }
            demoInfiniteAdapter = new DemoInfiniteAdapter(getApplicationContext(), photos, true);
            looping_view_pager_post.setAdapter(demoInfiniteAdapter);
            cardNoImages.setVisibility(View.GONE);
            Utils.attachIndicatiorToViewPager(looping_view_pager_post, indicatorView);


        } else {
//            ArrayList<String> staticPhotos = new ArrayList<>();
//            staticPhotos.add(String.valueOf(R.drawable.seyana_logo_no_bg));
            cardNoImages.setVisibility(View.VISIBLE);
//            demoInfiniteAdapter = new DemoInfiniteAdapter(getApplicationContext(), staticPhotos, true);
//            looping_view_pager_post.setAdapter(demoInfiniteAdapter);
        }
    }

    private void addTextWatcherToEditTextSendComment() {
        etWriteComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String s = charSequence.toString();
                if (s.length() > 0) {
                    ivSendComment.setEnabled(true);
                } else {
                    ivSendComment.setEnabled(false);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {


            }
        });
    }

    private void accessFields() {
        tv_customer_post_location_address.setText(orderRequest.getLocationAddress());
        if (orderRequest.getLocation() != null) {
            tv_customer_post_city_loc.setText(orderRequest.getLocation().getCity());
            tv_customer_post_country_loc.setText(orderRequest.getLocation().getCountry());
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
                                tv_customer_post_category.setText(catName);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        tv_post_start_date.setText(orderRequest.getCreationDate());
        tv_customer_post_time.setText(orderRequest.getCreationTime());
        tv_customer_order_description.setText(orderRequest.getOrderDescription());
        rv_comments.setHasFixedSize(false);
        rv_comments.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rv_comments.setItemAnimator(new DefaultItemAnimator());
        Log.e(TAG, "accessFields: " + orderRequest.getOrderId());
        loadPreviousCommentsIntoRecyclerView();

    }

    private void loadPreviousCommentsIntoRecyclerView() {
        RefBase.refComments(orderRequest.getOrderId())
                .orderByChild(Constants.ACCEPTED_COMMENT)
                .equalTo(true)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        dataSnapshot.getRef().removeEventListener(this);
                        if (dataSnapshot.exists()) {
                            exist = Constants.EXIST;
                        } else {
                            exist = null;
                        }
                        RefBase.refComments(orderRequest.getOrderId())
                                .addValueEventListener(new ValueEventListener() {
                                                           @Override
                                                           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                               if (dataSnapshot.exists()) {
                                                                   Log.e(TAG, "onDataChange: " + dataSnapshot.getKey());
                                                                   listComments.clear();
                                                                   for (DataSnapshot snapShot :
                                                                           dataSnapshot.getChildren()) {
                                                                       //Log.e(TAG, "comments " + dataSnapshot.toString());
                                                                       HashMap<String, Object> map = (HashMap<String, Object>) snapShot.getValue();
                                                                       if (map != null) {
                                                                           Comment comment = new Comment();
                                                                           comment.setComment(map.get("comment").toString());
                                                                           comment.setFreelancerId(map.get("freelancerId").toString());
                                                                           if (map.get(Constants.ACCEPTED_COMMENT) != null) {
//                                                               comment.setSelected((boolean) map.get(Constants.ACCEPTED_COMMENT));
                                                                               comment.setSelected((boolean) map.get(Constants.ACCEPTED_COMMENT));
                                                                               if (map.get(Constants.TYPE) != null) {
                                                                                   comment.setType(map.get("type").toString());
                                                                               }
                                                                               Log.e(TAG, "fffffffsdfs: ");
                                                                           }
                                                                           comment.setCommentId(snapShot.getKey());
                                                                           listComments.add(comment);

                                                                           Log.e(TAG, "getComment: " + comment.getComment());
                                                                           Log.e(TAG, "getFreelancerId: " + comment.getFreelancerId());
                                                                           Log.e(TAG, "cancel check: load comments " + cancel);

                                                                           rv_comments.setAdapter(new CommentAdapter(getApplicationContext(), exist, cancel,
                                                                                   listComments, Constants.USER,
                                                                                   CustomerPostDetails.this, CustomerPostDetails.this));

//                                                                           for (int i = 0; i < rv_comments.getChildCount(); i++) {
//                                                                               View viewGroup = rv_comments.getChildAt(i);
//                                                                               if (listComments.get(i).isSelected()) {
//                                                                                   viewGroup.findViewById(R.id.btn_prev).setVisibility(View.VISIBLE);
//                                                                                   viewGroup.findViewById(R.id.btn_accept).setVisibility(View.GONE);
//                                                                               } else {
//                                                                                   viewGroup.findViewById(R.id.btn_prev).setVisibility(View.GONE);
//                                                                                   viewGroup.findViewById(R.id.btn_accept).setVisibility(View.VISIBLE);
//                                                                               }
//                                                                           }

                                                                       }

                                                                   }

                                                                   if (listComments.isEmpty()) {
//                                                                       rv_comments.setVisibility(View.GONE);
                                                                       llComments.setVisibility(View.GONE);
                                                                       tvNoCommentsYet.setVisibility(View.VISIBLE);
                                                                   } else {
//                                                                       rv_comments.setVisibility(View.VISIBLE);
                                                                       llComments.setVisibility(View.VISIBLE);
                                                                       tvNoCommentsYet.setVisibility(View.GONE);
                                                                   }
                                                               }
                                                           }

                                                           @Override
                                                           public void onCancelled(@NonNull DatabaseError databaseError) {

                                                           }
                                                       }
                                );


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }

    @Override
    public void OnAcceptClicked(Comment comment, int pos) {
        Log.e(TAG, "OnAcceptClicked: " + comment.isSelected());
        cancelOrder.setVisibility(View.GONE);
//        for (int i = 0; i < rv_comments.getChildCount(); i++) {
//            View viewGroup = rv_comments.getChildAt(i);
//            if (listComments.get(i).isSelected()) {
//                viewGroup.findViewById(R.id.btn_accept).setVisibility(View.VISIBLE);
//                viewGroup.findViewById(R.id.btn_accept).setVisibility(View.GONE);
//            } else {
//                viewGroup.findViewById(R.id.btn_accept).setVisibility(View.GONE);
//                viewGroup.findViewById(R.id.btn_accept).setVisibility(View.VISIBLE);
//            }
//        }

        RefBase.refComments(orderRequest.getOrderId())
                .orderByChild(Constants.ACCEPTED_COMMENT)
                .equalTo(true)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().removeEventListener(this);
                        if (!dataSnapshot.exists()) {
                            Log.e(TAG, "onDataChangzxczxcezxc: ");
//                            Map map_comment = (HashMap<String, Object>) dataSnapshot.getValue();
//                            Log.e(TAG, "onDataChangzxczxcezxc:.. " + map_comment);
//                            Comment comment1 = dataSnapshot.getValue(Comment.class);
                            Map<String, Object> map = new HashMap<>();
                            map.put(Constants.ORDER_STATE, Constants.FREELANCE_WORKING);

                            RefBase.refRequests(orderRequest.getOrderId())
                                    .updateChildren(map)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
//                                            Toasty.success(getApplicationContext(), "Changing the order state to " + Constants.FREELANCE_WORKING).show();
                                            Toasty.success(getApplicationContext(), getString(R.string.post_changed)).show();

                                            String key = RefBase.refFreelancersConnection()
                                                    .push()
                                                    .getKey();
                                            ConnectionFree connectionFree = new ConnectionFree();
                                            connectionFree.setCustomerId(orderRequest.getCustomerId());
                                            connectionFree.setFreelancerId(comment.getFreelancerId());
                                            connectionFree.setRequestId(orderRequest.getOrderId());
                                            connectionFree.setState(Constants.FREELANCE_WORKING);

                                            if (comment.getType() != null) {
                                                if (TextUtils.equals(comment.getType(), Constants.COMPANY_TYPE)) {
                                                    //it's a company
                                                    Log.e(TAG, "onDataChangzxczxcezxc: it's a company 1.1");
                                                    connectionFree.setType(Constants.COMPANY_TYPE);
                                                } else {
                                                    //it's freelancer
                                                    connectionFree.setType(Constants.FREELANCER);
                                                    Log.e(TAG, "onDataChangzxczxcezxc: it's a freelancer 1.1");
                                                }
                                            } else {
                                                Log.e(TAG, "onDataChangzxczxcezxc: it's null ");
                                            }

                                            RefBase.refFreelancersConnection().child(key)
                                                    .setValue(connectionFree)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.e(TAG, "onSuccess: FreeLancer Working ");

                                                        }
                                                    });

                                        }
                                    });
                            HashMap<String, Object> map1 = new HashMap<>();
                            map1.put(Constants.ACCEPTED_COMMENT, true);
                            RefBase.refComments(orderRequest.getOrderId())
                                    .child(comment.getCommentId())
                                    .updateChildren(map1);
                        } else {
//                            Toasty.success(getApplicationContext(), "Free info").show();


                            View v = LayoutInflater.from(getApplicationContext())
                                    .inflate(R.layout.layout_free_info_from_comment, null);
                            AlertDialog alertDialog;
                            AlertDialog.Builder builder;
                            builder = new AlertDialog.Builder(CustomerPostDetails.this)
                                    .setView(v)
                                    .setPositiveButton(getString(R.string.ok), (dialog, which) ->
                                            dialog.dismiss()
                                    );
                            alertDialog = builder.create();
                            if (!alertDialog.isShowing()) {
                                alertDialog.show();
                            }

                            TextView tvFullName = v.findViewById(R.id.tvFullName);
                            TextView tvEmail = v.findViewById(R.id.tvEmail);
                            TextView tvPhone = v.findViewById(R.id.tvPhone);
                            CircularImageView ivUserPhoto = v.findViewById(R.id.ivUserPhoto);
                            SpinKitView progress = v.findViewById(R.id.progress);

                            Log.e(TAG, "onDataChange: test company comment 3 " + comment.getFreelancerId());
                            if (TextUtils.equals(comment.getType(), Constants.COMPANY_TYPE)) {
                                Log.e(TAG, "onDataChange: test company comment 1 " + comment.getType());
                                RefBase.refCompany(comment.getFreelancerId())
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                dataSnapshot.getRef().removeEventListener(this);
                                                if (dataSnapshot.exists()) {
                                                    //it's Freelancer
                                                    Company company = dataSnapshot.getValue(Company.class);
                                                    tvFullName.setText(company.getCompanyNameInArabic());
                                                    tvPhone.setText(company.getCompanyPhone());
                                                    tvEmail.setText(company.getCompanyEmail());
//                                                    tvPhone.setOnClickListener(new View.OnClickListener() {
//                                                        @Override
//                                                        public void onClick(View view) {
//                                                            Log.e(TAG, "onDataChange: call company ");
//                                                            Intent intent = new Intent(Intent.ACTION_CALL);
//                                                            intent.setData(Uri.parse("tel:+" + company.getCompanyPhone()));
//                                                            if (ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
//                                                                startActivity(intent);
//                                                            } else {
//                                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                                                                    requestPermissions(new String[]{CALL_PHONE}, 1);
//                                                                }
//                                                            }
//                                                        }
//                                                    });
                                                    Log.e(TAG, "onDataChange: test company comment 2 ");

                                                    Log.e(TAG, "onDataChange: " + company.getCompanyEmail());
                                                    Log.e(TAG, "onDataChange: " + company.getCompanyNameInArabic());
                                                    Picasso.get()
                                                            .load(company.getCompanyPhoto())
                                                            .into(ivUserPhoto, new Callback() {
                                                                @Override
                                                                public void onSuccess() {
                                                                    progress.setVisibility(View.GONE);
                                                                }

                                                                @Override
                                                                public void onError(Exception e) {
                                                                    progress.setVisibility(View.GONE);

                                                                }
                                                            });
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
//                            } else if (TextUtils.equals(comment.getType(), Constants.FREELANCER)) {
                            } else {
                                RefBase.refWorker(comment.getFreelancerId())
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                dataSnapshot.getRef().removeEventListener(this);
                                                if (dataSnapshot.exists()) {
                                                    //it's Freelancer
                                                    CMWorker cmWorker = dataSnapshot.getValue(CMWorker.class);
                                                    tvFullName.setText(cmWorker.getWorkerNameInArabic());
                                                    tvPhone.setText(cmWorker.getWorkerPhone());
                                                    tvEmail.setText(cmWorker.getWorkerEmail());

//                                                    tvPhone.setOnClickListener(new View.OnClickListener() {
//                                                        @Override
//                                                        public void onClick(View view) {
//                                                            Log.e(TAG, "onDataChange: call Freelancer ");
//                                                            Intent intent = new Intent(Intent.ACTION_CALL);
//                                                            intent.setData(Uri.parse("tel:+" + cmWorker.getWorkerPhone()));
//                                                            if (ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
//                                                                startActivity(intent);
//                                                            } else {
//                                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                                                                    requestPermissions(new String[]{CALL_PHONE}, 1);
//                                                                }
//                                                            }
//                                                        }
//                                                    });

                                                    Log.e(TAG, "onDataChange: " + cmWorker.getWorkerEmail());
                                                    Log.e(TAG, "onDataChange: " + cmWorker.getWorkerNameInEnglish());
                                                    Picasso.get()
                                                            .load(cmWorker.getWorkerPhoto())
                                                            .into(ivUserPhoto, new Callback() {
                                                                @Override
                                                                public void onSuccess() {
                                                                    progress.setVisibility(View.GONE);
                                                                }

                                                                @Override
                                                                public void onError(Exception e) {
                                                                    progress.setVisibility(View.GONE);

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


        if (!comment.isSelected()) {

        } else {


        }


    }

    public void getRateFromCustomer() {
//        RefBase.refRequests().
//                orderByChild(Constants.ORDER_STATE)
//                .equalTo(Constants.FREELANCE_FINISHED)
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
//                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
//                                OrderRequest orderRequest = dataSnapshot1.getValue(OrderRequest.class);
        if (orderRequest.getRate().length() != 1) {
            Log.e(TAG, "getRateFromCustomer: Customer rate ");
            RefBase.rate(orderRequest.getRate()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                        dataSnapshot.getRef().removeEventListener(this);
                        Rate rate = dataSnapshot.getValue(Rate.class);
                        if (rate != null) {
                            ll_customer_order_rate.setVisibility(View.VISIBLE); //not finished
                            rb_customer_order_rate.setRating(Float.parseFloat(rate.getNumberOfStars()));

                            tv_customer_order_comment_rate.setText(rate.getMessage());
                            Log.e(TAG, "onDataChange: comment" + "your comment Customer " + rate.getMessage());

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } else {
            ll_customer_order_rate.setVisibility(View.INVISIBLE);
            Log.e(TAG, "getRateFromCustomer: No rate ");
        }

//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
    }

    @Override
    public void OnCallClicked(Comment comment, int pos) {
        if (TextUtils.equals(comment.getType(), Constants.COMPANY_TYPE)) {
            Log.e(TAG, "onDataChange: test company comment 1 " + comment.getType());
            RefBase.refCompany(comment.getFreelancerId())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            dataSnapshot.getRef().removeEventListener(this);
                            if (dataSnapshot.exists()) {
                                //it's Freelancer
                                Company company = dataSnapshot.getValue(Company.class);
                                Log.e(TAG, "onDataChange: call company ");
                                Intent intent = new Intent(Intent.ACTION_CALL);
                                intent.setData(Uri.parse("tel:+" + company.getCompanyPhone()));
                                if (ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                                    startActivity(intent);
                                } else {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{CALL_PHONE}, 1);
                                    }
                                }
                                Log.e(TAG, "onDataChange: test company comment 2 ");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
//                            } else if (TextUtils.equals(comment.getType(), Constants.FREELANCER)) {
        } else {
            RefBase.refWorker(comment.getFreelancerId())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            dataSnapshot.getRef().removeEventListener(this);
                            if (dataSnapshot.exists()) {
                                //it's Freelancer
                                CMWorker cmWorker = dataSnapshot.getValue(CMWorker.class);
                                Log.e(TAG, "onDataChange: call Freelancer ");
                                Intent intent = new Intent(Intent.ACTION_CALL);
                                intent.setData(Uri.parse("tel:+" + cmWorker.getWorkerPhone()));
                                if (ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                                    startActivity(intent);
                                } else {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{CALL_PHONE}, 1);
                                    }
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
