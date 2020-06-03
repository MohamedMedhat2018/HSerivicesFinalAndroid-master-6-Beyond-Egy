package com.ahmed.homeservices.activites.cutomer_or_worker.login;

import android.animation.Animator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ahmed.homeservices.R;
import com.ahmed.homeservices.activites.cutomer_or_worker.CustomerOrWorkerActivity;
import com.ahmed.homeservices.activites.cutomer_or_worker.register.CompanyRegisterActivity;
import com.ahmed.homeservices.activites.cutomer_or_worker.register.CustomerRegisterActivity;
import com.ahmed.homeservices.activites.cutomer_or_worker.register.WorkerRegisterActivity;
import com.ahmed.homeservices.activites.meowbottomnavigaion.MainActivity;
import com.ahmed.homeservices.activites.sms.EnterSmsCodeActivity;
import com.ahmed.homeservices.bottom_sheet.BottomFrgForgotPass;
import com.ahmed.homeservices.common.Common;
import com.ahmed.homeservices.config.AppConfig;
import com.ahmed.homeservices.constants.Constants;
import com.ahmed.homeservices.fire_utils.RefBase;
import com.ahmed.homeservices.interfaces.OnLoggedOut;
import com.ahmed.homeservices.interfaces.password.OnPasswordDecrypted;
import com.ahmed.homeservices.internet_checker.ConnectivityReceiver;
import com.ahmed.homeservices.messages.MsgFrmLoginToNotification;
import com.ahmed.homeservices.messages.MsgFromLoginToFragment;
import com.ahmed.homeservices.models.CMWorker;
import com.ahmed.homeservices.models.Company;
import com.ahmed.homeservices.models.PhoneMaterials;
import com.ahmed.homeservices.models.PhoneRegistered;
import com.ahmed.homeservices.models.User;
import com.ahmed.homeservices.phone_utils.PhoneUtils;
import com.ahmed.homeservices.utils.Utils;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.Task;
import com.google.common.reflect.TypeToken;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.pixplicity.easyprefs.library.Prefs;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;
import uk.co.deanwild.materialshowcaseview.ShowcaseTooltip;

import static com.ahmed.homeservices.constants.Constants.STATE_CODE_SENT;
import static com.ahmed.homeservices.constants.Constants.STATE_INITIALIZED;
import static com.ahmed.homeservices.constants.Constants.STATE_SIGNIN_FAILED;
import static com.ahmed.homeservices.constants.Constants.STATE_SIGNIN_SUCCESS;
import static com.ahmed.homeservices.constants.Constants.STATE_VERIFY_FAILED;
import static com.ahmed.homeservices.constants.Constants.STATE_VERIFY_SUCCESS;

//import com.coderfolk.multilamp.customView.MultiLamp;
//import com.coderfolk.multilamp.model.Target;
//import com.coderfolk.multilamp.shapes.Circle;
//import com.coderfolk.multilamp.shapes.Rectangle;
//import com.coderfolk.multilamp.shapes.Shape;

public class CustomerWorkerLoginActivity extends AppCompatActivity implements
        ConnectivityReceiver.ConnectivityReceiverListener, OnPasswordDecrypted {
    private static final String TAG = "CustomerWorkerLogin";
    private static final int RE_CODE_FORGOT_PASS = 3223;
    private static final int RE_CODE_LOGGED_FROM_ANOTHER_DEVICE = 2222;

    @BindView(R.id.etEnterPhone)
    EditText etEnterPhone;
    @BindView(R.id.llPhone)
    LinearLayout llPhone;
    //    MaskedEditText etEnterPhone;
    @BindView(R.id.etEnterPass)
    EditText etEnterPass;
    @BindView(R.id.tvLogin)
    LinearLayout tvLogin;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.ivShowHidePass)
    ImageView ivShowHidePass;
    AlertDialog dialogProgress;
    User user = new User();
    CMWorker cmWorker = new CMWorker();
    //must pair with lifecyle of activity
    ConnectivityReceiver connectivityReceiver = new ConnectivityReceiver();
    FirebaseAuth firebaseAuth;
    //    AlertDialog dlgProgress;
    PhoneMaterials phoneMaterials = new PhoneMaterials();
    Gson gson = new Gson();
    int duration = 600;
    boolean isChecked = true;
    @BindView(R.id.llPassword)
    View llPassword;
    boolean autoRetrieval = false;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private boolean forgotOrNewUserForInstId = false;
    private boolean flagLoggedFromAnotherDevice = false;

    @OnClick(R.id.tvRegister)
    public void tvRegisterNewUser(View v) {
        finish();
        Intent intent = new Intent();

        if (!Utils.companyOrNot()) {
            if (Utils.customerOrWorker()) {
                intent.setClass(this, CustomerRegisterActivity.class);
            } else {
                intent.setClass(this, WorkerRegisterActivity.class);
            }
        } else {
            intent.setClass(this, CompanyRegisterActivity.class);
        }
        startActivity(intent);
    }

    @OnClick(R.id.ivShowHidePass)
    public void showHidePassword() {
        if (!etEnterPass.getText().toString().isEmpty()) {
            if (isChecked) {
                ivShowHidePass.setImageResource(R.drawable.ic_visibility);
//                etEnterPass.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                etEnterPass.setTransformationMethod(null);
//                etEnterPass.setText("zoooz");
                Log.e(TAG, "showHidePassword: 123");
                isChecked = false;
            } else {
                ivShowHidePass.setImageResource(R.drawable.ic_visibility_off);
                etEnterPass.setTransformationMethod(new PasswordTransformationMethod());
                isChecked = true;
                Log.e(TAG, "showHidePassword: 1234");
            }

        }
    }

    @OnClick(R.id.btn_login)
    public void btnLoginClicked(View v) {
        YoYo.with(Techniques.ZoomIn)
                .duration(200)
                .onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        llPassword.setVisibility(View.VISIBLE);
                    }
                })
                .playOn(etEnterPass);

//        llPassword.setVisibility(View.VISIBLE);

        if (checkInfo()) {
            return;
        }
        //??
        //for saving the phone to the user
        Prefs.edit().putString(Constants.LOGIN_PHONE, etEnterPhone.getText().toString()).apply();

        if (!Common.checkConnection(this)) {
            Log.e(TAG, "button_verify_phone: disconnected");
            Common.showSnackError(this, false);
            return;
        }

        dialogProgress = Utils.getInstance().pleaseWait(CustomerWorkerLoginActivity.this);
        //else
        dialogProgress.show();
        //??
        Query query = RefBase.registerPhone()
                .orderByChild(Constants.PHONE_FROM_USER_MODEL)
                .equalTo(etEnterPhone.getText().toString().trim());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().removeEventListener(this);
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Log.e(TAG, "exists: ");
                    query.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            dataSnapshot.getRef().removeEventListener(this);
                            if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                                PhoneRegistered phoneRegistered = dataSnapshot.getValue(PhoneRegistered.class);
                                Log.e(TAG, "onDataChange: " + phoneRegistered.getCustomerId());
                                //check if query for this child is exist mean there is registers users with this sms number (there are child in REG_PHONES)
                                if (Utils.customerOrWorker()) {
                                    RefBase.refUser(phoneRegistered.getCustomerId())
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    dataSnapshot.getRef().removeEventListener(this);
                                                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                                                        user = dataSnapshot.getValue(User.class);
                                                        if (!user.isActiviationState()) {
                                                            dialogProgress.dismiss();
                                                            new AlertDialog.Builder(CustomerWorkerLoginActivity.this)
                                                                    .setMessage(getString(R.string.you_are_deactivated))
                                                                    .setTitle(getString(R.string.app_name_root))
                                                                    .setCancelable(false)
                                                                    .setPositiveButton(getString(R.string.log_out), (dialog, which) -> {
                                                                                Common.logout(new OnLoggedOut() {
                                                                                    @Override
                                                                                    public void onLoggedOutSuccess() {
                                                                                        Log.e(TAG, "3333 ");
                                                                                        finish();
                                                                                        startActivity(new Intent(getApplicationContext(), CustomerOrWorkerActivity.class));
//                                                                                        Toasty.warning(getApplicationContext(), R.string.Please_log_in_again, Toasty.LENGTH_SHORT).show();
                                                                                    }

                                                                                    @Override
                                                                                    public void onLoggedOutFailed() {
                                                                                    }
                                                                                });

                                                                            }
                                                                    )
                                                                    .show();
                                                            return;
                                                        }


//                                                        String decPassword = "";
//                                                        try {
//                                                            decPassword = Utils.decrypt(user.getUserPassword().trim());
//                                                            Log.e(TAG, "zzzzzzzzzzzzzzzoz" + decPassword);
//                                                        } catch (Exception e) {
//                                                            Log.e(TAG, "zzzzzzzzzzzzzzzoz" + e.getMessage());
//                                                            e.printStackTrace();
//                                                        }


                                                        Log.e(TAG, Utils.customerOrWorker() + "");
                                                        if (!TextUtils.equals(user.getUserPassword(),
                                                                etEnterPass.getText().toString().trim())) {

                                                            etEnterPhone.setError(null);// for clearing the error icon
                                                            etEnterPass.setError(Constants.INVALID_PASS);
                                                            Utils.startWobble(getApplicationContext(), etEnterPass);
                                                            dialogProgress.dismiss();
                                                            Log.e(TAG, "FIREBASE_UID:3 " +
                                                                    Prefs.getString(Constants.FIREBASE_UID, ""));
                                                            Toasty.error(getApplicationContext(), Constants.INVALID_PASS).show();
                                                        } else {
                                                            if (!user.isLogin()) {
                                                                Log.e(TAG, "onDataChange_3737");
                                                                Prefs.edit().putString(Constants.FIREBASE_UID,
                                                                        user.getUserId()).apply();
                                                                FirebaseInstanceId.getInstance()
                                                                        .getInstanceId()
                                                                        .addOnCompleteListener(task -> {
                                                                            if (task.isSuccessful()) {
                                                                                String token = updateMessageToken(task);
                                                                                HashMap<String, Object> map = new HashMap<>();
                                                                                map.put(Constants.MESSAGE_TOKEN, token);
                                                                                map.put(Constants.LOGIN, true);
                                                                                Log.e(TAG, "onComplete: " + Constants.LOGIN);
                                                                                RefBase.refUser(Prefs.getString(Constants.FIREBASE_UID, ""))
                                                                                        .getRef()
                                                                                        .updateChildren(map)
                                                                                        .addOnCompleteListener(task1 -> {
                                                                                            if (task1.isSuccessful()) {
                                                                                                dialogProgress.dismiss();
                                                                                                Log.e(TAG, "onDataChange: hero ");
                                                                                                EventBus.getDefault().removeAllStickyEvents();
                                                                                                EventBus.getDefault().postSticky(new MsgFrmLoginToNotification());
                                                                                                EventBus.getDefault().postSticky(new MsgFromLoginToFragment());
                                                                                                //EventBus.getDefault().post(new MsgEventReloadFragment(true));
                                                                                                goToHmePage();
                                                                                            }
                                                                                        });

                                                                            }
                                                                        });

                                                            } else {
                                                                Log.e(TAG, "onDataChange: you are already login ");
                                                                dialogProgress.dismiss();
                                                                if (!isFinishing()) {
                                                                    new AlertDialog.Builder(CustomerWorkerLoginActivity.this)
                                                                            .setTitle(getString(R.string.app_name_root))
                                                                            .setMessage(R.string.login_from_another_device)
                                                                            .setCancelable(false)
                                                                            .setPositiveButton(R.string.yes, (dialog, which) -> {
                                                                                Log.e(TAG, "onClick: Yes ");
                                                                                dialogProgress.show();
                                                                                forgotOrNewUserForInstId = false;
                                                                                flagLoggedFromAnotherDevice = true;
                                                                                checkPhone();

                                                                            })
                                                                            .setNegativeButton(R.string.no, (dialog, which) -> Log.e(TAG, "onClick: No ")).show();
                                                                }
                                                            }
                                                        }
                                                    } else {
                                                        dialogProgress.dismiss();
                                                        etEnterPhone.setError(Constants.INVALID_PHONE);
                                                        Toasty.error(getApplicationContext(), Constants.INVALID_PHONE).show();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                } else {
                                    if (Utils.companyOrNot()) {
//                                        Toast.makeText(CustomerWorkerLoginActivity.this, "Companies", Toast.LENGTH_SHORT).show();
                                        Log.e(TAG, Utils.customerOrWorker() + "test company login5");
                                        RefBase.refCompany(phoneRegistered.getCustomerId())
                                                .addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        dataSnapshot.getRef().removeEventListener(this);
                                                        if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
//                                                            cmWorker = dataSnapshot.getValue(CMWorker.class);
                                                            company = dataSnapshot.getValue(Company.class);

                                                            //if (!cmWorker.isWorkerStatusActivation()) {
                                                            if (false) {
                                                                dialogProgress.dismiss();
                                                                Log.e(TAG, Utils.customerOrWorker() + "test company login4");
                                                                new AlertDialog.Builder(CustomerWorkerLoginActivity.this)
                                                                        .setMessage(getString(R.string.you_are_deactivated))
                                                                        .setTitle(getString(R.string.app_name_root))
                                                                        .setCancelable(false)
                                                                        .setPositiveButton(getString(R.string.ok), (dialog, which) -> {
                                                                                    dialog.dismiss();
                                                                                }
                                                                        )
                                                                        .show();
                                                                return;
                                                            }
//                                                        Prefs.edit().putString(Constants.FIREBASE_UID, cmWorker.getWorkerId()).apply();
                                                            Log.e(TAG, Utils.customerOrWorker() + "test company login3");
//                                                            switch (cmWorker.getWorkerType()) {
//                                                                case Constants.CM:
////                                                                Toasty.success(getApplicationContext(), Constants.CM).show();
////                                                                Toasty.success(getApplicationContext(), Utils.setFirstUpperCharOfWord(Constants.CM)).show();
//                                                                    Prefs.edit().putString(Constants.WORKER_LOGGED_AS, Constants.CM).apply();
//                                                                    break;
//                                                                case Constants.FREELANCER:
////                                                                Toasty.success(getApplicationContext(), Utils.setFirstUpperCharOfWord(Constants.FREELANCER)).show();
////                                                                Toasty.success(getApplicationContext(), Constants.FREELANCER).show();
//                                                                    Prefs.edit().putString(Constants.WORKER_LOGGED_AS, Constants.FREELANCER).apply();
//                                                                    break;
//                                                            }

//                                                        new AsyncPasswordDecrypt(CustomerWorkerLoginActivity.this)
//                                                                .execute(cmWorker.getWorkerPassword().trim());
                                                            onPasswordDecrypted(company.getCompanyPassword());

                                                        } else {
                                                            dialogProgress.dismiss();
                                                            etEnterPhone.setError(Constants.INVALID_PHONE);
                                                            Toasty.error(getApplicationContext(), Constants.INVALID_PHONE).show();
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });


                                    } else {
                                        RefBase.refWorker(phoneRegistered.getCustomerId())
                                                .addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        dataSnapshot.getRef().removeEventListener(this);
                                                        if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                                                            cmWorker = dataSnapshot.getValue(CMWorker.class);
                                                            //if (!cmWorker.isWorkerStatusActivation()) {
                                                            if (false) {
                                                                dialogProgress.dismiss();
                                                                Log.e(TAG, Utils.customerOrWorker() + "test company login2 ");
                                                                new AlertDialog.Builder(CustomerWorkerLoginActivity.this)
                                                                        .setMessage(getString(R.string.you_are_deactivated))
                                                                        .setTitle(getString(R.string.app_name_root))
                                                                        .setCancelable(false)
                                                                        .setPositiveButton(getString(R.string.ok), (dialog, which) -> {
                                                                                    dialog.dismiss();
                                                                                }
                                                                        )
                                                                        .show();
                                                                return;
                                                            }
//                                                        Prefs.edit().putString(Constants.FIREBASE_UID, cmWorker.getWorkerId()).apply();
                                                            Log.e(TAG, Utils.customerOrWorker() + "test company login");
//                                                            switch (cmWorker.getWorkerType()) {
//                                                                case Constants.CM:
////                                                                Toasty.success(getApplicationContext(), Constants.CM).show();
////                                                                Toasty.success(getApplicationContext(), Utils.setFirstUpperCharOfWord(Constants.CM)).show();
//                                                                    Prefs.edit().putString(Constants.WORKER_LOGGED_AS, Constants.CM).apply();
//                                                                    break;
//                                                                case Constants.FREELANCER:
////                                                                Toasty.success(getApplicationContext(), Utils.setFirstUpperCharOfWord(Constants.FREELANCER)).show();
////                                                                Toasty.success(getApplicationContext(), Constants.FREELANCER).show();
//                                                                    Prefs.edit().putString(Constants.WORKER_LOGGED_AS, Constants.FREELANCER).apply();
//                                                                    break;
//                                                            }

//                                                        new AsyncPasswordDecrypt(CustomerWorkerLoginActivity.this)
//                                                                .execute(cmWorker.getWorkerPassword().trim());
                                                            onPasswordDecrypted(cmWorker.getWorkerPassword());

                                                        } else {
                                                            dialogProgress.dismiss();
                                                            etEnterPhone.setError(Constants.INVALID_PHONE);
                                                            Toasty.error(getApplicationContext(), Constants.INVALID_PHONE).show();
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                    }


                                }

                            } else {
                                //Toasty.error(getApplicationContext(),
//                                        Constants.PHONE_EXIST).show();
//                                Utils.clearAllErrors(findViewById(android.R.id.content));
//                                etEnterPhone.setError(Constants.PHONE_EXIST);
//                                dialogProgress.dismiss();
                            }
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot
                                                           dataSnapshot, @Nullable String s) {
                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                } else {
                    dialogProgress.dismiss();
                    Log.e(TAG, " not exists: ");
                    etEnterPhone.setError(Constants.INVALID_PHONE);
//                    Sneaker.with((Activity) getApplicationContext()).setCategoryName("Invalid Phone Number !").sneakError();
                    Toasty.error(getApplicationContext(), Constants.INVALID_PHONE).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
            }
        });


    }

    private Company company = new Company();


    @NotNull
    private String updateMessageToken(Task<InstanceIdResult> task) {
        String token = task.getResult().getToken();
        Prefs.edit().putString(Constants.MESSAGE_TOKEN, token).apply();
        return token;
    }

    private boolean isEmpty(Editable text) {
        return TextUtils.isEmpty(text.toString());
    }

    //check the required fields
    private boolean checkInfo() {
        if (isEmpty(etEnterPhone.getText())) {
            //start anim ??
            Utils.startWobble(getApplicationContext(), etEnterPhone);
//            etPhoneLogin.setError("Enter email address");
            etEnterPhone.setError(getString(R.string.enter_phone));
            return true;
        }

        if (isEmpty(etEnterPass.getText())) {
            //start anim
            Utils.startWobble(getApplicationContext(), etEnterPass);
            etEnterPass.setError(getString(R.string.enter_pass));
            return true;
        }
        return false;
    }

    @OnClick(R.id.tvForgetPass)
    public void tvForgetPass(View v) {
        if (isEmpty(etEnterPhone.getText())) {
            //start anim
            Utils.startWobble(getApplicationContext(), etEnterPhone);
//            etPhoneLogin.setError("Enter email address");
            etEnterPhone.setError(getString(R.string.enter_phone));
        } else {
//            YoYo.with(Techniques.ZoomOut)
//                    .duration(duration)
//                    .onEnd(new YoYo.AnimatorCallback() {
//                        @Override
//                        public void call(Animator animator) {
//                            Prefs.edit().putString(Constants.LOGIN_PHONE, etEnterPhone.getText().toString()).apply();
////                            etEnterPass.setVisibility(View.GONE);
//                            llPassword.setVisibility(View.GONE);
//                            forgotOrNewUserForInstId = true;
//                            forgotPassword();
//                        }
//                    })
//                    .playOn(etEnterPass);


            Prefs.edit().putString(Constants.LOGIN_PHONE, etEnterPhone.getText().toString()).apply();
//                            etEnterPass.setVisibility(View.GONE);
//            llPassword.setVisibility(View.GONE);

            //activate forget ot new user status for sms activity purpose
            forgotOrNewUserForInstId = true;

            forgotPassword();


        }

    }

//    private void forgotPassword2() {
//
//        if (isEmpty(etEnterPhone.getText())) {
//            //start anim
//            Utils.startWobble(getApplicationContext(), etEnterPhone);
////            etPhoneLogin.setError("Enter email address");
//            etEnterPhone.setError(getString(R.string.enter_phone));
//        } else {
//
//        }
//    }

    private void forgotPassword() {
        dialogProgress.show();

        //check if the phone number exists or not
        Query query = RefBase.registerPhone()
                .orderByChild(Constants.PHONE_FROM_USER_MODEL)
                .equalTo(etEnterPhone.getText().toString().trim());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().removeEventListener(this);
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Log.e(TAG, "exists: ");

                    //get the first phone registered model
                    query.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            dataSnapshot.getRef().removeEventListener(this);
                            if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                                PhoneRegistered phoneRegistered = dataSnapshot.getValue(PhoneRegistered.class);
                                Log.e(TAG, "onDataChange: " + phoneRegistered.getCustomerId());
                                //check if query for this child is exist mean there is registers users with this sms number (there are child in REG_PHONES)
                                if (Utils.customerOrWorker()) {
                                    //fetch the user info from firebae into shared prefs
                                    RefBase.refUser(phoneRegistered.getCustomerId())
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    dataSnapshot.getRef().removeEventListener(this);
                                                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                                                        User user = dataSnapshot.getValue(User.class);
                                                        assert user != null;
                                                        Prefs.edit().putString(Constants.FIREBASE_UID,
                                                                user.getUserId()).apply();

                                                        Log.e(TAG, Utils.customerOrWorker() + "");
                                                        dialogProgress.dismiss();
                                                        ///.........
                                                        checkPhone();
                                                    } else {
                                                        dialogProgress.dismiss();
                                                        etEnterPhone.setError(Constants.INVALID_PHONE);
                                                        Toasty.error(getApplicationContext(), Constants.INVALID_PHONE).show();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                } else {
                                    //fetch the worker info from the firebase database into shared pref for the future usage
                                    RefBase.refWorker(phoneRegistered.getCustomerId())
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    dataSnapshot.getRef().removeEventListener(this);
                                                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                                                        CMWorker cmWorker = dataSnapshot.getValue(CMWorker.class);
                                                        assert cmWorker != null;
                                                        Prefs.edit().putString(Constants.FIREBASE_UID, cmWorker.getWorkerId()).apply();
                                                        Log.e(TAG, Utils.customerOrWorker() + "");
                                                        Prefs.edit().putString(Constants.WORKER_LOGGED_AS, Constants.FREELANCER).apply();
//                                                        switch (cmWorker.getWorkerType()) {
////                                                            case Constants.CM:
//////                                                                Toasty.success(getApplicationContext(), Constants.CM).show();
//////                                                                Toasty.success(getApplicationContext(), Utils.setFirstUpperCharOfWord(Constants.CM)).show();
////                                                                Prefs.edit().putString(Constants.WORKER_LOGGED_AS, Constants.CM).apply();
////                                                                break;
//                                                            case Constants.FREELANCER:
////                                                                Toasty.success(getApplicationContext(), Utils.setFirstUpperCharOfWord(Constants.FREELANCER)).show();
////                                                                Toasty.success(getApplicationContext(), Constants.FREELANCER).show();
//                                                                Prefs.edit().putString(Constants.WORKER_LOGGED_AS, Constants.FREELANCER).apply();
//                                                                break;
//                                                        }
                                                        dialogProgress.dismiss();
                                                        //............
                                                        checkPhone();
                                                    } else {
                                                        dialogProgress.dismiss();
                                                        etEnterPhone.setError(Constants.INVALID_PHONE);
                                                        Toasty.error(getApplicationContext(), Constants.INVALID_PHONE).show();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                }
                            } else {
//                                dialogProgress.dismiss();
//                                Log.e(TAG, " not exists: ");
//                                etEnterPhone.setError(Constants.INVALID_PHONE);
////                    Sneaker.with((Activity) getApplicationContext()).setCategoryName("Invalid Phone Number !").sneakError();
//                                Toasty.error(getApplicationContext(), Constants.INVALID_PHONE).show();
                            }
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot
                                                           dataSnapshot, @Nullable String s) {
                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                } else {
                    dialogProgress.dismiss();
                    Log.e(TAG, " not exists: ");
                    etEnterPhone.setError(Constants.INVALID_PHONE);
//                    Sneaker.with((Activity) getApplicationContext()).setCategoryName("Invalid Phone Number !").sneakError();
                    Toasty.error(getApplicationContext(), Constants.INVALID_PHONE).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
            }
        });


    }

    private void checkPhone() {

        //        String fullPhone = tvCountryCode.getText().toString() + etEnterPhone.getText().toString();
//        String fullPhone = getString(R.string.country_code) + etEnterPhone.getText().toString();
        String fullPhone = Constants.COUNTRY_CODE + etEnterPhone.getText().toString();


        //start phone verification
        new PhoneUtils(CustomerWorkerLoginActivity.this)
                .startPhoneNumberVerification(fullPhone, mCallbacks);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.fullScreen(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        Prefs.remove(Constants.LOGIN_PHONE);
        setContentView(R.layout.activity_customer_worker_login);
        ButterKnife.bind(this);
    }

    private void setKeyboardVisibilityEventListener() {
        try {
            KeyboardVisibilityEvent.setEventListener(
                    this,
                    isOpen -> {
                        // write your code
                        Log.e(TAG, "onVisibilityChanged: " + isOpen);
//                           tvLogin.setVisibility(isOpen ? View.GONE : View.VISIBLE);
                        YoYo.with(isOpen ? Techniques.ZoomOut : Techniques.ZoomIn)
                                .duration(500)
                                .onEnd(new YoYo.AnimatorCallback() {
                                    @Override
                                    public void call(Animator animator) {
                                        tvLogin.setVisibility(isOpen ? View.GONE : View.VISIBLE);
                                    }
                                })
                                .playOn(tvLogin);

                    });
        } catch (Exception e) {
            Log.e(TAG, "onCreate: " + e.getMessage());

        }
    }

    private void initVars() {

//        dlgProgress = Utils.getInstance().pleaseWait(this);

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onCodeAutoRetrievalTimeOut(String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                Log.e(TAG, "onCodeAutoRetrievalTimeOut: TimeOut");
            }

            //Called when verification is done without user interaction , ex- when user is verified without code,
            // it's takes PhoneAuthCredential (info about Auth Credential for sms)
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) { //listener for if the code is send to the same device,
                // credential phoneNum style and its details
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the sms number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.e("onVerificationCompleted", "onVerificationCompleted:" + credential);

                // Update the UI and attempt sign in with the sms credential
//                updateUI(STATE_VERIFY_SUCCESS, credential);
                signInWithPhoneAuthCredential(credential);


            }

            //Called when some error occurred such as failing of sending SMS or Number format exception
            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the sms number format is not valid.
                Log.e(TAG, "onVerificationFailed 1" + e.getMessage());
//                btnRegisterPhoneNumber.setText(getString(R.string.lets_go));
                //Number format exception
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Log.e(TAG, "onVerificationFailed 4");
                    // Invalid request
                    etEnterPhone.setError(Constants.INVALID_PHONE);
//                    Sneaker.with(CustomerRegisterActivity.this).setCategoryName(Constants.INVALID_PHONE).sneakError();
                    Utils.startWobble(getApplicationContext(), etEnterPhone);
                    dialogProgress.dismiss();
                    Toasty.error(getApplicationContext(), Constants.INVALID_PHONE).show();

                } else if (e instanceof FirebaseTooManyRequestsException) {
//                    Log.e(TAG, "onVerificationFailed 5" + dialogProgress.isShowing() + " - " + dlgProgress.isShowing()  );

                    dialogProgress.dismiss();
                    // Quota exceeded
                    // The SMS quota for the project has been exceeded (u send a lot of codes in short time )
//                    Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.quota_exceeded),
//                            Snackbar.LENGTH_SHORT).show();
//                    initCountDownTimerResendCode();
                    Toasty.error(getApplicationContext(), getResources().getString(R.string.quota_exceeded), Toasty.LENGTH_SHORT)
                            .show();
//                    btnRegisterPhoneNumber.setEnabled(false);
//                    countDownTimer.start();
                } else {
                    Log.e(TAG, "onVerificationFailed 2" + e.getMessage());
                }

                // Show a message and update the UI
//                updateUI(STATE_VERIFY_FAILED);
            }

            //Called when verification code is successfully sent to the sms number.
            //A 'token' that can be used to force re-sending an SMS verification code
            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided sms number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.e(TAG, "onVerificationFailed 6");
                Log.e(TAG, "onCodeSent:" + verificationId);
                Log.e(TAG, "onCodeSent2:" + token);
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                updateUI(STATE_CODE_SENT);
                dialogProgress.dismiss();

//                Prefs.edit().putString(Constants.VERIFICATRION_ID, mVerificationId).apply();
            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        Log.e(TAG, "signInWithPhoneAuthCredential: ");
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.e(TAG, "signInWithPhoneAuthCredential:    isSuccessful ");
                        dialogProgress.dismiss();
                        autoRetrieval = true;
                        codeSentSuccess();
//                            Intent intent = new Intent(VerifyPhoneActivity.this, ProfileActivity.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            startActivity(intent);

                    } else {
//                            Toast.makeText(VerifyPhoneActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
//                            progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void updateUI(int uiState) {
        updateUI(uiState, firebaseAuth.getCurrentUser(), null);
    }

    private void updateUI(int uiState, FirebaseUser user, PhoneAuthCredential cred) {
        if (dialogProgress != null) {
            dialogProgress.dismiss();
        }
        switch (uiState) {
            case STATE_INITIALIZED:
                Log.e(TAG, "STATE_INITIALIZED");
                // Initialized state, show only the sms number field and start button
                break;
            case STATE_CODE_SENT:
                Log.e(TAG, "STATE_CODE_SENT");
//                Prefs.edit().remove(Constants.BOTTOM_SHEET_IS_SHOWN).apply();
                codeSentSuccess();
                break;
            case STATE_VERIFY_FAILED:
                Log.e(TAG, "STATE_VERIFY_FAILED");
                break;
            case STATE_VERIFY_SUCCESS:
                Log.e(TAG, "STATE_VERIFY_SUCCESS");
                break;
            case STATE_SIGNIN_FAILED:
                Log.e(TAG, "STATE_SIGNIN_FAILED");
                // No-op, handled by sign-in check
                break;
            case STATE_SIGNIN_SUCCESS:
                Log.e(TAG, "STATE_SIGNIN_SUCCESS");
                // Np-op, handled by sign-in check
//                registerUserToFireDatabase(user);
                break;
        }

        if (user == null) {
            // Signed out


        } else {
            // Signed in
//            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
////          intent.putExtra(Constants.USER_TYPE, Constants.DRIVERS);
//            startActivity(intent);
//            finish();
        }
    }

    private void codeSentSuccess() {

        phoneMaterials.setmResendToken(mResendToken);
        phoneMaterials.setVerificationId(mVerificationId);
        phoneMaterials.setPhoneNumber(etEnterPhone.getText().toString());

        Prefs.putString(Constants.PHONE_MATERIALS, gson.toJson(phoneMaterials, new TypeToken<PhoneMaterials>() {
        }.getType()));

        Intent intent = new Intent(this, EnterSmsCodeActivity.class);


        if (autoRetrieval) {
            intent.putExtra(Constants.AUTO_SMS_RETRIEVAL, true);
        }

        if (forgotOrNewUserForInstId) {
            Prefs.edit().remove(Constants.FIREBASE_UID).apply();
            intent.putExtra(Constants.FORGOT_PASS, Constants.FORGOT_PASS);


            startActivityForResult(intent, RE_CODE_FORGOT_PASS);
            Log.e(TAG, "codeSentSuccess: FORGOT_PASS");
//            finish();
        } else {
            if (flagLoggedFromAnotherDevice) {
                intent.putExtra(Constants.LOGGED_FROM_ANOTHER_DEVICE, Constants.LOGGED_FROM_ANOTHER_DEVICE);
                intent.putExtra(Constants.FIREBASE_UID, user.getUserId());
                startActivityForResult(intent, RE_CODE_LOGGED_FROM_ANOTHER_DEVICE);
            } else {
                intent.putExtra(Constants.NO_CURRENT_USER, Constants.NO_CURRENT_USER);
//            startActivityForResult(intent, RE_CODE_FORGOT_PASS);
                startActivity(intent);
            }
        }

//        startActivity(intent);
//        overridePendingTransition(R.anim.enter, R.anim.exit);
//        finish();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RE_CODE_FORGOT_PASS) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    Log.e(TAG, "onActivityResult: ");
//                        Toast.makeText(this, "update password", Toast.LENGTH_SHORT).show();
                    BottomFrgForgotPass bottomFrgForgotPass = new BottomFrgForgotPass();
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.PHONE, etEnterPhone.getText().toString());
//                    if (Utils.customerOrWorker()) {
//                        bundle.putSerializable(Constants.USER, user);
//                    } else {
//                        bundle.putSerializable(Constants.WORKER, cmWorker);
//                    }
                    bottomFrgForgotPass.setArguments(bundle);
//                        bottomFrgForgotPass.showNow(getSupportFragmentManager(), BottomFrgForgotPass.class.getName());
                    bottomFrgForgotPass.show(getSupportFragmentManager(), "BottomFrgForgotPass");
                }
            }
        }


        if (requestCode == RE_CODE_LOGGED_FROM_ANOTHER_DEVICE) {
            if (resultCode == Activity.RESULT_OK) {
                flagLoggedFromAnotherDevice = false;
//                checkIfUserIsLoggedInFromAndotherDevice();
                Toasty.success(getApplicationContext(), getString(R.string.okay_login_again), Toasty.LENGTH_SHORT)
                        .show();

            }
        }
    }

    private void checkIfUserIsLoggedInFromAndotherDevice() {
        if (Utils.customerOrWorker() && !isFinishing()) {
            Log.e(TAG, "checkIfUserIs ");
            //user
            Log.e(TAG, "checkIfUserIs: " + Prefs.getString(Constants.FIREBASE_UID, ""));
            Common.checkIfUserIsLoggedInFromAndotherDevice(Prefs.getString(Constants.FIREBASE_UID, ""), (isLoggedFromAnotherDevice, dataSnapshot) -> {
                Log.e(TAG, "checkIfUserIs: ");
                if (isLoggedFromAnotherDevice) {
                    if (!isFinishing()) {
                        HashMap<String, Object> hashMap = (HashMap<String, Object>) dataSnapshot.getValue();
                        if (dataSnapshot.hasChild(Constants.MESSAGE_TOKEN) && hashMap != null && hashMap.get(Constants.LOGIN) != null) {
//                           hashMap.get(Constants.LOGIN);
//                           hashMap.get(Constants.LOGIN);
//                           hashMap.put(Constants.LOGIN, false);
//                           dataSnapshot.getRef().setValue(hashMap);
                            Log.e(TAG, "1111 ");
                            boolean b = (boolean) hashMap.get(Constants.LOGIN);
                            if (!b) {
                                Log.e(TAG, "2222 ");
                                Common.logout(new OnLoggedOut() {
                                    @Override
                                    public void onLoggedOutSuccess() {
//                                        spotsDialog.dismiss();
                                        Log.e(TAG, "3333 ");
                                        finish();
                                        startActivity(new Intent(getApplicationContext(), CustomerOrWorkerActivity.class));
                                        Toasty.warning(getApplicationContext(), R.string.Please_log_in_again, Toasty.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onLoggedOutFailed() {
                                    }
                                });
                            }
                        }

//                        new AlertDialog.Builder(MainActivity.this)
//                                .setMessage(getString(R.string.login_from_another_device))
//                                .setTitle(getString(R.string.app_name_root))
//                                .setCancelable(false)
//                                .setPositiveButton(getString(R.string.log_out), (dialog, which) -> {
//                                            dialog.dismiss();
////                                        Prefs.remove(Constants.FIREBASE_UID);
////                                        Intent i = new Intent(getApplicationContext(), LanguageActivity.class);
////                                        startActivity(i);
////                                        finish();
//
//                                            spotsDialog = Utils.getInstance().pleaseWait(this);
//                                            spotsDialog.setMessage(getString(R.string.logging_out));
//                                            spotsDialog.setCanceledOnTouchOutside(false);
//                                            spotsDialog.setCancelable(false);
//                                            spotsDialog.show();
//
//
//                                        }
//                                )
//                                .show();
                    }


                } else {
                    Log.e(TAG, "checkIfWorkerIsDeactivated: ");
                }
            });
        }

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        //        Log.e(TAG, "onVerificationFailed 3");


        initVars();


        setKeyboardVisibilityEventListener();

        // ATTENTION: This was auto-generated to handle app links.
//        Intent appLinkIntent = getIntent();
//        String appLinkAction = appLinkIntent.getAction();
//        Uri appLinkData = appLinkIntent.getData();
//        if (appLinkData != null)
//            Log.e(TAG, "appLinkData: " + appLinkData.toString());
//        else
//            Log.e(TAG, "appLinkData: null ");


//        toggleBetweenWorkerAndCustomer();
        init();
//        accessPhoneAndPassIfExist();
        addTextWatcherToEditText(etEnterPass, ivShowHidePass);

//        showCase();


    }

    private static final String SHOWCASE_ID = "sequence example";

    private void showCase() {


//        new MaterialShowcaseView.Builder(this)
//                .setTarget(etEnterPhone)
//                .setDismissText("GOT IT")
//                .setContentText("This is some amazing feature you should know about")
//                .setDelay(withDelay) // optional but starting animations immediately in onCreate can make them choppy
//                .singleUse(SHOWCASE_ID) // provide a unique ID used to ensure it is only shown once
//                .show();


        ShowcaseTooltip toolTip1 = ShowcaseTooltip.build(this)
                .corner(20)
                .textColor(Color.parseColor("#333333"))
                .setTextGravity(Gravity.CENTER)
                .text("This is a <b>very funky</b> tooltip<br><br>This is a very long sentence to test how this tooltip behaves with longer strings. <br><br>Tap anywhere to continue");


        ShowcaseTooltip toolTip2 = ShowcaseTooltip.build(this)
                .corner(20)
                .setTextGravity(Gravity.CENTER)
                .textColor(Color.parseColor("#333333"))
                .text("This is a <b>very funky</b> tooltip<br><br>This is a very long sentence to test how this tooltip behaves with longer strings. <br><br>Tap anywhere to continue");


        ShowcaseTooltip toolTip3 = ShowcaseTooltip.build(this)
                .corner(20)
                .setTextGravity(Gravity.CENTER)
                .textColor(Color.parseColor("#333333"))
                .text("This is a <b>very funky</b> tooltip<br><br>This is a very long sentence to test how this tooltip behaves with longer strings. <br><br>Tap anywhere to continue");


        // sequence example
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(100); // half second between each showcase view

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, "hh25");

        sequence.setConfig(config);


        sequence.addSequenceItem(

                new MaterialShowcaseView.Builder(this)
                        .setTarget(llPhone)
                        .setToolTip(toolTip1)
                        .withRectangleShape()
                        .setTooltipMargin(30)
                        .setShapePadding(20)
                        .setDismissOnTouch(true)
                        .setMaskColour(getResources().getColor(R.color.grey_206))
                        .build()
        );

        sequence.addSequenceItem(

                new MaterialShowcaseView.Builder(this)
                        .setTarget(etEnterPass)
                        .setToolTip(toolTip2)
                        .withRectangleShape()
                        .setTooltipMargin(30)
                        .setShapePadding(20)
                        .setDismissOnTouch(true)
                        .setMaskColour(getResources().getColor(R.color.grey_206))
                        .build()
        );

        sequence.addSequenceItem(

                new MaterialShowcaseView.Builder(this)
                        .setTarget(btnLogin)
                        .setToolTip(toolTip3)
                        .withRectangleShape()
                        .setTooltipMargin(30)
                        .setShapePadding(20)
                        .setDismissOnTouch(true)
                        .setMaskColour(getResources().getColor(R.color.grey_206))
                        .build()
        );


//
//        sequence.addSequenceItem(
//
//                new MaterialShowcaseView.Builder(this)
//                        .setTarget(etEnterPass)
//                        .setToolTip(toolTip1)
//                        .withRectangleShape()
//                        .setTooltipMargin(30)
//                        .setShapePadding(50)
//                        .setDismissOnTouch(true)
//                        .setMaskColour(getResources().getColor(R.color.grey_400_alpha))
//                        .build()
//        );
//
//
//
//        sequence.addSequenceItem(
//
//                new MaterialShowcaseView.Builder(this)
//                        .setTarget(btnLogin)
//                        .setToolTip(toolTip1)
//                        .withRectangleShape()
//                        .setTooltipMargin(30)
//                        .setShapePadding(50)
//                        .setDismissOnTouch(true)
//                        .setMaskColour(getResources().getColor(R.color.grey_400_alpha))
//                        .build()
//        );


//        sequence.addSequenceItem(
//                new MaterialShowcaseView.Builder(this)
//                        .setSkipText("SKIP")
//                        .setTarget(etEnterPhone)
//                        .setDismissText("Next")
//                        .setContentText("This is button two")
////                        .withRectangleShape(true)
//                        .withRectangleShape()
//                        .build()
//        );

//        sequence.addSequenceItem(
//                new MaterialShowcaseView.Builder(this)
//                        .setSkipText("SKIP")
//                        .setTarget(etEnterPass)
//                        .setDismissText("Next")
//                        .setContentText("This is button two")
//
////                        .withRectangleShape(true)
//                        .withRectangleShape()
//                        .build()
//        );
//
//
//        sequence.addSequenceItem(
//                new MaterialShowcaseView.Builder(this)
//                        .setSkipText("SKIP")
//                        .setTarget(btnLogin)
//                        .setDismissText("Next")
//                        .setContentText("This is button two")
////                        .withRectangleShape(true)
//                        .withRectangleShape()
//                        .build()
//        );


//        sequence.addSequenceItem(etEnterPhone,
//                "This is button one", "GOT IT");

//
//        sequence.addSequenceItem(etEnterPass,
//                "This is button two", "GOT IT");

//        sequence.addSequenceItem(btnLogin,
//                "This is button three", "GOT IT");

        sequence.start();

    }

    //for highlight
//    private void showCase() {
//
//        MultiLamp multiLamp = new MultiLamp(CustomerWorkerLoginActivity.this);
////        multiLamp.setBackgroundColor(getResources().getColor(R.color.white));
////        multiLamp.setBackgroundColor(getResources().getColor(R.color.white));
//
//        ArrayList<Target> targets = new ArrayList<>();
////        targets.add(new Target(etEnterPhone, "This is button 1", MultiLamp.RIGHT, new Circle(40)));
//        targets.add(new Target(etEnterPhone, "Here you can Enter your phone as user", MultiLamp.TOP, new Rectangle()));
//        targets.add(new Target(etEnterPass, "Here you can Enter your pass as user ", MultiLamp.BOTTOM, new Rectangle()));
////        targets.add(new Target(btnLogin, "you can login by clicking here", MultiLamp.BOTTOM,  new Rectangle()));
//        multiLamp.build(targets);
//
//    }

    private void addTextWatcherToEditText(EditText editText, ImageView imageView) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (editText.length() > 0) {
                    imageView.setVisibility(View.VISIBLE);
                    Log.e(TAG, "afterTextChanged: appear");
                } else {
                    imageView.setVisibility(View.INVISIBLE);
                    Log.e(TAG, "afterTextChanged: not appear");

                }

            }
        });

    }

//    private void accessPhoneAndPassIfExist() {
//
//        if (Prefs.contains(Constants.LOGIN_PHONE)) {
//            if (etEnterPhone != null) {
//                etEnterPhone.setText(Prefs.getString(Constants.LOGIN_PHONE, ""));
//            }
//        }
////        if(Prefs.contains(Constants.LOGIN_PASS)){
//////            etEnterPass.setText(et);
////        }
//    }

    @Override
    protected void onResume() {
        super.onResume();
        AppConfig.getInstance().setConnectivityListener(this);
    }

    private void init() {
        dialogProgress = Utils.getInstance().pleaseWait(this);
        firebaseAuth = FirebaseAuth.getInstance();
        registerReceiver(connectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (connectivityReceiver != null)
            unregisterReceiver(connectivityReceiver);
    }

//    private void toggleBetweenWorkerAndCustomer() {
//        if (Prefs.contains(Constants.WORKER_OR_CUSTOMER)) {
//            if (TextUtils.equals(Prefs.getString(Constants.WORKER_OR_CUSTOMER, ""),
//                    Constants.CUSTOMERS)) {
//                Log.e(TAG, "CUSTOMERS: ");
//            } else {
//                Log.e(TAG, "WORKERS: ");
//            }
//        }
//    }

    private void goToHmePage() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) {
        } else {
            Common.showSnackError(this, isConnected);
        }
    }

    public void loginAfterPasswordDecrption(String pass) {
        Log.e(TAG, "loginAfterPasswordDecrption:1 " + pass);
        if (!TextUtils.equals(pass, etEnterPass.getText().toString().trim())) {
            Log.e(TAG, "loginAfterPasswordDecrption:2" + pass);

            etEnterPhone.setError(null);// for clearing the error icon
            etEnterPass.setError(Constants.INVALID_PASS);
            Utils.startWobble(getApplicationContext(), etEnterPass);
            Toasty.error(getApplicationContext(), Constants.INVALID_PASS).show();
            Log.e(TAG, "FIREBASE_UID:2 " +
                    Prefs.getString(Constants.FIREBASE_UID, ""));
            dialogProgress.dismiss();
        } else {
            if (Utils.companyOrNot()) {
                Log.e(TAG, "loginAfterPasswordDecrption:3 " + pass);

                Prefs.edit().putString(Constants.FIREBASE_UID,
                        company.getCompanyId()).apply();
//            FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener((task)->{});
                FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.e(TAG, "loginAfterPasswordDecrption:5 " + pass + " " + Prefs.contains(Constants.WORKER_LOGGED_AS));
                        HashMap<String, Object> map = new HashMap<>();
                        map.put(Constants.MESSAGE_TOKEN, task.getResult().getToken());
                        RefBase.refCompany(Prefs.getString(Constants.FIREBASE_UID, ""))
                                .getRef()
                                .updateChildren(map)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Log.e(TAG, "loginAfterPasswordDecrption:6 " + pass);
                                        dialogProgress.dismiss();
                                        Log.e(TAG, "onDataChange: hero ");
                                        EventBus.getDefault().removeAllStickyEvents();
                                        EventBus.getDefault().postSticky(new MsgFrmLoginToNotification());
                                        EventBus.getDefault().postSticky(new MsgFromLoginToFragment());
                                        Prefs.edit().putString(Constants.FIREBASE_UID, company.getCompanyId()).apply();
                                        Prefs.edit().putString(Constants.WORKER_LOGGED_AS, Constants.COMPANY).apply();
                                        //EventBus.getDefault().post(new MsgEventReloadFragment(true));
                                        goToHmePage();
                                    }
                                });

                    }
                });
            } else {
                Log.e(TAG, "loginAfterPasswordDecrption:4 " + pass);

                Prefs.edit().putString(Constants.FIREBASE_UID,
                        cmWorker.getWorkerId()).apply();
//            FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener((task)->{});
                FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        HashMap<String, Object> map = new HashMap<>();
                        map.put(Constants.MESSAGE_TOKEN, task.getResult().getToken());
                        RefBase.refWorker(Prefs.getString(Constants.FIREBASE_UID, ""))
                                .getRef()
                                .updateChildren(map)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        dialogProgress.dismiss();
                                        Log.e(TAG, "onDataChange: hero ");
                                        EventBus.getDefault().removeAllStickyEvents();
                                        EventBus.getDefault().postSticky(new MsgFrmLoginToNotification());
                                        EventBus.getDefault().postSticky(new MsgFromLoginToFragment());
                                        Prefs.edit().putString(Constants.FIREBASE_UID, cmWorker.getWorkerId()).apply();
                                        Prefs.edit().putString(Constants.WORKER_LOGGED_AS, Constants.FREELANCER).apply();
                                        //EventBus.getDefault().post(new MsgEventReloadFragment(true));
                                        goToHmePage();
                                    }
                                });

                    }
                });
            }

//
        }

    }

    @Override
    public void onPasswordDecrypted(String decryptedPassword) {
        loginAfterPasswordDecrption(decryptedPassword);
    }
}


