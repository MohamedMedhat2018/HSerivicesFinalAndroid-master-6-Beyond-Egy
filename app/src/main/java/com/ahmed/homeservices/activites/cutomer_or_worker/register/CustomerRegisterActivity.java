package com.ahmed.homeservices.activites.cutomer_or_worker.register;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ahmed.homeservices.R;
import com.ahmed.homeservices.activites.cutomer_or_worker.CustomerOrWorkerActivity;
import com.ahmed.homeservices.activites.sms.EnterSmsCodeActivity;
import com.ahmed.homeservices.constants.Constants;
import com.ahmed.homeservices.easy_image.SeyanahEasyImage;
import com.ahmed.homeservices.fire_utils.RefBase;
import com.ahmed.homeservices.models.PhoneMaterials;
import com.ahmed.homeservices.models.User;
import com.ahmed.homeservices.phone_utils.PhoneUtils;
import com.ahmed.homeservices.snekers.Snekers;
import com.ahmed.homeservices.utils.Utils;
import com.developer.kalert.KAlertDialog;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetBuilder;
import com.google.android.material.bottomsheet.BottomSheetDialog;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.pixplicity.easyprefs.library.Prefs;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.aprilapps.easyphotopicker.MediaFile;
import pl.aprilapps.easyphotopicker.MediaSource;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

import static com.ahmed.homeservices.constants.Constants.STATE_CODE_SENT;
import static com.ahmed.homeservices.constants.Constants.STATE_INITIALIZED;
import static com.ahmed.homeservices.constants.Constants.STATE_SIGNIN_FAILED;
import static com.ahmed.homeservices.constants.Constants.STATE_SIGNIN_SUCCESS;
import static com.ahmed.homeservices.constants.Constants.STATE_VERIFY_FAILED;
import static com.ahmed.homeservices.constants.Constants.STATE_VERIFY_SUCCESS;

public class CustomerRegisterActivity extends AppCompatActivity implements Validator.ValidationListener,
        EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks {

    public static final String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
//            Manifest.permission.WRذITE_CONTACTS,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    private static final String TAG = "CustomerRegister";
    private static final int RC_CAMERA_AND_STORAGE = 121;
    //    @TextRule(order = 1, minLength = 6, message = "Enter at least 6 characters.")
    @BindView(R.id.etEnterPass)
    EditText etEnterPass;
    @BindView(R.id.etEnterFullName)
    EditText etEnterFullName;
    //    @ConfirmPassword(order = 2)
    @BindView(R.id.etEnterConfPass)
    EditText etEnterConfPass;
    @BindView(R.id.etEnterEmail)
    EditText etEnterEmail;
    @BindView(R.id.etEnterPhone)
    EditText etEnterPhone;
    //    MaskedEditText etEnterPhone;
    @BindView(R.id.ivUserPhoto)
    ImageView ivUserPhoto;
    AlertDialog dlgProgress;
    @BindView(R.id.tvCountryCode)
    TextView tvCountryCode;
    FirebaseAuth firebaseAuth;
    User user = new User();
    String phoneNumber = "";
    Gson gson = new Gson();
    PhoneMaterials phoneMaterials = new PhoneMaterials();
    ChildEventListener childEventListener;
    ValueEventListener valueEventListener;
    StorageReference ref = null;
    FirebaseStorage storage;
    StorageReference storageReference;
    ProgressDialog progressDialog;
    AlertDialog spotsDialog;
    Validator validator;
    boolean validated = false;
    Uri uri;
    @BindView(R.id.scrollView)
    ScrollView scrollView;
    ArrayList<TextView> errors = new ArrayList<>();
    //    MixpanelAPI mixpanelAPI;
//    JSONObject jsonObject = new JSONObject();
    boolean autoRetrieval = false;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    //    private void addingErrors() {
//        errors.clear();
//        errors.add(tvErrPhoneNumber);
//        errors.add(tvErrNameInAr);
//        errors.add(tvErrNameInEn);
//        errors.add(tvErrPassword);
//        errors.add(tvErrConfPass);
//        errors.add(tvErrEmail);
//
//    }
    //    @BindView(R.id.tvErrPhoneNumber)
//    TextView tvErrPhoneNumber;
//    @BindView(R.id.tvErrNameInAr)
//    TextView tvErrNameInAr;
//    @BindView(R.id.tvErrNameInEn)
//    TextView tvErrNameInEn;
//    @BindView(R.id.tvErrPassword)
//    TextView tvErrPassword;
//    @BindView(R.id.tvErrConfPass)
//    TextView tvErrConfPass;
//    @BindView(R.id.tvErrEmail)
//    TextView tvErrEmail;
    private BottomSheetDialog dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Utils.fullScreen(this);


        setContentView(R.layout.activity_customer_register);
//        InfoLayoutBinding bind = DataBindingUtil.setContentView(this, R.layout.activity_customer_register);
//        bind.mainView.setOnClickListener(new OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                //your action
////            }
////        });

        ButterKnife.bind(this);
        init();
        updateProfilePhoto();
        initVars();
//        addingErrors();


//        String projectToken = YOUR_PROJECT_TOKEN; // e.g.: "1ef7e30d2a58d27f4b90c42e31d6d7ad"
//        mixpanelAPI = MixpanelAPI.getInstance(this, getString(R.string.MixpanelAPI));
//        try {
//            jsonObject.put("Prop name", "Prop value");
//            jsonObject.put("Prop 2", "Value 2");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        mixpanelAPI.track("Event name", jsonObject);


    }

    private void showError(int pos, CharSequence error) {

        for (int i = 0; i < errors.size(); i++) {
            errors.get(i).setVisibility(View.GONE);
        }

        errors.get(pos).setVisibility(View.VISIBLE);
        errors.get(pos).setText(error.toString());
    }

    @OnClick(R.id.ivUserPhoto)
    public void ivUserPhoto(View v) {
        Log.e(TAG, "ivUserPhoto: ");
        requestCamAndStoragePerms();
    }

    @AfterPermissionGranted(RC_CAMERA_AND_STORAGE)
    private void requestCamAndStoragePerms() {
        if (EasyPermissions.hasPermissions(CustomerRegisterActivity.this, PERMISSIONS)) {
            if (!dialog.isShowing()) {
                updateProfilePhoto();
                dialog.show();
            }
        } else {
            // Do not have permissions, request them now
//            EasyPermissions.requestPermissions(this, getString(R.string.contacts_and_storage_rationale),
//                    RC_CONTACT_AND_STORAGE, perms);
            EasyPermissions.requestPermissions(
                    new PermissionRequest.Builder(this, RC_CAMERA_AND_STORAGE, PERMISSIONS)
                            .setRationale(R.string.cam_and_storage_rationale)
                            .setPositiveButtonText(R.string.rationale_ask_ok)
                            .setNegativeButtonText(R.string.rationale_ask_cancel)
//                            .setTheme(R.style.AppTheme)
                            .build());

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            // Get a list of picked images
            List<Image> images = ImagePicker.getImages(data);
            // or get a single image only
            Image image = ImagePicker.getFirstImageOrNull(data);
            uri = Uri.fromFile(new File(image.getPath()));
            Picasso.get().load(uri)
                    .into(ivUserPhoto, new Callback() {
                        @Override
                        public void onSuccess() {
//                                Toast.makeText(getActivity(), "Photo updated", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
        }

        super.onActivityResult(requestCode, resultCode, data);

//        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
//            @Override
//            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
//                uri = Uri.fromFile(imageFile);
//                Picasso.get().load(uri)
//                        .into(ivUserPhoto, new Callback() {
//                            @Override
//                            public void onSuccess() {
////                                Toast.makeText(getActivity(), "Photo updated", Toast.LENGTH_SHORT).show();
//                            }
//
//                            @Override
//                            public void onError(Exception e) {
//
//                            }
//                        });
////                uploadPhoto(uri);
//            }
//        });

        SeyanahEasyImage.easyImage(this).handleActivityResult(requestCode, resultCode, data, this, new EasyImage.Callbacks() {
            @Override
            public void onImagePickerError(@NotNull Throwable throwable, @NotNull MediaSource mediaSource) {

            }

            @Override
            public void onMediaFilesPicked(@NotNull MediaFile[] mediaFiles, @NotNull MediaSource mediaSource) {
                uri = Uri.fromFile(mediaFiles[0].getFile());
                Picasso.get().load(uri)
                        .into(ivUserPhoto, new Callback() {
                            @Override
                            public void onSuccess() {
//                                Toast.makeText(getActivity(), "Photo updated", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });
//                uploadPhoto(uri);
            }

            @Override
            public void onCanceled(@NotNull MediaSource mediaSource) {

            }
        });


    }

    private void initVars() {
        spotsDialog = Utils.getInstance().pleaseWait(this);

        try {
            KeyboardVisibilityEvent.setEventListener(
                    this,
                    new KeyboardVisibilityEventListener() {
                        @Override
                        public void onVisibilityChanged(boolean isOpen) {
                            // write your code
                            Log.e(TAG, "onVisibilityChanged: " + isOpen);
//                           tvLogin.setVisibility(isOpen ? View.GONE : View.VISIBLE);
//                            YoYo.with(isOpen ? Techniques.ZoomOut : Techniques.ZoomIn)
//                                    .duration(500)
//                                    .onEnd(new YoYo.AnimatorCallback() {
//                                        @Override
//                                        public void call(Animator animator) {
////                                            tvLogin.setVisibility(isOpen ? View.GONE : View.VISIBLE);
//                                        }
//                                    })
//                                    .playOn(tvLogin);

                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "onCreate: " + e.getMessage());

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);


    }

    private void init() {
        dlgProgress = Utils.getInstance().pleaseWait(this);

        firebaseAuth = FirebaseAuth.getInstance();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onCodeAutoRetrievalTimeOut(String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                Log.e(TAG, "onCodeAutoRetrievalTimeOut: TimeOut");
            }

            //Called when verification is done without user interaction , ex- when user is verified without code,
            // it's takes PhoneAuthCredential (info about Auth Credential for sms)
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                //listener for if the code is send to the same device,
                // credential phoneNum style and its details
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the sms number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.e("onVerificationCompleted", "onVerificationCompleted:" + credential);
//                dlgProgress.dismiss();
                // Update the UI and attempt sign in with the sms credential
//                updateUI(STATE_VERIFY_SUCCESS, credential);
                signInWithPhoneAuthCredential(credential);


            }

            //Called when some error occurred such as failing of sending SMS or Number format exception
            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the sms number format is not valid.
                Log.e(TAG, "onVerificationFailed", e);
//                btnRegisterPhoneNumber.setText(getString(R.string.lets_go));
                //Number format exception
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request

                    etEnterPhone.setError(Constants.INVALID_PHONE);
//                    Sneaker.with(CustomerRegisterActivity.this).setCategoryName(Constants.INVALID_PHONE).sneakError();
                    Utils.startWobble(getApplicationContext(), etEnterPhone);
                    dlgProgress.dismiss();
                    Toasty.error(getApplicationContext(), Constants.INVALID_PHONE).show();

                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Log.e(TAG, "onVerificationFailed: Quota exceeded");
                    Toasty.error(getApplicationContext(), "Quota exceeded").show();
                    // Quota exceeded
                    // The SMS quota for the project has been exceeded (u send a lot of codes in short time )
//                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
//                            Snackbar.LENGTH_SHORT).show();
//                    initCountDownTimerResendCode();

//                    btnRegisterPhoneNumber.setEnabled(false);
//                    countDownTimer.start();
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
                Log.e(TAG, "onCodeSent:" + verificationId);
                Log.e(TAG, "onCodeSent2:" + token);
                // Save verification ID and resending token so we can use them later


                mVerificationId = verificationId;
                mResendToken = token;
                updateUI(STATE_CODE_SENT);


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
                        autoRetrieval = true;
                        dlgProgress.dismiss();
                        codeSentSuccess();
//                            Intent intent = new Intent(VerifyPhoneActivity.this, ProfileActivity.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            startActivity(intent);

                    } else {
                        Log.e(TAG, "signInWithPhoneAuthCredential: " + task.getException().getMessage());
//                            Toast.makeText(VerifyPhoneActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
//                            progressBar.setVisibility(View.GONE);
                    }
                });
    }

    @OnClick(R.id.tvLoginCustomer)
    void tvLoginCustomer(View view) {


        finish();


        Intent intentWorkers = new Intent();
        intentWorkers.setClass(this, CustomerOrWorkerActivity.class);
        startActivity(intentWorkers);


    }

    @OnClick(R.id.btnRegisterCustomer)
    void btnRegisterCustomer(View view) {

        //validator.validate();

//        if (uri == null) {
////            Utils.startWobble(getApplicationContext(), etEnterPhone);
////            etEnterPhone.setError("Enter sms number");
//            Toasty.warning(getApplicationContext(), "Upload photo").show();
//            scrollView.fullScroll(View.FOCUS_UP);
//            updateProfilePhoto();
//            return;
//        }


        if (TextUtils.isEmpty(etEnterPhone.getText())) {
            Utils.startWobble(getApplicationContext(), etEnterPhone);
//            etEnterPhone.setError(getString(R.string.error_enter_phone));
            //etEnterPhone.setError(getString(R.string.error_enter_phone));
//            showError(0, getString(R.string.error_enter_phone));
            Snekers.getInstance().error(getString(R.string.error_enter_phone), this);

            return;
        }

//        if (TextUtils.isEmpty(etEnterFullName.getText())) {
//            Utils.startWobble(getApplicationContext(), etEnterFullName);
//            etEnterFullName.setError(getString(R.string.enter_fullname));
////            showError(1, getString(R.string.enter_fullname));
//
//            return;
//        }

//        if (TextUtils.isEmpty(etEnterEmail.getText())) {
//            Utils.startWobble(getApplicationContext(), etEnterEmail);
//            etEnterEmail.setError(getString(R.string.enter_email));
////            showError(2, getString(R.string.enter_email));
//            return;
//        }

//        if (!Utils.isEmailValid(etEnterEmail.getText().toString())) {
//            Utils.startWobble(getApplicationContext(), etEnterEmail);
//            etEnterEmail.setError(getString(R.string.enter_valid_email));
////            showError(3, getString(R.string.enter_valid_email));
//            return;
//        }

        if (TextUtils.isEmpty(etEnterPass.getText())) {
            Utils.startWobble(getApplicationContext(), etEnterPass);
            //etEnterPass.setError(getString(R.string.enter_pass));
//            showError(4, getString(R.string.enter_pass));
            Snekers.getInstance().error(getString(R.string.enter_pass), this);
            return;
        }


        if (!Utils.isValidPassword(etEnterPass.getText().toString().trim())) {
            Utils.startWobble(getApplicationContext(), etEnterPass);
            //etEnterPass.setError(getString(R.string.enter_6_chars));
//            showError(5, getString(R.string.enter_6_chars));
//            Snekers.getInstance().error(getString(R.string.enter_6_chars), this);

            KAlertDialog kDialog = new KAlertDialog(this, KAlertDialog.WARNING_TYPE)
                    .setTitleText(getString(R.string.app_name_root))
                    .setContentText(getString(R.string.enter_6_chars))
                    .setConfirmText(getString(R.string.ok))
                    .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                        @Override
                        public void onClick(KAlertDialog kAlertDialog) {
                            kAlertDialog.dismissWithAnimation();
                        }
                    });
            kDialog.setCancelable(true);
            kDialog.setCanceledOnTouchOutside(true);
            kDialog.show();
            return;
        }


        if (!TextUtils.equals(etEnterConfPass.getText(), etEnterPass.getText())) {
            Utils.startWobble(getApplicationContext(), etEnterConfPass);
            //etEnterConfPass.setError(getString(R.string.error_pass_donot_match));
//            showError(6, getString(R.string.error_pass_donot_match));
            Snekers.getInstance().error(getString(R.string.error_pass_donot_match), this);
            return;
        }


        //else
        //register

        if (dlgProgress != null && !dlgProgress.isShowing()) {
            dlgProgress.setMessage(getString(R.string.registering_now));
            dlgProgress.show();
        }


//        track("Customer_Reg_Heah", "Customer reg");


        Query query = RefBase.registerPhone()
                .orderByChild(Constants.PHONE_FROM_USER_MODEL)
                .equalTo(etEnterPhone.getText().toString().trim());

        valueEventListener = query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().removeEventListener(this);
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {

                    childEventListener = query.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            dataSnapshot.getRef().removeEventListener(this);
//                            track("onChildAdded_5555", "fkfkff");

                            if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                                Log.e(TAG, "onChildAdded: 3");
//                                Toasty.error(getApplicationContext(),
//                                        Constants.PHONE_EXIST).show();
                                Utils.clearAllErrors(findViewById(android.R.id.content));
                                etEnterPhone.setError(Constants.PHONE_EXIST);
                                dlgProgress.dismiss();
//                                track("onChildAdded_7337", "fkfkff");


                            } else {
                                Log.e(TAG, "onChildAdded: 4");
//                                                    Toasty.success(getApplicationContext(),
//                                                           "Phone not exist").show();
//                                track("onChildAdded_793939", "fkfkff");
                                startVirificationOperation();
                            }
//                            dataSnapshot.getRef().removeEventListener(childEventListener);
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot,
                                                   @Nullable String s) {
                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            dlgProgress.dismiss();
//                            track("onCancelled12", databaseError.getMessage());

                        }
                    });
//                    dataSnapshot.getRef().removeEventListener(valueEventListener);
                } else {
                    Log.e(TAG, "data not exist: ");
//                    dlgProgress.dismiss();
//                    track("onDataChange_793939", "fkfkff");
                    startVirificationOperation();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                dlgProgress.dismiss();
//                track("onCancelled134034", databaseError.getMessage());
            }
        });
    }

//    private JSONObject track(String name, String value) {
//        try {
//            jsonObject = new JSONObject();
//            jsonObject.put(name, value);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
////        mixpanelAPI.track(TAG, jsonObject);
//        return this.jsonObject;
//    }

    private void startVirificationOperation() {
        user.setUserEmail(etEnterEmail.getText().toString());
        user.setUserPassword(etEnterPass.getText().toString());
        user.setUserPhoneNumber(etEnterPhone.getText().toString());
        user.setUserName(etEnterFullName.getText().toString());
        user.setLogin(true);
        Log.e(TAG, "onComplete: " + user.isLogin());

//        user.setUserType(new Random().nextBoolean() ? Constants.USER_TYPE_FREE : Constants.USER_TYPE_PREMIUM);
//        user.setUserStatusActivation(new Random().nextBoolean());
//        user.setCustomerId(FirebaseAuth.getInstance().getCurrentUser().getUid());
        Date date = Calendar.getInstance().getTime();
        // Display a date in day, month, year format
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String today = formatter.format(date);
        user.setCreateDate(today);
//        user.setAddressOrCurruntLocation("Giza");

        //        String fullPhone = tvCountryCode.getText().toString() + etEnterPhone.getText().toString();
//        String fullPhone = getString(R.string.country_code) + etEnterPhone.getText().toString();
        String fullPhone = Constants.COUNTRY_CODE + etEnterPhone.getText().toString();


//        String fullPhone = "+2" + etEnterPhone.getText().toString();
//        String fullPhone = "20" + etEnterPhone.getText().toString();
//        String fullPhone = "+971" + etEnterPhone.getText().toString();

//        PhoneNumberUtil phoneUtil = PhoneNumberUtil.createInstance(getApplicationContext());
//        try {
//            Phonenumber.PhoneNumber swissNumberProto = phoneUtil.parse(fullPhone, "EG");
//        } catch (NumberParseException e) {
//            System.err.println("NumberParseException was thrown: " + e.toString());
//        }
//        phoneUtil.format(swissNumberProto, PhoneNumberUtil.PhoneNumberFormat.E164);

        fullPhone = fullPhone.trim();
        Log.e(TAG, "register: " + fullPhone);
//        finish();

        new PhoneUtils(this).startPhoneNumberVerification(fullPhone, mCallbacks);
//        new PhoneUtils(this).startPhoneNumberVerification("+201156749640", mCallbacks);
    }

    @Override
    public View onCreateView(View view, @NotNull String name, @NotNull Context context, @NotNull AttributeSet attrs) {
        return super.onCreateView(view, name, context, attrs);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);


//        validator = new Validator(this);
//        validator.setValidationListener(this);


    }

    private void updateUI(int uiState) {
        updateUI(uiState, firebaseAuth.getCurrentUser(), null);
    }

    private void updateUI(int uiState, FirebaseUser user, PhoneAuthCredential cred) {
        if (dlgProgress != null) {
            dlgProgress.dismiss();
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
        //zozo
        if (Utils.customerOrWorker()) {

            phoneMaterials.setmResendToken(mResendToken);
            phoneMaterials.setVerificationId(mVerificationId);
            phoneMaterials.setPhoneNumber(etEnterPhone.getText().toString());

            Prefs.putString(Constants.PHONE_MATERIALS, gson.toJson(phoneMaterials, new TypeToken<PhoneMaterials>() {
            }.getType()));
            Prefs.putString(Constants.USER, gson.toJson(user, new TypeToken<User>() {
            }.getType()));

        }

        Intent intent = new Intent(this, EnterSmsCodeActivity.class);

        if (autoRetrieval) {
            intent.putExtra(Constants.AUTO_SMS_RETRIEVAL, true);
        }

        if (uri != null) {
            intent.putExtra(Constants.USER_PHOTO_URI, uri.toString());
        }
        startActivity(intent);
        overridePendingTransition(R.anim.enter, R.anim.exit);
        finish();
    }

    public void onValidationSucceeded() {
//        Toast.makeText(this, "Yay! we got it right!", Toast.LENGTH_SHORT).show();
        validated = true;
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {


    }

    public void onValidationFailed(View failedView, Rule<?> failedRule) {
//        validated = false;
//        String message = failedRule.getFailureMessage();
//
//        if (failedView instanceof EditText) {
//            failedView.requestFocus();
//            ((EditText) failedView).setError(message);
//        } else {
//            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
//        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }

    private void updateProfilePhoto() {
        dialog = new BottomSheetBuilder(this, null)
                .setMode(BottomSheetBuilder.MODE_LIST)
//                .setMode(BottomSheetBuilder.MODE_GRID)
                .addDividerItem()
                .expandOnStart(true)
                .setDividerBackground(R.color.grey_400)
                .setBackground(R.drawable.ripple_grey)
                .setMenu(R.menu.menu_image_picker)
                .setItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.chooseFromCamera:
                            //EasyImage.openChooserWithGallery(getApplicationContext(), "Ch", int type);
//                            SeyanahEasyImage.easyImage(getActivity()).openCameraForImage(FragmentProfile.this);
                            SeyanahEasyImage.openCamera(this);
                            break;
                        case R.id.chooseFromGellery:
//                            SeyanahEasyImage.easyImage(getActivity()).openGallery(FragmentProfile.this);
                            SeyanahEasyImage.openGallery(this);
                            break;
//                        case R.id.removePhoto:
//                            removePhoto();
//                            break;
                    }
                })
                .createDialog();
//        dialog.show();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onRationaleAccepted(int requestCode) {
//        updateProfilePhoto();
    }

    @Override
    public void onRationaleDenied(int requestCode) {

    }


}
