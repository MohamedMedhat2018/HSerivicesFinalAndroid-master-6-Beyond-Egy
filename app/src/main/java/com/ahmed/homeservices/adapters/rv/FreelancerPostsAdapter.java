package com.ahmed.homeservices.adapters.rv;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmed.homeservices.R;
import com.ahmed.homeservices.constants.Constants;
import com.ahmed.homeservices.interfaces.OnItemClick;
import com.ahmed.homeservices.interfaces.OnMakeCallClick;
import com.ahmed.homeservices.models.User;
import com.ahmed.homeservices.models.orders.OrderRequest;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FreelancerPostsAdapter extends RecyclerView.Adapter<FreelancerPostsAdapter.ViewHolder> implements Serializable {

    private static final String TAG = "MyOrderCmWorkingAdapter";
    private OnItemClick onItemClick;
    private OnMakeCallClick onMakeCallClick;
    private Context context;
    private List<OrderRequest> listOfCmOrder = new ArrayList<>();
    private List<User> users = null;

    public FreelancerPostsAdapter(Context context, List<OrderRequest> listofFreelancerOrder, List<User> users, OnItemClick onItemClick, OnMakeCallClick onMakeCallClick) {
        this.context = context;
        this.listOfCmOrder = listofFreelancerOrder;
        this.users = users;
        this.onItemClick = onItemClick;
        this.onMakeCallClick = onMakeCallClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_customer_post_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderRequest orderRequest = listOfCmOrder.get(position);

        Log.e(TAG, "onBindViewHolder: reverse " + orderRequest.getOrderDescription());

//        Log.e(TAG, "onBindViewHolder: " + orderRequest.getFrom());
//        Log.e(TAG, "onBindViewHolder: " + orderRequest.getTo());
//        Log.e(TAG, "onBindViewHolder: " + orderRequest.getCategoryId());
//        Log.e(TAG, "onBindViewHolder: " + orderRequest.getCustomerComment());

//        if (!cmTask.getOrderPhotos().isEmpty()) {
////            Log.e(TAG, "onBindViewHolder:12 ");
//            //https://firebasestorage.googleapis.com/v0/b/servizi-528e9.appspot.com/o/images%2F50f5774f-3646-419e-a98a-0172526fdd2f?alt=media&token=15f8391a-8221-4141-bfa9-c27b2df246d9
//            Glide.with(context).load(Uri.parse(order.getOrderPhotos().get(0))).into(holder.customer_order_img);
//            //Log.e(TAG, "onBindViewHolder: " + order.getOrderPhotos().get(0) );
//            //Log.e(TAG, "onBindViewHolder: " );
//        }

//        Log.e(TAG, "onBindViewHolder:13 ");

        if (orderRequest.getState().equals(Constants.FREELANCE_WORKING)) {

//            holder.tv_call_freelancer_or_company.setText(users.get(position).getUserPhoneNumber());
            holder.rr_call_freelancer_or_company.setVisibility(View.VISIBLE);

            holder.tv_call_freelancer_or_company.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onMakeCallClick.onCallClick(view, position, users.get(position).getUserPhoneNumber());
                    Log.e(TAG, "onSuccess: please work i need to sleep ");

                }
            });
        } else {
            holder.rr_call_freelancer_or_company.setVisibility(View.GONE);
        }


//

        if (!users.isEmpty()) {
            Log.e(TAG, "onBindViewHolder: " + users.get(position).getUserName());
            if (!TextUtils.equals(users.get(position).getUserPhoto(), "Null")) {
                Log.e(TAG, "777777777777777: ");
                Picasso.get()
                        .load(users.get(position).getUserPhoto())
                        .into(holder.iv_customer_img_post, new Callback() {
                            @Override
                            public void onSuccess() {
                                Log.e(TAG, "onSuccess: ");
//                                               holder.iv_freelancer_profile.setTag(cmWorker.getWorkerPhone());
                            }

                            @Override
                            public void onError(Exception e) {
                                Log.e(TAG, "onError: " + e.getMessage()

                                );
                            }
                        });
            }
            holder.tv_customer_name_post.setText(users.get(position).getUserName());
        } else {
            Log.e(TAG, "onBindViewHolder: teeeet");
//            holder.iv_customer_img_post.setImageResource(R.mipmap.account);
        }


        holder.tv_customer_country_post.setText(orderRequest.getLocation().getCountry());
        holder.tv_customer_city_post.setText(orderRequest.getLocation().getCity());
        holder.tv_post_description.setText(orderRequest.getOrderDescription());
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
        return listOfCmOrder.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        //        ImageView iv_customer_img_post; //still not use yet
//        CircleImageView iv_customer_img_post; //still not use yet
        CircularImageView iv_customer_img_post; //still not use yet
        TextView tv_customer_name_post;
        TextView tv_customer_city_post;
        TextView tv_customer_country_post;
        TextView tv_post_description;
        RelativeLayout rr_call_freelancer_or_company;
        Button tv_call_freelancer_or_company;


        ViewHolder(@NonNull View itemView) {

            super(itemView);

            iv_customer_img_post = itemView.findViewById(R.id.iv_customer_img_post);
            tv_customer_name_post = itemView.findViewById(R.id.tv_customer_name_post);
            tv_customer_city_post = itemView.findViewById(R.id.tv_customer_city_post);
            tv_customer_country_post = itemView.findViewById(R.id.tv_customer_country_post);
            tv_post_description = itemView.findViewById(R.id.tv_post_description);
            rr_call_freelancer_or_company = itemView.findViewById(R.id.rr_call_freelancer_or_company);
            tv_call_freelancer_or_company = itemView.findViewById(R.id.tv_call_freelancer_or_company);

        }
    }
}
