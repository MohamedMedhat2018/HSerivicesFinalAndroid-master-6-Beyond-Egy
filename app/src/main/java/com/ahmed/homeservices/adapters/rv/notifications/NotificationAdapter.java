package com.ahmed.homeservices.adapters.rv.notifications;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmed.homeservices.R;
import com.ahmed.homeservices.constants.Constants;
import com.ahmed.homeservices.fire_utils.RefBase;
import com.ahmed.homeservices.interfaces.OnNotificationClick;
import com.ahmed.homeservices.models.Notification;
import com.ahmed.homeservices.models.User;
import com.ahmed.homeservices.utils.Utils;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.pixplicity.easyprefs.library.Prefs;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {
    private static final String TAG = "NotificationAdapter";
    private Context context;
    private List<Notification> listNotifications = new ArrayList<>();
    private OnNotificationClick onNotificationClick;

    public NotificationAdapter(Context context, List<Notification> listNotifications,
                               OnNotificationClick onNotificationClick) {
        this.context = context;
        this.onNotificationClick = onNotificationClick;
        this.listNotifications = listNotifications;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Notification notification = listNotifications.get(position);
        holder.tvMessage.setText(notification.getBody());
        holder.tvTitle.setText(notification.getTitle());


//        ReadMoreOption readMoreOption = new ReadMoreOption.Builder(context).build();
        // OR using options to customize
//        ReadMoreOption readMoreOption = new ReadMoreOption.Builder(context)
//                .textLength(3, ReadMoreOption.TYPE_LINE) // OR
//                //.textLength(300, ReadMoreOption.TYPE_CHARACTER)
//                .moreLabel("MORE")
//                .lessLabel("LESS")
//                .moreLabelColor(Color.RED)
//                .lessLabelColor(Color.BLUE)
//                .labelUnderLine(true)
//                .expandAnimation(true)
//                .build();
//        readMoreOption.addReadMoreTo(holder.tvMessage, holder.tvMessage.getText().toString());


        if (notification.isShown()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                holder.card.setCardBackgroundColor(context.getColor(R.color.grey_200));
                holder.card.setBackgroundColor(context.getColor(R.color.white));
            } else {
//                holder.card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.grey_200));
                holder.card.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                holder.card.setCardBackgroundColor(context.getColor(R.color.white));
                holder.card.setBackgroundColor(context.getColor(R.color.orange1));
            } else {
//                holder.card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white));
                holder.card.setBackgroundColor(ContextCompat.getColor(context, R.color.orange1));
            }
        }


        if (Prefs.contains(Constants.FIREBASE_UID)) {
            if (Utils.customerOrWorker()) {
                RefBase.refUser(Prefs.getString(Constants.FIREBASE_UID, ""))
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                dataSnapshot.getRef().removeEventListener(this);
                                if (dataSnapshot.exists()) {
                                    HashMap<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                                    Log.e(TAG, dataSnapshot.getKey());
                                    User user = dataSnapshot.getValue(User.class);
                                    String userPhoto = user.getUserPhoto();
                                    Log.e(TAG, userPhoto);
                                    if (!TextUtils.equals(user.getUserPhoto(), "Null") &&
                                            map.get(Constants.USER_PHOTO) != null && map.get(Constants.USER_PHOTO).toString().length() != 0) {
                                        Log.e(TAG, "userPhoto: ");
//                                    Picasso.get().load(map.get(Constants.USER_PHOTO).toString())
                                        Picasso.get().load(user.getUserPhoto())
                                                .into(holder.ivPhoto, new Callback() {
                                                    @Override
                                                    public void onSuccess() {
//                                                    Toast.makeText(ProfileActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                                        holder.progressBar.setVisibility(View.GONE);

                                                    }

                                                    @Override
                                                    public void onError(Exception e) {
                                                        holder.progressBar.setVisibility(View.GONE);

                                                    }
                                                });
                                    } else {
                                        holder.progressBar.setVisibility(View.GONE);
//                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                                            holder.ivPhoto.setBackground(getActivity().getDrawable(R.mipmap.account));
//                                        } else {
//                                            holder.ivPhoto.setBackground(ContextCompat.getDrawable(getActivity(), R.mipmap.account));
//                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            } else {

                switch (Prefs.getString(Constants.WORKER_LOGGED_AS, "")) {

                }

            }
        } else {


        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // send selected contact in callback
                //Log.e(TAG, "onClick: " + holder.tvComment.getText().toString());
                onNotificationClick.onNotificationClick(notification, position);
                //Toasty.success(context, "Accept "  + comment.getComment()).show();
            }
        });

        YoYo.with(Techniques.FadeIn)
                .duration(600)
                .playOn(holder.itemView);

    }

    @Override
    public int getItemCount() {
        return listNotifications.size();
    }



    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage;
        TextView tvTitle;
        CircularImageView ivPhoto;
        //        CardView card;
        LinearLayout card;
        ProgressBar progressBar;



        MyViewHolder(View view) {
            super(view);
            tvMessage = view.findViewById(R.id.tvMessage);
            tvTitle = view.findViewById(R.id.tvTitle);
            ivPhoto = view.findViewById(R.id.ivUserPhoto);
            card = view.findViewById(R.id.card);
            progressBar = view.findViewById(R.id.progressBar);


        }
    }
}