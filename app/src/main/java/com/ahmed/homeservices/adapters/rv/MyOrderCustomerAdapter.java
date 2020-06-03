package com.ahmed.homeservices.adapters.rv;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmed.homeservices.R;
import com.ahmed.homeservices.constants.Constants;
import com.ahmed.homeservices.fire_utils.RefBase;
import com.ahmed.homeservices.interfaces.OnItemClick;
import com.ahmed.homeservices.models.Category;
import com.ahmed.homeservices.models.ConnectionFree;
import com.ahmed.homeservices.models.orders.OrderRequest;
import com.ahmed.homeservices.utils.Utils;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.abdularis.civ.CircleImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MyOrderCustomerAdapter extends RecyclerView.Adapter<MyOrderCustomerAdapter.ViewHolder>
        implements Serializable {

    private static final String TAG = "MyOrderCustomerAdapter";
    private OnItemClick onItemClick;
    private Context context;
    private List<OrderRequest> listOfOrder = new ArrayList<>();
    private String orderOrPost = Constants.ORDER;


//    public MyOrderCustomerAdapter(Context context, List<OrderRequest> listOfOrder, OnItemClick onItemClick) {
//        this.context = context;
//        this.listOfOrder = listOfOrder;
//        this.onItemClick = onItemClick;
//    }


    public MyOrderCustomerAdapter(Context context, List<OrderRequest> listOfOrder, String orderOrPost, OnItemClick onItemClick) {
        this.context = context;
        this.listOfOrder = listOfOrder;
        this.onItemClick = onItemClick;
//        this.orderOrPost = orderOrPost;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;

        switch (orderOrPost) {
            case Constants.ORDER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_customer_order_item, parent, false);
                break;
//            case Constants.POST:
//                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_customer_post_item, parent, false);
//                break;
        }
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderRequest order = listOfOrder.get(position);
//        switch (orderOrPost) {
//            case Constants.POST:
//
//                CircleImageView customer_img_post;
//                TextView tv_customer_name_post;
//                TextView tv_customer_city_post;
//                TextView tv_customer_country_post;
//                TextView tv_post_description;
//
//                holder.tv_customer_name_post.setText(order.getCost() + "");
//                holder.tv_customer_name_post.setText(order.getCost() + "");
//                holder.tv_customer_country_post.setText(order.getCost() + "");
//                holder.tv_post_description.setText(order.getCost() + "");
//                Log.e(TAG, "onBindViewHolder: fuck u " );
//
//
//                break;
//            case Constants.ORDER:
        if (!order.getOrderPhotos().isEmpty()) {
            Log.e(TAG, "koooo  ");
            //https://firebasestorage.googleapis.com/v0/b/servizi-528e9.appspot.com/o/images%2F50f5774f-3646-419e-a98a-0172526fdd2f?alt=media&token=15f8391a-8221-4141-bfa9-c27b2df246d9
//            Glide.with(context).load(Uri.parse(order.getOrderPhotos().get(0))).into(holder.customer_order_img);
            //Log.e(TAG, "onBindViewHolder: " + order.getOrderPhotos().get(0));
            //Log.e(TAG, "onBindViewHolder: " );
            Picasso.get().load(Uri.parse(order.getOrderPhotos().get(0)))
                    .into(holder.customer_order_img, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {
                            holder.progressBar.setVisibility(View.GONE);
                        }
                    });

        } else {
            holder.progressBar.setVisibility(View.GONE);
            Log.e(TAG, "onBindViewHolder: null koooo");
            holder.customer_order_img.setImageResource(R.drawable.seyana_logo05);
        }
//        Log.e(TAG, "onBindViewHolder:13 ");
        holder.customer_order_coast.setText(order.getCost() + "");
        holder.customer_order_coast.setVisibility(Integer.valueOf(order.getCost()) > 0 ? View.VISIBLE : View.INVISIBLE);
        holder.order_start_date.setText(order.getCreationDate());
        holder.customer_order_time.setText(order.getCreationTime());


        RefBase.FreeCustomerConnection()
                .orderByChild(Constants.REQUEST_ID)
                .equalTo(order.getOrderId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                            holder.progressBarState.setVisibility(View.GONE);
                            ConnectionFree connectionFree = dataSnapshot.getChildren().iterator().next().getValue(ConnectionFree.class);
                            if (TextUtils.equals(connectionFree.getType(), (Constants.COMPANY_TYPE))) {
                                if (TextUtils.equals(order.getState(), Constants.FREELANCE_WORKING)) {
                                    //it's working
                                    holder.customer_ordr_state.setText(Constants.COMPANY_WORKING);
                                } else {
                                    //it's finish
                                    holder.customer_ordr_state.setText(Constants.COMPANY_FINISHED);
                                }
                            } else {
                                holder.customer_ordr_state.setText(order.getState());
                            }
                        } else {
                            holder.progressBarState.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        holder.customer_ordr_state.setText(Utils.setFirstUpperCharOfWord(order.getState()));
        RefBase.refCategory(order.getCategoryId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().removeEventListener(this);
                        if (dataSnapshot.exists()) {
                            Category category = dataSnapshot.getValue(Category.class);
                            holder.customer_order_category.setText(Utils.getCatName(category));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
//                break;
//        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClick.onClick(view, position);


            }
        });
        YoYo.with(Techniques.FadeIn)
                .duration(600)
                .playOn(holder.itemView);

    }

    @Override
    public int getItemCount() {
        return listOfOrder.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        //        ImageView customer_order_img;
        RoundedImageView customer_order_img;
        TextView customer_order_coast;
        TextView customer_order_category;
        TextView order_start_date;
        TextView customer_order_time;
        TextView customer_ordr_state;
        ProgressBar progressBar;
        View progressBarState;

        CircleImageView customer_img_post;
        TextView tv_customer_name_post;
        TextView tv_customer_city_post;
        TextView tv_customer_country_post;
        TextView tv_post_description;


        ViewHolder(@NonNull View itemView) {
            super(itemView);
            switch (orderOrPost) {
//                case Constants.POST:
//
//                    customer_img_post = itemView.findViewById(R.id.iv_customer_img_post);
//                    tv_customer_name_post = itemView.findViewById(R.id.tv_customer_name_post);
//                    tv_customer_city_post = itemView.findViewById(R.id.tv_customer_city_post);
//                    tv_customer_country_post = itemView.findViewById(R.id.tv_customer_country_post);
//                    tv_post_description = itemView.findViewById(R.id.tv_post_description);
//
//
//                    break;
                case Constants.ORDER:
                    customer_order_img = itemView.findViewById(R.id.iv_customer_order_img);
                    customer_order_category = itemView.findViewById(R.id.tv_customer_order_category);
                    customer_order_coast = itemView.findViewById(R.id.tv_customer_order_coast);
                    order_start_date = itemView.findViewById(R.id.order_start_date);
                    customer_order_time = itemView.findViewById(R.id.tv_customer_order_time);
                    customer_ordr_state = itemView.findViewById(R.id.tv_customer_ordr_state);
                    progressBar = itemView.findViewById(R.id.progressBar);
                    progressBarState = itemView.findViewById(R.id.spinKitView_state);
                    break;
            }


        }
    }
}
