package com.ahmed.homeservices.bottom_sheet;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ahmed.homeservices.R;
import com.ahmed.homeservices.constants.Constants;
import com.ahmed.homeservices.fire_utils.RefBase;
import com.ahmed.homeservices.utils.Utils;
import com.andrefrsousa.superbottomsheet.SuperBottomSheetFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.annotations.NotNull;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BottomFrgForgotPass extends SuperBottomSheetFragment {

    private static final String TAG = "BottomSheetFragment";
    @BindView(R.id.etNewPass)
    EditText etNewPass;
    @BindView(R.id.etConfNewPass)
    EditText etConfNewPass;
    private boolean mShowingFragments = false;
    private String phoneNumber;

    public BottomFrgForgotPass() {
        setCancelable(true);

    }

    public static float dpToPixels(int dp, Context context) {
        return dp * (context.getResources().getDisplayMetrics().density);
    }

    @OnClick(R.id.btnUpdatePassword)
    void btnUpdatePassword(View v) {


        if (TextUtils.isEmpty(etNewPass.getText())) {
            Utils.startWobble(getActivity(), etNewPass);
            etNewPass.setError(getString(R.string.enter_pass));
            return;
        }

        if (!Utils.isValidPassword(etNewPass.getText().toString().trim())) {
            Utils.startWobble(getActivity(), etNewPass);
            etNewPass.setError(getString(R.string.enter_6_chars));
            return;
        }

        if (!TextUtils.equals(etNewPass.getText(), etConfNewPass.getText())) {
            Utils.startWobble(getActivity(), etNewPass);
            etConfNewPass.setError(getString(R.string.error_pass_donot_match));
            return;
        }

        if (getArguments() != null) {
            if (getArguments().containsKey(Constants.WORKER)) {
//                CMWorker cmWorker = (CMWorker) getArguments().getSerializable(Constants.WORKER);
//                if (cmWorker != null) {
////                    tvEmail.setText(cmWorker.getWorkerEmail());
////                    tvArName.setText(cmWorker.getWorkerNameInArabic());
////                    tvEnName.setText(cmWorker.getWorkerNameInEnglish());
//
//
//                }
            }
            if (getArguments().containsKey(Constants.PHONE)) {
                phoneNumber = getArguments().getString(Constants.PHONE);
//                User cmWorker = (User) getArguments().getSerializable(Constants.WORKER);
//                if (cmWorker != null) {
//                }
                DatabaseReference ref = null;
                if (Utils.customerOrWorker()) {
//                    ref = RefBase.refUser(Prefs.getString(Constants.FIREBASE_UID_FORGOT_PASS, ""));
//                    ref.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            //stop listener
//                            dataSnapshot.getRef().removeEventListener(this);
//                            if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
//                                User user = dataSnapshot.getValue(User.class);
//                                user.setUserPassword(etNewPass.getText().toString());
//
//                                //i'm in the same referance
//                                dataSnapshot.getRef().setValue(user);
//                                dismiss();
//
//                                Toast.makeText(getActivity(), "Updated", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                        }
//                    });


                    HashMap<String, Object> map = new HashMap<>();
                    map.put(Constants.PASSWORD, etNewPass.getText().toString());
                    Log.e(TAG, "btnUpdatePassword: " + Prefs.getString(Constants.FIREBASE_UID_FORGOT_PASS, ""));
                    RefBase.refUser(Prefs.getString(Constants.FIREBASE_UID_FORGOT_PASS, ""))
                            .updateChildren(map)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        dismiss();
                                        Toast.makeText(getActivity(), getString(R.string.password_updated), Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                    Prefs.edit().remove(Constants.FIREBASE_UID_FORGOT_PASS).apply();
                    Prefs.remove(Constants.FIREBASE_UID_FORGOT_PASS);


                } else {
                    ref = RefBase.refWorker(Prefs.getString(Constants.FIREBASE_UID_FORGOT_PASS, ""));
//                    ref.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            dataSnapshot.getRef().removeEventListener(this);
//                            if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
//                                //stop listener
////                                dataSnapshot.getRef().removeEventListener(this);
//                                CMWorker user = dataSnapshot.getValue(CMWorker.class);
//                                user.setWorkerPassword(etNewPass.getText().toString());
//
//                                //i'm in the same referance
//                                dataSnapshot.getRef().setValue(user);
//                                dismiss();
//
//                                Toast.makeText(getActivity(), "Updated", Toast.LENGTH_SHORT).show();
//
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                        }
//                    });


                    HashMap<String, Object> map = new HashMap<>();
                    map.put(Constants.WORKER_PASSWORD, etNewPass.getText().toString());
                    Log.e(TAG, "btnUpdatePassword: " + Prefs.getString(Constants.FIREBASE_UID_FORGOT_PASS, ""));
                    RefBase.refWorker(Prefs.getString(Constants.FIREBASE_UID_FORGOT_PASS, ""))
                            .updateChildren(map)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        dismiss();
                                        Toast.makeText(getActivity(), getString(R.string.password_updated), Toast.LENGTH_SHORT).show();
//                                        Prefs.edit().remove(Constants.FIREBASE_UID_FORGOT_PASS).apply();
                                    }
                                }
                            });
                    Prefs.edit().remove(Constants.FIREBASE_UID_FORGOT_PASS).apply();
                    Prefs.remove(Constants.FIREBASE_UID_FORGOT_PASS);


                }

            }
        }

    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        Log.e(TAG, "onDismiss: ");

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "onStart: ");
//        EventBus.getDefault().register(this);
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
    public void onStop() {
        super.onStop();
        Log.e(TAG, "onStop: ");
//        EventBus.getDefault().unregister(this);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater,
                             @org.jetbrains.annotations.Nullable ViewGroup container,
                             @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View viewRoot;
//        viewRoot = inflater.inflate(R.layout.fragment_user_worker_profile, container, false);
        viewRoot = inflater.inflate(R.layout.layout_forgot_pass, container, false);
        ButterKnife.bind(BottomFrgForgotPass.this, viewRoot);
        return viewRoot;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


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


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: ");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e(TAG, "onDestroyView: ");

    }
}