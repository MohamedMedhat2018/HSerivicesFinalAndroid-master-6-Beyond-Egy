package com.ahmed.homeservices.adapters.rv.comments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ahmed.homeservices.R;
import com.ahmed.homeservices.models.Comment;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.siyamed.shapeimageview.CircularImageView;

import java.util.List;

public class CommentFreelancerAdapter extends RecyclerView.Adapter<CommentFreelancerAdapter.MyViewHolder> {
    private static final String TAG = "CommentAdapter";
    private Context context;
    private List<Comment> listComment;
    private OnAcceptClicked onAcceptClicked;
    private String userType;
    private String exist;

    public CommentFreelancerAdapter(Context context, List<Comment> listComment) {
        this.context = context;
        this.listComment = listComment;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_item_freeelancer, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Comment comment = listComment.get(position);
        holder.tvComment.setText(comment.getComment() + "   AED" );
//        if (exist == Constants.EXIST) {
//            Log.e(TAG, "ppppppppp: ");
//            if (comment.isSelected()) {
//                holder.btnPrev.setVisibility(View.VISIBLE);
//                holder.btnSendComment.setVisibility(View.GONE);
//                Log.e(TAG, "hhhhh: ");
//            } else {
//                holder.btnPrev.setVisibility(View.GONE);
//                holder.btnSendComment.setVisibility(View.GONE);
//                Log.e(TAG, "jjjjj: ");
//            }
//
//        } else {
//            holder.btnSendComment.setVisibility(View.VISIBLE);
//            Log.e(TAG, "eeeeeeee: ");
//        }
//
//        holder.btnPrev.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // send selected contact in callback
//                //Log.e(TAG, "onClick: " + holder.tvComment.getText().toString());
//                onAcceptClicked.OnAcceptClicked(comment, position);
//                //Toasty.success(context, "Accept "  + comment.getComment()).show();
//            }
//        });
//        holder.btnSendComment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // send selected contact in callback
//                //Log.e(TAG, "onClick: " + holder.tvComment.getText().toString());
//                onAcceptClicked.OnAcceptClicked(comment, position);
//                //Toasty.success(context, "Accept "  + comment.getComment()).show();
//            }
//        });


//        RefBase.refWorker(comment.getFreelancerId())
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        if (dataSnapshot.exists()) {
//                            dataSnapshot.getRef().removeEventListener(this);
//                            CMWorker cmWorker = dataSnapshot.getValue(CMWorker.class);
////                                                tvFullName.setText(cmWorker.getWorkerNameInArabic());
////                                                tvEmail.setText(cmWorker.getWorkerEmail());
//                            Log.e(TAG, "onDataChange: " + cmWorker.getWorkerEmail());
//                            Log.e(TAG, "onDataChange: " + cmWorker.getWorkerNameInEnglish());
//                            Log.e(TAG, "onDataChange: " + cmWorker.getWorkerPhoto());
//
//                            Picasso.get()
//                                    .load(cmWorker.getWorkerPhoto())
//                                    .into(holder.iv_freelancer_profile, new Callback() {
//                                        @Override
//                                        public void onSuccess() {
//
//                                        }
//
//                                        @Override
//                                        public void onError(Exception e) {
//
//                                        }
//                                    });
//
//
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });

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

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvComment;
        Button btnSendComment;
        Button btnPrev;
        CircularImageView iv_freelancer_profile;

        MyViewHolder(View view) {
            super(view);
            tvComment = view.findViewById(R.id.tv_comment);
            btnSendComment = view.findViewById(R.id.btn_accept);
            btnPrev = view.findViewById(R.id.btn_prev);
            iv_freelancer_profile = view.findViewById(R.id.iv_freelancer_profile);


        }
    }
}