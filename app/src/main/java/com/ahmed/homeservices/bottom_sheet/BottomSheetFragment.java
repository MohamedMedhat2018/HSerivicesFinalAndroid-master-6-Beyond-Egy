package com.ahmed.homeservices.bottom_sheet;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ahmed.homeservices.R;
import com.ahmed.homeservices.constants.Constants;
import com.ahmed.homeservices.models.CMWorker;
import com.andrefrsousa.superbottomsheet.SuperBottomSheetFragment;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.firebase.database.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BottomSheetFragment extends SuperBottomSheetFragment {

    private static final String TAG = "BottomSheetFragment";

    @BindView(R.id.ivUserPhoto)
    CircularImageView ivUserPhoto;

    @BindView(R.id.tvArName)
    TextView tvArName;

    @BindView(R.id.tvEnName)
    TextView tvEnName;

    @BindView(R.id.tvEmail)
    TextView tvEmail;


    private boolean mShowingFragments = false;

    public BottomSheetFragment() {
        setCancelable(true);

    }

    public static float dpToPixels(int dp, Context context) {
        return dp * (context.getResources().getDisplayMetrics().density);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "onStart: ");
//        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e(TAG, "onStop: ");
//        EventBus.getDefault().unregister(this);
    }

    //Dependence injection (DI) check massage has error from Model
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onSmsCodeError(MsgEvevntErrorSms msgEvevntErrorSms) {
//        if (msgEvevntErrorSms.isErrorSmsCode()) {
//            tvError.setText("Invalid sms code !");
//        } else {
//
//        }
//    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater,
                             @org.jetbrains.annotations.Nullable ViewGroup container,
                             @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View viewRoot;
//        viewRoot = inflater.inflate(R.layout.fragment_user_worker_profile, container, false);
        viewRoot = inflater.inflate(R.layout.layout_free_info_sheet, container, false);
        ButterKnife.bind(BottomSheetFragment.this, viewRoot);
        return viewRoot;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            if (getArguments().containsKey(Constants.CM_WORKER)) {
                CMWorker cmWorker = (CMWorker) getArguments().getSerializable(Constants.CM_WORKER);
                if (cmWorker != null) {
                    tvEmail.setText(cmWorker.getWorkerEmail());
                    tvArName.setText(cmWorker.getWorkerNameInArabic());
                    tvEnName.setText(cmWorker.getWorkerNameInEnglish());
                }
            }
//            if (getArguments().containsKey(Constants.FREE_PHOTO_URL)) {
//                Picasso.get()
//                        .load(getArguments().getString(Constants.FREE_PHOTO_URL))
//                        .into(ivUserPhoto, new Callback() {
//                            @Override
//                            public void onSuccess() {
//                                Log.e(TAG, "onSuccess: ");
//                            }
//
//                            @Override
//                            public void onError(Exception e) {
//
//                            }
//                        });
//            }


        }


    }


    @Override
    public int getPeekHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
//        int width = displayMetrics.widthPixels;
        int halfOfScreen = height * 2 / 3;
        return halfOfScreen;
    }

    @Override
    public float getCornerRadius() {
        return getResources().getDimension(R.dimen.demo_sheet_rounded_corner);
    }

    @Override
    public int getStatusBarColor() {
        return super.getStatusBarColor();
    }

    @Override
    public int getBackgroundColor() {
        return super.getBackgroundColor();
    }

    @Override
    public float getDim() {
        return super.getDim();
    }


}