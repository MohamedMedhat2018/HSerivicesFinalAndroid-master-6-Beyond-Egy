package com.ahmed.homeservices.adapters.rv.comments;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmed.homeservices.R;
import com.ahmed.homeservices.constants.Constants;
import com.ahmed.homeservices.fire_utils.RefBase;
import com.ahmed.homeservices.models.CMWorker;
import com.ahmed.homeservices.models.Comment;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder> {
    private static final String TAG = "CommentAdapter";
    private Context context;
    private List<Comment> listComment;
    private OnAcceptClicked onAcceptClicked;
    private OnCallClicked onCallClicked;


    //    public CommentAdapter(Context context, List<Comment> listComment, ContactsAdapterListener listener) {
//        this.context = context;
//        this.listener = listener;
//        this.listComment = listComment;
//    }
    private String userType;
    private String exist;
    private Boolean cancel;

    public CommentAdapter(Context context, String exist, Boolean cancel, List<Comment> listComment,
                          String userType, OnAcceptClicked onAcceptClicked, OnCallClicked onCallClicked) {
        this.context = context;
        this.onAcceptClicked = onAcceptClicked;
        this.onCallClicked = onCallClicked;
        this.userType = userType;
        this.listComment = listComment;
        this.exist = exist;
        this.cancel = cancel;

    }

    public CommentAdapter(Context context, List<Comment> listComment, String userType, OnAcceptClicked onAcceptClicked) {
        this.context = context;
        this.onAcceptClicked = onAcceptClicked;
        this.userType = userType;
        this.listComment = listComment;
        this.exist = exist;

    }

    public CommentAdapter(Context context, List<Comment> listComment) {
        this.context = context;
        this.listComment = listComment;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Comment comment = listComment.get(position);

        holder.btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // send selected contact in callback
                //Log.e(TAG, "onClick: " + holder.tvComment.getText().toString());
                onAcceptClicked.OnAcceptClicked(comment, position);
                //Toasty.success(context, "Accept "  + comment.getComment()).show();
            }
        });
        holder.btnSendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // send selected contact in callback
                //Log.e(TAG, "onClick: " + holder.tvComment.getText().toString());
                onAcceptClicked.OnAcceptClicked(comment, position);
                //Toasty.success(context, "Accept "  + comment.getComment()).show();
            }
        });
        holder.btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCallClicked.OnCallClicked(comment, position);
            }
        });


        RefBase.refWorker(comment.getFreelancerId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            dataSnapshot.getRef().removeEventListener(this);
                            CMWorker cmWorker = dataSnapshot.getValue(CMWorker.class);
//                                                tvFullName.setText(cmWorker.getWorkerNameInArabic());
//                                                tvEmail.setText(cmWorker.getWorkerEmail());
                            Log.e(TAG, "onDataChange: " + cmWorker.getWorkerEmail());
                            Log.e(TAG, "onDataChange: " + cmWorker.getWorkerNameInEnglish());
                            Log.e(TAG, "onDataChange: " + cmWorker.getWorkerPhoto());

                            holder.tv_worker_name.setText(cmWorker.getWorkerNameInEnglish());
//                            Log.e(TAG, "onDataChange:22 " + cmWorker.getWorkerNameInEnglish() );
                            //cmWorker.getWorkerNameInEnglish()

//                            holder.iv_freelancer_profile.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
////                                    onFreePostImageClciked.onFreeImageClicked((String) holder.iv_freelancer_profile.getTag()
////                                            , cmWorker, position);
//                                    onFreePostImageClciked.onFreeImageClicked(cmWorker, position);
//                                }
//                            });


                            if (cmWorker.getWorkerPhoto() != null &&
                                    cmWorker.getWorkerPhoto().length() != 0) {
                                Picasso.get()
                                        .load(cmWorker.getWorkerPhoto())
                                        .into(holder.iv_freelancer_profile, new Callback() {
                                            @Override
                                            public void onSuccess() {
                                                Log.e(TAG, "onSuccess: ");
//                                               holder.iv_freelancer_profile.setTag(cmWorker.getWorkerPhone());
                                            }

                                            @Override
                                            public void onError(Exception e) {

                                            }
                                        });
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        holder.tvComment.setText(comment.getComment() + "   AED");


        switch (userType) {
            case Constants.CM:

                break;
            case Constants.FREELANCER:
                holder.btnSendComment.setVisibility(View.GONE);

                break;
            case Constants.USER:
//                holder.btnSendComment.setVisibility(View.VISIBLE);
                if (exist == Constants.EXIST) {
                    Log.e(TAG, "ppppppppp: ");
                    if (comment.isSelected()) {
                        holder.btnPrev.setVisibility(View.VISIBLE);
                        holder.btnCall.setVisibility(View.VISIBLE);
                        holder.btnSendComment.setVisibility(View.GONE);
                        //........
                        holder.ivDone.setVisibility(View.VISIBLE);

                        Log.e(TAG, "hhhhh: ");
                    } else {
                        holder.btnPrev.setVisibility(View.GONE);
                        holder.btnCall.setVisibility(View.GONE);
                        holder.btnSendComment.setVisibility(View.GONE);
                        Log.e(TAG, "jjjjj: ");
                    }

                } else {
                    Log.e(TAG, "cancel check: Adapter" + cancel);

                    //check is canceled or not to solve notification problem
                    if (cancel) {
                        holder.btnSendComment.setVisibility(View.GONE);
                        Log.e(TAG, "eeeeeeee 2: ");

                    } else {
                        holder.btnSendComment.setVisibility(View.VISIBLE);
                        Log.e(TAG, "eeeeeeee 1 ");
                    }


                }

                break;
        }


        YoYo.with(Techniques.FadeIn)
                .duration(600)
                .playOn(holder.itemView);


    }

    @Override
    public int getItemCount() {
        return listComment.size();
    }

    public interface OnAcceptClicked {
        void OnAcceptClicked(Comment comment, int pos);
    }

    public interface OnCallClicked {
        void OnCallClicked(Comment comment, int pos);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvComment;
        TextView tv_worker_name;
        Button btnSendComment;
        Button btnPrev;
        Button btnCall;
        CircularImageView iv_freelancer_profile;
        ImageView ivDone;

        MyViewHolder(View view) {
            super(view);
            tvComment = view.findViewById(R.id.tv_comment);
            btnSendComment = view.findViewById(R.id.btn_accept);
            btnPrev = view.findViewById(R.id.btn_prev);
            btnCall = view.findViewById(R.id.btn_call);
            iv_freelancer_profile = view.findViewById(R.id.iv_freelancer_profile);
            tv_worker_name = view.findViewById(R.id.tv_worker_name);
            ivDone = view.findViewById(R.id.ivDone);

        }
    }
}