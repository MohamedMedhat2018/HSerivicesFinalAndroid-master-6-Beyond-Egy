package com.ahmed.homeservices.adapters.rv;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmed.homeservices.R;
import com.ahmed.homeservices.interfaces.OnItemClick;
import com.ahmed.homeservices.models.CmTask;
import com.ahmed.homeservices.models.User;
import com.ahmed.homeservices.utils.Utils;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MyOrderCmWorkingAdapter extends RecyclerView.Adapter<MyOrderCmWorkingAdapter.ViewHolder> implements Serializable {

    private static final String TAG = "MyOrderCmWorkingAdapter";
    private OnItemClick onItemClick;
    private Context context;
    private List<CmTask> listOfCmOrder = new ArrayList<>();

    public MyOrderCmWorkingAdapter(Context context, List<CmTask> listOfCmOrder, OnItemClick onItemClick) {
        this.context = context;
        this.listOfCmOrder = listOfCmOrder;
        this.onItemClick = onItemClick;
    }
    public MyOrderCmWorkingAdapter(Context context, List<CmTask> listOfCmOrder, User  user, OnItemClick onItemClick) {
        this.context = context;
        this.listOfCmOrder = listOfCmOrder;
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_cm_order_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CmTask cmTask = listOfCmOrder.get(position);


        Log.e(TAG, "onBindViewHolder: " + cmTask.getFrom());
        Log.e(TAG, "onBindViewHolder: " + cmTask.getTo());
        Log.e(TAG, "onBindViewHolder: " + cmTask.getCategoryId());
        Log.e(TAG, "onBindViewHolder: " + cmTask.getCustomerComment());


        //        if (!cmTask.getOrderPhotos().isEmpty()) {
////            Log.e(TAG, "onBindViewHolder:12 ");
//            //https://firebasestorage.googleapis.com/v0/b/servizi-528e9.appspot.com/o/images%2F50f5774f-3646-419e-a98a-0172526fdd2f?alt=media&token=15f8391a-8221-4141-bfa9-c27b2df246d9
//            Glide.with(context).load(Uri.parse(order.getOrderPhotos().get(0))).into(holder.customer_order_img);
//            //Log.e(TAG, "onBindViewHolder: " + order.getOrderPhotos().get(0) );
//            //Log.e(TAG, "onBindViewHolder: " );
//        }

//        Log.e(TAG, "onBindViewHolder:13 ");



//        holder.tv_order_cm_start_date.setText(cmTask.getFrom() + "");
//        holder.tv_order_cm_end_date.setText(cmTask.getTo() + "");
//        holder.tv_cm_order_coast.setText(cmTask.getCost() + "");
//        holder.tv_cm_ordr_state.setText(cmTask.getType() + "");
//
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onItemClick.onClick(view, position);
//            }
//        });

        holder.tv_order_cm_start_date.setText(cmTask.getFrom() + "");
        holder.tv_order_cm_end_date.setText(cmTask.getTo() + "");
        holder.tv_cm_order_coast.setText(cmTask.getCost() + " " +context.getString(R.string.
                aed));

//        holder.tv_cm_ordr_state.setText(cmTask.getType() + "");
        holder.tv_cm_ordr_state.setText(Utils.setFirstUpperCharOfWord(cmTask.getType()));

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
        ImageView iv_cm_order_img;
        TextView tv_order_cm_start_date;
        TextView tv_order_cm_end_date;
        TextView tv_cm_order_coast;
        TextView tv_cm_ordr_state;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_cm_order_img = itemView.findViewById(R.id.iv_cm_order_img);
            tv_order_cm_start_date = itemView.findViewById(R.id.tv_order_cm_start_date);
            tv_order_cm_end_date = itemView.findViewById(R.id.tv_order_cm_end_date);
            tv_cm_order_coast = itemView.findViewById(R.id.tv_cm_order_coast);
            tv_cm_ordr_state = itemView.findViewById(R.id.tv_cm_ordr_state);
        }
    }
}
